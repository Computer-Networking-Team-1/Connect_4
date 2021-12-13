package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: UserList
 * description: ������ ����ڵ��� ������ ǥ��
 * comment: ������ �� ����� ���Ͽ��� ���� �Ӹ� �ƴ϶� ID���� �����;� ��
 * @author 201937402 ������
 */
public class UserList extends JPanel {
	public DefaultListModel content;
	Box userBox, statusBox;
	public JList users;
	public JLabel userStatus;
	public JScrollPane userScroll;
	public BtnPanel userBtn;
	public BtnPanel miscBtn;
	
	public UserList(int width, int height) { // �ʺ�� ���̸� �Է¹޾� ���
		super();
		
		// component�� ������ �Ʒ��� ��ġ�ϱ� ���� �ڽ�
		userBox = Box.createVerticalBox();
		userBox.setSize(width, height);
		
		content = new DefaultListModel();
		
		
		// �����ͷ� ����Ʈ ����
		users = new JList(content);

		// ��ũ���� �ִ� ����Ʈ ����
		userScroll = new JScrollPane(users);
		
		// �Է¹��� ������ 2/3 ������ ���
		userScroll.setPreferredSize(new Dimension(width, height * 2 / 3));

		// �ڽ��� ��ũ���� �ִ� �������� ����Ʈ�� ����
		userBox.add(userScroll);
		
		// ����ڸ� �����ϰ� '���� Ȯ��' ��ư�� ������ �� ���� ���, �·� ���� ǥ���� ��
		userStatus = new JLabel("user's record displayed here");
		
		userStatus.setMinimumSize(new Dimension(width, height / 6));
		userStatus.setPreferredSize(new Dimension(width, height / 6));
		userStatus.setMaximumSize(new Dimension(width, height / 6));
		
		statusBox = Box.createHorizontalBox();
		
		// ����� ���� ���� ����� ��ġ�ϱ� ���� ������ �ڽ� ���
		statusBox.add(userStatus, BorderLayout.CENTER);
		
		// �ڽ��� ����� ���� �� ����
		userBox.add(statusBox);

		// ������ �迭�� ��� ��û ��ư�� ���� Ȯ�� ��ư�� ����� �ڽ��� ����
		userBtn = new BtnPanel("��� ��û", "���� Ȯ��", width, (height / 12) - 20);
		userBox.add(userBtn);
		
		// ������ �迭�� ���� ���� ��ư�� ������ ��ư�� ����� �ڽ��� ����
		miscBtn = new BtnPanel("���� ����", "������", width, (height / 12) - 20);
		userBox.add(miscBtn);
		setSize(width, height);
		add(userBox);
	}
}