package Player;

import GUI.*;
import Server.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;

public class GameStart {
	public static void main(String[] args) throws Exception {
		new Launch();
	}
}

class Launch {
	public final static String SERVER_IP = "127.0.0.1";
	public final static int SERVER_PORT = 9999;

	Game game;
	Login login;
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	String name;

	boolean myTurn; // 자기 턴인지 나타내는 변수, true일 때 자기 턴
	boolean imReady, urReady; // 나와 상대의 준비 상태를 나타내는 변수, true일 때 준비
	boolean begin; // 게임이 시작했는지를 나타내는 변수, true일 때 이미 시작함
	char[][] board; // 누군가 이겼는지 판단하기 위한 변수

	public Launch() throws Exception {
		login = new Login();
		init(); // 서버와 연결하는 함수
	}

	class Login extends TestSign implements ActionListener {
		private static final long serialVersionUID = 1L;

		public Login() throws Exception {
			this.setVisible(true);
			this.btns.btn1.addActionListener(this);
			this.btns.btn2.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			
			// 회원가입: sign up 버튼이 눌렸을 때 TextField에 입력된 값을 읽어서 서버에 type이 sign up인 메시지를 보냄
			if (obj == this.btns.btn1) {
				try {
					out.writeObject(new Protocol(3, text.infoField.getText()));
					name = text.infoField.getText();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			 // 게임 신청: challenge 버튼이 눌렸을 때 TextField에 입력된 값을 읽어서 서버에 type이 challenge인 메시지를 보냄
			else if (obj == this.btns.btn2 && !name.equals(text.infoField.getText())) {
				try {
					out.writeObject(new Protocol(5, name, text.infoField.getText()));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/* 초기화: 서버 연결 */
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

	class Game extends GameRoom implements ActionListener {
		// 게임을 실행할 때 상대의 이름과 자신의 이름을 입력받아서 이름만 표시
		// 추후 이름이 아니라 Player 객체를 입력하도록 수정해야 할듯
		int[] tops;

		public Game(String user, String opponent) throws Exception {
			this.setVisible(true);
			this.user.nickname.setText(user);
			this.user.readyBtn.addActionListener(this);
			this.user.readyBtn.setText("Not Ready");
			this.opponent.nickname.setText(opponent);
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
					out.writeObject(new Protocol(4, user.nickname.getText(), opponent.nickname.getText(),
							chat.chatCon.getText()));
					chat.chatCon.setText(null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// 게임 판 위의 버튼을 눌렀고 자기 차례인 경우 type이 play고 content가 그 버튼의 index인 메시지를 서버에 보냄
			else if (Arrays.stream(gameBoard.btn).anyMatch(obj::equals) && myTurn == true && begin == true) {
				int b = Arrays.asList(gameBoard.btn).indexOf(obj);

				if (tops[b] < 6) {
					try {
						// 차례를 종료
						myTurn = false;
						out.writeObject(new Protocol(11, user.nickname.getText(), opponent.nickname.getText(),
								Integer.toString(b)));
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
						out.writeObject(new Protocol(10, name, opponent.nickname.getText(), "do"));
					}
					// 준비한 상태라면 되돌리겠다는 content
					else {
						out.writeObject(new Protocol(10, name, opponent.nickname.getText(), "undo"));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/* 서버에서 오는 메시지를 계속 받아서 읽고 반응, 받는 메시지에 대한 응답 선언 */
	class ClientReceiver extends Thread {
		Protocol response = null;

		public ClientReceiver(Socket socket) {
			try {
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) { e.printStackTrace(); }
		}

		public void run() {
			while (in != null) {
				try {
					/* 서버가 보내는 메시지를 계속해서 읽음 */
					response = (Protocol) in.readObject();
					
					/* 서버가 보내는 메시지를 표시 */
					System.out.println("type: " + response.getType() + ", from: " + response.getFrom() + ", to: "
							+ response.getTo() + ", content: " + response.getContent());
					
					/* type이 invite인 메시지: 게임을 신청했을 때 게임을 실행함 */
					if (response.getType() == 7) {
						myTurn = false; // 게임을 실행함
						game = new Game(response.getFrom(), response.getTo());
						game.gameBoard.turn.setText("준비 중");
						login.setVisible(false);
					}
					
					/* type이 invited인 메시지: 게임을 신청받았을 때 */
					else if (response.getType() == 8) {
						myTurn = true; // 게임을 실행함
						game = new Game(response.getFrom(), response.getTo());
						game.gameBoard.turn.setText("준비 중");
						login.setVisible(false);
					}
					
					/* type이 chat인 메시지: 게임 중 누군가 채팅을 입력했을 때 */
					else if (response.getType() == 4) {
						/* 채팅창에 채팅 내용을 표시함 */
						game.chatWindow.Contents.addElement("[" + response.getFrom() + "]" + response.getContent());
					}
					
					/* type이 ready인 메시지: 게임 중 누군가 준비 버튼을 눌렀을 때 */
					else if (response.getType() == 10) {
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
					}
					/* type이 play인 메시지를 받았을 때 */
					else if (response.getType() == 11) {
						/* 내가 돌을 놓았다면 */
						if (response.getFrom().equals(name)) {
							int num = Integer.parseInt(response.getContent());
							game.gameBoard.cell[game.tops[num]][num].setIcon(game.gameBoard.yellow);
							board[game.tops[num]][num] = '1';
							char c = '1';
							/* 이겼는지 판단하고 이겼다면 type이 result고 content가 win인 메시지를 서버에 보냄 */
							if (isWinning(c, game.tops[num], num)) {
								out.writeObject(new Protocol(12, name, game.opponent.nickname.getText(), "win"));
								/* 게임 화면을 닫고 초기 화면으로 되돌아감 */
								game.dispose();
								login.setVisible(true);
							}
							game.tops[num]++;
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
								out.writeObject(new Protocol(12, name, game.opponent.nickname.getText(), "lose"));
								/* 게임 화면을 닫고 초기 화면으로 되돌아감 */
								game.dispose();
								login.setVisible(true);
							}
							game.tops[num]++;
							/* 상대의 차례가 끝나고 자기 차례로 */
							myTurn = true;
							game.gameBoard.turn.setText("당신의 차례입니다");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* 이겼는지 판단하는 함수 */
	public boolean isWinning(char c, int row, int col) {
		String s = new String();
		s = s + String.valueOf(c);
		s = s + String.valueOf(c);
		s = s + String.valueOf(c);
		s = s + String.valueOf(c);
		return (horizontal(s, row) || vertical(s, col) || slash(s, row, col) || backslash(s, row, col));
	}

	public boolean horizontal(String s, int row) {
		String hr = new String();
		for (char c : board[row]) {
			hr = hr + String.valueOf(c);
		}
		return (hr.contains(s));
	}

	public boolean vertical(String s, int col) {
		String vr = new String();
		for (int i = 0; i < 6; i++) {
			vr = vr + String.valueOf(board[i][col]);
		}
		return (vr.contains(s));
	}

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
