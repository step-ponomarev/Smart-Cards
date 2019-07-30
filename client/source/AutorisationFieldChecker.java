import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AutorisationFieldChecker implements Runnable {
  private JTextField m_loginField;
  private JTextField m_passField;
  private boolean m_key = false;
  private String [] m_userName;

  public AutorisationFieldChecker(JTextField login, JTextField pass, String [] user) {
    if ((login == null) || (pass == null)) {
      throw new NullPointerException("Null pointer");
    }

    m_loginField = login;
    m_passField = pass;
    m_userName = user;
  }

  public void run() {
    String lastLogin = " ";
    String lastPassword = " ";
    String login = " ";
    String password = " ";

    boolean loginKey = false;
    boolean passwordKey = false;


    while (true) {
      if ((login = m_loginField.getText()) != null) {
        login = login.trim();

        if (isChanged(lastLogin, login)) {
          loginKey = checkLogin(login);
          lastLogin = login;
        }
      }

      if ((password = m_passField.getText()) != null) {
        password = password.trim();

        if (isChanged(lastPassword, password)) {
          passwordKey = checkPassword(password);
          lastPassword = password;
        }
      }

      if (!loginKey) {
        if (login != null) {
          if (login.length() != 0) {
               //RED
            m_loginField.setBackground(new Color(255, 80, 80));
          } else {
            //WHITE
            m_loginField.setBackground(new Color(255, 255, 255));
          }
        }
      } else {
        //WHITE
        m_loginField.setBackground(new Color(255, 255, 255));
      }

      if (!passwordKey) {
        if (password != null) {
          if (password.length() != 0) {
            //RED
            m_passField.setBackground(new Color(255, 80, 80));
          } else {
            //WHITE
            m_passField.setBackground(new Color(255, 255, 255));
          }
        }
      } else {
        //RED
        m_passField.setBackground(new Color(255, 255, 255));
      }

      m_key = checkKeys(loginKey, passwordKey);
      if (m_key) {
        m_userName[0] = login;
      }
    }
  }

  private boolean isChanged(String last, String newbe) {
    if (newbe == null) {
      return false;
    }

    if (last.equals(newbe)) {
      return false;
    }

    return true;
  }

  private boolean checkLogin(String login) {
    if (login.length() == 0) {
      return false;
    }

    if (Character.isDigit(login.charAt(0))) {
      return false;
    }

    if (login.length() < 5) {
      return false;
    }

    if (login.length() > 16) {
      return false;
    }

    if (login.contains(" ")) {
      return false;
    }

    if (login.equals("Guest")) {
      return false;
    }

    return true;
  }

  private boolean checkPassword(String password) {
    if (password == null) {
      throw new NullPointerException("null pass");
    }

    if (password.length() == 0) {
      return false;
    }

    if (password.length() < 8) {
      return false;
    }

    if (password.length() > 30) {
      return false;
    }

    if (password.contains(" ")) {
      return false;
    }

    if (!isContainsDigit(password)) {
      return false;
    }

    return true;
  }

  private boolean isContainsDigit(String str) {
    for (int i = 0; i < str.length(); ++i) {
      if (Character.isDigit(str.charAt(i))) {
        return true;
      }
    }

    return false;
  }

  private boolean checkKeys(boolean key1, boolean key2) {
    return (key1 && key2);
  }

  public boolean getKey() {
    return m_key;
  }

};
