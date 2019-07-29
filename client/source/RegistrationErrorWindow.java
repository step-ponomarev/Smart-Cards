import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RegistrationErrorWindow extends WindowAdapter {
  private JFrame m_window;
  private JFrame m_parent;

  public RegistrationErrorWindow(JFrame parent) {
    m_parent = parent;
  }

  public void go() {
    m_parent.setEnabled(false);
    m_window = new JFrame("Registration Error");

    m_window.setVisible(true);
  }

  public void windowClosing(WindowEvent ev) {
    m_parent.setEnabled(false);
  }

};
