package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * [class Server]
 */
public class Server extends Thread {
	private final static int SERVER_PORT = 9999;
	private static final int SALT_SIZE = 16;		// 16bytes = 128bits
	private static Player playerDB = new Player();	// ArrayList, File�� �̿��� �÷��̾� �����ͺ��̽�
	
	// ���� ���� ���� ������� Player, OutputStream, Nickname
	public static ArrayList<Player> players = new ArrayList<Player>();
	public static HashMap<String, ObjectOutputStream> sockets = new HashMap<String, ObjectOutputStream>();
	public static HashMap<String, Player> nicknameToPlayer = new HashMap<String, Player>();	// ���� ���� ����
	private Player player = null;
	private Socket socket = null;
	
	// �ش� Ŭ���̾�Ʈ�� ���� ��� ������ Ȯ���ϴ� HashMap
	private HashMap<String, String> inBattle;
	
	/**
	 * [Constructor Server]
	 */
	public Server(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * [method addPlayer] ������ ����� ��Ͽ� �߰�
	 * @param player
	 * @param outToClient
	 */
	public synchronized void addPlayer(Player p, ObjectOutputStream outToClient) {
		sockets.put(p.getNickname(), outToClient);
		nicknameToPlayer.put(p.getNickname(), p);
		players.add(p);
	}
	
	/**
	 * [method removePlayer] ������ ����ų� �α׾ƿ��ϴ� ����� ��Ͽ��� ����
	 * @param player
	 */
	public synchronized void removePlayer(Player p) {
		sockets.remove(p.getNickname());
		nicknameToPlayer.remove(p.getNickname());
		players.remove(p);
	}
	
	/**
	 * [method run] thread ����
	 */
	public void run() {
		System.out.println(">> " + socket);
		Protocol request = null;
		ObjectInputStream in = null;	// Ŭ���̾�Ʈ�κ��� ��ü�� �о���� ����
		ObjectOutputStream out = null;	// Ŭ���̾�Ʈ�� ��ü�� �������� ����
		inBattle = new HashMap<String, String>();
		
		try {
			in = new ObjectInputStream(this.socket.getInputStream());
			out = new ObjectOutputStream(this.socket.getOutputStream());
			
			while(true) {	// Ŭ���̾�Ʈ���� ���� �޽����� ��ٷȴٰ� �����ؼ� ����
				request = (Protocol) in.readObject();	// Ŭ���̾�Ʈ�κ��� �������� ��ü ���޹ޱ�
				System.out.println(">> type: " + request.getType() + " from: " + request.getFrom() + " to: " + request.getTo() + " content: " + request.getContent() + " player: " + request.getPlayer() + " users: " + players.size());
				
				int type = request.getType();	// Ÿ�� ����
				Player temp = null;
				String tempSalt = null;
				String tempPassword = null;
				
				switch(type) {

				/* [type: login] outputStream ���� */
				case 1:
					Player.setInformation();
					temp = (Player) request.getPlayer();	// ���̵�� �н����� ������ ��� ��ü ���޹ޱ�
					tempSalt = playerDB.getSalt(temp.getId());
					tempPassword = hashing(temp.getPasswordByte(), tempSalt);
					
					System.out.println(">> " + socket.getLocalAddress().toString());
					
					// �α��� ����
					if(playerDB.check(temp.getId(), tempPassword, socket.getLocalAddress().toString())) {
						System.out.println(">> login success");
						player = playerDB.getPlayerById(temp.getId());
						player.setStatus(1);
						addPlayer(player, out);
						
						out.writeObject(new Protocol(1, "SUCCESS", player));	// Ŭ���̾�Ʈ���� �����ߴٴ� �޽��� ����
						out.flush();
						
						int idx = playerDB.getPlayerIndex(temp.getId());
						Player.players.set(idx, player);
					}
					// �α��� ����
					else {
						System.out.println(">> login failure");
						out.writeObject(new Protocol(1, request.getFrom(), request.getTo(), "FAIL"));	// Ŭ���̾�Ʈ���� �����ߴٴ� �޽��� ����
						out.flush();
					}
					break;
					
				/* [type: sign up] �÷��̾� ��Ͽ� �߰� */
				case 2:
					Player.setInformation();
					temp = (Player) request.getPlayer();

					if(playerDB.idCheck(temp.getId())) {	// ���̵� �ߺ� Ȯ��
						if(playerDB.nickCheck(temp.getNickname())) {	// ������ �ߺ��Ǵ��� Ȯ��
								tempSalt = createSalt();
								tempPassword = hashing(temp.getPasswordByte(), tempSalt);
								playerDB.setPlayer(temp.getId(), tempPassword, tempSalt, temp);
								Player.updateInformation();	// �÷��̾� ���� ������Ʈ
								
								out.writeObject(new Protocol(2, null, null, "success"));	// Ŭ���̾�Ʈ���� �����ߴٴ� �޽��� ����
								System.out.printf(">> Sign Up %d %s (# of users: %d)\n", request.getType(), request.getFrom(), players.size());
						}
						else {	// ������ �ߺ��Ǹ� �ߺ��� ���� �������� �˸��� �޽��� ����
							out.writeObject(new Protocol(2, null, null, "nick"));
						}
					}
					else {	// ���̵� �ߺ��Ǹ� �ߺ��� ���� ���̵����� �˸��� �޽��� ����
						out.writeObject(new Protocol(2, null, null, "id"));
					}
					break;
					
				/* [type: chat] ���� ���� ���� Ŭ���̾�Ʈ�� ä���� ������ �޽����� ���� */
				case 3:
					System.out.println(request.getContent());
						out.writeObject(new Protocol(3, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						
						// HashMap�� �÷��̾� �̸��� key�� ����� OutputStream�� ã��
						sockets.get(request.getTo()).writeObject(new Protocol(3, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					break;
					
				/* [type: challenge] ����� ��û�� ��밡 ������ ���� client�� ����� �϶�� response ���� */
				case 4:
					// ��밡 ���� ���� �ƴϸ� ��� ��û �޽����� ����
					if(nicknameToPlayer.get(request.getTo()).getStatus() == 1) {
						sockets.get(request.getTo()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), "invite"));
						sockets.get(request.getTo()).flush();
					}
					// ��밡 ���� ���̸� ���� ���̶�� �޽����� ����
					else {
						out.writeObject(new Protocol(4, request.getFrom(), request.getTo(), "in game"));
						out.flush();
					}
					break;
					
				/* [type: information] ���� �˻��� ����� Player ��ü�� ���� */
				case 5:
					out.writeObject(new Protocol(5, nicknameToPlayer.get(request.getTo())));
					break;
					
				case 6:
					break;
				
				/* [type: invited] ��� ��û�� ���� ����� �ൿ�� ó�� */
				case 7:
					// ��밡 ����� �������� ��
					if(request.getContent().equals("confirm")) {
							// ���� �÷��̾ ����� �϶�� �޽����� ����
							nicknameToPlayer.get(request.getFrom()).setStatus(2);
							nicknameToPlayer.get(request.getTo()).setStatus(2);
							out.writeObject(new Protocol(7, request.getFrom(), request.getTo(), nicknameToPlayer.get(request.getFrom())));
							out.flush();
							
							// HashMap�� �÷��̾� �̸��� key�� ����� OutputStream�� ã��
							sockets.get(request.getFrom()).writeObject(new Protocol(6, request.getFrom(), request.getTo(), player));
							sockets.get(request.getFrom()).flush();
							inBattle.put(request.getFrom(), request.getTo());
							inBattle.put(request.getTo(), request.getFrom());
					}
					
					// ��밡 ����� �������� ��
					if(request.getContent().equals("cancel")) {
						// ����� ��û�� ���� ���� �޽����� ����
						nicknameToPlayer.get(request.getFrom()).setStatus(1);
						nicknameToPlayer.get(request.getTo()).setStatus(1);
						sockets.get(request.getFrom()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), "cancel"));
						sockets.get(request.getFrom()).flush();
					}
					break;
				
				/* [type: change] ������ ���� */
				case 8:
					// �� ������ �ٸ� ����� ����� �ߺ��Ǵ��� Ȯ��
					if(playerDB.changeNickCheck(request.getPlayer().getNickname(), request.getContent())) {
						Player tmp = request.getPlayer();
						
						// ������ players�� Player�� players�� ����
						for(Player p: players) {
							if(p.getId().equals(player.getId())) {
								p.setName(tmp.getName());
								p.setNickname(tmp.getNickname());
								p.setEmail(tmp.getEmail());
								p.setSite(tmp.getSite());
								p.setStatus(tmp.getStatus());
							}
						}
						for(Player p: Player.players) {
							if(p.getId().equals(tmp.getId())) {
								p.setName(tmp.getName());
								p.setNickname(tmp.getNickname());
								p.setEmail(tmp.getEmail());
								p.setSite(tmp.getSite());
								p.setStatus(tmp.getStatus());
							}
						}
						
						// �� Ŭ���̾�Ʈ�� Player ��ü ����
						player.setName(tmp.getName());
						player.setNickname(tmp.getNickname());
						player.setEmail(tmp.getEmail());
						player.setSite(tmp.getSite());
						player.setStatus(tmp.getStatus());
						
						// ����� Player, ����� OutputStream�� map �ϴ� HashMap ����
						nicknameToPlayer.remove(request.getContent());
						nicknameToPlayer.put(player.getNickname(), player);
						sockets.remove(request.getContent());
						sockets.put(player.getNickname(), out);
						
						// ���ǿ� �ִ� ������� ��� ��Ͽ��� ������ �����ٰ� �� ������ �߰���Ŵ
						for(Entry<String, ObjectOutputStream> e1:sockets.entrySet()) {
					    	if(nicknameToPlayer.get(e1.getKey()).getStatus()==1) {
								e1.getValue().writeObject(new Protocol(14, request.getContent()));
								e1.getValue().flush();
					    	}
					    }
						
						for(Entry<String, ObjectOutputStream> e:sockets.entrySet()) {
							e.getValue().writeObject(new Protocol(12, player.getNickname()));
							e.getValue().flush();
						}
						
						// �����ߴٴ� �޽��� ����
						out.writeObject(new Protocol(8, "success", player));
					}
					
					// �ߺ��Ǹ� fail �޽����� ����
					else {
						out.writeObject(new Protocol(8, "fail", player));
					}
					break;
					
				/* [type: ready] ���� client�� ������ �غ� ��ư�� �����ٰ� �˸� */
				case 9:
						out.writeObject(new Protocol(9, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(9, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					break;
					
				/* [type: play] ���� client�� ���� ��� ���� ���Ҵ��� �˸� */
				case 10:
						out.writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					break;
					
				/* ������ ������ �� ó���� ���� */
				case 11:
					// ��� ������ Ȯ���ϴ� HashMap�� ���
					inBattle.clear();
					
						// �̰��� ��
						if(request.getContent().equals("win")) {
							player.setCountWin(player.getCountWin() + 1);
							System.out.println(">> win: " + player.getCountWin());
							player.setStatus(1);
							
							nicknameToPlayer.remove(player.getNickname());
							nicknameToPlayer.put(player.getNickname(), player);
							System.out.println(">> the number of wins: " + player.getCountWin());
							for(String name:nicknameToPlayer.keySet()) {
								System.out.println(name);
							}
						}
						
						// ���� ��
						else if(request.getContent().equals("lose")) {
							player.setCountLose(player.getCountLose() + 1);
							System.out.println(">> lose: " + player.getCountLose());
							player.setStatus(1);
							
							nicknameToPlayer.remove(player.getNickname());
							nicknameToPlayer.put(player.getNickname(), player);
							System.out.println(">> the number of loses: " + player.getCountLose());
							for(String name:nicknameToPlayer.keySet()) {
								System.out.println(name);
							}
						}
						
						// ����� ��
						else if(request.getContent().equals("draw")){
							player.setCountDraw(player.getCountDraw() + 1);
							System.out.println("draw: " + player.getCountDraw());
							player.setStatus(1);
							
							nicknameToPlayer.remove(player.getNickname());
							nicknameToPlayer.put(player.getNickname(), player);
							System.out.println(">> the number of draws: " + player.getCountDraw());
						}
						
						int idx = playerDB.getPlayerIndex(player.getId());
						Player.players.set(idx, player);
						Player.updateInformation();
						Player.setInformation();
					break;
					
				/* [type: wait] ������ �α��� �߰ų� ������ ������ ���ǿ� �������� �� ó���ϴ� ���� */
				case 12:
						// �̹� �������ִ� ������� ����� ����
						for(Player p:players) {
							out.writeObject(new Protocol(12, p.getNickname()));
							out.flush();
						}
						// �ٸ� ����鿡�� ���� ������ ����� ������ ����
						for(Entry<String, ObjectOutputStream> e:sockets.entrySet()) {
							e.getValue().writeObject(new Protocol(12, player.getNickname()));
							e.getValue().flush();
						}
					break;
					
				/* [type: allchat] ���ǿ� �ִ� ������� ä�� */
				case 13:
					// ���ǿ� �ִ� ��� ������� ä���� ����
					for(Entry<String, ObjectOutputStream> e:sockets.entrySet()) {
						if(nicknameToPlayer.get(e.getKey()).getStatus()==1) {
							e.getValue().writeObject(new Protocol(13, request.getFrom(), request.getTo(), request.getContent()));
							e.getValue().flush();
						}
					}
					break;
					
				/* [type: logout] �α׾ƿ� ���� �� ó���� ���� */
				case 14:
					// �α��� �߾����� Ȯ��
					if(player!=null) {
						// Player�� players�� update
						for(Player p: Player.players) {
							if(p.getId().equals(player.getId())) p = player;
						}
						// player_information ������ update
						Player.updateInformation();
						// ������ ������� ��Ͽ��� �� ����ڸ� ����
						removePlayer(player);
						// �������ִ� ����� �� ���ǿ� �ִ� ������� ��� ��Ͽ��� �� ����ڸ� ����
					    for(Entry<String, ObjectOutputStream> e1:sockets.entrySet()) {
					    	if(nicknameToPlayer.get(e1.getKey()).getStatus()==1) {
					    		try {
									e1.getValue().writeObject(new Protocol(14, player.getNickname()));
									e1.getValue().flush();
								} catch (Exception e2) {
									e2.printStackTrace();
								}
					    	}
					    }
					    player = null;
					}
					break;
				default:
					break;
				}
			}
		}catch(SocketException e) {
			// ������ ����ų� â�� �ݾ��� ��: SocketException �߻�
			// logout ���� ���� �Ȱ��� ó����
			if(player!=null) {
				for(Player p: Player.players) {
					if(p.getId().equals(player.getId())) p = player;
				}
				
			    if(!inBattle.isEmpty()) {
			    	try {
						sockets.get(inBattle.get(player.getNickname())).writeObject(new Protocol(11, player.getNickname()));
						sockets.get(inBattle.get(player.getNickname())).flush();
						inBattle.clear();
						player.setCountDraw(player.getCountDraw()+1);
						
						int idx = playerDB.getPlayerIndex(player.getId());
						Player.players.set(idx, player);
						request.setPlayer(player);
						
						Player.updateInformation();
						Player.setInformation();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
			    }
				Player.updateInformation();
			    removePlayer(player);
			    
			    for(Entry<String, ObjectOutputStream> e1: sockets.entrySet()) {
			    	if(nicknameToPlayer.get(e1.getKey()).getStatus()==1) {
			    		try {
							e1.getValue().writeObject(new Protocol(14, player.getNickname()));
							e1.getValue().flush();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
			    	}
			    }
			    player = null;
			}
		}catch(Exception e) {
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
	
	/**
	 * [method main] ����
	 */
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
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}