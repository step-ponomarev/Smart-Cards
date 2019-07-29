import java.io.*;

public class Card implements Serializable {
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

  public String getAnswer() {
    return m_answer;
  }

  public Card getCard() {
    return new Card(m_question, m_answer);
  }

  public void setQuestion(String question) {
    m_question = question;
  }

  public void setAnawer(String answer) {
    m_answer = answer;
  }
};
