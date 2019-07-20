import javax.swing.*;
import java.awt.*;

public final class HelpMethods {
  private HelpMethods(){};

  public static void frameOnCenter(JFrame frame) {
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) (dim.getWidth() / 2 - frame.getWidth() / 2);
    int y = (int) (dim.getHeight() / 2 - frame.getHeight() / 2);
    frame.setLocation(x, y);
  }

  public static JScrollPane createScrollPane(JList<String> obj) {
    JScrollPane descrScroll = new JScrollPane(obj);
    descrScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    descrScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    return descrScroll;
  }

  public static JScrollPane createScrollPane(JTextArea obj) {
    JScrollPane descrScroll = new JScrollPane(obj);
    descrScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    descrScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    return descrScroll;
  }
};
