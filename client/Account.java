import java.io.*;

public class Account implements Serializable {
  private String m_login;
  private String m_password;
  private String m_phone;

  public Account(String login, String password, String phone) {
    m_login = login;
    m_password = password;
    m_phone = phone;
  }

  public String getLogin() {
    return m_login;
  }

  public String getPass() {
    return m_password;
  }

  public String getPhone() {
    return m_phone;
  }
};
