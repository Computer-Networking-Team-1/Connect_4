package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class Server extends Thread {
	private final static int SERVER_PORT = 9999;
	private static final int SALT_SIZE = 16;		// 16bytes = 128bits
	private static Player playerDB = new Player();	// player database (with ArrayList and file)
	
	public static ArrayList<Player> players = new ArrayList<Player>();
	public static HashMap<String, ObjectOutputStream> sockets = new HashMap<String, ObjectOutputStream>();

	private Socket socket = null;
	
	public Server(Socket socket) {
		this.socket = socket;
	}
	
	public synchronized void addUser(Protocol request, ObjectOutputStream outToClient) {
		sockets.put(request.getFrom(), outToClient);
		players.add(request.getPlayer());
	}
	
	public void run() {
		System.out.println(">> " + socket);
		Protocol request = null;
		ObjectInputStream in = null;	// Ŭ���̾�Ʈ�κ��� ��ü�� �о���� ����
		ObjectOutputStream out = null;	// Ŭ���̾�Ʈ�� ��ü�� �������� ����
		
		try {
			in = new ObjectInputStream(this.socket.getInputStream());
			out = new ObjectOutputStream(this.socket.getOutputStream());
			
			/* Ŭ���̾�Ʈ�� ��������� �˸� */
			out.writeObject(new Protocol(9, "server", "user", "connected"));
			out.flush();
			
			/* Ŭ���̾�Ʈ���� ���� �޽����� ��ٷȴٰ� �����ؼ� ���� */
			while(in != null) {
				request = (Protocol) in.readObject();	// Ŭ���̾�Ʈ�κ��� �������� ��ü�� ���޹ޱ�
				System.out.println(">> type: " + request.getType() + " from: " + request.getFrom() + " to: " + request.getTo() + " content: " + request.getContent() + " player: " + request.getPlayer() + " users: " + players.size());
				
				int type = request.getType();
				Player temp = null;
				String tempSalt = null;
				String tempPassword = null;
				
				switch(type) {

				/* [type: login] */
				case 1:
					Player.setInformation();
					temp = (Player) request.getPlayer();	// ���̵�� �н����� ������ ��� ��ü�� �޾Ƽ�
					tempSalt = playerDB.getSalt(temp.getId());
					tempPassword = hashing(temp.getPasswordByte(), tempSalt);
					
					if(playerDB.check(temp.getId(), tempPassword)) {	// �α��� ����
						System.out.println(">> login success");
						out.writeObject(new Protocol(1, request.getFrom(), request.getTo(), "SUCCESS"));
						out.flush();
					}
					else {	// �α��� ����
						System.out.println(">> login fail");
						out.writeObject(new Protocol(1, request.getFrom(), request.getTo(), "FAIL"));
						out.flush();
					}
					break;
					
				/* [type: sign up] �÷��̾� ��Ͽ� �߰��ϰ� outputStream mapping */
				case 2:
					Player.setInformation();
					temp = (Player) request.getPlayer();
					tempSalt = createSalt();
					tempPassword = hashing(temp.getPasswordByte(), tempSalt);
					playerDB.setPlayer(temp.getId(), tempPassword, tempSalt, temp);
					
					addUser(request, out);
					Server.players = Player.players;
					Player.updateInformation();

					System.out.printf(">> ");
					System.out.printf("Sign Up %d %s (# of users: %d)\n", request.getType(), request.getFrom(), players.size());
					break;
					
				/* [type: chat] ���� client�� ä���� ������ �޽����� ����*/
				case 3:
					System.out.println(request.getContent());
					try {
						out.writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						
						// HashMap�� �÷��̾� �̸��� key�� ����� OutputStream�� ã��
						sockets.get(request.getTo()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
					break;
					
				/* [type: challenge] ����� ��û�� ��밡 ������ ���� client�� ����� �϶�� response ���� */
				case 4:
					// ����� ��û�� ����� �̸��� �÷��̾� ��ϰ� ���ؼ� �ִ��� Ȯ��
					// ��밡 ������ ���� client�� ����� �϶�� �޽����� ����
					if(players.contains(request.getTo())) {
						try {
							out.writeObject(new Protocol(7, request.getFrom(), request.getTo(), "challenge"));
							out.flush();
							
							// HashMap�� �÷��̾� �̸��� key�� ����� OutputStream�� ã��
							sockets.get(request.getTo()).writeObject(new Protocol(8, request.getTo(), request.getFrom(), "chellenge"));
							sockets.get(request.getTo()).flush();
						} catch (Exception e) {e.printStackTrace();}
					}
					// ��밡 ������ �߰ߵ��� �ʾҴٴ� �޽����� ����
					else {
						try {
							out.writeObject(new Protocol(5, request.getFrom(), request.getTo(), "User not found"));
							out.flush();
						} catch (Exception e) {e.printStackTrace();}
					}
					break;
					
				case 5:
					break;
					
				case 6:
					break;

				/* [type: ready] ���� client�� ������ �غ� ��ư�� �����ٰ� �˸� */
				case 10:
					try {
						out.writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
					break;
					
				/* [type: play] ���� client�� ���� ��� ���� ���Ҵ��� �˸� */
				case 11:
					try {
						out.writeObject(new Protocol(11, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(11, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				/* ������ ������ �� ó���� ���� */
				case 12:
					try {
						if(request.getContent().equals("win")) {
							
						}
						else if(request.getContent().equals("lose")) {
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				default:
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * [method hashing] hashing password
	 * @param password
	 * @param salt
	 * @return hashing password
	 */
	private String hashing(byte[] password, String salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");	// use SHA-256 hash function
		
		for(int i = 0; i < 10000; i++) {
			String temp = byteToHex(password) + salt;	// �н������ ��Ʈ�� ���� ���ο� ���ڿ� ����
			md.update(temp.getBytes());					// �ؽ��� ���ڿ��� ����
			password = md.digest();						// ��������Ʈ�� ���� �н����� ����
		}
		
		return byteToHex(password);
	}
	
	/**
	 * [method createSalt] create a value of salt
	 * @return a value of salt (hex)
	 */
	private String createSalt() throws Exception {
		SecureRandom random = new SecureRandom();
		byte[] temp = new byte[SALT_SIZE];
		random.nextBytes(temp);	// ������ �迭�� �־� ����Ʈ �迭�� ������ ����� ä�������� �����ν� ��Ʈ ����
		return byteToHex(temp);
	}
	
	/**
	 * [method byteToString] convert byte to hex
	 * @param array of bytes
	 * @return hex
	 */
	private String byteToHex(byte[] temp) {
		StringBuilder sb = new StringBuilder();
		
		for(byte a: temp) {
			sb.append(String.format("02x", a));
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		ServerSocket listener = null;
		Socket client = null;
		
		try {
			listener = new ServerSocket(SERVER_PORT);
			while(true) {
				client = listener.accept();
				Server server = new Server(client);
				server.start();
			}
		} catch(Exception e) {System.out.println(e.getMessage());}
	}
}