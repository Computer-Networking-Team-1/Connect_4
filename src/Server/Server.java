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
	private static Player playerDB = new Player();	// ArrayList, File을 이용한 플레이어 데이터베이스
	
	// 현재 접속 중인 사람들의 Player, OutputStream, Nickname
	public static ArrayList<Player> players = new ArrayList<Player>();
	public static HashMap<String, ObjectOutputStream> sockets = new HashMap<String, ObjectOutputStream>();
	public static HashMap<String, Player> nicknameToPlayer = new HashMap<String, Player>();	// 대기실 구현 위해
	private Player player = null;
	private Socket socket = null;
	
	// 해당 클라이언트가 현재 대결 중인지 확인하는 HashMap
	private HashMap<String, String> inBattle;
	
	/**
	 * [Constructor Server]
	 */
	public Server(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * [method addPlayer] 접속한 사람을 목록에 추가
	 * @param player
	 * @param outToClient
	 */
	public synchronized void addPlayer(Player p, ObjectOutputStream outToClient) {
		sockets.put(p.getNickname(), outToClient);
		nicknameToPlayer.put(p.getNickname(), p);
		players.add(p);
	}
	
	/**
	 * [method removePlayer] 연결이 끊기거나 로그아웃하는 사람을 목록에서 제거
	 * @param player
	 */
	public synchronized void removePlayer(Player p) {
		sockets.remove(p.getNickname());
		nicknameToPlayer.remove(p.getNickname());
		players.remove(p);
	}
	
	/**
	 * [method run] thread 실행
	 */
	public void run() {
		System.out.println(">> " + socket);
		Protocol request = null;
		ObjectInputStream in = null;	// 클라이언트로부터 객체를 읽어오기 위해
		ObjectOutputStream out = null;	// 클라이언트로 객체를 내보내기 위해
		inBattle = new HashMap<String, String>();
		
		try {
			in = new ObjectInputStream(this.socket.getInputStream());
			out = new ObjectOutputStream(this.socket.getOutputStream());
			
			while(true) {	// 클라이언트에서 오는 메시지를 기다렸다가 반응해서 응답
				request = (Protocol) in.readObject();	// 클라이언트로부터 프로토콜 객체 전달받기
				System.out.println(">> type: " + request.getType() + " from: " + request.getFrom() + " to: " + request.getTo() + " content: " + request.getContent() + " player: " + request.getPlayer() + " users: " + players.size());
				
				int type = request.getType();	// 타입 저장
				Player temp = null;
				String tempSalt = null;
				String tempPassword = null;
				
				switch(type) {

				/* [type: login] outputStream 연결 */
				case 1:
					Player.setInformation();
					temp = (Player) request.getPlayer();	// 아이디와 패스워드 정보가 담긴 객체 전달받기
					tempSalt = playerDB.getSalt(temp.getId());
					tempPassword = hashing(temp.getPasswordByte(), tempSalt);
					
					System.out.println(">> " + socket.getLocalAddress().toString());
					
					// 로그인 성공
					if(playerDB.check(temp.getId(), tempPassword, socket.getLocalAddress().toString())) {
						System.out.println(">> login success");
						player = playerDB.getPlayerById(temp.getId());
						player.setStatus(1);
						addPlayer(player, out);
						
						out.writeObject(new Protocol(1, "SUCCESS", player));	// 클라이언트에게 성공했다는 메시지 전송
						out.flush();
						
						int idx = playerDB.getPlayerIndex(temp.getId());
						Player.players.set(idx, player);
					}
					// 로그인 실패
					else {
						System.out.println(">> login failure");
						out.writeObject(new Protocol(1, request.getFrom(), request.getTo(), "FAIL"));	// 클라이언트에게 실패했다는 메시지 전송
						out.flush();
					}
					break;
					
				/* [type: sign up] 플레이어 목록에 추가 */
				case 2:
					Player.setInformation();
					temp = (Player) request.getPlayer();

					if(playerDB.idCheck(temp.getId())) {	// 아이디 중복 확인
						if(playerDB.nickCheck(temp.getNickname())) {	// 별명이 중복되는지 확인
								tempSalt = createSalt();
								tempPassword = hashing(temp.getPasswordByte(), tempSalt);
								playerDB.setPlayer(temp.getId(), tempPassword, tempSalt, temp);
								Player.updateInformation();	// 플레이어 정보 업데이트
								
								out.writeObject(new Protocol(2, null, null, "success"));	// 클라이언트에게 성공했다는 메시지 전송
								System.out.printf(">> Sign Up %d %s (# of users: %d)\n", request.getType(), request.getFrom(), players.size());
						}
						else {	// 별명이 중복되면 중복된 것이 별명임을 알리는 메시지 전송
							out.writeObject(new Protocol(2, null, null, "nick"));
						}
					}
					else {	// 아이디가 중복되면 중복된 것이 아이디임을 알리는 메시지 전송
						out.writeObject(new Protocol(2, null, null, "id"));
					}
					break;
					
				/* [type: chat] 게임 중인 양쪽 클라이언트에 채팅한 내용을 메시지로 보냄 */
				case 3:
					System.out.println(request.getContent());
						out.writeObject(new Protocol(3, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						
						// HashMap에 플레이어 이름을 key로 사용해 OutputStream을 찾음
						sockets.get(request.getTo()).writeObject(new Protocol(3, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					break;
					
				/* [type: challenge] 대결을 신청한 상대가 있으면 양쪽 client에 대결을 하라는 response 전송 */
				case 4:
					// 상대가 게임 중이 아니면 대결 신청 메시지를 보냄
					if(nicknameToPlayer.get(request.getTo()).getStatus() == 1) {
						sockets.get(request.getTo()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), "invite"));
						sockets.get(request.getTo()).flush();
					}
					// 상대가 게임 중이면 게임 중이라는 메시지를 보냄
					else {
						out.writeObject(new Protocol(4, request.getFrom(), request.getTo(), "in game"));
						out.flush();
					}
					break;
					
				/* [type: information] 전적 검색한 상대의 Player 객체를 전송 */
				case 5:
					out.writeObject(new Protocol(5, nicknameToPlayer.get(request.getTo())));
					break;
					
				case 6:
					break;
				
				/* [type: invited] 대결 신청을 받은 사람의 행동을 처리 */
				case 7:
					// 상대가 대결을 수락했을 때
					if(request.getContent().equals("confirm")) {
							// 양쪽 플레이어에 대결을 하라는 메시지를 보냄
							nicknameToPlayer.get(request.getFrom()).setStatus(2);
							nicknameToPlayer.get(request.getTo()).setStatus(2);
							out.writeObject(new Protocol(7, request.getFrom(), request.getTo(), nicknameToPlayer.get(request.getFrom())));
							out.flush();
							
							// HashMap에 플레이어 이름을 key로 사용해 OutputStream을 찾음
							sockets.get(request.getFrom()).writeObject(new Protocol(6, request.getFrom(), request.getTo(), player));
							sockets.get(request.getFrom()).flush();
							inBattle.put(request.getFrom(), request.getTo());
							inBattle.put(request.getTo(), request.getFrom());
					}
					
					// 상대가 대결을 거절했을 때
					if(request.getContent().equals("cancel")) {
						// 대결을 신청한 측에 거절 메시지를 보냄
						nicknameToPlayer.get(request.getFrom()).setStatus(1);
						nicknameToPlayer.get(request.getTo()).setStatus(1);
						sockets.get(request.getFrom()).writeObject(new Protocol(4, request.getFrom(), request.getTo(), "cancel"));
						sockets.get(request.getFrom()).flush();
					}
					break;
				
				/* [type: change] 정보를 수정 */
				case 8:
					// 새 별명이 다른 사람의 별명과 중복되는지 확인
					if(playerDB.changeNickCheck(request.getPlayer().getNickname(), request.getContent())) {
						Player tmp = request.getPlayer();
						
						// 서버의 players와 Player의 players를 수정
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
						
						// 이 클라이언트의 Player 객체 수정
						player.setName(tmp.getName());
						player.setNickname(tmp.getNickname());
						player.setEmail(tmp.getEmail());
						player.setSite(tmp.getSite());
						player.setStatus(tmp.getStatus());
						
						// 별명과 Player, 별명과 OutputStream을 map 하는 HashMap 수정
						nicknameToPlayer.remove(request.getContent());
						nicknameToPlayer.put(player.getNickname(), player);
						sockets.remove(request.getContent());
						sockets.put(player.getNickname(), out);
						
						// 대기실에 있는 사람들의 대기 목록에서 별명을 지웠다가 새 별명을 추가시킴
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
						
						// 성공했다는 메시지 전송
						out.writeObject(new Protocol(8, "success", player));
					}
					
					// 중복되면 fail 메시지를 보냄
					else {
						out.writeObject(new Protocol(8, "fail", player));
					}
					break;
					
				/* [type: ready] 양쪽 client에 누군가 준비 버튼을 눌렀다고 알림 */
				case 9:
						out.writeObject(new Protocol(9, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(9, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					break;
					
				/* [type: play] 양쪽 client에 누가 어디에 돌을 놓았는지 알림 */
				case 10:
						out.writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						out.flush();
						sockets.get(request.getTo()).writeObject(new Protocol(10, request.getFrom(), request.getTo(), request.getContent()));
						sockets.get(request.getTo()).flush();
					break;
					
				/* 게임이 끝났을 때 처리할 내용 */
				case 11:
					// 대결 중인지 확인하는 HashMap을 비움
					inBattle.clear();
					
						// 이겼을 때
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
						
						// 졌을 때
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
						
						// 비겼을 때
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
					
				/* [type: wait] 누군가 로그인 했거나 게임을 끝내서 대기실에 입장했을 때 처리하는 내용 */
				case 12:
						// 이미 접속해있던 사람들의 목록을 전송
						for(Player p:players) {
							out.writeObject(new Protocol(12, p.getNickname()));
							out.flush();
						}
						// 다른 사람들에게 새로 접속한 사람의 별명을 전송
						for(Entry<String, ObjectOutputStream> e:sockets.entrySet()) {
							e.getValue().writeObject(new Protocol(12, player.getNickname()));
							e.getValue().flush();
						}
					break;
					
				/* [type: allchat] 대기실에 있는 사람들의 채팅 */
				case 13:
					// 대기실에 있는 모든 사람에게 채팅을 보냄
					for(Entry<String, ObjectOutputStream> e:sockets.entrySet()) {
						if(nicknameToPlayer.get(e.getKey()).getStatus()==1) {
							e.getValue().writeObject(new Protocol(13, request.getFrom(), request.getTo(), request.getContent()));
							e.getValue().flush();
						}
					}
					break;
					
				/* [type: logout] 로그아웃 했을 때 처리할 내용 */
				case 14:
					// 로그인 했었는지 확인
					if(player!=null) {
						// Player의 players를 update
						for(Player p: Player.players) {
							if(p.getId().equals(player.getId())) p = player;
						}
						// player_information 파일을 update
						Player.updateInformation();
						// 접속한 사람들의 목록에서 이 사용자를 삭제
						removePlayer(player);
						// 접속해있는 사람들 중 대기실에 있는 사람들의 대기 목록에서 이 사용자를 삭제
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
			// 접속이 끊기거나 창을 닫았을 때: SocketException 발생
			// logout 했을 때와 똑같이 처리함
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
	
	/**
	 * [method main] 실행
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