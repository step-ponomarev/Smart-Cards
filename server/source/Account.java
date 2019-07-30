import java.io.*;
import java.util.*;

public class Account implements Serializable, Comparable<Account> {
  private String m_login;
  private String m_password;

  public Account(String login, String password) {
    m_login = login;
    m_password = password;
  }

  public String getLogin() {
    return m_login;
  }

  public String getPass() {
    return m_password;
  }

  public String toString() {
    return m_login;
  }

  public void setLogin(final String login) {
    if (login == null) {
      throw new NullPointerException("Null login");
    }

    m_login = login;
  }

  public void setPass(final String pass) {
    if (pass == null) {
      throw new NullPointerException("Null pass");
    }

    m_password = pass;
  }

  public int compareTo(Account another) {
    return m_login.compareTo(another.getLogin());
  }
};
