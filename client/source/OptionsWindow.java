import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class OptionsWindow extends WindowAdapter {
  protected JFrame m_parent;
  protected JFrame m_window;

  protected OptionsWindow(JFrame main) {
    m_window = new JFrame("Options");
    m_parent = main;
  }

  public abstract void go();
  public void windowClosing(WindowEvent event) {
    m_parent.setEnabled(true);
  }
};
