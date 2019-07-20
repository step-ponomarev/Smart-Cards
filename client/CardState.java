import java.io.*;
import java.util.Date;

class CardState implements Serializable {
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

  public String getAnswer() {
    return m_card.getAnswer();
  }

  public Date getCheckedDate() {
    return m_checkedDate;
  }

  public Date getUnlocksDate() {
    return m_checkDate;
  }

  public void setQuestion(String question) {
    m_card.setQuestion(question);
  }

  public void setAnawer(String answer) {
    m_card.setAnawer(answer);
  }
};
