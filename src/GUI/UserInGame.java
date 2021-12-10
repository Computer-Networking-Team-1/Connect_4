package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: UserInGame
 * description: �ڽŰ� ��뿡 ���� ����â�� ����
 * @author 201937402 ������
 */
public class UserInGame extends JPanel {
	Box mainBox, readyBox, profileBox;
	public JLabel nickname, readyStatus, profile;
	public JButton readyBtn;
	
	public UserInGame(int width, int height, int flag, String nick) { // �ʺ�, ����, flag, ���� �Է¹���
		super();
		setSize(width, height);
		
		
		mainBox = Box.createVerticalBox();
		mainBox.setSize(width, height);
		readyBox = Box.createHorizontalBox(); //�غ� ��ư, ����� �غ� ����, ������ ������ �迭�Ǵ� �ڽ�
		readyBox.setSize(width, height/4);
		/* �Էµ� ������ ǥ���� ���� ����� �߰� */
		nickname = new JLabel(nick);
		nickname.setMinimumSize(new Dimension(width / 2, height / 4));
		nickname.setPreferredSize(new Dimension(width / 2, height / 4));
		nickname.setMaximumSize(new Dimension(width / 2, height / 4));
		nickname.setOpaque(true);
		nickname.setBackground(new Color(255, 255, 255));
		readyBox.add(nickname);
		
		if(flag == 0) { // ����� �غ� ���¸� ��Ÿ���� ���� ���� �� �߰�
			readyStatus = new JLabel("Ready / Not Ready");
			readyStatus.setMinimumSize(new Dimension(width / 2, height / 4));
			readyStatus.setPreferredSize(new Dimension(width / 2, height / 4));
			readyStatus.setMaximumSize(new Dimension(width / 2, height / 4));
			readyStatus.setOpaque(true);
			readyStatus.setBackground(new Color(200,200,200));
			readyBox.add(readyStatus);
		}
		else { // �ڽ��� �غ� ���¸� �����ϴ� ��ư�� ���� �� �߰�
			readyBtn = new JButton("<html>press this <br>when ready</html>");
			readyBtn.setMinimumSize(new Dimension(width / 2, height / 4));
			readyBtn.setPreferredSize(new Dimension(width / 2,height / 4));
			readyBtn.setMaximumSize(new Dimension(width / 2, height / 4));
			readyBox.add(readyBtn);
		}

		mainBox.add(readyBox);
		profile = new JLabel("user record displayed here");
		profile.setMinimumSize(new Dimension(width, height * 3/4));
		profile.setPreferredSize(new Dimension(width, height * 3/4));
		profile.setMaximumSize(new Dimension(width, height * 3/4));
		profileBox = Box.createHorizontalBox();
		profileBox.add(profile, BorderLayout.CENTER);
		mainBox.add(profileBox);
		add(mainBox);
	}
}