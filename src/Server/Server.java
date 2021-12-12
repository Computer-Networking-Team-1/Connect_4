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
		ObjectInputStream in = null;	// 클라이언트로부터 객체를 읽어오기 위해
		ObjectOutputStream out = null;	// 클라이언트로 객체를 내보내기 위해
		
		try {
			in = new ObjectInputStream(this.socket.getInputStream());
			out = new ObjectOutputStream(this.socket.getOutputStream());
			
			/* 클라이언트에 연결됐음을 알림 */
			out.writeObject(new Protocol(9, "server", "user", "connected"));
			out.flush();
			
			/* 클라이언트에서 오는 메시지를 기다렸다가 반응해서 응답 */
			while(in != null) {
				request = (Protocol) in.readObject();	// 클라이언트로부터 프로토콜 객체를 전달받기
				System.out.println(">> type: " + request.getType() + " from: " + request.getFrom() + " to: " + request.getTo() + " content: " + request.getContent() + " player: " + request.getPlayer() + " users: " + players.size());
				
				int type = request.getType();
				Player temp = null;
				String tempSalt = null;
				String tempPassword = null;
				
				switch(type) {

				/* [type: login] */
				case 1:
					Player.setInformation();
					temp = (Player) request.getPlayer();	// 아이디와 패스워드 정보가 담긴 객체를 받아서
					tempSalt = playerDB.getSalt(temp.getId());
					tempPassword = hashing(temp.getPasswordByte(), tempSalt);
					
					if(playerDB.check(temp.getId(), tempPassword)) {	// 로그인 성공
						System.out.println(">> login success");
						out.writeObject(new Protocol(1, request.getFrom(), request.getTo(), "SUCCESS"));
						out.flush();
					}
					else {	// 로그인 실패
						System.out.println(">> login fail");
						out.writeObject(new Protocol(1, request.getFrom(), request.getTo(), "FAIL"));
						out.flush();
					}
					break;
					
				/* [type: sign up] 플레이어 목록에 추가하고 outputStream mapping */
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
					
				/* [type: chat] 양쪽 client에 채팅한 내용을 메시지로 보냄*/
				case 3:
					System.out.println(request.getContent());
					try {
						out.writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						
						// HashMap에 플레이어 이름을 key로 사용해 OutputStream을 찾음
						sockets.get(request.getTo()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
					break;
					
				/* [type: challenge] 대결을 신청한 상대가 있으면 양쪽 client에 대결을 하라는 response 전송 */
				case 4:
					// 대결을 신청한 대상의 이름을 플레이어 목록과 비교해서 있는지 확인
					// 상대가 있으면 양쪽 client에 대결을 하라는 메시지를 보냄
					if(players.contains(request.getTo())) {
						try {
							out.writeObject(new Protocol(7, request.getFrom(), request.getTo(), "challenge"));
							out.flush();
							
							// HashMap에 플레이어 이름을 key로 사용해 OutputStream을 찾음
							sockets.get(request.getTo()).writeObject(new Protocol(8, request.getTo(), request.getFrom(), "chellenge"));
							sockets.get(request.getTo()).flush();
						} catch (Exception e) {e.printStackTrace();}
					}
					// 상대가 없으면 발견되지 않았다는 메시지를 보냄
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

				/* [type: ready] 양쪽 client에 누군가 준비 버튼을 눌렀다고 알림 */
				case 10:
					try {
						out.writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
					break;
					
				/* [type: play] 양쪽 client에 누가 어디에 돌을 놓았는지 알림 */
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
					
				/* 게임이 끝났을 때 처리할 내용 */
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
			String temp = byteToHex(password) + salt;	// 패스워드와 솔트를 합쳐 새로운 문자열 생성
			md.update(temp.getBytes());					// 해싱한 문자열을 저장
			password = md.digest();						// 다이제스트를 통해 패스워드 갱신
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
		random.nextBytes(temp);	// 생성된 배열을 넣어 바이트 배열이 임의의 값들로 채워지도록 함으로써 솔트 생성
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