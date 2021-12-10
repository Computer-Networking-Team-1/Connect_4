package GUI;

import java.awt.*;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * class: ChatWindow
 * description: ����ڵ��� ä�� ������ ǥ���ϱ� ���� ä��â
 * @author 201937402 ������
 */
public class ChatWindow extends JPanel{
	public DefaultListModel Contents;
	public JList chats;
	public JScrollPane chatScroll;
	Box chatBox;
	JScrollBar vertical;
	
	public ChatWindow(int width, int height) { // �ʺ�� ���̸� �Է¹޾Ƽ� ���
		super();

		Contents = new DefaultListModel();
		setSize(width, height);

		chats = new JList(Contents); // �����ͷ� ����Ʈ ����
		chatScroll = new JScrollPane(chats); // ��ũ�� ������ ����Ʈ ����
		chatScroll.setPreferredSize(new Dimension(width, height));
		vertical = chatScroll.getVerticalScrollBar();
		add(chatScroll, BorderLayout.WEST);
	}
}