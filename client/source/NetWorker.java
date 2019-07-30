import java.io.*;
import java.net.*;

public class NetWorker {
  private Socket m_sock;
  private PrintWriter m_serverWriter;
  private BufferedReader m_serverReader;

  public NetWorker() throws IOException {
    m_sock = new Socket("127.0.0.1", 1984);
    InputStreamReader reader = new InputStreamReader(m_sock.getInputStream());
    m_serverReader = new BufferedReader(reader);
    m_serverWriter = new PrintWriter(m_sock.getOutputStream());
  }

  public int singUp(final String login, final String pass) {
    if ((login == null) || (pass == null)) {
      throw new NullPointerException("Null login or password");
    }

    int code = -1;
    try {
      String request = "singUp";

      m_serverWriter.println(request);
      m_serverWriter.println(login);
      m_serverWriter.println(pass);
      m_serverWriter.flush();

      code = Integer.parseInt(m_serverReader.readLine());
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      closeConnection();
      return code;
    }
  }

  public int logIn(final String login, final String pass) {
    if ((login == null) || (pass == null)) {
      throw new NullPointerException("Null login or password");
    }

    int code = -1;
    try {
      String request = "logIn";

      m_serverWriter.println(request);
      m_serverWriter.println(login);
      m_serverWriter.println(pass);
      m_serverWriter.flush();

      code = Integer.parseInt(m_serverReader.readLine());
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      closeConnection();
      return code;
    }
  }

  public int settings(final String login, final String pass) {
    if ((login == null) || (pass == null)) {
      throw new NullPointerException("Null login or password");
    }

    int code = -1;
    try {
      String request = "settings";

      String newLogin = login;
      String newPassword = pass;

      m_serverWriter.println(request);
      m_serverWriter.println(login);
      m_serverWriter.println(pass);
      m_serverWriter.flush();

      code = Integer.parseInt(m_serverReader.readLine());
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      closeConnection();
      return code;
    }
  }

  public int synch(final File path, final String userName) {
    if ((path == null) || (userName == null)) {
      throw new NullPointerException("Null login or file path");
    }

    File filaPath = path;
    String login = userName;

    String request = "synch";
    m_serverWriter.println(request);
    m_serverWriter.println(login);
    m_serverWriter.flush();

    try {
      int code = Integer.parseInt(m_serverReader.readLine());
      ObjectOutputStream onServer = new ObjectOutputStream(m_sock.getOutputStream());

      if (code == 0) {
        ObjectInputStream fileReader = new ObjectInputStream(new FileInputStream(filaPath));
        Object obj;
        while ((obj = fileReader.readObject()) != null) {
          onServer.writeObject(obj);
        }

        fileReader.close();
        onServer.close();
      }
    } catch(Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

  private void closeConnection() {
    try {
      m_sock.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }
};
