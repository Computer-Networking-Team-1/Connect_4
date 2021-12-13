package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: ChatBar
 * description: ����ڰ� ä���� ������ �Է��ϴ� â, �� ������ ä��â���� ������ ��ư
 * @author 201937402 ������
 */
public class ChatBar extends JPanel {
	public JTextField chatCon;
	public JButton enter;
	
	public ChatBar(int width, int height) { // �ʺ�� ���̸� �Է¹޾Ƽ� ���
		super();

		chatCon = new JTextField(50); // ������ �Է��� �ʵ�
		enter = new JButton("Enter"); // ������ ä��â���� ���� ��ư
		
		enter.setPreferredSize(new Dimension(width / 10 - 10, height));
		chatCon.setPreferredSize(new Dimension(width * 9 / 10, height));
		setSize(width, height);
		
		/* �Է�â�� ��ư �߰� */
		add(chatCon, BorderLayout.LINE_START);
		add(enter, BorderLayout.LINE_END);
	}
}