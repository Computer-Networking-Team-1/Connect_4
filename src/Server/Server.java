package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	private final static int SERVER_PORT = 9999;
	private Socket socket = null;
	
	// 로그인 끝난 뒤 아래 두 변수에 저장이 되어야 함
	public static ArrayList<String> players = new ArrayList<String>();
	public static HashMap<String, ObjectOutputStream> sockets = new HashMap<String, ObjectOutputStream>();

	public Server(Socket socket) {
		this.socket = socket;
	}
	
	/* 들어온 유저 정보가 올바른지를 체크 */
	public static boolean check(Player player) {
		return true;
	}
	
	public synchronized void addUser(Protocol request, ObjectOutputStream outToClient) {
		sockets.put(request.getFrom(), outToClient);
		players.add(request.getFrom()); // getPlayer();
	}
	
	public void run() {
		System.out.println(">> Socket: " + socket);
		Protocol request = null;
		
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
		// file
		File file = new File("./player_information.txt");
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream toFile = null;	// 파일에 객체 입력하기 위해서
		
		try {
			/* inputStream, outputStream 생성 */
			in = new ObjectInputStream(this.socket.getInputStream());
			out = new ObjectOutputStream(this.socket.getOutputStream());
			
			/* client 측에 연결됐음을 알림 */
			out.writeObject(new Protocol(9, "server", "user", "connected"));
			out.flush();
			
			/* client 측에서 오는 메시지를 기다렸다가 반응해서 응답 */
			while(in != null) {
				request = (Protocol) in.readObject();
				System.out.print(">> ");
				System.out.println("type: " + request.getType() + " from: " + request.getFrom() + " to: " + request.getTo() + " content: " + request.getContent() + " player: " + request.getPlayer() + " users: " + players.size());
				
				int type = request.getType();
				switch(type) {
				
				/* [type: login] */
				case 1:
					Player loginTemp = (Player) request.getPlayer();
					
					
					break;
					
				/* [type: sign up] 플레이어 목록에 추가하고 outputStream mapping */
				case 3:
					Player signUpTemp = (Player) request.getPlayer();
					if(check(signUpTemp) == true) addUser(request, out);	// 들어온 플레이어 정보가 올바른지 체크 (아직 구현 X)
					
					fileOutputStream = new FileOutputStream(file);
					toFile = new ObjectOutputStream(fileOutputStream);
					toFile.writeObject(signUpTemp);
					
					System.out.printf(">> ");
					System.out.printf("[sign up] %d %s (# of users: %d)\n", request.getType(), request.getFrom(), players.size());
					break;
					
				/* [type: chat] 양쪽 client에 채팅한 내용을 메시지로 보냄*/
				case 4:
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
				case 5:
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
					
				case 6:
					break;
					
				case 7:
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
			
			toFile.close();
			fileOutputStream.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
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