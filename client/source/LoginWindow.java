import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class LoginWindow extends OptionsWindow {
  private JTextField m_loginField;
  private JPasswordField m_passwordField;
  private JCheckBox m_rememberCheckBox;

  private Thread m_helper;
  private NetWorker m_netWorker;

  private AutorisationFieldChecker m_myThread;
  private Thread m_fieldChecker;
  private String [] m_userName;
  private String [] m_newUserName;

  private boolean m_key = false;

  public LoginWindow (JFrame main, String [] userName, Thread helper) throws IOException {
    super(main);
    m_netWorker = new NetWorker();

    m_loginField = new JTextField();
    m_passwordField = new JPasswordField();
    m_passwordField.setEchoChar('*');

    m_helper = helper;
    m_userName = userName;
    m_newUserName = new String[2];
    m_newUserName[0] = m_userName[0];
    m_newUserName[1] = m_userName[1];

    m_myThread = new AutorisationFieldChecker(m_loginField, m_passwordField, m_newUserName);
    m_fieldChecker = new Thread(m_myThread);
  }

  public void go() {
    m_parent.setEnabled(false);

    Font buttonFont = new Font("Bree", Font.BOLD, 18);
    Font labelFont = new Font("Bree", Font.BOLD, 20);
    Font fieldFont = new Font("Bree", Font.BOLD, 14);

    m_window.setLayout(new BorderLayout());
    m_window.addWindowListener(this);
    m_window.setSize(300, 220);
    HelpMethods.frameOnCenter(m_window);
    m_window.setBackground(Color.white);
    m_window.setResizable(false);

    JPanel topLabelPanel = new JPanel();
    topLabelPanel.setBackground(Color.white);

    JPanel autorisationPanel = new JPanel();
    autorisationPanel.setLayout(new BoxLayout(autorisationPanel, BoxLayout.Y_AXIS));
    autorisationPanel.setBackground(Color.white);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BorderLayout());
    buttonPanel.setBackground(Color.white);

    JLabel windowNameLabel = new JLabel("Autorisation");
    windowNameLabel.setFont(labelFont);

    JPanel loginPanel = new JPanel();
    loginPanel.setBackground(Color.white);

    JLabel loginLabel = new JLabel("Login: ");
    loginLabel.setFont(buttonFont);
    m_loginField.setFont(fieldFont);
    m_loginField.setPreferredSize(new Dimension(200, 20));

    loginPanel.add(loginLabel);
    loginPanel.add(m_loginField);

    JPanel passPanel = new JPanel();
    passPanel.setBackground(Color.white);

    JLabel passLabel = new JLabel("Password: ");
    passLabel.setFont(buttonFont);
    m_passwordField.setFont(fieldFont);
    m_passwordField.setPreferredSize(new Dimension(160, 20));

    passPanel.add(passLabel);
    passPanel.add(m_passwordField);

    m_rememberCheckBox = new JCheckBox("Remember me");
    m_rememberCheckBox.setFont(fieldFont);

    JButton loginButton = new JButton("Login");
    loginButton.addActionListener(new LoginListener());
    loginButton.setFont(buttonFont);

    JButton registerButton = new JButton("New Account");
    registerButton.addActionListener(new RegisterListener());
    registerButton.setFont(buttonFont);

    topLabelPanel.add(windowNameLabel);

    autorisationPanel.add(loginPanel);
    autorisationPanel.add(passPanel);
    autorisationPanel.add(m_rememberCheckBox);

    buttonPanel.add(BorderLayout.NORTH, loginButton);
    buttonPanel.add(BorderLayout.SOUTH, registerButton);

    m_window.add(BorderLayout.NORTH, topLabelPanel);
    m_window.add(BorderLayout.CENTER, autorisationPanel);
    m_window.add(BorderLayout.SOUTH, buttonPanel);
    m_window.setVisible(true);

    m_fieldChecker.start();
  }

  public void windowClosing(WindowEvent event) {
    m_parent.setEnabled(true);
    m_fieldChecker.stop();
  }

  class RegisterListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      m_key = m_myThread.getKey();

      if (m_key) {
        String login = m_loginField.getText();
        String pass = m_passwordField.getText();
        m_newUserName[1] = pass;
        m_userName[0] = m_newUserName[0];
        m_userName[1] = m_newUserName[1];

        int code = m_netWorker.singUp(login, pass);

        if (code == 0) {
          m_fieldChecker.stop();
          if (m_rememberCheckBox.isSelected()) {
            m_helper.start();

            try {
              UserWindow window = new UserWindow(m_parent, m_userName, m_helper);
              window.go();
            } catch(IOException e) {
              NoConnectionWindow errorWindow = new NoConnectionWindow();
              errorWindow.go();
            }
          } else {
            // Окно об успешной регистрации
          }

          m_window.dispose();
          m_parent.setEnabled(true);
        } else {
          // окно о неуспешной регистрации
        }
      }
    }
  };

  class LoginListener implements ActionListener {
    public void actionPerformed(ActionEvent even) {
      m_key = m_myThread.getKey();
      if (m_key) {
        String login = m_loginField.getText();
        String pass = m_passwordField.getText();
        m_newUserName[1] = pass;
        m_userName[0] = m_newUserName[0];
        m_userName[1] = m_newUserName[1];

        int code = m_netWorker.logIn(login, pass);

        if (code == 0) {
          m_fieldChecker.stop();
          if (m_rememberCheckBox.isSelected()) {
            m_helper.start();
          }

          try {
            UserWindow window = new UserWindow(m_parent, m_userName, m_helper);
            window.go();
          } catch(IOException e) {
            NoConnectionWindow errorWindow = new NoConnectionWindow();
            errorWindow.go();
          }
          m_window.dispose();
          m_parent.setEnabled(true);
        } else {
          try {
            m_netWorker = new NetWorker();
            m_loginField.setText("");
            m_passwordField.setText("");
          } catch(IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

  };
};
