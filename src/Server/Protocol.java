package Server;

import java.io.Serializable;

/** ������ Ŭ���̾�Ʈ ���̿� �ְ�޴� �޽���  */
@SuppressWarnings("serial")
public class Protocol implements Serializable {
   
   /**
    * type
    *    1. login
    *    2. sign up (ȸ������)
    *    3. chat (ä��)
    *    4. challenge (����)
    *    5. information (����)
    *    6. invite
    *    7. invited
    *    8. notice
    *    9. ready
    *    10. play
    *    11. result
    */
   private int type;
   private String from;		// sender
   private String to;		// receiver
   private String content;
   private Player player;

   /**
    * 
    * @param type   ����
    */
   public Protocol(int type) {
      this.type = type;
      this.from = null;
      this.to = null;
      this.content = null;
      this.player = null;
   }

   /**
    * @param type   ����
    * @param from   ������ �� (sender)
    */
   public Protocol(int type, String from) {
      this.type = type;
      this.from = from;
      this.to = null;
      this.content = null;
      this.player = null;
   }
   
   /**
    * @param type   ����
    * @param from   ������ �� (sender)
    * @param to		�޴� �� (receiver)
    */
   public Protocol(int type, String from, String to) {
      this.type = type;
      this.from = from;
      this.to = to;
      this.content = null;
      this.player = null;
   }
   
   /**
    * @param type		����
    * @param from		������ �� (sender)
    * @param to			�޴� �� (receiver)
    * @param content	����
    */
   public Protocol(int type, String from, String to, String content) {
      this.type = type;
      this.from = from;
      this.to = to;
      this.content = content;
      this.player = null;
   }
   
   /**
    * @param type		����
    * @param player		�÷��̾� ��ü
    */
   public Protocol(int type, Player player) {
	   this.type = type;
	   this.from = null;
	   this.to = null;
	   this.content = null;
	   this.player = player;
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
   
   public void setPlayer(Player player) {
	   this.player = player;
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
   
   public Player getPlayer() {
	   return player;
   }
}
