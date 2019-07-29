import java.net.*;
import java.io.*;
import java.util.*;

public class SmartCardsServer {
  private ServerSocket m_serverSock;
  private HashMap<String, Account> m_account;

  public SmartCardsServer() {
    try {
      m_serverSock = new ServerSocket(1984);
    } catch(IOException ex) {
      ex.printStackTrace();
    }

    m_account = new HashMap<String, Account>();
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
        default:
          closeConnection();
          break;
      }
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
        closeConnection();

        return;
      } else {
        if ((login == null) && (password == null)) {
          m_clientWriter.println(-1);
          m_clientWriter.flush();
          closeConnection();

          return;
        }

        Account newAccount = new Account(login, password);
        m_account.put(login, newAccount);
        System.out.println("Registered: " + newAccount);
      }

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
        closeConnection();

        return;
      }

      Account client = m_account.get(login);
      if (client.getPass() != password) {
        m_clientWriter.println(-1);
        m_clientWriter.flush();

        closeConnection();
        return;
      }

      m_clientWriter.println(0);
      m_clientWriter.flush();
      closeConnection();
    }

    private void closeConnection() {
      try {
        m_clientSocket.close();
      } catch(IOException ex) {
        ex.printStackTrace();
      }
    }
  };


  public static void main(String [] args) {
    SmartCardsServer server = new SmartCardsServer();
    server.go();
  }
};
