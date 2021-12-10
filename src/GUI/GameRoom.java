package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
//���� ����� �غ� ���� ǥ�ô� Opponent.OppoReady�� ������ �� �ֽ��ϴ�, type�� JLabel
//�ڽ��� �غ� ���¸� �����ϴ� ��ư�� User.ReadyBtn���� ������ �� �ֽ��ϴ�, type�� JButton

/**
 * class: GameRoom
 * description: ������ ���� ���� �� ���̴� ȭ��
 * @author 201937402 ������
 */
public class GameRoom extends JFrame{
	
	public Box chatBox, playBox;
	
	/* ���� ���
	 * ���� ����: opponent.nickname (JLabel)
	 * �غ� ���� ǥ��: opponent.readyStatus (JLabel)
	 * ���� ǥ��: opponent.profile (JLabel) */
	public UserInGame opponent;
	
	/* ����
	 * ���� ����: user.nickname (JLabel)
	 * �غ� ���� ����: user.readyBtn (JButton)
	 * ���� ǥ��: user.profile (JLabel)
	 * JLabel, JButton�� setText(String s)�� ���� ���� ���� */
	public UserInGame user;

	/* ä��â
	 * ���� ���: chatWindow.chatScroll (JScrollPane)
	 * ǥ�õǴ� ����: chatWindow.Contents (DefaultListModel) */
	public ChatWindow chatWindow;
	
	/* ä�� �Է�â
	 * ���� ���: chat.chatCon (JTextField)
	 * �Էµ� ���� ���� ���: chat.chatCon.getText() (String)
	 * ���� ��ư: chat.enter (JButton) */
	public ChatBar chat;
	
	/* ������
	 * �� ��ư�� ���� ���: gameBoard.btn[int index] (JButton)
	 * �� ĭ�� ���� ���: gameBoard.cell[int row][int col] (JLabel)
	 * ������ ���� ���: setIcon(Icon icon) (JLabel)
	 * ����ִ� ������: gameBoard.empty
	 * ���� ���� ä���� ������: gameBoard.red
	 * ��� ���� ä���� ������: gameBoard.yellow */
	public Connect4 gameBoard;
	
	public GameRoom() throws IOException {
		super("Connect 4");
		
		// ä��â�� ä�� �Է�â�� ���Ʒ��� ���� �ڽ�
		chatBox = Box.createVerticalBox();

		// ����� ����, ������, ���� ������ ������ ���� �ڽ�
		playBox = Box.createHorizontalBox();

		// ������ opponent nickname�� ����� ����â
		// �� ��° ���ڰ� 0�� ��� ����� ����â
		opponent = new UserInGame(225, 490, 0, "opponent nickname");
		
		// ������ user nickname�� ���� ����â
		// �� ��° ���ڰ� 0�� �ƴ� ��� �ڽ��� ����â
		user = new UserInGame(225,490, 1, "user nickname");
		
		// ������ ����� ������
		gameBoard = new Connect4(550, 490);
		
		// ����� ����â, ������, ���� ����â�� ȭ�鿡 �߰�
		playBox.add(opponent);
		playBox.add(gameBoard);
		playBox.add(user);
		add(playBox);

		/* ä�� �Է�â */
		chat = new ChatBar(1000, 25);

		/* ä��â */
		chatWindow = new ChatWindow(970, 160);
		chatBox.add(chatWindow);
		chatBox.add(chat);
		
		add(chatBox, BorderLayout.SOUTH); // ä��â�� ä�� �Է�â�� ȭ�鿡 �߰�

		setSize(1000, 750);
		setResizable(false);
		setLocationRelativeTo(null); // �������� �� ������� ����� ���Բ�
		setDefaultCloseOperation(EXIT_ON_CLOSE); // x ��ư ������ �� ����, �ٸ� �ൿ ���� ����
		setVisible(true); // ȭ�鿡 ���̵���
	}
}