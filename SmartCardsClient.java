import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

class Account implements Serializable {
  private String m_login;
  private String m_password;
  private String m_phone;

  public Account(String login, String password, String phone) {
    m_login = login;
    m_password = password;
    m_phone = phone;
  }

  public String getLogin() {
    return m_login;
  }

  public String getPass() {
    return m_password;
  }

  public String getPhone() {
    return m_phone;
  }
};

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
  private String m_description;
  private ArrayList<CardState> m_cards;

  public CardCase(String newName) {
    if (newName == null) {
      throw new NullPointerException("Null name");
    }

    m_name = newName;
    m_description = "";
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

  public int getSize() {
    return m_cards.size();
  }

  public void setDescription(String description) {
    if (description == null) {
      throw new NullPointerException("Null description of card-case");
    }
    m_description = description;
  }
};

public class SmartCardsClient extends WindowAdapter {
  private JFrame m_main;
  private JTextArea m_stateLabel;
  private JTextArea m_question;
  private JTextArea m_answer;
  private JList<String> m_casesList;
  private ArrayList<CardCase> m_cases;
  private Account m_account;

  public SmartCardsClient() {
    m_cases = new ArrayList<CardCase>(0);

    //setAccount();
    //setUpNetworking();
  }

  public void go() {
    m_main = new JFrame("Smart Cards");
    m_main.setEnabled(true);
    m_main.setSize(640, 500);
    m_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    m_main.addWindowListener(this);

    setUpList();
    setButtons();
    setMenuBar();

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) (dim.getWidth() / 2 - m_main.getWidth() / 2);
    int y = (int) (dim.getHeight() / 2 - m_main.getHeight() / 2);
    m_main.setBackground(Color.white);
    m_main.setLocation(x, y);
    m_main.setResizable(false);
    m_main.setVisible(true);
    //setTextAreas();
  }

  private void setUpList() {
    Font buttonFont = new Font("Bree", Font.BOLD, 16);
    Font listFont = new Font("Bree", Font.BOLD, 20);
    Font labelFont = new Font("Bree", Font.BOLD, 18);

    JPanel casesPanel = new JPanel();
    m_casesList = new JList<String>();
    m_casesList.setSelectionMode​(ListSelectionModel.SINGLE_SELECTION);
    m_casesList.addListSelectionListener(new ListSelectListener());
    casesPanel.setLayout(new BorderLayout());

    JScrollPane caseScroller = new JScrollPane(m_casesList);
    caseScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    caseScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    Box buttonBox = new Box(2);
    buttonBox.setBackground(Color.white);
    JButton editButton = new JButton("Edit");
    JButton studyButton = new JButton("Study");

    buttonBox.add(BorderLayout.NORTH, editButton);
    buttonBox.add(BorderLayout.CENTER, studyButton);

    JPanel caseState = new JPanel();
    caseState.setLayout(new BorderLayout());
    caseState.setBackground(Color.white);

    m_stateLabel = new JTextArea(5, 11);
    m_stateLabel.setEditable(false);
    m_stateLabel.setFont(labelFont);

    editButton.setFont(buttonFont);
    studyButton.setFont(buttonFont);
    m_casesList.setFont(listFont);

    caseState.add(BorderLayout.CENTER, m_stateLabel);

    JPanel statePanel = new JPanel();
    statePanel.setLayout(new BorderLayout());
    statePanel.add(BorderLayout.CENTER, caseState);
    statePanel.add(BorderLayout.SOUTH, buttonBox);

    casesPanel.add(BorderLayout.EAST, statePanel);
    casesPanel.add(BorderLayout.CENTER, caseScroller);

    updateList();
    updateStatePanel();
    m_main.getContentPane().add(BorderLayout.CENTER, casesPanel);
  }

  private void updateList() {
    String [] list = new String[m_cases.size()];
    for (int k = 0; k < m_cases.size(); ++k) {
      list[k] = m_cases.get(k).getName();
    }
    Arrays.sort(list);

    m_casesList.setListData(list);
    m_casesList.revalidate();
    m_casesList.repaint();
  }

  private void setButtons() {
    Font myFont = new Font("Bree", Font.BOLD, 18);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BorderLayout());
    buttonPanel.setBackground(Color.white);

    JButton newCardKitButton = new JButton("Create new kit");
    newCardKitButton.addActionListener(new NewKitListener());
    newCardKitButton.setFont(myFont);

    JButton newCardsButton = new JButton("Add Card");
    //newCardsButton.addActionListener(new NewKitListener());
    newCardsButton.setFont(myFont);

    buttonPanel.add(BorderLayout.CENTER, newCardKitButton);
    buttonPanel.add(BorderLayout.SOUTH, newCardsButton);

    m_main.add(BorderLayout.SOUTH, buttonPanel);
  }

  private void updateStatePanel(CardCase thisCase) {
    if (thisCase != null) {
      m_stateLabel.setText("");
      m_stateLabel.append("Size: " + Integer.toString(thisCase.getSize()) + "\n");
    }
  }

  private void updateStatePanel() {
    m_stateLabel.setText("Numb of kits: " + Integer.toString(m_cases.size()));
  }

  private void setTextAreas() {
    Font myFont = new Font("Bree", Font.BOLD, 18);

    JPanel textPantl = new JPanel();

    BoxLayout layout = new BoxLayout(textPantl, BoxLayout.Y_AXIS);
    textPantl.setLayout(layout);

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

  public void windowClosing(WindowEvent e) {

  }

  private CardCase findCardCase(String name) {
    CardCase newCase = null;
    for (int i = 0 ; i < m_cases.size(); ++i) {
      if (m_cases.get(i).getName().equals(name)) {
        newCase = m_cases.get(i);
      }
    }

    return newCase;
  }

  class ListSelectListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent lse) {
      if (!lse.getValueIsAdjusting()) {
        Font labelFont = new Font("Bree", Font.BOLD, 18);

        String name = (String) m_casesList.getSelectedValue();
        CardCase thisCase = findCardCase(name);

        updateStatePanel(thisCase);
      }
    }
  };

  class NewKitListener extends WindowAdapter implements ActionListener {
    private JTextField m_kitNameTextField;
    private JTextArea m_kitDescrTextField;
    private JFrame m_newKitFrame;
    private ArrayList<String> m_nameList;
    private Thread m_checker;
    private boolean m_noMistakes;

    public void actionPerformed(ActionEvent event) {
      m_nameList = new ArrayList<String>();
      m_checker = new Thread(new MyFieldm_checker());

      for (int k = 0; k < m_cases.size(); ++k) {
        m_nameList.add(m_cases.get(k).getName());
      }

      Font fieldFont = new Font("Bree", Font.BOLD, 14);
      Font buttonFont = new Font("Bree", Font.BOLD, 18);
      Font labelFont = new Font("Bree", Font.BOLD, 20);
      m_noMistakes = false;

      JPanel newKitPanel = new JPanel();
      newKitPanel.setBackground(Color.white);
      JLabel newKitLabe = new JLabel("Kit Options");
      newKitLabe.setFont(labelFont);
      newKitPanel.add(newKitLabe);

      m_newKitFrame = new JFrame("New Kit");
      JPanel addFormPanel = new JPanel();
      addFormPanel.setBackground(Color.white);
      addFormPanel.setLayout(new BoxLayout(addFormPanel, BoxLayout.Y_AXIS));
      m_newKitFrame.setResizable(false);
      m_newKitFrame.addWindowListener(this);
      m_main.setEnabled(false);

      JPanel namePanel = new JPanel();
      namePanel.setBackground(Color.white);
      JLabel nameLabel = new JLabel("Name of kit:");
      nameLabel.setFont(buttonFont);
      m_kitNameTextField = new JTextField();
      m_kitNameTextField.setFont(fieldFont);
      m_kitNameTextField.setPreferredSize(new Dimension(250,20));

      namePanel.add(nameLabel);
      namePanel.add(m_kitNameTextField);

      JPanel descrPanel = new JPanel();
      descrPanel.setBackground(Color.white);
      descrPanel.setLayout(new BorderLayout());
      JLabel descrLabel = new JLabel("  Description:");
      descrLabel.setBackground(Color.white);
      descrLabel.setFont(labelFont);

      m_kitDescrTextField = new JTextArea();
      m_kitDescrTextField.setLineWrap(true);
      m_kitDescrTextField.setFont(fieldFont);

      JScrollPane descrScroll = new JScrollPane(m_kitDescrTextField);
      descrScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      descrScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

      descrPanel.add(BorderLayout.NORTH, descrLabel);
      descrPanel.add(BorderLayout.CENTER, descrScroll);

      JButton saveButton = new JButton("Save");
      saveButton.setFont(buttonFont);
      saveButton.addActionListener(new CreateKitListener());

      addFormPanel.add(namePanel);
      addFormPanel.add(descrPanel);

      m_newKitFrame.getContentPane().add(BorderLayout.NORTH, newKitPanel);
      m_newKitFrame.getContentPane().add(BorderLayout.CENTER, addFormPanel);
      m_newKitFrame.getContentPane().add(BorderLayout.SOUTH, saveButton);

      m_newKitFrame.setSize(400, 500);
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (int) (dim.getWidth() / 2 - m_newKitFrame.getWidth() / 2);
      int y = (int) (dim.getHeight() / 2 - m_newKitFrame.getHeight() / 2);
      m_newKitFrame.setLocation(x, y);
      m_newKitFrame.setVisible(true);

      m_checker.start();
    }

    class MyFieldm_checker implements Runnable {
      private boolean nameIsChanged = false;
      private boolean descrIsChanged = true;
      private String lastName = "";
      private String lastDescr = "";
      private boolean nameTrue = false;
      private boolean descrTrue = false;
      public void run() {
        String testName = "";
        String testDescr = "";
        while (true) {
          testName = m_kitNameTextField.getText();
          testDescr = m_kitDescrTextField.getText();

          if ((testName != null) && (testDescr != null)) {
            testName = testName.trim();
            testDescr = testDescr.trim();

            if (!lastName.equals(testName)) {
              nameIsChanged = true;
            }

            if (!lastDescr.equals(testDescr)) {
              descrIsChanged = true;
            }

            if (nameIsChanged) {
              lastName = testName;
              lastDescr = testDescr;

              if ((testName.length() == 0) || (testName.length() > 30) || (m_nameList.contains(testName))) {
                if ((nameTrue) || m_nameList.contains(testName)) {
                  m_kitNameTextField.setBackground(new Color(255, 80, 80));
                  nameTrue = false;
                }
              } else if (!nameTrue) {
                nameTrue = true;
                m_kitNameTextField.setBackground(new Color(102, 255, 153));
              }

              nameIsChanged = false;
            }

            if (descrIsChanged) {
              if ((testDescr.length() > 120)) {
                if (descrTrue) {
                  m_kitDescrTextField.setBackground(new Color(255, 80, 80));
                  descrTrue = false;
                }
              } else if (!descrTrue) {
                m_kitDescrTextField.setBackground(new Color(255, 255, 255));
                descrTrue = true;
              }

              descrIsChanged = false;
            }

            if (descrTrue && nameTrue) {
              m_noMistakes = true;
            } else {
              m_noMistakes = false;
            }
          }
        }
      }
    };

    public void windowClosing(WindowEvent event) {
      m_main.setEnabled(true);
    }

    class CreateKitListener implements ActionListener {

      public void actionPerformed(ActionEvent event) {
        if (m_noMistakes) {
          m_checker.stop();
          String name = m_kitNameTextField.getText().trim();
          String description = m_kitDescrTextField.getText().trim();
          CardCase newCase = new CardCase(name);
          newCase.setDescription(description);

          m_cases.add(newCase);
          m_main.setEnabled(true);
          m_newKitFrame.dispose();
          updateStatePanel();
          updateList();
        }
      }
    };
  };

  public static void main(String [] args) {
    SmartCardsClient gui = new SmartCardsClient();
    gui.go();
  }
};
