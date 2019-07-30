import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NoConnectionWindow {
  private JFrame m_window;

  public NoConnectionWindow() {
    m_window = new JFrame("Connection is closed");
  }

  public void go() {
    Font buttonFont = new Font("Bree", Font.BOLD, 18);
    Font labelFont = new Font("Bree", Font.BOLD, 20);
    Font fieldFont = new Font("Bree", Font.BOLD, 14);

    m_window.setSize(200, 200);
    HelpMethods.frameOnCenter(m_window);

    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(Color.white);
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    JPanel labelPanel = new JPanel();
    labelPanel.setBackground(Color.white);

    JLabel messageLabel = new JLabel("No connection...");
    messageLabel.setFont(buttonFont);

    labelPanel.add(messageLabel);

    JButton okButton = new JButton("Ok");
    okButton.addActionListener(new okListener());
    okButton.setFont(buttonFont);

    m_window.add(BorderLayout.CENTER, labelPanel);
    m_window.add(BorderLayout.SOUTH, okButton);

    m_window.setVisible(true);
  }

  class okListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      m_window.dispose();
    }
  };
};
