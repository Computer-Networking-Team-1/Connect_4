package Client;

import GUI.*;
import Server.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;

/**
 * [class GameStart] 게임 실행
 */
public class GameStart {
	public static void main(String[] args) throws Exception {
		new Launch();
	}
}

/**
 * [class Launch]
 */
class Launch {
	public final static String SERVER_IP = "127.0.0.1";
	public final static int SERVER_PORT = 9999;
	
	Login login;
	SignUp signUp;
	WaitingRoom waitingRoom;
	ChangeInfo changeinfo;
	Game game;
	Result result;
	Invite invite;
	
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	
	// 해당 클라이언트의 Player와 별명
	String nickname;
	Player player;
	
	boolean myTurn;		// 참일 때 내 차례
	boolean imReady;	// 참일 때 준비됐음을 의미
	boolean urReady;	// 참일 때 상대방이 준비됐음을 의미
	boolean begin;		// 참일 때 이미 게임이 시작되었음을 의미
	char[][] board;		// 누가 이겼는지 판단
	
	/**
	 * [Constructor Launch]
	 */
	public Launch() throws Exception {
		login = new Login();
		init();	// 서버와 연결하는 함수
	}

	/**
	 * [method setNickname] 닉네임 설정
	 * @param nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * [method init] 서버 연결
	 */
	public void init() {
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
			out = new ObjectOutputStream(socket.getOutputStream());

			/* Receiver 실행 */
			Thread receiver = new Thread(new ClientReceiver(socket));
			receiver.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * [class Login] 로그인
	 */
	class Login extends GUI.Login implements ActionListener {

		/**
		 * [Constructor Login]
		 */
		public Login() throws Exception {
			this.setVisible(true);
			this.error.setText(null);
			this.btn.btn1.addActionListener(this); // 로그인 버튼
			this.btn.btn2.addActionListener(this); // 회원가입 버튼
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();

			// 로그인 버튼 눌렀을 때
			if (obj == this.btn.btn1) {
				try {
					String id = idBar.infoField.getText();
					char[] temp = passwordBar.infoField.getPassword();
					String password = new String(temp);
					Player player = new Player(id, password.getBytes());
					
					out.writeObject(new Protocol(1, player));
					out.flush();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}

			// 회원가입 버튼 눌렀을 때
			else if (obj == this.btn.btn2) {
				this.setVisible(false);	// 로그인 화면을 숨김

				try {
					signUp = new SignUp(); // 회원가입하기 위한 클래스 호출
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * [class SignUp] 플레이어 계정 생성 (회원가입)
	 */
	class SignUp extends GUI.SignUp implements ActionListener {
		
		/**
		 * [Constructor SignUp]
		 */
		public SignUp() throws Exception {
			this.setVisible(true);
			this.error.setText(null);
			this.btn.btn1.addActionListener(this);	// 확인 버튼
			this.btn.btn2.addActionListener(this);	// 취소 버튼
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			// 확인 버튼 눌렀을 때
			if (obj == this.btn.btn1) {
				try {
					String id = idBar.infoField.getText();
					char[] temp1 = passwordBar.infoField.getPassword();
					String password = new String(temp1);
					char[] temp2 = rePasswordBar.infoField.getPassword();
					String rePassword = new String(temp2);
					String name = nameBar.infoField.getText();
					String nickname = nicknameBar.infoField.getText();
					String email = emailBar.infoField.getText();
					String site = siteBar.infoField.getText();
					Player player = new Player(id, password.getBytes(), name, nickname, email, site);
					
					// 아이디가 비어있을 때
					if(id.equals(null)) {
						this.error.setText("ID가 입력되지 않았습니다");
					}
					// 비밀번호가 비어있을 때
					else if(passwordBar.infoField.getPassword().equals(null) || rePasswordBar.infoField.getPassword().equals(null)) {
						this.error.setText("비밀번호가 입력되지 않았습니다");
					}
					// 비밀번호가 일치하지 않을 때
					else if(!password.equals(rePassword)) {
						this.error.setText("비밀번호가 일치하지 않습니다");
					}
					// sign up 메시지 전송
					else {
						out.writeObject(new Protocol(2, player));
						out.flush();
					}
					
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			// 취소 버튼 눌렀을 때
			else if (obj == this.btn.btn2) {
				this.dispose();
				try {
					login.setVisible(true); // 회원가입을 취소하였으므로 다시 로그인 화면으로 돌아감
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * [class WaitingRoom] 전체 대기실
	 */
	class WaitingRoom extends GUI.WaitingRoom implements ActionListener {

		public WaitingRoom() {
			player.setStatus(1);
			this.setVisible(true);
			// 채팅창의 내용과 대기 목록을 비움
			chatWindow.Contents.removeAllElements();
			currentUsers.content.removeAllElements();
			
			// 대기실에 입장했다는 메세지를 전송
			try {
				out.writeObject(new Protocol(12, nickname));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentUsers.userStatus.setText(null);
			chat.enter.addActionListener(this);
			currentUsers.userBtn.btn1.addActionListener(this);
			currentUsers.userBtn.btn2.addActionListener(this);
			currentUsers.miscBtn.btn1.addActionListener(this);
			currentUsers.miscBtn.btn2.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			// enter 버튼을 눌렀을 때
			if(obj==this.chat.enter) {
				// 채팅 입력창을 비우고 채팅 내용을 메세지로 전송
				try {
					out.writeObject(new Protocol(13, nickname, "server", this.chat.chatCon.getText()));
					out.flush();
					this.chat.chatCon.setText(null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// 나가기 버튼을 눌렀을 때
			else if(obj==this.currentUsers.miscBtn.btn2) {
				// 창을 닫고 로그아웃 한다는 메세지를 전송
				dispose();
				try {
					out.writeObject(new Protocol(14, nickname));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// 로그인 화면으로 되돌아감
				login.setVisible(true);
			}
			// 정보 수정 버튼을 눌렀을 때
			else if(obj==this.currentUsers.miscBtn.btn1) {
				player.setStatus(2);	// 대결을 신청할 수 없는 상태로 바꾸고
				changeinfo = new ChangeInfo();	// 정보 수정 화면을 open
			}
			// 대결을 신청할 수 있는 상태고 대결 신청 버튼을 눌렀을 때
			else if(obj == this.currentUsers.userBtn.btn1 && player.getStatus() == 1) {
				try {
					// 선택한 사람의 별명을 메시지로 전송
					out.writeObject(new Protocol(4, nickname, currentUsers.users.getSelectedValue().toString()));
					out.flush();
					player.setStatus(2);
				} catch (IOException e1) { e1.printStackTrace(); }
			}
			// 전적 확인 버튼을 눌렀을 때
			else if(obj == this.currentUsers.userBtn.btn2) {
				try {
					// 선택한 사람의 별명을 메세지로 전송
					out.writeObject(new Protocol(5, nickname, currentUsers.users.getSelectedValue().toString()));
					out.flush();
				} catch (Exception e1) { e1.printStackTrace(); }
			}
		}
	}

	/**
	 * [class ChangeInfo] 정보 수정
	 */
	class ChangeInfo extends GUI.SignUp implements ActionListener {
		Player tmp;
		String oldNick;
		
		public ChangeInfo() {
			this.setVisible(true);
			this.error.setText(null);
			this.idBar.infoField.setText(player.getId());
			this.idBar.infoField.setEditable(false);
			this.passwordBar.infoField.setEditable(false);
			this.rePasswordBar.infoField.setEditable(false);
			this.nameBar.infoField.setText(player.getName());
			this.nicknameBar.infoField.setText(player.getNickname());
			this.emailBar.infoField.setText(player.getEmail());
			this.siteBar.infoField.setText(player.getSite());
			this.btn.btn1.addActionListener(this);
			this.btn.btn2.addActionListener(this);
			tmp = player;
			oldNick = player.getNickname();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			// cancel 버튼을 눌렀을 때
			if(obj == this.btn.btn2) {
				// 정보 수정 화면을 닫음
				this.dispose();
			}
			// confirm 버튼을 눌렀을 때
			else if(obj == this.btn.btn1) {
				// 임시로 Player 객체를 만들어서 원래 별명과 함께 메세지로 전송
				tmp.setName(this.nameBar.infoField.getText());
				tmp.setNickname(this.nicknameBar.infoField.getText());
				tmp.setEmail(this.emailBar.infoField.getText());
				tmp.setSite(this.siteBar.infoField.getText());
				tmp.setStatus(1);
				try {
					out.writeObject(new Protocol(8, oldNick, tmp));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * [class Game] 게임 실행
	 */
	class Game extends GameRoom implements ActionListener {
		int[] tops;

		public Game(Player user, Player opponent) throws Exception {
			this.setVisible(true);
			// 내 정보 표시
			this.user.nickname.setText(user.getNickname());
			this.user.profile.setText("<html>승: " + user.getCountWin() + "<br/>"
			+ "패: " + user.getCountLose() + "<br/>"
			+ "무: " + user.getCountDraw() + "</html>");
			this.user.readyBtn.addActionListener(this);
			this.user.readyBtn.setText("Not Ready");
			// 상대의 정보 표시
			this.opponent.nickname.setText(opponent.getNickname());
			this.opponent.profile.setText("<html>승: " + opponent.getCountWin() + "<br/>"
			+ "패: " + opponent.getCountLose() + "<br/>"
			+ "무: " + opponent.getCountDraw() + "</html>");
			this.opponent.readyStatus.setText("Not Ready");
			chat.enter.addActionListener(this);
			board = new char[6][7];
			tops = new int[7];
			imReady = false;
			urReady = false;
			begin = false;
			for (int i = 0; i < 7; i++) {
				tops[i] = 0;
				gameBoard.btn[i].addActionListener(this);

				for (int j = 0; j < 6; j++) {
					board[j][i] = '0';
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			// enter 버튼을 눌렀다면 type이 chat인 메시지를 서버에 보냄
			if (obj == chat.enter) {
				try {
					out.writeObject(new Protocol(3, user.nickname.getText(), opponent.nickname.getText(),
							chat.chatCon.getText()));
					out.flush();
					chat.chatCon.setText(null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// 게임판 위의 버튼을 눌렀고 자기 차례인 경우 type이 play고 content가 그 버튼의 index인 메시지를 서버에 보냄
			else if (Arrays.stream(gameBoard.btn).anyMatch(obj::equals) && myTurn == true && begin == true) {
				int b = Arrays.asList(gameBoard.btn).indexOf(obj);

				if (tops[b] < 6) {
					try {
						// 차례를 종료
						myTurn = false;
						out.writeObject(new Protocol(10, user.nickname.getText(), opponent.nickname.getText(),
								Integer.toString(b)));
						out.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			// 준비 버튼을 눌렀고 게임이 시작하지 않은 상태라면 type이 ready인 메시지를 보냄
			else if (obj == user.readyBtn && begin == false) {
				try {
					// 준비한 상태가 아니라면 준비하겠다는 content
					if (!imReady) {
						out.writeObject(new Protocol(9, nickname, opponent.nickname.getText(), "do"));
						out.flush();
					}
					// 준비한 상태라면 되돌리겠다는 content
					else {
						out.writeObject(new Protocol(9, nickname, opponent.nickname.getText(), "undo"));
						out.flush();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * [class Result] 결과 표시
	 */
	class Result extends Notice1 implements ActionListener{
		String flag;
		
		/**
		 * [Constructor Result]
		 */
		public Result(String s) {
			this.flag = null;
			this.setVisible(true);
			this.Notice.setText(s);
			this.Btn.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// 이겼을 경우
			if(flag.equals("win")) {
				// 이겼다는 메시지를 전송하고 승리 횟수를 늘림
				try {
					out.writeObject(new Protocol(11, nickname, game.opponent.nickname.getText(), "win"));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				/* 게임 화면을 닫고 초기 화면으로 되돌아감 */
				game.dispose();
				this.dispose();
				player.setCountWin(player.getCountWin()+1);
				waitingRoom = new WaitingRoom();
				player.setStatus(1);
			}
			// 졌을 경우
			else if(flag.equals("lose")) {
				// 졌다는 메세지를 전송하고 패배 횟수를 늘림
				try {
					out.writeObject(new Protocol(11, nickname, game.opponent.nickname.getText(), "lose"));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				/* 게임 화면을 닫고 초기 화면으로 되돌아감 */
				game.dispose();
				this.dispose();
				player.setCountLose(player.getCountLose()+1);
				waitingRoom = new WaitingRoom();
				player.setStatus(1);
			}
			// 비겼을 경우
			else if(flag.equals("draw")) {
				//비겼다는 메세지를 전송하고 무승부 횟수를 늘림
				try {
					out.writeObject(new Protocol(11, nickname, game.opponent.nickname.getText(), "draw"));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				/* 게임 화면을 닫고 초기 화면으로 되돌아감 */
				game.dispose();
				this.dispose();
				player.setCountDraw(player.getCountDraw()+1);
				waitingRoom = new WaitingRoom();
				player.setStatus(1);
			}
		}
		// 승패 여부를 확인
		public void setFlag(String flag) {
			this.flag = flag;
		}
	}
	
	/**
	 * [class Invite] 게임 신청 받았을 때
	 */
	class Invite extends Notice2 implements ActionListener{
		String oppo;
		public Invite(String s){
			this.setVisible(true);
			this.oppo = s;
			this.Notice.setText(s + "가 대결을 신청했습니다");
			this.Btns.btn1.addActionListener(this);
			this.Btns.btn2.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			// 거절 버튼을 눌렀을 때
			if(obj == Btns.btn2) {
				try {
					// 거절한다는 메시지를 전송
					out.writeObject(new Protocol(7, oppo, nickname, "cancel"));
					out.flush();
					player.setStatus(1);
					dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// 수락 버튼을 눌렀을 때
			else if(obj == Btns.btn1) {
				try {
					//수락한다는 메시지를 전송
					out.writeObject(new Protocol(7, oppo, nickname, "confirm"));
					out.flush();
					dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * [class ClientReceiver] 서버에서 오는 메시지를 계속 받아서 읽고 반응, 받는 메시지에 대한 응답 선언
	 */
	class ClientReceiver extends Thread {
		Protocol response = null;

		/**
		 * [Constructor ClientReceiver]
		 */
		public ClientReceiver(Socket socket) {
			try {
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		public void run() {
			while (in != null) {
				try {
					/* 서버가 보내는 메시지를 계속해서 읽음 */
					response = (Protocol) in.readObject();

					/* 서버가 보내는 메시지를 표시 */
					System.out.println("type: " + response.getType() + ", from: " + response.getFrom() + ", to: "
							+ response.getTo() + ", content: " + response.getContent());

					int type = response.getType();
					switch(type) {
					
					/* [type: login] */
					case 1:
						if(response.getContent().equals("SUCCESS")) {	// success
							player = response.getPlayer();
							nickname = player.getNickname();
							System.out.println(">> go waiting room");
							waitingRoom = new WaitingRoom();
							login.setVisible(false);
						}
						// 로그인에 실패했을 경우
						else {
							login.error.setText("존재하지 않는 계정입니다");
						}
						break;
						
					/* [type: sign up] 회원가입 신청을 했을 때 받는 메세지를 처리 */
					case 2:
						// 아이디가 중복되었다는 메시지를 받았을 때
						if(response.getContent().equals("id")) {
							signUp.error.setText("이미 사용 중인 ID입니다");
						}
						// 별명이 중복되었다는 메시지를 받았을 때
						else if(response.getContent().equals("nick")) {
							signUp.error.setText("이미 사용 중인 별명입니다");
						}
						// 성공했을 때
						else {
							// 회원가입 화면을 닫고 로그인 화면으로 돌아감
							signUp.dispose();
							login.setVisible(true);
						}
						
					/* [type: chat] 게임 중 누군가 채팅을 입력했을 때 */
					case 3:
						/* 채팅창에 채팅 내용을 표시함 */
						game.chatWindow.Contents.addElement("[" + response.getFrom() + "]" + response.getContent());
						break;
						
					/* [type: challenge] 대결 신청과 관련된 메세지를 처리 */
					case 4:
						// 대결 신청을 받았을 때
						if(response.getContent().equals("invite")) {
							//대결을 신청할 수 없는 상태로 만들고 대결 신청 화면을 띄움
							player.setStatus(2);
							invite = new Invite(response.getFrom());
						}
						// 상대가 대결을 거절했을 때
						if(response.getContent().equals("cancel")) {
							player.setStatus(1);
							waitingRoom.currentUsers.userStatus.setText("상대가 대결을 거절했습니다");
						}
						// 상대가 이미 대결 중일 때
						if(response.getContent().equals("in game")) {
							player.setStatus(1);
							waitingRoom.currentUsers.userStatus.setText("상대가 현재 대결 중입니다");
						}
						break;
					
					/* [type: information] 상대의 전적을 확인 */
					case 5:
						Player tmp = response.getPlayer();
						String[] log = tmp.getLog();
						waitingRoom.currentUsers.userStatus.setText("<html>" + tmp.getNickname() + "<br/>"
								+ "플레이 횟수: " + tmp.getTotalCount() + "<br/>"
								+ "승: " + tmp.getCountWin() + "<br/>"
								+ "패: " + tmp.getCountLose() + "<br/>"
								+ "무: " + tmp.getCountDraw() + "<br/>"
								+ "마지막 접속: " + log[1] + "</html>");
						break;
					
					/* [type: invite] 게임을 신청했을 때 게임을 실행함 */
					case 6:
						myTurn = false; // 게임을 실행함
						game = new Game(player, response.getPlayer());
						game.gameBoard.turn.setText("준비 중");
						waitingRoom.dispose();
						player.setStatus(2);
						break;
						
					/* [type: invited] 게임을 신청받았을 때 */
					case 7:
						myTurn = true; // 게임을 실행함
						game = new Game(player, response.getPlayer());
						game.gameBoard.turn.setText("준비 중");
						waitingRoom.dispose();
						player.setStatus(2);
						break;
					
					/* [type: change] 정보를 수정할 때 */
					case 8:
						// 별명이 중복되지 않을 경우
						if(response.getContent().equals("success")) {
							//이 클라이언트의 Player 객체와 별명을 수정
							player = response.getPlayer();
							nickname = player.getNickname();
							changeinfo.dispose();
						}
						else {
							changeinfo.error.setText("이미 사용 중인 별명입니다");
						}
						break;
					
					/* [type: ready] 게임 중 누군가 준비 버튼을 눌렀을 때 */
					case 9:
						/* 상대가 준비 버튼을 눌렀다면 */
						if (response.getFrom().equals(game.opponent.nickname.getText())) {
							/* 상대가 준비 상태에 들어가는 거라면 */
							if (response.getContent().equals("do")) {
								game.opponent.readyStatus.setText("Ready");
								urReady = true;
								/* 자신이 이미 준비 상태였다면 */
								if (imReady == true) {
									/* 게임을 시작 */
									if (myTurn) {
										game.gameBoard.turn.setText("당신의 차례입니다");
									} else {
										game.gameBoard.turn.setText("상대의 차례입니다");
									}
									begin = true;
								}
							}
							/* 상대가 준비 상태를 해제하는 거라면 */
							else {
								game.opponent.readyStatus.setText("Not Ready");
								urReady = false;
							}
						}
						/* 내가 준비 버튼을 눌렀다면 */
						else {
							/* 내가 준비 상태에 들어가는 거라면 */
							if (response.getContent().equals("do")) {
								imReady = true;
								game.user.readyBtn.setText("Ready");
								/* 상대가 이미 준비한 상태라면 */
								if (urReady == true) {
									/* 게임을 시작 */
									if (myTurn) {
										game.gameBoard.turn.setText("당신의 차례입니다");
									} else {
										game.gameBoard.turn.setText("상대의 차례입니다");
									}
									begin = true;
								}
							}
							/* 내가 준비 상태를 해제하는 거라면 */
							else {
								game.user.readyBtn.setText("Not Ready");
								imReady = false;
							}
						}
						break;
						
					/* [type: play] */
					case 10:
						/* 내가 돌을 놓았다면 */
						if (response.getFrom().equals(nickname)) {
							int num = Integer.parseInt(response.getContent());
							game.gameBoard.cell[game.tops[num]][num].setIcon(game.gameBoard.yellow);
							board[game.tops[num]][num] = '1';
							char c = '1';
							/* 이겼는지 판단하고 이겼다면 type이 result고 content가 win인 메시지를 서버에 보냄 */
							if (isWinning(c, game.tops[num], num)) {
								result = new Result("승리했습니다");
								result.setFlag("win");
							}
							game.tops[num]++;
							// 무승부인지 확인하고 무승부라면 draw 메시지를 보냄
							if (isDraw()) {
								result = new Result("무승부입니다");
								result.setFlag("draw");
							}
							game.gameBoard.turn.setText("상대의 차례입니다");
						}
						/* 상대가 돌을 놓았다면 */
						else {
							int num = Integer.parseInt(response.getContent());
							game.gameBoard.cell[game.tops[num]][num].setIcon(game.gameBoard.red);
							board[game.tops[num]][num] = '2';
							char c = '2';
							/* 졌는지 판단하고 졌다면 type이 result고 content가 lose인 메시지를 서버에 보냄 */
							if (isWinning(c, game.tops[num], num)) {
								result = new Result("패배했습니다");
								result.setFlag("lose");
							}
							game.tops[num]++;
							// 무승부인지 확인하고 무승부라면 draw 메시지를 보냄
							if (isDraw()) {
								result = new Result("무승부입니다");
								result.setFlag("draw");
							}
							/* 상대의 차례가 끝나고 자기 차례로 */
							myTurn = true;
							game.gameBoard.turn.setText("당신의 차례입니다");
						}
						break;
					
					/* 상대가 대결 도중 연결이 끊어졌을 때 */
					case 11:
						// 무승부로 처리
						result = new Result("상대의 연결이 끊겼습니다");
						result.setFlag("draw");
						break;
					
					/* 대기실에 입장할 때, 다른 누군가가 대기실에 입장했을 때 받는 메시지 */
					case 12:
						// 다른 사람의 별명을 대기 목록에 추가함
						if(!response.getFrom().equals(nickname)&&!waitingRoom.currentUsers.content.contains(response.getFrom())) waitingRoom.currentUsers.content.addElement(response.getFrom());
						break;
					
					/* [type: all chat] 대기실에서 누군가 채팅을 보냈을 때 */
					case 13:
						// 대기실의 채팅 내용 목록에 추가함
						waitingRoom.chatWindow.Contents.addElement("[" + response.getFrom() + "] " + response.getContent());
					    break;
					
					/* [type: logout] 다른 누군가 로그아웃 했거나 연결이 끊겼을 때 */
					case 14:
						// 대기 목록에서 그 사람을 지움
						waitingRoom.currentUsers.content.removeElement(response.getFrom());
					    break;
					    
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * [method isDraw] 무승부인지 판단
	 */
	public boolean isDraw() {
		for(int i: game.tops) {
			if (i < 6) return false;
		}
		return true;
	}

	/**
	 * [method isWinning] 이겼는지 판단
	 */
	public boolean isWinning(char c, int row, int col) {
		String s = new String();
		s = s + String.valueOf(c);
		s = s + String.valueOf(c);
		s = s + String.valueOf(c);
		s = s + String.valueOf(c);
		return (horizontal(s, row) || vertical(s, col) || slash(s, row, col) || backslash(s, row, col));
	}

	/**
	 * [method horizontal]
	 */
	public boolean horizontal(String s, int row) {
		String hr = new String();
		for (char c : board[row]) {
			hr = hr + String.valueOf(c);
		}
		return (hr.contains(s));
	}

	/**
	 * [method vertical]
	 */
	public boolean vertical(String s, int col) {
		String vr = new String();
		for (int i = 0; i < 6; i++) {
			vr = vr + String.valueOf(board[i][col]);
		}
		return (vr.contains(s));
	}

	/**
	 * [method slash]
	 */
	public boolean slash(String s, int row, int col) {
		int r = row;
		int c = col;
		String sl = new String();
		while (r > 0 && c > 0) {
			r--;
			c--;
		}
		while (r < 6 && c < 7) {
			sl = sl + String.valueOf(board[r][c]);
			r++;
			c++;
		}
		return (sl.contains(s));
	}

	/**
	 * [method backslash]
	 */
	public boolean backslash(String s, int row, int col) {
		int r = row;
		int c = col;
		String bs = new String();
		while (r < 5 && c > 0) {
			r++;
			c--;
		}
		while (r > -1 && c < 7) {
			bs = bs + String.valueOf(board[r][c]);
			r--;
			c++;
		}
		return (bs.contains(s));
	}
}
