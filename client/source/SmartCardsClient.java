import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class SmartCardsClient extends WindowAdapter {
  private JFrame m_main;
  private boolean m_connection = false;
  private JTextArea m_stateLabel;
  private JList<String> m_casesList;
  private ArrayList<CardCase> m_cases;

  private File m_path;

  private String m_guestName = "Guest";
  private String m_userName;
  private String m_userPassword;

  private Thread m_synchronizator;

  public SmartCardsClient() {
    m_synchronizator = new Thread(new SmartCardsSychronizator());
    m_cases = new ArrayList<CardCase>(0);
    setUpAccount();
    setUpFileSystem();
  }

  public void go() {
    m_main = new JFrame("Smart Cards");
    m_main.setEnabled(true);
    m_main.setSize(550, 300);
    m_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    m_main.addWindowListener(this);

    setUpList();
    setButtons();
    setMenuBar();

    HelpMethods.frameOnCenter(m_main);
    m_main.setResizable(false);
    m_main.setVisible(true);
  }

  private void setUpAccount() {
    m_userName = m_guestName;
    m_userPassword = "";
  }

  private void setUpFileSystem() {
    m_path = new File("local.ser");
    try {
      if (!m_path.exists()) {
        m_path.createNewFile();

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(m_path));

        out.writeObject(new Date());
        out.writeObject(m_guestName);
        out.writeObject(m_userPassword);
        out.writeObject(m_cases);

        out.close();
      } else {
        ObjectInputStream instream = new ObjectInputStream(new FileInputStream(m_path));

        Date uploadedVersion = (Date) instream.readObject();
        m_userName = (String) instream.readObject();
        m_userPassword = (String) instream.readObject();
        m_cases = (ArrayList<CardCase>) instream.readObject();

        instream.close();
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void synch() {
    try {
        ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(m_path));
        Date dateOfSynch = new Date();

        outstream.writeObject(dateOfSynch);
        outstream.writeObject(m_userName);
        outstream.writeObject(m_userPassword);
        outstream.writeObject(m_cases);

        outstream.close();
        System.out.println("Синхронезированно офлайн");

        if (!m_userName.equals(m_guestName)) {
          int code = -1;
          try {
            NetWorker netWorker = new NetWorker();
            code = netWorker.synch(m_path, m_userName);
          } catch(IOException e) {
            NoConnectionWindow window = new NoConnectionWindow();
            window.go();
          }


          if (code == 0) {
            System.out.println("Синхронезированно онлайн");
          } else {
            System.out.println("Ошибка онлайн синхронизации");
          }
        }

    } catch(IOException ex) {
      ex.printStackTrace();
    }
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

    JPanel buttonBox = new JPanel();
    Border border = BorderFactory.createLineBorder(new Color(230, 230, 230), 5);
    buttonBox.setBackground(Color.white);
    buttonBox.setBorder(border);
    JButton editButton = new JButton("Edit");
    editButton.addActionListener(new EditKitListener());
    JButton studyButton = new JButton("Study");
    studyButton.addActionListener(new KitStudyListener());

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
    updateStatePanel();
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

  private void setMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu smartCards = new JMenu("Smart Cards");
    JMenuItem settings = new JMenuItem("Settings");

    settings.addActionListener(new SettingsListener());

    smartCards.add(settings);
    menuBar.add(smartCards);

    m_main.setJMenuBar(menuBar);
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

  private CardCase findCardCase(String name) {
    CardCase newCase = null;
    for (int i = 0 ; i < m_cases.size(); ++i) {
      if (m_cases.get(i).getName().equals(name)) {
        newCase = m_cases.get(i);
      }
    }

    return newCase;
  }

  private void removeCardCase(String name) {
    ArrayList<CardCase> newCases = new ArrayList<CardCase>();
    CardCase removedCardCase = findCardCase(name);
    for (int i = 0; i < m_cases.size(); ++i) {
      if (!m_cases.get(i).getName().equals(removedCardCase.getName())) {
        newCases.add(m_cases.get(i));
      }
    }

    m_cases = newCases;
  }

  public void windowClosing(WindowEvent e) {
    synch();
  }

  class SmartCardsSychronizator implements Runnable {
    public void run() {
      updateList();
      synch();
    }
  };

  class SettingsListener implements ActionListener {
    OptionsWindow m_window;

    public void actionPerformed(ActionEvent event) {
      String [] userData = {m_userName, m_userPassword};
      Thread dataChanger = new Thread(new UserDataChanger(userData));

      try {
        if (!m_userName.equals(m_guestName)) {
          m_window = new UserWindow(m_main, userData, dataChanger);
          m_window.go();
        } else {
          m_window = new LoginWindow(m_main, userData, dataChanger);
          m_window.go();
        }
      } catch(IOException e) {
        System.out.println("No Connection");
        NoConnectionWindow window = new NoConnectionWindow();
        window.go();
      }
    }

    class UserDataChanger implements Runnable {
      private String [] m_UserData;

      public UserDataChanger(final String [] data) {
        m_UserData = data;
      }

      public void run () {
        m_userName = m_UserData[0];
        m_userPassword = m_UserData[1];
      }
    };

  };

  class KitStudyListener implements ActionListener {
    private StudyKitWindow m_studyWindow;

    public void actionPerformed(ActionEvent event) {
      if (m_casesList.getSelectedValue() != null) {
        CardCase thisCase = findCardCase(m_casesList.getSelectedValue());
        if ((thisCase.getSize() > 0) && (thisCase.getAvailable().size() > 0)) {
          m_studyWindow = new StudyKitWindow(m_main, thisCase, m_synchronizator);
          m_studyWindow.go();
        }
      }

    }
  };

  class EditKitListener implements ActionListener {
    private KitSettingsWindow m_window;

    public void actionPerformed(ActionEvent event) {
      if ((m_cases.size() > 0) && (m_casesList.getSelectedValue() != null)) {
        String name = (String) m_casesList.getSelectedValue();
        CardCase thisCase = findCardCase(name);

        m_window = new KitSettingsWindow(thisCase);
        m_window.go();
        m_main.setEnabled(false);
      }
    }
  };

  class KitSettingsWindow extends WindowAdapter {
    private JFrame m_editWindow;
    private JLabel m_sizeLabel;
    private JTextField m_nameKit;
    private JTextArea m_descriptionKit;
    private CardCase m_thisCase;
    private ArrayList<String> m_nameList;
    private Thread m_checker;
    private boolean m_noMistakes = false;

    public KitSettingsWindow(final CardCase theCase) {
      m_editWindow = new JFrame("Kit Settings");
      m_nameKit = new JTextField();
      m_descriptionKit = new JTextArea();
      m_thisCase = theCase;
      m_checker = new Thread(new MyKitEditChecker());

      m_nameList = new ArrayList<String>();
      for (int k = 0; k < m_cases.size(); ++k) {
        m_nameList.add(m_cases.get(k).getName());
      }
    }

    public void go() {
      Font buttonFont = new Font("Bree", Font.BOLD, 16);
      Font listFont = new Font("Bree", Font.BOLD, 20);
      Font labelFont = new Font("Bree", Font.BOLD, 18);
      Font textFont = new Font("Bree", Font.BOLD, 14);

      m_editWindow.setSize(400, 300);
      m_editWindow.addWindowListener(this);
      m_editWindow.setBackground(Color.white);

      JPanel nameLabelPane = new JPanel();
      nameLabelPane.setBackground(Color.white);
      JLabel nameLabel = new JLabel("Kit Settings");
      nameLabel.setFont(labelFont);
      nameLabelPane.add(nameLabel);

      JPanel settingPanel = new JPanel();
      settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));
      settingPanel.setBackground(Color.white);

      JPanel globalNamePanel = new JPanel();
      globalNamePanel.setBackground(Color.white);
      globalNamePanel.setLayout(new BoxLayout(globalNamePanel, BoxLayout.Y_AXIS));

      JPanel namePanel =  new JPanel();
      namePanel.setBackground(Color.white);
      JLabel nameKitLabel = new JLabel("Name: ");
      nameKitLabel.setFont(buttonFont);
      m_nameKit.setPreferredSize(new Dimension(300, 20));
      m_nameKit.setText(m_thisCase.getName());
      m_nameKit.setFont(textFont);

      namePanel.add(nameKitLabel);
      namePanel.add(m_nameKit);

      globalNamePanel.add(nameLabelPane);
      globalNamePanel.add(namePanel);

      JPanel sizePanel = new JPanel();
      sizePanel.setBackground(Color.white);
      sizePanel.setLayout(new BorderLayout());
      JPanel labelPanel = new JPanel();
      labelPanel.setBackground(Color.white);
      JButton checkCardsButton = new JButton("Cards settings");
      checkCardsButton.setFont(buttonFont);
      checkCardsButton.addActionListener(new CardSettingsListener());
      m_sizeLabel = new JLabel();
      m_sizeLabel.setFont(buttonFont);
      m_sizeLabel.setText("Size: " + Integer.toString(m_thisCase.getSize()) + "                             ");
      labelPanel.add(m_sizeLabel);
      labelPanel.add(checkCardsButton);

      sizePanel.add(labelPanel);

      JPanel descrPanel = new JPanel();
      descrPanel.setLayout(new BorderLayout());
      descrPanel.setBackground(Color.white);
      JLabel descrLabel = new JLabel("Description:");
      descrLabel.setFont(buttonFont);
      m_descriptionKit.setText(m_thisCase.getDescription());
      m_descriptionKit.setFont(textFont);
      m_descriptionKit.setLineWrap(true);
      JScrollPane descrScroll = new JScrollPane(m_descriptionKit);
      descrScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      descrScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      descrPanel.add(BorderLayout.NORTH, descrLabel);
      descrPanel.add(BorderLayout.CENTER, descrScroll);

      JPanel buttonPanel = new JPanel();
      buttonPanel.setBackground(Color.white);
      buttonPanel.setLayout(new BorderLayout());
      JButton saveButton = new JButton("Save");
      saveButton.setFont(buttonFont);
      saveButton.addActionListener(new SaveKitListener());
      JButton deleteButton = new JButton("Delete Kit");
      deleteButton.setFont(buttonFont);
      deleteButton.addActionListener(new DeleteKitListener());

      buttonPanel.add(BorderLayout.NORTH, deleteButton);
      buttonPanel.add(BorderLayout.SOUTH, saveButton);

      settingPanel.add(globalNamePanel);
      settingPanel.add(sizePanel);
      settingPanel.add(descrPanel);

      HelpMethods.frameOnCenter(m_editWindow);

      m_editWindow.setResizable(false);
      m_editWindow.setVisible(true);

      m_editWindow.add(BorderLayout.NORTH, nameLabelPane);
      m_editWindow.add(BorderLayout.CENTER, settingPanel);
      m_editWindow.add(BorderLayout.SOUTH, buttonPanel);

      m_checker.start();
    }

    public void windowClosing(WindowEvent event) {
      m_main.setEnabled(true);
      m_checker.stop();

      m_casesList.clearSelection();
      updateStatePanel();
    }

    class SaveKitListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
        if (m_noMistakes) {
          String name = m_nameKit.getText();
          if (name != null) {
            name = name.trim();
            m_thisCase.setName(name);
          }

          String descr = m_descriptionKit.getText();
          if (descr != null) {
            descr = descr.trim();
            m_thisCase.setDescription(descr);
          }
          m_checker.stop();

          m_synchronizator.run();

          m_main.setEnabled(true);
          m_editWindow.dispose();
        }
      }
    };

    class DeleteKitListener extends WindowAdapter implements ActionListener {
      private JFrame m_acceptenceDia;
      private JFrame owner;

      public void actionPerformed(ActionEvent event) {
        m_editWindow.setEnabled(false);

        Font buttonFont = new Font("Bree", Font.BOLD, 16);
        Font listFont = new Font("Bree", Font.BOLD, 20);
        Font labelFont = new Font("Bree", Font.BOLD, 18);
        Font textFont = new Font("Bree", Font.BOLD, 14);

        m_acceptenceDia = new JFrame("Confirm deletion");
        m_acceptenceDia.setSize(372, 150);

        HelpMethods.frameOnCenter(m_acceptenceDia);
        m_acceptenceDia.setResizable(false);

        JPanel panelWarn = new JPanel();
        panelWarn.setBackground(Color.white);
        JLabel atempLabel = new JLabel("Warning!");
        atempLabel.setFont(labelFont);
        panelWarn.add(atempLabel);

        JPanel panelText1 = new JPanel();
        panelText1.setLayout(new BoxLayout(panelText1, BoxLayout.Y_AXIS));
        panelText1.setBackground(Color.white);

        JLabel textLabel1 = new JLabel("  Kit " + m_thisCase.getName());
        textLabel1.setFont(buttonFont);
        JLabel textLabel2 = new JLabel("  Will be deleted forever!");
        textLabel2.setFont(buttonFont);

        panelText1.add(textLabel1);
        panelText1.add(textLabel2);

        JPanel buttomPanel = new JPanel();
        buttomPanel.setBackground(Color.white);
        buttomPanel.setLayout(new BorderLayout());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelKitDeletionListener());
        cancelButton.setFont(buttonFont);

        JButton acceptButton = new JButton("Ok");
        acceptButton.addActionListener(new OkKitDeletionListener());
        acceptButton.setFont(buttonFont);

        buttomPanel.add(BorderLayout.NORTH, cancelButton);
        buttomPanel.add(BorderLayout.SOUTH, acceptButton);

        m_acceptenceDia.add(BorderLayout.NORTH, panelWarn);
        m_acceptenceDia.add(BorderLayout.CENTER, panelText1);
        m_acceptenceDia.add(BorderLayout.SOUTH, buttomPanel);
        m_acceptenceDia.setVisible(true);
        m_acceptenceDia.setAlwaysOnTop(true);
      }

      public void windowClosing(WindowEvent ev) {
        m_editWindow.setEnabled(true);
      }

      class CancelKitDeletionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
          m_acceptenceDia.dispose();
          m_editWindow.setEnabled(true);
        }
      };

      class OkKitDeletionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
          removeCardCase(m_thisCase.getName());
          m_acceptenceDia.dispose();
          m_main.setEnabled(true);
          m_editWindow.dispose();

          m_checker.stop();
          m_synchronizator.run();
        }
      };
    };

    class MyKitEditChecker implements Runnable {
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
          testName = m_nameKit.getText();
          testDescr = m_descriptionKit.getText();

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
              if (((testName.length() > 30) || (m_nameList.contains(testName)) && !testName.equals(m_thisCase.getName()))) {
                m_nameKit.setBackground(new Color(255, 80, 80));
                nameTrue = false;
              } else if ((!nameTrue) || (testName.length() == 0)) {
                if ((testName.length() == 0)) {
                  nameTrue = false;
                } else {
                  nameTrue = true;
                }
                m_nameKit.setBackground(new Color(255, 255, 255));
              }

              nameIsChanged = false;
            }

            if (descrIsChanged) {
              lastDescr = testDescr;
              if ((testDescr.length() > 120)) {
                if (descrTrue) {
                  m_descriptionKit.setBackground(new Color(255, 80, 80));
                  descrTrue = false;
                }
              } else if (!descrTrue) {
                m_descriptionKit.setBackground(new Color(255, 255, 255));
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

    class CardSettingsListener implements ActionListener {
      private CardSettingsWindow m_checkCrardWindow;

      public void actionPerformed(ActionEvent eve) {
        if (m_thisCase.getSize() > 0) {
          m_editWindow.setEnabled(false);
          m_checkCrardWindow = new CardSettingsWindow();
          m_checkCrardWindow.go();
        } else {
          // window no cards
        }
      }
    };

    class CardSettingsWindow extends WindowAdapter {
      private JFrame m_cardsWindow;
      private JTextArea m_cardState;
      private JList<String> m_cardsList;
      private String m_selectedElement;
      private String [] m_cardNames;
      private ArrayList<String> m_removedCards;

      public CardSettingsWindow() {
        m_cardsWindow = new JFrame("Cards in " + m_thisCase.getName());
        m_cardsWindow.addWindowListener(this);

        m_cardNames = m_thisCase.getCardList();
        m_cardsList = new JList<String>(m_cardNames);
        m_removedCards = new ArrayList<String>();
      }

      public void go() {
        Font buttonFont = new Font("Bree", Font.BOLD, 16);
        Font listFont = new Font("Bree", Font.BOLD, 20);
        Font labelFont = new Font("Bree", Font.BOLD, 18);
        Font textFont = new Font("Bree", Font.BOLD, 14);

        m_cardsWindow.setBackground(Color.white);
        m_cardsWindow.setSize(400, 300);
        HelpMethods.frameOnCenter(m_cardsWindow);
        JPanel namePanel = new JPanel();
        namePanel.setBackground(Color.white);
        JLabel nameLabe = new JLabel(m_thisCase.getName());
        nameLabe.setFont(labelFont);
        namePanel.add(nameLabe);

        JScrollPane scrollList = HelpMethods.createScrollPane(m_cardsList);
        m_cardsList.setSelectionMode​(ListSelectionModel.SINGLE_SELECTION);
        m_cardsList.addListSelectionListener(new ListSelectedCardListener());
        m_cardsList.setFont(listFont);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBackground(Color.white);

        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BorderLayout());
        statePanel.setBackground(Color.white);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.white);

        JButton editButton = new JButton("Edit");
        editButton.setFont(buttonFont);
        editButton.addActionListener(new EditCardListener());

        JButton deleteButton = new JButton("Remove");
        deleteButton.setFont(buttonFont);
        deleteButton.addActionListener(new RemoveCardListener());

        m_cardState = new JTextArea();
        m_cardState.setEditable(false);
        m_cardState.setFont(textFont);
        m_cardState.setText("  Total cards: " + m_cardNames.length + "  ");

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        statePanel.add(BorderLayout.NORTH, buttonPanel);
        statePanel.add(BorderLayout.CENTER, m_cardState);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.white);
        buttonsPanel.setLayout(new BorderLayout());

        JButton saveButton = new JButton("Save");
        saveButton.setFont(buttonFont);
        saveButton.addActionListener(new SaveCardsSettingsListener());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(buttonFont);
        cancelButton.addActionListener(new CancelCardsSettingsListener());

        buttonsPanel.add(BorderLayout.NORTH, cancelButton);
        buttonsPanel.add(BorderLayout.SOUTH, saveButton);

        m_cardsWindow.add(BorderLayout.NORTH, namePanel);
        m_cardsWindow.add(BorderLayout.EAST, statePanel);
        m_cardsWindow.add(BorderLayout.CENTER, scrollList);
        m_cardsWindow.add(BorderLayout.SOUTH, buttonsPanel);
        m_cardsWindow.setResizable(false);
        m_cardsWindow.setVisible(true);
      }

      public void windowClosing(WindowEvent event) {
        m_editWindow.setEnabled(true);
      }

      public void updateCardList() {
        m_cardNames = m_thisCase.getCardList();

        m_cardsList.setListData(m_cardNames);
        m_cardsList.revalidate();
        m_cardsList.repaint();
      }

      class RemoveCardListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
          if (m_selectedElement != null) {
            String [] newNameList = new String[m_cardNames.length - 1];
            int j = 0;
            for (int i = 0; i < m_cardNames.length; ++i) {
              if (!m_cardNames[i].equals(m_selectedElement)) {
                newNameList[j] = m_cardNames[i];
                ++j;
              }
            }
            m_cardNames = newNameList;

            m_removedCards.add(m_selectedElement);
            m_selectedElement = null;

            m_cardState.setText("  Total cards: " + m_cardNames.length + "  ");

            m_cardsList.setListData(m_cardNames);
            m_cardsList.revalidate();
            m_cardsList.repaint();
          }
        }
      };

      class SaveCardsSettingsListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
          m_thisCase.removeCards(m_removedCards);
          m_synchronizator.run();
          m_sizeLabel.setText("Size: " + Integer.toString(m_thisCase.getSize()) + "                             ");

          m_cardsWindow.dispose();
          m_editWindow.setEnabled(true);
        }
      };

      class CancelCardsSettingsListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
          m_cardsWindow.dispose();
          m_editWindow.setEnabled(true);
        }
      };

      class ListSelectedCardListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
          if (!lse.getValueIsAdjusting()) {
            if (m_cardsList.getSelectedValue() != null) {
              m_selectedElement = (String) m_cardsList.getSelectedValue();

              m_cardState.setText(" Checked: " + m_thisCase.getChecked(m_selectedElement) + " \n" + "\n");
              m_cardState.append(" Unlocks: " +  m_thisCase.getUnlocks(m_selectedElement) + " \n");
            }
          }
        }
      };

      class EditCardListener implements ActionListener {
        private CardWindow m_cardsWindow;

        public void actionPerformed(ActionEvent event) {
          m_cardsWindow = new CardWindow();
          m_cardsWindow.setUp(m_thisCase, m_selectedElement);
          m_cardsWindow.go();
        }
      };

      class CardWindow extends WindowAdapter {
        private JFrame m_editCardFrame;
        private JTextArea m_questionArea;
        private JTextArea m_answerArea;
        private CardState m_thisCard;
        private CardCase m_thisCase;
        private String m_startQuestion;
        private ArrayList<String> m_questionList;
        private boolean m_noEditMistakes = false;

        public void setUp(final CardCase theCase, final String question) {
          m_thisCase = theCase;

          m_startQuestion = question;
          m_thisCard = m_thisCase.getCard(m_startQuestion);

          int size = m_thisCase.getSize();
          ArrayList<CardState> cards = m_thisCase.getCards();
          m_questionList = new ArrayList<String>();
          for (int i = 0; i < cards.size(); ++i) {
            m_questionList.add(cards.get(i).getQuestion());
          }
        }

        public void go() {
          m_editWindow.setEnabled(false);

          Font buttonFont = new Font("Bree", Font.BOLD, 18);
          Font labelFont = new Font("Bree", Font.BOLD, 20);
          Font fieldFont = new Font("Bree", Font.BOLD, 14);

          m_editCardFrame = new JFrame("Card settings");
          m_editCardFrame.setLayout(new BorderLayout());
          m_editCardFrame.addWindowListener(this);
          m_editCardFrame.setSize(500, 350);
          HelpMethods.frameOnCenter(m_editCardFrame);
          m_editCardFrame.setBackground(Color.white);
          m_editCardFrame.setResizable(false);

          JPanel textEditPantl = new JPanel();
          textEditPantl.setLayout(new BoxLayout(textEditPantl, BoxLayout.Y_AXIS));
          textEditPantl.setBackground(Color.white);

          m_questionArea = new JTextArea();
          m_questionArea.setFont(fieldFont);
          m_questionArea.setLineWrap(true);
          m_questionArea.setText(m_thisCard.getQuestion());

          m_answerArea = new JTextArea();
          m_answerArea.setFont(fieldFont);
          m_answerArea.setLineWrap(true);
          m_answerArea.setText(m_thisCard.getAnswer());

          JPanel chooseCasePanel = new JPanel();
          chooseCasePanel.setBackground(Color.white);

          JLabel chooseCaseLabel = new JLabel("Card settings");
          chooseCaseLabel.setFont(labelFont);
          chooseCasePanel.add(chooseCaseLabel);

          JScrollPane questionScroller = HelpMethods.createScrollPane(m_questionArea);
          JScrollPane answerScroller = HelpMethods.createScrollPane(m_answerArea);

          JLabel qestuibLabel = new JLabel("Front card site:");
          qestuibLabel.setFont(labelFont);
          qestuibLabel.setBackground(Color.white);
          textEditPantl.add(qestuibLabel);
          textEditPantl.add(questionScroller);

          JLabel answerLabel= new JLabel("Back card site:");
          answerLabel.setFont(labelFont);
          answerLabel.setBackground(Color.white);
          textEditPantl.add(answerLabel);
          textEditPantl.add(answerScroller);

          JButton addButton = new JButton("Save");
          addButton.setBackground(Color.white);
          addButton.setFont(buttonFont);
          addButton.addActionListener(new SaveCardChangesListener());

          m_editCardFrame.getContentPane().add(BorderLayout.NORTH, chooseCasePanel);
          m_editCardFrame.getContentPane().add(BorderLayout.CENTER, textEditPantl);
          m_editCardFrame.getContentPane().add(BorderLayout.SOUTH, addButton);

          m_editCardFrame.setVisible(true);
        }

        public void windowClosing(WindowEvent event) {
          m_editWindow.setEnabled(true);
        }

        class SaveCardChangesListener implements ActionListener {
          public void actionPerformed(ActionEvent event) {
            String question = m_questionArea.getText();
            String answer = m_answerArea.getText();

            if (((m_questionList.contains(question)) || (question.length() == 0)) && (!question.equals(m_startQuestion))) {
              m_noEditMistakes = false;
            } else {
              m_noEditMistakes = true;
            }

            if (m_noEditMistakes) {
              m_thisCard.setQuestion(question);
              m_thisCard.setAnawer(answer);
              updateCardList();
              m_editCardFrame.dispose();
              m_editWindow.setEnabled(true);
            }
          }
        };
      };
    };
  };

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

  class NewCardListener implements ActionListener {
    private NewCardWindow m_newCardWindow;

    public void actionPerformed(ActionEvent event) {

      if (m_cases.size() > 0) {
        m_newCardWindow = new NewCardWindow();
        m_newCardWindow.setUp();
        m_newCardWindow.go();
        m_newCardWindow.updateQuestionList();

        m_main.setEnabled(false);
      } else {
        //noKitsWindow();
      }
    }
  };

  class NewCardWindow extends WindowAdapter {
    private JTextArea m_questionArea;
    private JTextArea m_answerArea;
    private JComboBox m_caseBox;
    private Vector<String> m_nameCases;
    private CardCase m_choosed;

    private Thread m_checker;
    private boolean m_noMistakes;
    private ArrayList<String> m_questionList;

    public void setUp() {
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

      m_checker = new Thread(new MyFieldChecer());
    }

    public void updateQuestionList() {
      ArrayList<CardState> CardsInCase = m_choosed.getCards();
      m_questionList = new ArrayList<String>();
      for (int i = 0; i < CardsInCase.size(); ++i) {
        m_questionList.add(CardsInCase.get(i).getQuestion());
      }
    }

    public void go() {
      Font buttonFont = new Font("Bree", Font.BOLD, 18);
      Font labelFont = new Font("Bree", Font.BOLD, 20);
      Font fieldFont = new Font("Bree", Font.BOLD, 14);

      JFrame newCardFrame = new JFrame("Add Card");
      newCardFrame.addWindowListener(this);
      newCardFrame.setSize(500, 350);
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

      JScrollPane questionScroller = HelpMethods.createScrollPane(m_questionArea);
      JScrollPane answerScroller = HelpMethods.createScrollPane(m_answerArea);

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

      HelpMethods.frameOnCenter(newCardFrame);
      newCardFrame.setVisible(true);

      newCardFrame.getContentPane().add(BorderLayout.NORTH, chooseCasePanel);
      newCardFrame.getContentPane().add(BorderLayout.CENTER, textPantl);
      newCardFrame.getContentPane().add(BorderLayout.SOUTH, addButton);

      m_checker.start();
    }

    public void windowClosing(WindowEvent event) {
      m_casesList.clearSelection();
      updateStatePanel();

      m_synchronizator.run();
      m_main.setEnabled(true);
      m_checker.stop();
    }

    class ChoosedCaseListener implements ActionListener {
      public void actionPerformed(ActionEvent event) {
        if (m_caseBox.getSelectedItem() != null) {
          String name = (String) m_caseBox.getSelectedItem();
          m_choosed = findCardCase(name);
          updateQuestionList();

          m_checker.stop();
          m_checker = new Thread(new MyFieldChecer());
          m_checker.start();
        }
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

          m_choosed.updateCardSets();

          updateStatePanel(m_choosed);
          updateQuestionList();
          m_synchronizator.run();
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
      m_checker = new Thread(new MyFieldChecker());

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

      m_newKitFrame.setSize(400, 300);
      HelpMethods.frameOnCenter(m_newKitFrame);
      m_newKitFrame.setVisible(true);

      m_checker.start();
    }

    class MyFieldChecker implements Runnable {
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
          m_synchronizator.run();
        }
      }
    };
  };

  public static void main(String [] args) {
    SmartCardsClient gui = new SmartCardsClient();
    gui.go();
  }
};
