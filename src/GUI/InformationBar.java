package GUI;

import java.awt.*;
import javax.swing.*;


/**
 * class: InfoBar
 * description: Ư���� ������ �Է��ϴ� â�� ����
 * @author 201937402 ������
 */
public class InformationBar extends JPanel {
	JLabel infoLabel;
	public JTextField infoField;
	Box infoBox;

	public InformationBar(String info) {
		super();
	
		/* �Է¹��� String�� ���� ������ �Է��ؾ� �ϴ��� ������ �󺧷� ��� */
		infoLabel = new JLabel(info);
		
		/* ������ �Է��� �ʵ� */
		infoField = new JTextField(20);

		/* component�� ������ �Ʒ��� ��ġ�� �ڽ� */
		infoBox = Box.createVerticalBox();

		infoBox.add(infoLabel);
		infoBox.add(infoField);
		add(infoBox);
	}
}