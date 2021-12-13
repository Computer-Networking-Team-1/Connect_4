package Server;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Player implements Serializable {
	
	public static ArrayList<Player> players = new ArrayList<Player>();
	
	private String id;				// 아이디
	private byte[] passwordToByte;	//
	private String password;		// 비밀번호 (복호화를 못 하도록 단방향 해시 함수 통해 암호화)
	private String salt;			// 비밀번호 암호화를 위한 솔트
	private String name;			// 이름
	private String nickname;		// 닉네임
	private String email;			// 이메일
	private String site;			// SNS 및 홈페이지 주소
	
	private int countWin = 0;		// 이긴 횟수 (승)
	private int countDraw = 0;		// 비긴 횟수 (무승부)
	private int countLose = 0;		// 진 횟수 (패)
	private int totalCount = 0;		// 전체 게임 횟수
	
	private int loginCount;					// 접속 횟수
	private String[] log = new String[2];	// 마지막 접속 정보 (IP 주소, 시간)
	private int status = 0; 				// [0] 로그아웃 [1] 대기중 [2] 게임중

	public Player() {}
	
	// 로그인 (일회성)
	public Player(String id, byte[] passwordToByte) {
		this.id = id;
		this.passwordToByte = passwordToByte;
	}
	
	// 회원가입 (일회성)
	public Player(String id, byte[] passwordToByte, String name, String nickname, String email, String site) {
		this.id = id;
		this.passwordToByte = passwordToByte;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.site = site;
	}
	
	/**
	 * [method setPlayer] 플레이어 생성 (at Sign Up)
	 * @param id
	 * @param password
	 * @param salt (비밀번호 해싱을 위한)
	 */
	public void setPlayer(String id, String password, String salt, Player temp) {
		this.id = id;
		this.password = password;
		this.salt = salt;
		this.name = temp.getName();
		this.nickname = temp.getNickname();
		this.email = temp.getEmail();
		this.site = temp.getSite();
		this.loginCount = 0;
		players.add(this);
	}
	
	/**
	 * [method check] 아이디와 비밀번호가 일치하는지 체크
	 * @param id
	 * @param password
	 * @return true if matched, or false if not matched
	 */
	public boolean check(String id, String password, String IP) {
		for(int i = 0; i < players.size(); i++) {
			if(id.contentEquals(players.get(i).getId()) && password.equals(players.get(i).getPassword())) {
				players.get(i).setLoginCount();	// 접속할 때마다 ++
				players.get(i).setLog(IP);		// 접속 정보 갱신
				return true;
			}
		}
		return false;
	}

	/**
	 * [method idCheck] 중복되는 아이디인지 확인
	 */
	public boolean idCheck(String id) {
		for(Player p:players) {
			if(id.equals(p.getId())) return false;
		}
		return true;
	}
	
	/**
	 * [method nickCheck] 중복되는 닉네임인지 확인
	 */
	public boolean nickCheck(String nickname) {
		for(Player p: players) {
			if(nickname.equals(p.getNickname())) return false;
		}
		return true;
	}
	
	/**
	 * [method changeNickCheck] 정보를 수정할 때 다른 사람과 중복되는 닉네임인지 확인
	 */
	public boolean changeNickCheck(String nickname, String origin) {
		for(Player p: players) {
			if(nickname.equals(p.getNickname())&&!origin.equals(p.getNickname())) return false;
		}
		return true;
	}
	
	/**
	 * [method getPlayerByID] 아이디로 Player 객체 찾아서 반환
	 */
	public Player getPlayerById(String id) {
		for(Player p: players) {
			if(p.getId().equals(id)) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * [method getPlayerIndex] 아이디로 해당 Player 객체의 인덱스 반환
	 */
	public int getPlayerIndex(String id) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getId().equals(id)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * [method getSalt] 아이디에 해당하는 솔트 값 반환
	 * @param id
	 * @return salt if found, or false if not found
	 */
	public String getSalt(String id) {
		for(int i = 0; i < players.size(); i++) {
			if(id.contentEquals(players.get(i).getId())) {
				return players.get(i).getSalt();
			}
		}
		return null;	// 아이디가 존재하지 않을 경우 null
	}
	
	/**
	 * [method setInformation] 파일에 저장된 데이터로 초기 세팅
	 */
	public static void setInformation() {
		File file = new File("./player_information.txt");
		FileInputStream fileInputStream = null;
		ObjectInputStream fromFile = null;
		
		try {
			fileInputStream = new FileInputStream(file);
			fromFile = new ObjectInputStream(fileInputStream);
			players = new ArrayList<Player>();
			
			while(true) {
				Player temp = (Player) fromFile.readObject();
				players.add(temp);
				
				System.out.printf(">> [method setInformation] Deserializable object: %s %s %s %s %s %s\n", 
						temp.getId(), temp.getPassword(), temp.getName(), temp.getNickname(),
						temp.getEmail(), temp.getSite());
			}
		} catch(EOFException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fileInputStream != null) {
					fileInputStream.close();
				}
				
				if(fromFile != null) {
					fromFile.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(">> finish the initial setting");
	}
	
	/**
	 * [method updateInformation] 파일에 있는 정보를 업데이트
	 */
	public static void updateInformation() {
		File file = new File("./player_information.txt");
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream toFile = null;	// 파일에 객체 입력하기 위해서
		try {
			fileOutputStream = new FileOutputStream(file);
			toFile = new ObjectOutputStream(fileOutputStream);
			ObjectInputStream fromFile = new ObjectInputStream(new FileInputStream(file)); // 테스트: 파일에 쓴 객체 읽어와서 출력
			
			for(int i = 0; i < players.size(); i++) {
				toFile.writeObject(players.get(i));
				toFile.flush();
			}
			
			for(int i = 0; i < players.size(); i++) {
				Player test = (Player) fromFile.readObject();
				System.out.printf(">> [method updateInformation] Deserializable object: %s %s %s %s %s %s\n", 
						test.getId(), test.getPassword(), test.getName(), test.getNickname(),
						test.getEmail(), test.getSite());
			}
			
			fileOutputStream.close();
			toFile.close();
			fromFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setID(String id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	public void setLoginCount() {
		this.loginCount += 1;
	}
	
	public void setLog(String IP) {
		/* IP 주소 */
		this.log[0] = IP;
		
		/* 현재 날짜 구하기 */
		LocalDate nowDate = LocalDate.now();
		int dayOfMonth = nowDate.getDayOfMonth();
		String dd = Integer.toString(dayOfMonth);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM" + dd);
		String today = nowDate.format(formatter);
		this.log[1] = today;
	}
	
	public void setFirstLog(String IP) {
		/* IP 주소 */
		this.log[0] = IP;
	}
	
	public void setSecondLog() {
		/* 현재 날짜 구하기 */
		LocalDate nowDate = LocalDate.now();
		int dayOfMonth = nowDate.getDayOfMonth();
		String dd = Integer.toString(dayOfMonth);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM" + dd);
		String today = nowDate.format(formatter);
		this.log[1] = today;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getId() {
		return this.id;
	}
	
	public byte[] getPasswordByte() {
		return this.passwordToByte;
	}
	
	private String getPassword() {
		return this.password;
	}
	
	public String getSalt() {
		return this.salt;
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
	
	public int getStatus() {
		return this.status;
	}
	
	public int getLoginCount() {
		return this.loginCount;
	}
	
	public String[] getLog() {
		return this.log;
	}
}