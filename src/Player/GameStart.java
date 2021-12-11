package Player;

import GUI.*;
import Server.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;

/**
 * [class GameStart] to play the game, it must be executed
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

	Game game;
	Login login;

	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;

	String name;
	boolean myTurn; // true means my turn
	boolean imReady; // true means "I'm ready."
	boolean urReady; // true means "The opponent is ready."
	boolean begin; // true means the game is already started
	char[][] board; // ���� �̰���� �Ǵ�

	public Launch() throws Exception {
		login = new Login();
		init(); // ������ �����ϴ� �Լ�
	}

	/**
	 * [method setName] set the name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * [class Login]
	 */
	class Login extends GUI.Login implements ActionListener {

		public Login() throws Exception {
			this.setVisible(true);
			this.btn.btn1.addActionListener(this); // �α��� ��ư
			this.btn.btn2.addActionListener(this); // ȸ������ ��ư
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();

			// �α��� ��ư ������ ��
			if (obj == this.btn.btn1) {
				try {
					String id = idBar.infoField.getText();
					char[] temp = passwordBar.infoField.getPassword();
					String password = new String(temp);
					Player player = new Player(id, password);
					
					out.writeObject(new Protocol(1, player));
					out.flush();

					// �α��� �Ǿ��� �� ���Ƿ� �Ѿ���� ó������� ��
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}

			// ȸ������ ��ư ������ ��
			else if (obj == this.btn.btn2) {
				this.setVisible(false);

				try {
					new SignUp(); // ȸ�������ϱ� ���� Ŭ���� ȣ��
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * [class SignUp] create player account
	 */
	class SignUp extends GUI.SignUp implements ActionListener {

		public SignUp() throws Exception {
			this.setVisible(true);
			this.btn.btn1.addActionListener(this); // Ȯ�� ��ư
			this.btn.btn2.addActionListener(this); // ��� ��ư
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();

			// Ȯ�� ��ư ������ ��
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
					Player player = new Player(id, password, rePassword, name, nickname, email, site);
					
					out.writeObject(new Protocol(2, player));
					out.flush();
					setName(name);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			// ��� ��ư ������ ��
			else if (obj == this.btn.btn2) {
				this.setVisible(false);

				try {
					new Login(); // ȸ�������� ����Ͽ����Ƿ� �ٽ� �α��� ȭ������ ���ư�
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/* �ʱ�ȭ: ���� ���� */
	public void init() {
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
			out = new ObjectOutputStream(socket.getOutputStream());

			/* Receiver ���� */
			Thread receiver = new Thread(new ClientReceiver(socket));
			receiver.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class Game extends GameRoom implements ActionListener {
		// ������ ������ �� ����� �̸��� �ڽ��� �̸��� �Է¹޾Ƽ� �̸��� ǥ��
		// ���� �̸��� �ƴ϶� Player ��ü�� �Է��ϵ��� �����ؾ� �ҵ�
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
			// enter ��ư�� �����ٸ� type�� chat�� �޽����� ������ ����
			if (obj == chat.enter) {
				try {
					out.writeObject(new Protocol(4, user.nickname.getText(), opponent.nickname.getText(),
							chat.chatCon.getText()));
					chat.chatCon.setText(null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// ���� �� ���� ��ư�� ������ �ڱ� ������ ��� type�� play�� content�� �� ��ư�� index�� �޽����� ������ ����
			else if (Arrays.stream(gameBoard.btn).anyMatch(obj::equals) && myTurn == true && begin == true) {
				int b = Arrays.asList(gameBoard.btn).indexOf(obj);

				if (tops[b] < 6) {
					try {
						// ���ʸ� ����
						myTurn = false;
						out.writeObject(new Protocol(11, user.nickname.getText(), opponent.nickname.getText(),
								Integer.toString(b)));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			// �غ� ��ư�� ������ ������ �������� ���� ���¶�� type�� ready�� �޽����� ����
			else if (obj == user.readyBtn && begin == false) {
				try {
					// �غ��� ���°� �ƴ϶�� �غ��ϰڴٴ� content
					if (!imReady) {
						out.writeObject(new Protocol(10, name, opponent.nickname.getText(), "do"));
					}
					// �غ��� ���¶�� �ǵ����ڴٴ� content
					else {
						out.writeObject(new Protocol(10, name, opponent.nickname.getText(), "undo"));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/* �������� ���� �޽����� ��� �޾Ƽ� �а� ����, �޴� �޽����� ���� ���� ���� */
	class ClientReceiver extends Thread {
		Protocol response = null;

		public ClientReceiver(Socket socket) {
			try {
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (in != null) {
				try {
					/* ������ ������ �޽����� ����ؼ� ���� */
					response = (Protocol) in.readObject();

					/* ������ ������ �޽����� ǥ�� */
					System.out.println("type: " + response.getType() + ", from: " + response.getFrom() + ", to: "
							+ response.getTo() + ", content: " + response.getContent());

					/* type�� invite�� �޽���: ������ ��û���� �� ������ ������ */
					if (response.getType() == 7) {
						myTurn = false; // ������ ������
						game = new Game(response.getFrom(), response.getTo());
						game.gameBoard.turn.setText("�غ� ��");
						login.setVisible(false);
					}

					/* type�� invited�� �޽���: ������ ��û�޾��� �� */
					else if (response.getType() == 8) {
						myTurn = true; // ������ ������
						game = new Game(response.getFrom(), response.getTo());
						game.gameBoard.turn.setText("�غ� ��");
						login.setVisible(false);
					}

					/* type�� chat�� �޽���: ���� �� ������ ä���� �Է����� �� */
					else if (response.getType() == 4) {
						/* ä��â�� ä�� ������ ǥ���� */
						game.chatWindow.Contents.addElement("[" + response.getFrom() + "]" + response.getContent());
					}

					/* type�� ready�� �޽���: ���� �� ������ �غ� ��ư�� ������ �� */
					else if (response.getType() == 10) {
						/* ��밡 �غ� ��ư�� �����ٸ� */
						if (response.getFrom().equals(game.opponent.nickname.getText())) {
							/* ��밡 �غ� ���¿� ���� �Ŷ�� */
							if (response.getContent().equals("do")) {
								game.opponent.readyStatus.setText("Ready");
								urReady = true;
								/* �ڽ��� �̹� �غ� ���¿��ٸ� */
								if (imReady == true) {
									/* ������ ���� */
									if (myTurn) {
										game.gameBoard.turn.setText("����� �����Դϴ�");
									} else {
										game.gameBoard.turn.setText("����� �����Դϴ�");
									}
									begin = true;
								}
							}
							/* ��밡 �غ� ���¸� �����ϴ� �Ŷ�� */
							else {
								game.opponent.readyStatus.setText("Not Ready");
								urReady = false;
							}
						}
						/* ���� �غ� ��ư�� �����ٸ� */
						else {
							/* ���� �غ� ���¿� ���� �Ŷ�� */
							if (response.getContent().equals("do")) {
								imReady = true;
								game.user.readyBtn.setText("Ready");
								/* ��밡 �̹� �غ��� ���¶�� */
								if (urReady == true) {
									/* ������ ���� */
									if (myTurn) {
										game.gameBoard.turn.setText("����� �����Դϴ�");
									} else {
										game.gameBoard.turn.setText("����� �����Դϴ�");
									}
									begin = true;
								}
							}
							/* ���� �غ� ���¸� �����ϴ� �Ŷ�� */
							else {
								game.user.readyBtn.setText("Not Ready");
								imReady = false;
							}
						}
					}
					/* type�� play�� �޽����� �޾��� �� */
					else if (response.getType() == 11) {
						/* ���� ���� ���Ҵٸ� */
						if (response.getFrom().equals(name)) {
							int num = Integer.parseInt(response.getContent());
							game.gameBoard.cell[game.tops[num]][num].setIcon(game.gameBoard.yellow);
							board[game.tops[num]][num] = '1';
							char c = '1';
							/* �̰���� �Ǵ��ϰ� �̰�ٸ� type�� result�� content�� win�� �޽����� ������ ���� */
							if (isWinning(c, game.tops[num], num)) {
								out.writeObject(new Protocol(12, name, game.opponent.nickname.getText(), "win"));
								/* ���� ȭ���� �ݰ� �ʱ� ȭ������ �ǵ��ư� */
								game.dispose();
								login.setVisible(true);
							}
							game.tops[num]++;
							game.gameBoard.turn.setText("����� �����Դϴ�");
						}
						/* ��밡 ���� ���Ҵٸ� */
						else {
							int num = Integer.parseInt(response.getContent());
							game.gameBoard.cell[game.tops[num]][num].setIcon(game.gameBoard.red);
							board[game.tops[num]][num] = '2';
							char c = '2';
							/* ������ �Ǵ��ϰ� ���ٸ� type�� result�� content�� lose�� �޽����� ������ ���� */
							if (isWinning(c, game.tops[num], num)) {
								out.writeObject(new Protocol(12, name, game.opponent.nickname.getText(), "lose"));
								/* ���� ȭ���� �ݰ� �ʱ� ȭ������ �ǵ��ư� */
								game.dispose();
								login.setVisible(true);
							}
							game.tops[num]++;
							/* ����� ���ʰ� ������ �ڱ� ���ʷ� */
							myTurn = true;
							game.gameBoard.turn.setText("����� �����Դϴ�");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* �̰���� �Ǵ��ϴ� �Լ� */
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
