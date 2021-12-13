package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: ChatBar
 * description: 사용자가 채팅할 내용을 입력하는 창, 그 내용을 채팅창으로 보내는 버튼
 * @author 201937402 강태훈
 */
public class ChatBar extends JPanel {
	public JTextField chatCon;
	public JButton enter;
	
	public ChatBar(int width, int height) { // 너비와 높이를 입력받아서 사용
		super();

		chatCon = new JTextField(50); // 내용을 입력할 필드
		enter = new JButton("Enter"); // 내용을 채팅창으로 보낼 버튼
		
		enter.setPreferredSize(new Dimension(width / 10 - 10, height));
		chatCon.setPreferredSize(new Dimension(width * 9 / 10, height));
		setSize(width, height);
		
		/* 입력창과 버튼 추가 */
		add(chatCon, BorderLayout.LINE_START);
		add(enter, BorderLayout.LINE_END);
	}
}