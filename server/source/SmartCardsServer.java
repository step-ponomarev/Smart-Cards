import java.net.*;
import java.io.*;
import java.util.*;

public class SmartCardsServer {
  private ServerSocket m_serverSock;
  private HashMap<String, Account> m_account;
  private File m_usersFile;

  public SmartCardsServer() {
    try {
      m_serverSock = new ServerSocket(1984);
    } catch(IOException ex) {
      ex.printStackTrace();
    }
    m_usersFile = new File("users.ser");

    m_account = new HashMap<String, Account>();
    setUpFile();
    uploadUsers();
  }

  public void go() {
    while (true) {
      try {
        Socket sock = m_serverSock.accept();
        Thread client = new Thread(new ClientHandler(sock));
        client.start();

      } catch(IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  class ClientHandler implements Runnable {
    private Socket m_clientSocket;
    private BufferedReader m_clientReader;
    private PrintWriter m_clientWriter;

    public ClientHandler(Socket sock) {
      m_clientSocket = sock;
      try {
        m_clientWriter = new PrintWriter(m_clientSocket.getOutputStream());
        InputStreamReader clientStreamReader = new InputStreamReader(m_clientSocket.getInputStream());
        m_clientReader = new BufferedReader(clientStreamReader);
      } catch(IOException ex) {
        ex.printStackTrace();
      }

      System.out.println("Connected");
    }

    public void run() {
     processRequest();
    }

    private void processRequest() {
      String request = null;
      try {
        request = m_clientReader.readLine();
        System.out.println(request);
      } catch(IOException e) {
        e.printStackTrace();
      }

      switch (request) {
        case "singUp":
          singUpUser();
          break;
        case "logIn":
          logIn();
          break;
        case "settings":
          settings();
          break;
      }

      closeConnection();
      sych();
    }

    private void singUpUser() {
      String login = null;
      String password = null;

      try {
        login = m_clientReader.readLine();
        password = m_clientReader.readLine();
      } catch(IOException ex) {
        ex.printStackTrace();
      }

      if (m_account.containsKey(login)) {
        m_clientWriter.println(-1);
        m_clientWriter.flush();
        return;
      } else {
        if ((login == null) && (password == null)) {
          m_clientWriter.println(-1);
          m_clientWriter.flush();
          return;
        }

        Account newAccount = new Account(login, password);
        m_account.put(login, newAccount);
        System.out.println("Registered: " + newAccount);
      }
      File userFile = new File(login);
      usersFileSetUp(userFile);

      m_clientWriter.println(0);
      m_clientWriter.flush();
    }

    private void logIn() {
      String login = null;
      String password = null;

      try {
        login = m_clientReader.readLine();
        password = m_clientReader.readLine();
      } catch(IOException ex) {
        ex.printStackTrace();
      }

      if ((m_account.get(login) == null) || (login == null) || (password == null)) {
        m_clientWriter.println(-1);
        m_clientWriter.flush();
        return;
      }

      Account client = m_account.get(login);
      if (!client.getPass().equals(password)) {
        m_clientWriter.println(-1);
        m_clientWriter.flush();
        return;
      }

      m_clientWriter.println(0);
      m_clientWriter.flush();
    }

    private void settings() {
      String login = null;
      String password = null;

      try {
        login = m_clientReader.readLine();
        password = m_clientReader.readLine();
      } catch(Exception e) {
        e.printStackTrace();
      }

      if (!m_account.containsKey(login)) {
        System.out.println("No user");
        m_clientWriter.println(-1);
        m_clientWriter.flush();
      }

      Account thisAccount = m_account.get(login);
      thisAccount.setLogin(login);
      thisAccount.setPass(password);

      m_clientWriter.println(0);
      m_clientWriter.flush();
    }

    private void synch() {
      try {
        ObjectInputStream fromClient = new ObjectInputStream(m_clientSocket.getInputStream());
        String userName = m_clientReader.readLine();
        if (m_account.containsKey(userName)) {
          m_clientWriter.println(0);
          File userFile = new File(userName);
          usersFileSetUp(userFile);

          ObjectOutputStream inFile = new ObjectOutputStream(new FileOutputStream(userFile));
          Object obj;
          while ((obj = fromClient.readObject()) != null) {
            inFile.writeObject(obj);
          }
          inFile.close();
          fromClient.close();
        } else {
          m_clientWriter.println(-1);
          return;
        }
      } catch(Exception ex) {
        ex.printStackTrace();
        m_clientWriter.println(-1);
        closeConnection();
        return;
      }
    }

    private void closeConnection() {
      try {
        m_clientSocket.close();
        System.out.println("Connection closed");
      } catch(IOException ex) {
        ex.printStackTrace();
      }
    }

    private void usersFileSetUp(File userFile) {
      if (!userFile.exists()) {
        try {
          userFile.createNewFile();
        } catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
  };

  private void setUpFile() {
    if (!m_usersFile.exists()) {
      try {
        m_usersFile.createNewFile();
      } catch(IOException e) {
        e.printStackTrace();
      }
      sych();
    }
  }

  private void uploadUsers() {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(m_usersFile));
      m_account = (HashMap<String, Account>) in.readObject();
      in.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void sych() {
    try {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(m_usersFile));
      out.writeObject(m_account);
      out.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String [] args) {
    SmartCardsServer server = new SmartCardsServer();
    server.go();
  }
};
