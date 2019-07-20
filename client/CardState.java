import java.io.*;
import java.util.Calendar;
import java.util.Date;

class CardState implements Serializable {
  private Card m_card;
  private Calendar m_checkedDate;
  private Calendar m_checkDate;

  public CardState(Card card) {
    if (card == null) {
      throw new NullPointerException("Null card");
    }

    m_card = card.getCard();
    m_checkedDate = Calendar.getInstance();
    m_checkDate = Calendar.getInstance();
  }

  public String getQuestion() {
    return m_card.getQuestion();
  }

  public String getAnswer() {
    return m_card.getAnswer();
  }

  public Date getCheckedDate() {
    return m_checkedDate.getTime();
  }

  public Date getUnlocksDate() {
    return m_checkDate.getTime();
  }

  public void setCheckedDate(Calendar date) {
    m_checkedDate = date;
  }

  public void setUnlocksDate(Calendar date) {
    m_checkDate = date;
  }

  public void setQuestion(String question) {
    m_card.setQuestion(question);
  }

  public void setAnawer(String answer) {
    m_card.setAnawer(answer);
  }
};
