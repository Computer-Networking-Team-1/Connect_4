package Player;

import java.io.*;
import java.util.*;

public class Player implements Serializable {
	
	/* field */
	private String ID;			// ȸ�� ���̵�
	private String hashingPW;	// ȸ�� ��й�ȣ (��ȣȭ�� �� �ϵ��� �ܹ��� �ؽ� �Լ� ���� ��ȣȭ)
	private String salt;		// ��й�ȣ ��ȣȭ�� ���� salt
	private String name;		// ȸ�� �̸�
	private String nickname;	// ȸ�� �г���
	private String email;		// ȸ�� �̸���
	private String site;		// ȸ�� SNS �� Ȩ������ �ּ�
	
	private int loginCount;		// ���� Ƚ��
	private String lastLog;		// ������ ���� ���� (IP�ּ�, �ð�)
	
	private int countWin = 0;	// �̱� Ƚ�� (��)
	private int countDraw = 0;	// ��� Ƚ�� (���º�)
	private int countLose = 0;	// �� Ƚ�� (��)
	private int totalCount = 0;	// ��ü ���� Ƚ��
	
	private int status = 0; // 0: �α׾ƿ�, 1: �����, 2: ������

	/* initialize constructor */
	public Player() {
		this.ID = null;
		this.hashingPW = null;
		this.name = null;
		this.nickname = null;
		this.email = null;
		this.site = null;
		this.loginCount = 0;
		this.lastLog = null;
		this.countWin = 0;
		this.countDraw = 0;
		this.countLose = 0;
		this.totalCount = this.countWin + this.countDraw + this.countLose;
	}

	/* ȸ�������� �� */
	public Player(String ID, String password, String name, String nickname, String email, String site) {
		this.ID = ID;
		this.hashingPW = password;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.site = site;
	}
	
	public Player(String ID, String password, String name, String nickname, String email, String site, int countWin, int countDraw, int countLose) {
		this.ID = ID;
		this.hashingPW = password;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.site = site;
		this.countWin = countWin;
		this.countDraw = countDraw;
		this.countLose = countLose;
		this.totalCount = this.countWin + this.countDraw + this.countLose;
	}

	/* set method */
	public void setID(String ID) {
		this.ID = ID;
	}

	public void setPW(String password) {
		this.hashingPW = password;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setCountWin(int count) {
		this.countWin = count; 
		this.setTotalCount();
	}

	public void setCountDraw(int count) {
		this.countDraw = count;
		this.setTotalCount();
	}

	public void setCountLose(int count) {
		this.countLose = count;
		this.setTotalCount();
	}

	private void setTotalCount() {
		this.totalCount = this.countWin + this.countDraw + this.countLose;
	}

	/* get method */
	public String getID() {
		return this.ID;
	}
	
	public String getHashingPW() {
		return this.hashingPW;
	}
	
	/* �ش� ID�� SALT �� ã�� */
	private String getSALT(ArrayList<Player> player, String ID) {
		for(int i = 0; i < player.size(); i++) {
			if(ID.equals(player.get(i).getID())) {
				return player.get(i).getHashingPW();
			}
		}
		return null; // ID�� �������� ���� ���
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getSite() {
		return this.site;
	}
	
	public int getCountWin() {
		return this.countWin;
	}
	
	public int getCountDraw() {
		return this.countDraw;
	}
	
	public int getCountLose() {
		return this.countLose;
	}

	public int getTotalCount() {
		return this.totalCount;
	}
	
	/* �α����� �� ID�� ��й�ȣ�� ��ġ�ϴ��� üũ */
	public boolean login(ArrayList<Player> player, String ID, String hashingPW) {
		for(int i = 0; i < player.size(); i++) {
			if(ID.equals(player.get(i).getID())) {	// ID�� ��ġ�ϴ� ���
				if(hashingPW.equals(player.get(i).getHashingPW())) {	// ��ȣȭ�� PW�� ��ġ�ϴ� ���
					return true;
				}
			}
		}
		return false;
	}
	
	/* �ش� ID�� SALT �� ã�� */
	public String getSalt(ArrayList<Player> player, String ID) {
		for(int i = 0; i < player.size(); i++) {
			if(ID.contentEquals(player.get(i).getID())) return player.get(i).getSALT(player, ID);
		}
		return null; // ID�� �������� ���� ���
	}
	
	/* return the information about login (ID and password) */
	public String[] getLoginInfo() {
		String[] loginInfo = new String[2];
		loginInfo[0] = this.ID;
		loginInfo[1] = this.hashingPW;

		return loginInfo;
	}
	
}