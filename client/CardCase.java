import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class CardCase implements Serializable {
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

  public void setName(final String name) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }

    m_name = name;
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

  public String getDescription() {
    if (m_description == null) {
      return "";
    }

    return m_description;
  }

  public void removeCard(String question) {
    if (question == null) {
      throw new NullPointerException("Null name");
    }

    ArrayList<CardState> newCards = new ArrayList<CardState>();

    for(int i = 0; i < m_cards.size(); ++i) {
      if (!m_cards.get(i).getQuestion().equals(question)) {
        newCards.add(m_cards.get(i));
      }
    }

    m_cards = newCards;
  }

  public ArrayList<CardState> getCards() {
    return m_cards;
  }

  public String [] getCardList() {
    String [] cardList = new String[m_cards.size()];
    for (int i = 0; i < m_cards.size(); i++) {
      cardList[i] = m_cards.get(i).getQuestion();
    }
    Arrays.sort(cardList);

    return cardList;
  }

  public CardState getCard(String question) {
    for (int i = 0; i < m_cards.size(); ++i) {
      if (m_cards.get(i).getQuestion().equals(question)) {
        return m_cards.get(i);
      }
    }

    return null;
  }

  public void removeCards(ArrayList<String> questionList) {
    ArrayList<CardState> newCardList = new ArrayList<CardState>();
    for (int i = 0; i < m_cards.size(); ++i) {
      if (!questionList.contains(m_cards.get(i).getQuestion())) {
        newCardList.add(m_cards.get(i));
      }
    }

    m_cards = newCardList;
  }

  public String getChecked(final String card) {
    Date checkedDate = new Date();
    for (int i = 0; i < m_cards.size(); ++i) {
      if (m_cards.get(i).getQuestion().equals(card)) {
        checkedDate = m_cards.get(i).getCheckedDate();
      }
    }

    SimpleDateFormat format = new SimpleDateFormat("dd.MM");
    return format.format(checkedDate);
  }

  public String getUnlocks(final String card) {
    Date unlocksDate = new Date();
    for (int i = 0; i < m_cards.size(); ++i) {
      if (m_cards.get(i).getQuestion().equals(card)) {
        unlocksDate = m_cards.get(i).getUnlocksDate();
      }
    }

    SimpleDateFormat format = new SimpleDateFormat("dd.MM");
    return format.format(unlocksDate);
  }
};
