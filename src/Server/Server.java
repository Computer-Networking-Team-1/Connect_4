package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	private final static int SERVER_PORT = 9999;
	private Socket socket = null;
	
	// �α��� ���� �� �Ʒ� �� ������ ������ �Ǿ�� ��
	public static ArrayList<String> players = new ArrayList<String>();
	public static HashMap<String, ObjectOutputStream> sockets = new HashMap<String, ObjectOutputStream>();

	public Server(Socket socket) {
		this.socket = socket;
	}
	
	/* ���� ���� ������ �ùٸ����� üũ */
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
		ObjectOutputStream toFile = null;	// ���Ͽ� ��ü �Է��ϱ� ���ؼ�
		
		try {
			/* inputStream, outputStream ���� */
			in = new ObjectInputStream(this.socket.getInputStream());
			out = new ObjectOutputStream(this.socket.getOutputStream());
			
			/* client ���� ��������� �˸� */
			out.writeObject(new Protocol(9, "server", "user", "connected"));
			out.flush();
			
			/* client ������ ���� �޽����� ��ٷȴٰ� �����ؼ� ���� */
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
					
				/* [type: sign up] �÷��̾� ��Ͽ� �߰��ϰ� outputStream mapping */
				case 3:
					Player signUpTemp = (Player) request.getPlayer();
					if(check(signUpTemp) == true) addUser(request, out);	// ���� �÷��̾� ������ �ùٸ��� üũ (���� ���� X)
					
					fileOutputStream = new FileOutputStream(file);
					toFile = new ObjectOutputStream(fileOutputStream);
					toFile.writeObject(signUpTemp);
					
					System.out.printf(">> ");
					System.out.printf("[sign up] %d %s (# of users: %d)\n", request.getType(), request.getFrom(), players.size());
					break;
					
				/* [type: chat] ���� client�� ä���� ������ �޽����� ����*/
				case 4:
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
				case 5:
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
					
				case 6:
					break;
					
				case 7:
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