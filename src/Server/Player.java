package Server;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Player implements Serializable {
	
	/* field */
	private String ID;			// ȸ�� ���̵�
	private String password;
	private String rePassword;
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
	
	private static HashMap<String, String> passwordMap = new HashMap<>();	// id and password
	private static HashMap<String, String> saltMap = new HashMap<>();

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
	public Player(String ID, String password, String rePassword, String name, String nickname, String email, String site) throws NoSuchAlgorithmException {
		this.ID = ID;
		this.password = password;
		this.rePassword = rePassword;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.site = site;
		saltedPassword(ID, password);
	}
	
	/* �α����� �� */
	public Player(String ID, String password) {
		this.ID = ID;
		this.password = password;
	}
	
	public Player(String ID, String password, String name, String nickname, String email, String site, int countWin, int countDraw, int countLose) throws NoSuchAlgorithmException {
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
		saltedPassword(ID, password);
	}

	public static void saltedPassword(String ID, String password) throws NoSuchAlgorithmException {
		byte[] newSalt = getSalt();
		byte[] newHash = getSaltedHash(password, newSalt);
		
		passwordMap.put(ID, bytesToHex(newHash));
		saltMap.put(ID, bytesToHex(newSalt));
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
	
    public static boolean checkPlayer(String ID) {
        return passwordMap.containsKey(ID);
    }
	
	public static boolean checkLogin(String ID, String password) throws NoSuchAlgorithmException {
        String salt = saltMap.get(ID);
        byte[] byteSalt = hexToBytes(salt);
        String saltedPasswordHash = bytesToHex(getSaltedHash(password, byteSalt));

        if (saltedPasswordHash.equals(passwordMap.get(ID))) {
            return true;
        } else {
            return false;
        }
    }
	
    /* return byte array of randomly generated salt number */
    public static byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        return salt;
    }
	
    /* use SHA-256 to create a hash of password string and salt byte array */
    public static byte[] getSaltedHash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashBytes = md.digest(password.getBytes());
        md.reset();

        return hashBytes; // return salted password hash as byte array
    }
	
    /* convert a byte array to a hexadecimal number */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
	
    /* convert a hexadecimal number to a byte array */
    public static byte[] hexToBytes(String hex) {
        byte[] hexBytes = new byte[hex.length() / 2];

        for (int i = 0; i < hexBytes.length; i++) {
            hexBytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return hexBytes;
    }
}