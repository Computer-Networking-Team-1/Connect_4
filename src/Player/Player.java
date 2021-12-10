package Player;

import java.io.*;
import java.util.*;

public class Player implements Serializable {
	
	/* field */
	private String ID;			// 회원 아이디
	private String hashingPW;	// 회원 비밀번호 (복호화를 못 하도록 단방향 해시 함수 통해 암호화)
	private String salt;		// 비밀번호 암호화를 위한 salt
	private String name;		// 회원 이름
	private String nickname;	// 회원 닉네임
	private String email;		// 회원 이메일
	private String site;		// 회원 SNS 및 홈페이지 주소
	
	private int loginCount;		// 접속 횟수
	private String lastLog;		// 마지막 접속 정보 (IP주소, 시간)
	
	private int countWin = 0;	// 이긴 횟수 (승)
	private int countDraw = 0;	// 비긴 횟수 (무승부)
	private int countLose = 0;	// 진 횟수 (패)
	private int totalCount = 0;	// 전체 게임 횟수
	
	private int status = 0; // 0: 로그아웃, 1: 대기중, 2: 게임중

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

	/* 회원가입할 때 */
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
	
	/* 해당 ID의 SALT 값 찾기 */
	private String getSALT(ArrayList<Player> player, String ID) {
		for(int i = 0; i < player.size(); i++) {
			if(ID.equals(player.get(i).getID())) {
				return player.get(i).getHashingPW();
			}
		}
		return null; // ID가 존재하지 않을 경우
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
	
	/* 로그인할 때 ID와 비밀번호가 일치하는지 체크 */
	public boolean login(ArrayList<Player> player, String ID, String hashingPW) {
		for(int i = 0; i < player.size(); i++) {
			if(ID.equals(player.get(i).getID())) {	// ID가 일치하는 경우
				if(hashingPW.equals(player.get(i).getHashingPW())) {	// 암호화된 PW가 일치하는 경우
					return true;
				}
			}
		}
		return false;
	}
	
	/* 해당 ID의 SALT 값 찾기 */
	public String getSalt(ArrayList<Player> player, String ID) {
		for(int i = 0; i < player.size(); i++) {
			if(ID.contentEquals(player.get(i).getID())) return player.get(i).getSALT(player, ID);
		}
		return null; // ID가 존재하지 않을 경우
	}
	
	/* return the information about login (ID and password) */
	public String[] getLoginInfo() {
		String[] loginInfo = new String[2];
		loginInfo[0] = this.ID;
		loginInfo[1] = this.hashingPW;

		return loginInfo;
	}
	
}