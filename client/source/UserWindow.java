import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class UserWindow extends OptionsWindow {
  private String [] m_userData;
  private NetWorker m_netWorker;

  private JTextField m_loginField;
  private JPasswordField m_passwordField;

  private Thread m_fieldChecker;
  private Thread m_dataChanger;

  public UserWindow (JFrame main, String [] data, Thread dataChanger) throws IOException {
    super(main);
    m_netWorker = new NetWorker();

    m_userData = data;
    m_dataChanger = dataChanger;

    m_loginField = new JTextField();
    m_loginField.setText(data[0]);
    m_loginField.setEditable(false);
    m_loginField.setPreferredSize(new Dimension(200, 20));

    m_passwordField = new JPasswordField();
    m_passwordField.setText(data[1]);
    m_passwordField.setEchoChar('*');
    m_passwordField.setEditable(true);
    m_passwordField.setPreferredSize(new Dimension(200, 20));

    String [] cacheData = new String[2];
    cacheData[0] = m_userData[0];
    cacheData[1] = m_userData[1];

    AutorisationFieldChecker fieldChecker = new AutorisationFieldChecker(m_loginField, m_passwordField, cacheData);
    m_fieldChecker = new Thread(fieldChecker);
  }

  public void go() {
    Font buttonFont = new Font("Bree", Font.BOLD, 18);
    Font labelFont = new Font("Bree", Font.BOLD, 20);
    Font fieldFont = new Font("Bree", Font.BOLD, 14);

    m_parent.setEnabled(false);
    m_window.setSize(400, 300);
    m_window.setResizable(false);
    HelpMethods.frameOnCenter(m_window);
    m_window.addWindowListener(this);

    JPanel settingsPanel = new JPanel();
    settingsPanel.setBackground(Color.white);
    //settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

    JPanel loginPanel = new JPanel();
    loginPanel.setBackground(Color.white);

    JLabel loginLabel = new JLabel("Login: ");
    loginLabel.setFont(labelFont);
    m_loginField.setFont(buttonFont);

    loginPanel.add(loginLabel);
    loginPanel.add(m_loginField);

    JPanel passwordPanel = new JPanel();
    passwordPanel.setBackground(Color.white);

    JLabel passwordLabel = new JLabel("Password: ");
    m_passwordField.setFont(fieldFont);
    passwordLabel.setFont(labelFont);

    passwordPanel.add(passwordLabel);
    passwordPanel.add(m_passwordField);

    settingsPanel.add(BorderLayout.NORTH, loginPanel);
    settingsPanel.add(BorderLayout.SOUTH, passwordPanel);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.white);

    JButton saveButton = new JButton("Save");
    saveButton.setFont(buttonFont);
    saveButton.addActionListener(new SaveListener());

    JButton logOutButton = new JButton("LogOut");
    logOutButton.setFont(buttonFont);
    logOutButton.addActionListener(new LogOutListener());

    buttonPanel.add(logOutButton);
    buttonPanel.add(saveButton);

    m_window.add(BorderLayout.CENTER, settingsPanel);
    m_window.add(BorderLayout.SOUTH, buttonPanel);

    m_window.setVisible(true);
    m_fieldChecker.start();
  }

  public void windowClosing(WindowEvent event) {
    String login = m_loginField.getText();
    String password = m_passwordField.getText();

    int code = m_netWorker.settings(login, password);
    if (code == 0) {
      m_fieldChecker.stop();
      m_window.dispose();
      m_parent.setEnabled(true);
    } else {
      NoConnectionWindow errorWindow = new NoConnectionWindow();
      errorWindow.go();
      m_window.dispose();
      m_parent.setEnabled(true);
    }

    m_parent.setEnabled(true);
    m_fieldChecker.stop();
  }

  class LogOutListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      String login = m_loginField.getText();
      String password = m_passwordField.getText();

      int code = m_netWorker.settings(login, password);
      if (code == 0) {
        m_fieldChecker.stop();
        m_window.dispose();
        m_parent.setEnabled(true);
      } else {
        NoConnectionWindow errorWindow = new NoConnectionWindow();
        errorWindow.go();
        m_window.dispose();
        m_parent.setEnabled(true);
      }

      m_userData[0] = "Guest";
      m_userData[1] = "";
      m_dataChanger.start();
      m_window.dispose();
      m_parent.setEnabled(true);
    }
  };

  class SaveListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      String login = m_loginField.getText();
      String password = m_passwordField.getText();

      int code = m_netWorker.settings(login, password);
      if (code == 0) {
        m_fieldChecker.stop();
        m_window.dispose();
        m_parent.setEnabled(true);
      } else {
        NoConnectionWindow errorWindow = new NoConnectionWindow();
        errorWindow.go();
        m_window.dispose();
        m_parent.setEnabled(true);
      }
    }
  };
};
