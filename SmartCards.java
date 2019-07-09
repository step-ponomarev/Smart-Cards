import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

import java.util.Date;

class Card {
  private String m_question;
  private String m_answer;

  public Card(String question, String answer) {
    if (question == null || answer == null) {
      throw new NullPointerException("Null question or answer");
    }

    m_question = question;
    m_answer = answer;
  }

  public Card getCard() {
    return new Card(m_question, m_answer);
  }
};

class CardState {
  private Card m_card;
  private Date m_checkedDate;
  private Date m_checkDate;

  public CardState(Card card) {
    if (card == null) {
      throw new NullPointerException("Null card");
    }

    m_card = card.getCard();
    m_checkedDate = null;
    m_checkDate = new Date();
  }
};

class CardCase {
  private String m_name;
  private ArrayList<CardState> m_cards;

  public CardCase(String newName) {
    if (newName == null) {
      throw new NullPointerException("Null name");
    }

    m_name = newName;
    m_cards = new ArrayList<CardState>();
  }

  public void addCard(CardState cardState) {
    if (cardState == null) {
      throw new NullPointerException("Null name");
    }

    m_cards.add(cardState);
  }

  public String getName() {
    return m_name;
  }
};

public class SmartCards /*extends WindoAdapter */{
  private JFrame m_main;
  private boolean m_subActive;
  private JTextArea m_question;
  private JTextArea m_answer;
  private ArrayList<CardCase> m_cases;
  private ArrayList<JPanel> m_caseState;

  public SmartCards() {
    m_subActive = false;
    m_cases = new ArrayList<CardCase>();
    m_caseState = new ArrayList<JPanel>();
    m_main = new JFrame("Smart Cards");
    m_main.setEnabled(true);
  }

  public void go() {
    setMainWindow();
    setCardsWindows();
   // setTextAreas();
    setMenuBar();

    m_main.setVisible(true);
  }

  private void setMainWindow() {
    m_main.setSize(640, 500);
    m_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) (dim.getWidth() / 2 - m_main.getWidth() / 2);
    int y = (int) (dim.getHeight() / 2 - m_main.getHeight() / 2);
    m_main.setLocation(x, y);

  }

  private void setCardsWindows() {
    JPanel casesWindows = new JPanel();
    Font myFont = new Font("Bree", Font.BOLD, 18);
    if (m_cases.size() == 0) {
      casesWindows.setLayout(new BorderLayout());
      JButton newCardKit = new JButton("Create new kit");
      newCardKit.setFont(myFont);
      casesWindows.add(BorderLayout.CENTER, newCardKit);
    } else {
      casesWindows.setLayout(new BoxLayout(casesWindows, BoxLayout.Y_AXIS));
      for (int k = 0; k < m_cases.size(); ++k) {
        m_caseState.add(new JPanel());
        m_caseState.get(k).setLayout(new BorderLayout());
        JLabel name = new JLabel(m_cases.get(k).getName());
        name.setFont(myFont);
        JPanel buttomsPanel = new JPanel();

        JButton editButton = new JButton("Edit");
        JButton studyButton = new JButton("Study");

        buttomsPanel.setLayout(new BorderLayout());
        buttomsPanel.add(BorderLayout.NORTH, editButton);
        buttomsPanel.add(BorderLayout.CENTER, studyButton);

        m_caseState.get(k).add(BorderLayout.NORTH, name);
        m_caseState.get(k).add(BorderLayout.SOUTH, buttomsPanel);

        casesWindows.add(m_caseState.get(k));
      }
    }

    m_main.getContentPane().add(casesWindows);
  }

  private void setTextAreas() {
    JPanel textPantl = new JPanel();

    BoxLayout layout = new BoxLayout(textPantl, BoxLayout.Y_AXIS);
    textPantl.setLayout(layout);

    Font myFont = new Font("Bree", Font.BOLD, 18);

    JTextArea questionArea = new JTextArea();
    questionArea.setFont(myFont);
    questionArea.setLineWrap(true);
    JTextArea answerArea = new JTextArea();
    answerArea.setFont(myFont);
    answerArea.setLineWrap(true);

    JScrollPane questionScroller = new JScrollPane(questionArea);
    questionScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    questionScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    JScrollPane answerScroller = new JScrollPane(answerArea);
    answerScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    answerScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    JLabel qestuibLabel = new JLabel("Front card site:");
    qestuibLabel.setFont(myFont);
    textPantl.add(qestuibLabel);
    textPantl.add(questionScroller);

    JLabel answerLabel= new JLabel("Back card site:");
    answerLabel.setFont(myFont);
    textPantl.add(answerLabel);
    textPantl.add(answerScroller);

    m_main.getContentPane().add(BorderLayout.CENTER, textPantl);
    textPantl.setVisible(true);
  }

  private void setMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu smartCards = new JMenu("Smart Cards");
    JMenuItem settings = new JMenuItem("Settings");

    smartCards.add(settings);
    menuBar.add(smartCards);

    m_main.setJMenuBar(menuBar);
  }

  public static void main(String [] args) {
    SmartCards gui = new SmartCards();
    gui.go();
  }
};
