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
	
	private String id;				// ���̵�
	private byte[] passwordToByte;	//
	private String password;		// ��й�ȣ (��ȣȭ�� �� �ϵ��� �ܹ��� �ؽ� �Լ� ���� ��ȣȭ)
	private String salt;			// ��й�ȣ ��ȣȭ�� ���� ��Ʈ
	private String name;			// �̸�
	private String nickname;		// �г���
	private String email;			// �̸���
	private String site;			// SNS �� Ȩ������ �ּ�
	
	private int countWin = 0;		// �̱� Ƚ�� (��)
	private int countDraw = 0;		// ��� Ƚ�� (���º�)
	private int countLose = 0;		// �� Ƚ�� (��)
	private int totalCount = 0;		// ��ü ���� Ƚ��
	
	private int loginCount;					// ���� Ƚ��
	private String[] log = new String[2];	// ������ ���� ���� (IP �ּ�, �ð�)
	private int status = 0; 				// [0] �α׾ƿ� [1] ����� [2] ������

	public Player() {}
	
	// �α��� (��ȸ��)
	public Player(String id, byte[] passwordToByte) {
		this.id = id;
		this.passwordToByte = passwordToByte;
	}
	
	// ȸ������ (��ȸ��)
	public Player(String id, byte[] passwordToByte, String name, String nickname, String email, String site) {
		this.id = id;
		this.passwordToByte = passwordToByte;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.site = site;
	}
	
	/**
	 * [method setPlayer] �÷��̾� ���� (at Sign Up)
	 * @param id
	 * @param password
	 * @param salt (��й�ȣ �ؽ��� ����)
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
	 * [method check] ���̵�� ��й�ȣ�� ��ġ�ϴ��� üũ
	 * @param id
	 * @param password
	 * @return true if matched, or false if not matched
	 */
	public boolean check(String id, String password, String IP) {
		for(int i = 0; i < players.size(); i++) {
			if(id.contentEquals(players.get(i).getId()) && password.equals(players.get(i).getPassword())) {
				players.get(i).setLoginCount();	// ������ ������ ++
				players.get(i).setLog(IP);		// ���� ���� ����
				return true;
			}
		}
		return false;
	}

	/**
	 * [method idCheck] �ߺ��Ǵ� ���̵����� Ȯ��
	 */
	public boolean idCheck(String id) {
		for(Player p:players) {
			if(id.equals(p.getId())) return false;
		}
		return true;
	}
	
	/**
	 * [method nickCheck] �ߺ��Ǵ� �г������� Ȯ��
	 */
	public boolean nickCheck(String nickname) {
		for(Player p: players) {
			if(nickname.equals(p.getNickname())) return false;
		}
		return true;
	}
	
	/**
	 * [method changeNickCheck] ������ ������ �� �ٸ� ����� �ߺ��Ǵ� �г������� Ȯ��
	 */
	public boolean changeNickCheck(String nickname, String origin) {
		for(Player p: players) {
			if(nickname.equals(p.getNickname())&&!origin.equals(p.getNickname())) return false;
		}
		return true;
	}
	
	/**
	 * [method getPlayerByID] ���̵�� Player ��ü ã�Ƽ� ��ȯ
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
	 * [method getPlayerIndex] ���̵�� �ش� Player ��ü�� �ε��� ��ȯ
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
	 * [method getSalt] ���̵� �ش��ϴ� ��Ʈ �� ��ȯ
	 * @param id
	 * @return salt if found, or false if not found
	 */
	public String getSalt(String id) {
		for(int i = 0; i < players.size(); i++) {
			if(id.contentEquals(players.get(i).getId())) {
				return players.get(i).getSalt();
			}
		}
		return null;	// ���̵� �������� ���� ��� null
	}
	
	/**
	 * [method setInformation] ���Ͽ� ����� �����ͷ� �ʱ� ����
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
	 * [method updateInformation] ���Ͽ� �ִ� ������ ������Ʈ
	 */
	public static void updateInformation() {
		File file = new File("./player_information.txt");
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream toFile = null;	// ���Ͽ� ��ü �Է��ϱ� ���ؼ�
		try {
			fileOutputStream = new FileOutputStream(file);
			toFile = new ObjectOutputStream(fileOutputStream);
			ObjectInputStream fromFile = new ObjectInputStream(new FileInputStream(file)); // �׽�Ʈ: ���Ͽ� �� ��ü �о�ͼ� ���
			
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
		/* IP �ּ� */
		this.log[0] = IP;
		
		/* ���� ��¥ ���ϱ� */
		LocalDate nowDate = LocalDate.now();
		int dayOfMonth = nowDate.getDayOfMonth();
		String dd = Integer.toString(dayOfMonth);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM" + dd);
		String today = nowDate.format(formatter);
		this.log[1] = today;
	}
	
	public void setFirstLog(String IP) {
		/* IP �ּ� */
		this.log[0] = IP;
	}
	
	public void setSecondLog() {
		/* ���� ��¥ ���ϱ� */
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