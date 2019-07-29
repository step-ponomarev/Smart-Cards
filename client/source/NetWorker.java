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
    }

    return code;
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
    }

    return code;
  }
};
