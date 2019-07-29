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

  public int compareTo(Account another) {
    return m_login.compareTo(another.getLogin());
  }
};
