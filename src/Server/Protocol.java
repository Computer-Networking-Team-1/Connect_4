package Server;

import java.io.Serializable;

/** 서버와 클라이언트 사이에 주고받는 메시지  */
@SuppressWarnings("serial")
public class Protocol implements Serializable {
   
   /**
    * type
    *    1. log in
    *    2. sign in
    *    3. sign up (회원가입)		type, from
    *    4. chat (채팅)			type, from, to, content
    *    5. challenge (도전)		type, from, to
    *    6. information (정보)	type, from, to
    *    7. invite
    *    8. invited
    *    9. notice
    *    10. ready
    *    11. play
    *    12. result
    *    
    */
   private int type;
   private String from;   // sender
   private String to;      // receiver
   private String content;

   /**
    * 
    * @param type   유형
    */
   public Protocol(int type) {
      this.type = type;
      this.from = null;
      this.to = null;
      this.content = null;
   }

   /**
    * @param type   유형
    * @param from   보내는 쪽 (sender)
    */
   public Protocol(int type, String from) {
      this.type = type;
      this.from = from;
      this.to = null;
      this.content = null;
   }
   
   /**
    * @param type   유형
    * @param from   보내는 쪽 (sender)
    * @param to   받는 쪽 (receiver)
    */
   public Protocol(int type, String from, String to) {
      this.type = type;
      this.from = from;
      this.to = to;
      this.content = null;
   }
   
   /**
    * @param type      유형
    * @param from      보내는 쪽 (sender)
    * @param to      받는 쪽 (receiver)
    * @param content   내용
    */
   public Protocol(int type, String from, String to, String content) {
      this.type = type;
      this.from = from;
      this.to = to;
      this.content = content;
   }
   
   public void setType(int type) {
      this.type = type;
   }
   
   public void setFrom(String from) {
      this.from = from;
   }
   
   public void setTo(String to) {
      this.to = to;
   }
   
   public void setContent(String content) {
      this.content = content;
   }
   
   public int getType() {
      return type;
   }
   
   public String getFrom() {
      return from;
   }
   
   public String getTo() {
      return to;
   }
   
   public String getContent() {
      return content;
   }
}
