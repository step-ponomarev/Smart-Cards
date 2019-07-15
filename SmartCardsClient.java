import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Vector;
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

  public String getQuestion() {
    return m_question;
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
    m_checkedDate = new Date();
    m_checkDate = new Date();
  }

  public String getQuestion() {
    return m_card.getQuestion();
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

  public void add(CardState cardState) {
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

  public ArrayList<CardState> getCards() {
    return m_cards;
  }
};

public class SmartCardsClient extends WindowAdapter {
  private JFrame m_main;
  private JTextArea m_stateLabel;
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
    newCardsButton.addActionListener(new NewCardListener());
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

  private void setMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu smartCards = new JMenu("Smart Cards");
    JMenuItem settings = new JMenuItem("Settings");

    smartCards.add(settings);
    menuBar.add(smartCards);

    m_main.setJMenuBar(menuBar);
  }

  public void windowClosing(WindowEvent e) {
    //There is sych after closing
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

  class NewCardListener extends WindowAdapter implements ActionListener {
    private JTextArea m_questionArea;
    private JTextArea m_answerArea;
    private JComboBox m_caseBox;
    private Vector<String> m_nameCases;
    private CardCase m_choosed;

    private Thread m_checker;
    private boolean m_noMistakes;
    private ArrayList<String> m_questionList;

    private void setUpNewCardListener() {
      m_nameCases = new Vector<String>();
      int size = m_cases.size();
      String [] namesArray = new String[size];
      for (int i = 0; i < size; ++i) {
        namesArray[i] = m_cases.get(i).getName();
      }
      Arrays.sort(namesArray);

      for (int i = 0; i< size; ++i) {
        m_nameCases.add(namesArray[i]);
      }

      m_caseBox = new JComboBox(m_nameCases);

      String name = (String) m_caseBox.getSelectedItem();
      m_choosed = findCardCase(name);

      m_noMistakes = false;
    }

    private void updateQuestionList() {
      ArrayList<CardState> CardsInCase = m_choosed.getCards();
      m_questionList = new ArrayList<String>();
      for (int i = 0; i < CardsInCase.size(); ++i) {
        m_questionList.add(CardsInCase.get(i).getQuestion());
      }
    }

    private void goAddWindow() {
      Font buttonFont = new Font("Bree", Font.BOLD, 18);
      Font labelFont = new Font("Bree", Font.BOLD, 20);
      Font fieldFont = new Font("Bree", Font.BOLD, 14);

      JFrame newCardFrame = new JFrame("Add Card");
      //newCardFrame.setResizable(false);
      newCardFrame.addWindowListener(this);
      newCardFrame.setSize(500, 400);
      newCardFrame.setBackground(Color.white);

      JPanel textPantl = new JPanel();
      BoxLayout layout = new BoxLayout(textPantl, BoxLayout.Y_AXIS);
      textPantl.setLayout(layout);
      textPantl.setBackground(Color.white);

      m_questionArea = new JTextArea();
      m_questionArea.setFont(fieldFont);
      m_questionArea.setLineWrap(true);
      m_questionArea.setText("");

      m_answerArea = new JTextArea();
      m_answerArea.setFont(fieldFont);
      m_answerArea.setLineWrap(true);
      m_answerArea.setText("");

      JPanel chooseCasePanel = new JPanel();
      chooseCasePanel.setBackground(Color.white);

      JLabel chooseCaseLabel = new JLabel("Choose a kit: ");
      chooseCaseLabel.setFont(labelFont);

      m_caseBox.addActionListener(new ChoosedCaseListener());
      m_caseBox.setPreferredSize(new Dimension(300, 50));

      chooseCasePanel.add(chooseCaseLabel);
      chooseCasePanel.add(m_caseBox);

      JScrollPane questionScroller = new JScrollPane(m_questionArea);
      questionScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      questionScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

      JScrollPane answerScroller = new JScrollPane(m_answerArea);
      answerScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      answerScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

      JLabel qestuibLabel = new JLabel("Front card site:");
      qestuibLabel.setFont(labelFont);
      qestuibLabel.setBackground(Color.white);
      textPantl.add(qestuibLabel);
      textPantl.add(questionScroller);

      JLabel answerLabel= new JLabel("Back card site:");
      answerLabel.setFont(labelFont);
      answerLabel.setBackground(Color.white);
      textPantl.add(answerLabel);
      textPantl.add(answerScroller);

      JButton addButton = new JButton("Add");
      addButton.setBackground(Color.white);
      addButton.setFont(buttonFont);
      addButton.addActionListener(new AddCardListener());

      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (int) (dim.getWidth() / 2 - newCardFrame.getWidth() / 2);
      int y = (int) (dim.getHeight() / 2 - newCardFrame.getHeight() / 2);
      newCardFrame.setLocation(x, y);
      newCardFrame.setVisible(true);

      newCardFrame.getContentPane().add(BorderLayout.NORTH, chooseCasePanel);
      newCardFrame.getContentPane().add(BorderLayout.CENTER, textPantl);
      newCardFrame.getContentPane().add(BorderLayout.SOUTH, addButton);
    }

    public void actionPerformed(ActionEvent event) {
      if (m_cases.size() > 0) {
        setUpNewCardListener();
        goAddWindow();
        updateQuestionList();
        m_main.setEnabled(false);

        m_checker = new Thread(new MyFieldChecer());
        m_checker.start();
      } else {
        //noKitsWindow();
      }
    }

    public void windowClosing(WindowEvent event) {
      m_main.setEnabled(true);
      m_checker.stop();
    }

    class ChoosedCaseListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
        String name = (String) m_caseBox.getSelectedItem();
        m_choosed = findCardCase(name);
        updateQuestionList();
      }
    };

    class MyFieldChecer implements Runnable {
      private String lastQuestion;

      public void run() {
        String question = "";
        lastQuestion = question;
        while (true) {
          question = m_questionArea.getText();
          if (question != null) {
            question = question.trim();
          }
          if ((!question.equals(lastQuestion)) && (question != null)) {
            if ((m_questionList.contains(question)) || (question.length() == 0)) {
              if (question.length() != 0) {
                m_questionArea.setBackground(new Color(255, 80, 80));
              } else {
                m_questionArea.setBackground(new Color(255, 255, 255));
              }
              m_noMistakes = false;
            } else {
              m_questionArea.setBackground(new Color(255, 255, 255));
              m_noMistakes = true;
            }

            lastQuestion = question;
          }
        }
      }
    };

    class AddCardListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
        if (m_noMistakes) {
          String question = m_questionArea.getText().trim();
          String answer = m_answerArea.getText().trim();

          Card newCard = new Card(question, answer);
          CardState newCardState = new CardState(newCard);
          m_choosed.add(newCardState);

          m_questionArea.setText("");
          m_answerArea.setText("");

          updateStatePanel(m_choosed);
          updateQuestionList();
        }
      }
    };
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
              if ((testName.length() > 30) || (m_nameList.contains(testName))) {
                m_kitNameTextField.setBackground(new Color(255, 80, 80));
                nameTrue = false;
              } else if ((!nameTrue) || (testName.length() == 0)) {
                if ((testName.length() == 0)) {
                  nameTrue = false;
                } else {
                  nameTrue = true;
                }
                m_kitNameTextField.setBackground(new Color(255, 255, 255));
              }

              nameIsChanged = false;
            }

            if (descrIsChanged) {
              lastDescr = testDescr;
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
      m_checker.stop();
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
