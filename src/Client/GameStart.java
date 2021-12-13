package Client;

import GUI.*;
import Server.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;

/**
 * [class GameStart] ���� ����
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
	
	// �ش� Ŭ���̾�Ʈ�� Player�� ����
	String nickname;
	Player player;
	
	boolean myTurn;		// ���� �� �� ����
	boolean imReady;	// ���� �� �غ������ �ǹ�
	boolean urReady;	// ���� �� ������ �غ������ �ǹ�
	boolean begin;		// ���� �� �̹� ������ ���۵Ǿ����� �ǹ�
	char[][] board;		// ���� �̰���� �Ǵ�
	
	/**
	 * [Constructor Launch]
	 */
	public Launch() throws Exception {
		login = new Login();
		init();	// ������ �����ϴ� �Լ�
	}

	/**
	 * [method setNickname] �г��� ����
	 * @param nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * [method init] ���� ����
	 */
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
	
	/**
	 * [class Login] �α���
	 */
	class Login extends GUI.Login implements ActionListener {

		/**
		 * [Constructor Login]
		 */
		public Login() throws Exception {
			this.setVisible(true);
			this.error.setText(null);
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
					Player player = new Player(id, password.getBytes());
					
					out.writeObject(new Protocol(1, player));
					out.flush();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}

			// ȸ������ ��ư ������ ��
			else if (obj == this.btn.btn2) {
				this.setVisible(false);	// �α��� ȭ���� ����

				try {
					signUp = new SignUp(); // ȸ�������ϱ� ���� Ŭ���� ȣ��
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * [class SignUp] �÷��̾� ���� ���� (ȸ������)
	 */
	class SignUp extends GUI.SignUp implements ActionListener {
		
		/**
		 * [Constructor SignUp]
		 */
		public SignUp() throws Exception {
			this.setVisible(true);
			this.error.setText(null);
			this.btn.btn1.addActionListener(this);	// Ȯ�� ��ư
			this.btn.btn2.addActionListener(this);	// ��� ��ư
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
					Player player = new Player(id, password.getBytes(), name, nickname, email, site);
					
					// ���̵� ������� ��
					if(id.equals(null)) {
						this.error.setText("ID�� �Էµ��� �ʾҽ��ϴ�");
					}
					// ��й�ȣ�� ������� ��
					else if(passwordBar.infoField.getPassword().equals(null) || rePasswordBar.infoField.getPassword().equals(null)) {
						this.error.setText("��й�ȣ�� �Էµ��� �ʾҽ��ϴ�");
					}
					// ��й�ȣ�� ��ġ���� ���� ��
					else if(!password.equals(rePassword)) {
						this.error.setText("��й�ȣ�� ��ġ���� �ʽ��ϴ�");
					}
					// sign up �޽��� ����
					else {
						out.writeObject(new Protocol(2, player));
						out.flush();
					}
					
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			// ��� ��ư ������ ��
			else if (obj == this.btn.btn2) {
				this.dispose();
				try {
					login.setVisible(true); // ȸ�������� ����Ͽ����Ƿ� �ٽ� �α��� ȭ������ ���ư�
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * [class WaitingRoom] ��ü ����
	 */
	class WaitingRoom extends GUI.WaitingRoom implements ActionListener {

		public WaitingRoom() {
			player.setStatus(1);
			this.setVisible(true);
			// ä��â�� ����� ��� ����� ���
			chatWindow.Contents.removeAllElements();
			currentUsers.content.removeAllElements();
			
			// ���ǿ� �����ߴٴ� �޼����� ����
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
			// enter ��ư�� ������ ��
			if(obj==this.chat.enter) {
				// ä�� �Է�â�� ���� ä�� ������ �޼����� ����
				try {
					out.writeObject(new Protocol(13, nickname, "server", this.chat.chatCon.getText()));
					out.flush();
					this.chat.chatCon.setText(null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// ������ ��ư�� ������ ��
			else if(obj==this.currentUsers.miscBtn.btn2) {
				// â�� �ݰ� �α׾ƿ� �Ѵٴ� �޼����� ����
				dispose();
				try {
					out.writeObject(new Protocol(14, nickname));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// �α��� ȭ������ �ǵ��ư�
				login.setVisible(true);
			}
			// ���� ���� ��ư�� ������ ��
			else if(obj==this.currentUsers.miscBtn.btn1) {
				player.setStatus(2);	// ����� ��û�� �� ���� ���·� �ٲٰ�
				changeinfo = new ChangeInfo();	// ���� ���� ȭ���� open
			}
			// ����� ��û�� �� �ִ� ���°� ��� ��û ��ư�� ������ ��
			else if(obj == this.currentUsers.userBtn.btn1 && player.getStatus() == 1) {
				try {
					// ������ ����� ������ �޽����� ����
					out.writeObject(new Protocol(4, nickname, currentUsers.users.getSelectedValue().toString()));
					out.flush();
					player.setStatus(2);
				} catch (IOException e1) { e1.printStackTrace(); }
			}
			// ���� Ȯ�� ��ư�� ������ ��
			else if(obj == this.currentUsers.userBtn.btn2) {
				try {
					// ������ ����� ������ �޼����� ����
					out.writeObject(new Protocol(5, nickname, currentUsers.users.getSelectedValue().toString()));
					out.flush();
				} catch (Exception e1) { e1.printStackTrace(); }
			}
		}
	}

	/**
	 * [class ChangeInfo] ���� ����
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
			// cancel ��ư�� ������ ��
			if(obj == this.btn.btn2) {
				// ���� ���� ȭ���� ����
				this.dispose();
			}
			// confirm ��ư�� ������ ��
			else if(obj == this.btn.btn1) {
				// �ӽ÷� Player ��ü�� ���� ���� ����� �Բ� �޼����� ����
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
	 * [class Game] ���� ����
	 */
	class Game extends GameRoom implements ActionListener {
		int[] tops;

		public Game(Player user, Player opponent) throws Exception {
			this.setVisible(true);
			// �� ���� ǥ��
			this.user.nickname.setText(user.getNickname());
			this.user.profile.setText("<html>��: " + user.getCountWin() + "<br/>"
			+ "��: " + user.getCountLose() + "<br/>"
			+ "��: " + user.getCountDraw() + "</html>");
			this.user.readyBtn.addActionListener(this);
			this.user.readyBtn.setText("Not Ready");
			// ����� ���� ǥ��
			this.opponent.nickname.setText(opponent.getNickname());
			this.opponent.profile.setText("<html>��: " + opponent.getCountWin() + "<br/>"
			+ "��: " + opponent.getCountLose() + "<br/>"
			+ "��: " + opponent.getCountDraw() + "</html>");
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
					out.writeObject(new Protocol(3, user.nickname.getText(), opponent.nickname.getText(),
							chat.chatCon.getText()));
					out.flush();
					chat.chatCon.setText(null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// ������ ���� ��ư�� ������ �ڱ� ������ ��� type�� play�� content�� �� ��ư�� index�� �޽����� ������ ����
			else if (Arrays.stream(gameBoard.btn).anyMatch(obj::equals) && myTurn == true && begin == true) {
				int b = Arrays.asList(gameBoard.btn).indexOf(obj);

				if (tops[b] < 6) {
					try {
						// ���ʸ� ����
						myTurn = false;
						out.writeObject(new Protocol(10, user.nickname.getText(), opponent.nickname.getText(),
								Integer.toString(b)));
						out.flush();
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
						out.writeObject(new Protocol(9, nickname, opponent.nickname.getText(), "do"));
						out.flush();
					}
					// �غ��� ���¶�� �ǵ����ڴٴ� content
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
	 * [class Result] ��� ǥ��
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
			// �̰��� ���
			if(flag.equals("win")) {
				// �̰�ٴ� �޽����� �����ϰ� �¸� Ƚ���� �ø�
				try {
					out.writeObject(new Protocol(11, nickname, game.opponent.nickname.getText(), "win"));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				/* ���� ȭ���� �ݰ� �ʱ� ȭ������ �ǵ��ư� */
				game.dispose();
				this.dispose();
				player.setCountWin(player.getCountWin()+1);
				waitingRoom = new WaitingRoom();
				player.setStatus(1);
			}
			// ���� ���
			else if(flag.equals("lose")) {
				// ���ٴ� �޼����� �����ϰ� �й� Ƚ���� �ø�
				try {
					out.writeObject(new Protocol(11, nickname, game.opponent.nickname.getText(), "lose"));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				/* ���� ȭ���� �ݰ� �ʱ� ȭ������ �ǵ��ư� */
				game.dispose();
				this.dispose();
				player.setCountLose(player.getCountLose()+1);
				waitingRoom = new WaitingRoom();
				player.setStatus(1);
			}
			// ����� ���
			else if(flag.equals("draw")) {
				//���ٴ� �޼����� �����ϰ� ���º� Ƚ���� �ø�
				try {
					out.writeObject(new Protocol(11, nickname, game.opponent.nickname.getText(), "draw"));
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				/* ���� ȭ���� �ݰ� �ʱ� ȭ������ �ǵ��ư� */
				game.dispose();
				this.dispose();
				player.setCountDraw(player.getCountDraw()+1);
				waitingRoom = new WaitingRoom();
				player.setStatus(1);
			}
		}
		// ���� ���θ� Ȯ��
		public void setFlag(String flag) {
			this.flag = flag;
		}
	}
	
	/**
	 * [class Invite] ���� ��û �޾��� ��
	 */
	class Invite extends Notice2 implements ActionListener{
		String oppo;
		public Invite(String s){
			this.setVisible(true);
			this.oppo = s;
			this.Notice.setText(s + "�� ����� ��û�߽��ϴ�");
			this.Btns.btn1.addActionListener(this);
			this.Btns.btn2.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			// ���� ��ư�� ������ ��
			if(obj == Btns.btn2) {
				try {
					// �����Ѵٴ� �޽����� ����
					out.writeObject(new Protocol(7, oppo, nickname, "cancel"));
					out.flush();
					player.setStatus(1);
					dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// ���� ��ư�� ������ ��
			else if(obj == Btns.btn1) {
				try {
					//�����Ѵٴ� �޽����� ����
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
	 * [class ClientReceiver] �������� ���� �޽����� ��� �޾Ƽ� �а� ����, �޴� �޽����� ���� ���� ����
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
					/* ������ ������ �޽����� ����ؼ� ���� */
					response = (Protocol) in.readObject();

					/* ������ ������ �޽����� ǥ�� */
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
						// �α��ο� �������� ���
						else {
							login.error.setText("�������� �ʴ� �����Դϴ�");
						}
						break;
						
					/* [type: sign up] ȸ������ ��û�� ���� �� �޴� �޼����� ó�� */
					case 2:
						// ���̵� �ߺ��Ǿ��ٴ� �޽����� �޾��� ��
						if(response.getContent().equals("id")) {
							signUp.error.setText("�̹� ��� ���� ID�Դϴ�");
						}
						// ������ �ߺ��Ǿ��ٴ� �޽����� �޾��� ��
						else if(response.getContent().equals("nick")) {
							signUp.error.setText("�̹� ��� ���� �����Դϴ�");
						}
						// �������� ��
						else {
							// ȸ������ ȭ���� �ݰ� �α��� ȭ������ ���ư�
							signUp.dispose();
							login.setVisible(true);
						}
						
					/* [type: chat] ���� �� ������ ä���� �Է����� �� */
					case 3:
						/* ä��â�� ä�� ������ ǥ���� */
						game.chatWindow.Contents.addElement("[" + response.getFrom() + "]" + response.getContent());
						break;
						
					/* [type: challenge] ��� ��û�� ���õ� �޼����� ó�� */
					case 4:
						// ��� ��û�� �޾��� ��
						if(response.getContent().equals("invite")) {
							//����� ��û�� �� ���� ���·� ����� ��� ��û ȭ���� ���
							player.setStatus(2);
							invite = new Invite(response.getFrom());
						}
						// ��밡 ����� �������� ��
						if(response.getContent().equals("cancel")) {
							player.setStatus(1);
							waitingRoom.currentUsers.userStatus.setText("��밡 ����� �����߽��ϴ�");
						}
						// ��밡 �̹� ��� ���� ��
						if(response.getContent().equals("in game")) {
							player.setStatus(1);
							waitingRoom.currentUsers.userStatus.setText("��밡 ���� ��� ���Դϴ�");
						}
						break;
					
					/* [type: information] ����� ������ Ȯ�� */
					case 5:
						Player tmp = response.getPlayer();
						String[] log = tmp.getLog();
						waitingRoom.currentUsers.userStatus.setText("<html>" + tmp.getNickname() + "<br/>"
								+ "�÷��� Ƚ��: " + tmp.getTotalCount() + "<br/>"
								+ "��: " + tmp.getCountWin() + "<br/>"
								+ "��: " + tmp.getCountLose() + "<br/>"
								+ "��: " + tmp.getCountDraw() + "<br/>"
								+ "������ ����: " + log[1] + "</html>");
						break;
					
					/* [type: invite] ������ ��û���� �� ������ ������ */
					case 6:
						myTurn = false; // ������ ������
						game = new Game(player, response.getPlayer());
						game.gameBoard.turn.setText("�غ� ��");
						waitingRoom.dispose();
						player.setStatus(2);
						break;
						
					/* [type: invited] ������ ��û�޾��� �� */
					case 7:
						myTurn = true; // ������ ������
						game = new Game(player, response.getPlayer());
						game.gameBoard.turn.setText("�غ� ��");
						waitingRoom.dispose();
						player.setStatus(2);
						break;
					
					/* [type: change] ������ ������ �� */
					case 8:
						// ������ �ߺ����� ���� ���
						if(response.getContent().equals("success")) {
							//�� Ŭ���̾�Ʈ�� Player ��ü�� ������ ����
							player = response.getPlayer();
							nickname = player.getNickname();
							changeinfo.dispose();
						}
						else {
							changeinfo.error.setText("�̹� ��� ���� �����Դϴ�");
						}
						break;
					
					/* [type: ready] ���� �� ������ �غ� ��ư�� ������ �� */
					case 9:
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
						break;
						
					/* [type: play] */
					case 10:
						/* ���� ���� ���Ҵٸ� */
						if (response.getFrom().equals(nickname)) {
							int num = Integer.parseInt(response.getContent());
							game.gameBoard.cell[game.tops[num]][num].setIcon(game.gameBoard.yellow);
							board[game.tops[num]][num] = '1';
							char c = '1';
							/* �̰���� �Ǵ��ϰ� �̰�ٸ� type�� result�� content�� win�� �޽����� ������ ���� */
							if (isWinning(c, game.tops[num], num)) {
								result = new Result("�¸��߽��ϴ�");
								result.setFlag("win");
							}
							game.tops[num]++;
							// ���º����� Ȯ���ϰ� ���ºζ�� draw �޽����� ����
							if (isDraw()) {
								result = new Result("���º��Դϴ�");
								result.setFlag("draw");
							}
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
								result = new Result("�й��߽��ϴ�");
								result.setFlag("lose");
							}
							game.tops[num]++;
							// ���º����� Ȯ���ϰ� ���ºζ�� draw �޽����� ����
							if (isDraw()) {
								result = new Result("���º��Դϴ�");
								result.setFlag("draw");
							}
							/* ����� ���ʰ� ������ �ڱ� ���ʷ� */
							myTurn = true;
							game.gameBoard.turn.setText("����� �����Դϴ�");
						}
						break;
					
					/* ��밡 ��� ���� ������ �������� �� */
					case 11:
						// ���ºη� ó��
						result = new Result("����� ������ ������ϴ�");
						result.setFlag("draw");
						break;
					
					/* ���ǿ� ������ ��, �ٸ� �������� ���ǿ� �������� �� �޴� �޽��� */
					case 12:
						// �ٸ� ����� ������ ��� ��Ͽ� �߰���
						if(!response.getFrom().equals(nickname)&&!waitingRoom.currentUsers.content.contains(response.getFrom())) waitingRoom.currentUsers.content.addElement(response.getFrom());
						break;
					
					/* [type: all chat] ���ǿ��� ������ ä���� ������ �� */
					case 13:
						// ������ ä�� ���� ��Ͽ� �߰���
						waitingRoom.chatWindow.Contents.addElement("[" + response.getFrom() + "] " + response.getContent());
					    break;
					
					/* [type: logout] �ٸ� ������ �α׾ƿ� �߰ų� ������ ������ �� */
					case 14:
						// ��� ��Ͽ��� �� ����� ����
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
	 * [method isDraw] ���º����� �Ǵ�
	 */
	public boolean isDraw() {
		for(int i: game.tops) {
			if (i < 6) return false;
		}
		return true;
	}

	/**
	 * [method isWinning] �̰���� �Ǵ�
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
