package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: WaitingRoom
 * description: ���� ȭ��
 * @author 201937402 ������
 */
public class WaitingRoom extends JFrame {
	
	Box mainBox;
	
	/* ä�� �Է�â
	 * ���� ���: chat.chatCon (JTextField)
	 * ���� ��ư: chat.enter (JButton)
	 * JTextField: �Է��� ������ getText()�� �̿��� String type���� return ���� */
	public ChatBar chat;
	
	/* ä��â
	 * ���: chatWindow.chatScroll (JScrollPane)
	 * ����� ���� ����: chatWindow ���� DefaultListModel ���� (����� chatWindow.test) */
	public ChatWindow chatWindow;

	/* �����ڵ�
	 * �����ڵ� ���: currentUsers.userScroll (JScrollPane)
	 * �����ڵ� ��� ����: currentUsers.content�� ���� (DefaultListModel)
	 * ������� ���� ǥ��: currentUsers.userStatus (JLabel)
	 * ��� ��û ��ư: currentUsers.userBtn.btn1 (JButton)
	 * ���� Ȯ�� ��ư: currentUsers.userBtn.btn2 (JButton)
	 * ���� ���� ��ư: currentUsers.miscBtn.btn1 (JButton)
	 * ������ ��ư: currentUsers.miscBtn.btn2 (JButton)
	 * setText(String s): JLabel ���� ���� ���� */
	public UserList currentUsers;
	
	public WaitingRoom() {
		super("Waiting room");
		setSize(1000, 700);
		
		// ����ڰ� ä���� ������ �Է��� â�� �� ������ ä��â���� ���� ��ư
		chat = new ChatBar(1000, 25);

		// ����ڵ��� ä�� ������ ǥ���� ä��â
		chatWindow = new ChatWindow(670, 620);

		// ������ ����ڵ��� ������ ǥ��
		currentUsers = new UserList(300, 640);

		// component�� ���ʿ��� ���������� ��ġ�� ���� �ڽ�
		mainBox = Box.createHorizontalBox();

		// ���� �ڽ��� ä��â�� ����
		mainBox.add(chatWindow);

		// ���� �ڽ��� ������ ����Ʈ�� ��ư�� ����
		mainBox.add(currentUsers);

		// ä��â�� ������ ����Ʈ �ؿ� ä�� �Է�â ��ġ
		add(mainBox, BorderLayout.WEST);
		add(chat, BorderLayout.SOUTH);
		setLocationRelativeTo(null); // ȭ���� ������� �߾ӿ� ��ġ
		setResizable(false); // ȭ���� ũ�� ����
	    setDefaultCloseOperation(EXIT_ON_CLOSE); // x ��ư ������ ȭ�� ����
		setVisible(true);
	}
	public static void main(String[] args) {
		new WaitingRoom();
	}
}