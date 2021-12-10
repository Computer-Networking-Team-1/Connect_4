package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: BtnPanel
 * description: �̸��� �� String 2��, �ʺ�, ���̸� �Է¹ް� ������ �迭�� ��ư 2���� ����
 * @author 201937402 ������
 */
public class BtnPanel extends JPanel {
	public JButton btn1;
	// JPanel rightPanel, leftPanel;
	public JButton btn2;

	public BtnPanel(String btn1, String btn2, int width, int height) {
		super();
		setSize(width, height);

		this.btn1 = new JButton(btn1);
		this.btn2 = new JButton(btn2);
		
		/* �Է¹��� 2���� String�� �� ��ư�� ǥ�� */
		this.btn1.setPreferredSize(new Dimension(width / 2 - 50, height));
		this.btn2.setPreferredSize(new Dimension(width / 2 - 50, height));
		
		/* ��ư 2���� �� �� �ֵ��� �ʺ�� ���� ���Ϸ� ��� */
		add(this.btn1, BorderLayout.WEST); // ù ��° ��ư�� ���ʿ� �߰�
		add(this.btn2, BorderLayout.EAST); // �� ��° ��ư�� �����ʿ� �߰�
	}
}