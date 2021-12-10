package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: UserInGame
 * description: 자신과 상대에 대한 정보창을 생성
 * @author 201937402 강태훈
 */
public class UserInGame extends JPanel {
	Box mainBox, readyBox, profileBox;
	public JLabel nickname, readyStatus, profile;
	public JButton readyBtn;
	
	public UserInGame(int width, int height, int flag, String nick) { // 너비, 높이, flag, 별명 입력받음
		super();
		setSize(width, height);
		
		
		mainBox = Box.createVerticalBox();
		mainBox.setSize(width, height);
		readyBox = Box.createHorizontalBox(); //준비 버튼, 상대의 준비 상태, 별명이 옆으로 배열되는 박스
		readyBox.setSize(width, height/4);
		/* 입력된 별명을 표시할 라벨을 만들고 추가 */
		nickname = new JLabel(nick);
		nickname.setMinimumSize(new Dimension(width / 2, height / 4));
		nickname.setPreferredSize(new Dimension(width / 2, height / 4));
		nickname.setMaximumSize(new Dimension(width / 2, height / 4));
		nickname.setOpaque(true);
		nickname.setBackground(new Color(255, 255, 255));
		readyBox.add(nickname);
		
		if(flag == 0) { // 상대의 준비 상태를 나타내는 라벨을 생성 및 추가
			readyStatus = new JLabel("Ready / Not Ready");
			readyStatus.setMinimumSize(new Dimension(width / 2, height / 4));
			readyStatus.setPreferredSize(new Dimension(width / 2, height / 4));
			readyStatus.setMaximumSize(new Dimension(width / 2, height / 4));
			readyStatus.setOpaque(true);
			readyStatus.setBackground(new Color(200,200,200));
			readyBox.add(readyStatus);
		}
		else { // 자신의 준비 상태를 변경하는 버튼을 생성 및 추가
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