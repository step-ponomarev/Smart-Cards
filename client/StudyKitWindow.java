import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Calendar;
//import javax.swing.border.*;

public class StudyKitWindow extends WindowAdapter {
  private JFrame m_window;
  private JTextArea m_questionArea;
  private JTextArea m_answerArea;
  private JButton openButton;
  private JPanel m_buttonsPanel;

  private JFrame m_parent;
  private CardCase m_thisCase;
  private CardState m_thisCard;
  private Thread m_synchThread;

  private ArrayList<CardState> m_studyingCards;

  public StudyKitWindow(JFrame parent, final CardCase aCase, final Thread synchThread) {
    m_parent = parent;
    m_thisCase = aCase;
    m_synchThread = synchThread;

    m_thisCase.updateCardSets();

    m_studyingCards = m_thisCase.getAvailable();
    m_thisCard = m_studyingCards.get(0);
  }

  public void upDateStudyList() {
    if (m_studyingCards.size() > 0) {
      m_thisCard = m_studyingCards.get(0);

      ArrayList<CardState> updatedList = new ArrayList<CardState>();
      for (int i = 1; i < m_studyingCards.size(); ++i) {
        updatedList.add(m_studyingCards.get(i));
      }

      m_studyingCards = updatedList;
    }
  }

  public void go() {
    m_parent.setEnabled(false);

    Font buttonFont = new Font("Bree", Font.BOLD, 18);
    Font labelFont = new Font("Bree", Font.BOLD, 20);
    Font fieldFont = new Font("Bree", Font.BOLD, 14);

    m_window = new JFrame("Study Cards");
    m_window.setLayout(new BorderLayout());
    m_window.addWindowListener(this);
    m_window.setSize(500, 350);
    HelpMethods.frameOnCenter(m_window);
    m_window.setBackground(Color.white);
    m_window.setResizable(false);

    JPanel textEditPantl = new JPanel();
    textEditPantl.setLayout(new BoxLayout(textEditPantl, BoxLayout.Y_AXIS));
    textEditPantl.setBackground(Color.white);

    upDateStudyList();

    m_questionArea = new JTextArea();
    m_questionArea.setFont(fieldFont);
    m_questionArea.setLineWrap(true);
    m_questionArea.setText(m_thisCard.getQuestion());
    m_questionArea.setEditable(false);

    m_answerArea = new JTextArea();
    m_answerArea.setFont(fieldFont);
    m_answerArea.setLineWrap(true);
    m_answerArea.setText("");
    m_answerArea.setEditable(false);

    JPanel chooseCasePanel = new JPanel();
    chooseCasePanel.setBackground(Color.white);

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

    m_buttonsPanel = new JPanel();
    m_buttonsPanel.setBackground(Color.white);

    openButton = new JButton("Open");
    openButton.setBackground(Color.white);
    openButton.setFont(buttonFont);
    openButton.addActionListener(new OpenBackSiteListener());

    m_buttonsPanel.add(openButton);

    m_window.getContentPane().add(BorderLayout.CENTER, textEditPantl);
    m_window.getContentPane().add(BorderLayout.SOUTH, m_buttonsPanel);

    m_window.setVisible(true);
  }

  public void finishStudying() {
    m_thisCase.updateCardSets();
    m_window.dispose();
    m_parent.setEnabled(true);
    m_thisCase.updateCardSets();
    m_synchThread.run();
  }

  public void windowClosing(WindowEvent event) {
    m_parent.setEnabled(true);
    m_thisCase.updateCardSets();
    m_synchThread.run();
  }

  class OpenBackSiteListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      Font buttonFont = new Font("Bree", Font.BOLD, 16);

      m_answerArea.setText(m_thisCard.getAnswer());

      JButton easyButton = new JButton("Easy");
      easyButton.addActionListener(new EasyListener());
      easyButton.setBackground(new Color(204, 255, 51));
      easyButton.setOpaque(true);
      easyButton.setFont(buttonFont);

      JButton hardButton = new JButton("Hard");
      hardButton.addActionListener(new HardListener());
      hardButton.setBackground(new Color(255, 204, 51));
      hardButton.setOpaque(true);
      hardButton.setFont(buttonFont);

      JButton mistakeButton = new JButton("Mistake");
      mistakeButton.addActionListener(new MistakeListener());
      mistakeButton.setBackground(new Color(255, 102, 51));
      mistakeButton.setOpaque(true);
      mistakeButton.setFont(buttonFont);

      m_buttonsPanel.add(easyButton);
      m_buttonsPanel.add(hardButton);
      m_buttonsPanel.add(mistakeButton);

      m_buttonsPanel.revalidate();
    }
  };

  class EasyListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if (m_studyingCards.size() != 0) {
        Calendar toDay = Calendar.getInstance();

        int difference = m_thisCard.getTineDifference();
        Calendar newDate = Calendar.getInstance();
        if (difference == 0) {
          newDate.add(5, 1);
        } else {
          newDate.add(5, difference * 2);
        }
        m_thisCard.setUnlocksDate(newDate);
        m_thisCard.setCheckedDate(toDay);

        upDateStudyList();

        m_questionArea.setText(m_thisCard.getQuestion());
        m_answerArea.setText("");

        m_buttonsPanel.removeAll();
        m_buttonsPanel.repaint();
        m_buttonsPanel.add(openButton);

        m_buttonsPanel.revalidate();
      } else {
        Calendar toDay = Calendar.getInstance();
        m_thisCard.setCheckedDate(toDay);

        Calendar newDate = Calendar.getInstance();
        newDate.add(5, 2);
        m_thisCard.setUnlocksDate(newDate);

        finishStudying();
      }
    }
  };

  class HardListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if (m_studyingCards.size() != 0) {
        Calendar toDay = Calendar.getInstance();

        int difference = m_thisCard.getTineDifference();
        Calendar newDate = Calendar.getInstance();
        if (difference == 0) {
          newDate.add(5, 1);
        } else {
          newDate.add(5, difference + 1);
        }
        m_thisCard.setUnlocksDate(newDate);
        m_thisCard.setCheckedDate(toDay);

        upDateStudyList();

        m_questionArea.setText(m_thisCard.getQuestion());
        m_answerArea.setText("");

        m_buttonsPanel.removeAll();
        m_buttonsPanel.repaint();
        m_buttonsPanel.add(openButton);

        m_buttonsPanel.revalidate();
      } else {
        Calendar toDay = Calendar.getInstance();
        int difference = m_thisCard.getTineDifference();
        Calendar newDate = Calendar.getInstance();
        if (difference == 0) {
          newDate.add(5, 1);
        } else {
          newDate.add(5, difference + 1);
        }
        m_thisCard.setUnlocksDate(newDate);
        m_thisCard.setCheckedDate(toDay);

        finishStudying();
      }
    }
  };

  class MistakeListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      Calendar toDay = Calendar.getInstance();
      m_thisCard.setCheckedDate(toDay);
      m_thisCard.setUnlocksDate(toDay);

      //CardState mistake = m_thisCard;
      m_studyingCards.add(m_thisCard);
      upDateStudyList();

      m_questionArea.setText(m_thisCard.getQuestion());
      m_answerArea.setText("");

      m_buttonsPanel.removeAll();
      m_buttonsPanel.repaint();
      m_buttonsPanel.add(openButton);

      m_buttonsPanel.revalidate();
    }
  }
};
