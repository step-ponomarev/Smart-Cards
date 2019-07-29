import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserWindow extends OptionsWindow {
  private JFrame m_parent;
  private Thread m_dataChanger;

  private String [] m_userData;

  private NetWorker m_netWorker;

  private JFrame m_window;

  public UserWindow (JFrame main, String [] data, Thread dataChanger) throws IOException {
    m_netWorker = new NetWorker();
    super(main);

    m_userData = data;
    m_dataChanger = dataChanger;
  }

  public void go() {
    m_parent.setEnabled(false);
    m_window = new JFrame("Options");
    m_window.setSize(200, 200);
    m_window.setResizable(false);
    HelpMethods.frameOnCenter(m_window);

    m_window.setVisible(true);
  }

  public void windowClosing(WindowEvent event) {
    m_parent.setEnabled(true);
  }
};
