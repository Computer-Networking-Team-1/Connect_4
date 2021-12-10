package GUI;

import java.awt.*;
import javax.swing.*;

public class Result extends JFrame {
	JLabel result;
	JButton back;
	Box btnBox, mainBox;
	public Result(boolean win) {
		super("Game Result");
		if(win) {
			result = new JLabel("�¸��߽��ϴ�");
		}
		else {
			result = new JLabel("�й��߽��ϴ�");
		}
		mainBox = Box.createHorizontalBox();
		mainBox.setPreferredSize(new Dimension(400, 300));
		btnBox = Box.createVerticalBox();
		btnBox.add(result);
		back = new JButton("Go back");
		back.setMinimumSize(new Dimension(150, 40));
		back.setPreferredSize(new Dimension(150, 40));
		back.setMaximumSize(new Dimension(150, 40));
		btnBox.add(back, BorderLayout.CENTER);
		mainBox.add(btnBox, BorderLayout.CENTER);
		add(mainBox);
		setSize(400, 300);
		setResizable(false);
		setLocationRelativeTo(null); // �������� �� ������� ����� ���Բ�
		setDefaultCloseOperation(EXIT_ON_CLOSE); // x ��ư ������ �� ����, �ٸ� �ൿ ���� ����
		setVisible(true); // ȭ�鿡 ���̵���
	}
}