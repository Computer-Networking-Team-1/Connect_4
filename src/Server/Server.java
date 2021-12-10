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
		System.out.println(socket + " ����");
		Protocol request = null;
		
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
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
				System.out.println("type: " + request.getType() + ", from: " + request.getFrom() + ", to: " + request.getTo() + ", content: " + request.getContent() + ", users: " + players.size());
				
				/* [type: sign up] �÷��̾� ��Ͽ� �߰��ϰ� outputStream mapping */
				if(request.getType() == 3) {
					addUser(request, out);
				}
				
				/* [type: challenge] ����� ��û�� ��밡 ������ ���� client�� ����� �϶�� response ���� */
				if(request.getType() == 5) {
					
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
				}
				
				/* [type: chat] ���� client�� ä���� ������ �޽����� ����*/
				if(request.getType() == 4) {
					System.out.println(request.getContent());
					try {
						out.writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						
						// HashMap�� �÷��̾� �̸��� key�� ����� OutputStream�� ã��
						sockets.get(request.getTo()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
				}
				
				/* [type: ready] ���� client�� ������ �غ� ��ư�� �����ٰ� �˸� */
				if(request.getType() == 10) {
					try {
						out.writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					} catch (Exception e) {e.printStackTrace();}
				}
				
				/* [type: play] ���� client�� ���� ��� ���� ���Ҵ��� �˸� */
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
				
				/* ������ ������ �� ó���� ���� */
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