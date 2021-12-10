package GUI;

import java.awt.*;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * class: ChatWindow
 * description: 사용자들의 채팅 내용을 표시하기 위한 채팅창
 * @author 201937402 강태훈
 */
public class ChatWindow extends JPanel{
	public DefaultListModel Contents;
	public JList chats;
	public JScrollPane chatScroll;
	Box chatBox;
	JScrollBar vertical;
	
	public ChatWindow(int width, int height) { // 너비와 높이를 입력받아서 사용
		super();

		Contents = new DefaultListModel();
		setSize(width, height);

		chats = new JList(Contents); // 데이터로 리스트 생성
		chatScroll = new JScrollPane(chats); // 스크롤 가능한 리스트 생성
		chatScroll.setPreferredSize(new Dimension(width, height));
		vertical = chatScroll.getVerticalScrollBar();
		add(chatScroll, BorderLayout.WEST);
	}
}