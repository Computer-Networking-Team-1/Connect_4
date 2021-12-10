package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	private final static int SERVER_PORT = 9999;
	private Socket socket = null;
	
	public static ArrayList<String> players = new ArrayList<String>();
	public static HashMap<String, ObjectOutputStream> sockets = new HashMap<String, ObjectOutputStream>();
	
	public synchronized void addUser(Protocol request, ObjectOutputStream outToClient) {
		sockets.put(request.getFrom(), outToClient);
		players.add(request.getFrom());
	}
	
	public Server(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		System.out.println(socket + " 연결");
		Protocol request = null;
		
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
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
				System.out.println("type: " + request.getType() + ", from: " + request.getFrom() + ", to: " + request.getTo() + ", content: " + request.getContent() + ", users: " + players.size());
				
				/* [type: sign up] 플레이어 목록에 추가하고 outputStream mapping */
				if(request.getType() == 3) {
					addUser(request, out);
				}
				
				/* [type: challenge] 대결을 신청한 상대가 있으면 양쪽 client에 대결을 하라는 response 전송 */
				if(request.getType() == 5) {
					
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
				}
				
				/* [type: chat] 양쪽 client에 채팅한 내용을 메시지로 보냄*/
				if(request.getType() == 4) {
					System.out.println(request.getContent());
					try {
						out.writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						
						// HashMap에 플레이어 이름을 key로 사용해 OutputStream을 찾음
						sockets.get(request.getTo()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
				}
				
				/* [type: ready] 양쪽 client에 누군가 준비 버튼을 눌렀다고 알림 */
				if(request.getType() == 10) {
					try {
						out.writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
				}
				
				/* [type: play] 양쪽 client에 누가 어디에 돌을 놓았는지 알림 */
				if(request.getType() == 11) {
					try {
						out.writeObject(new Protocol(11, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(11, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				/* 게임이 끝났을 때 처리할 내용 */
				if(request.getType() == 12) {
					try {
						if(request.getContent().equals("win")) {
							
						}
						else if(request.getContent().equals("lose")) {
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e) {e.printStackTrace();}
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