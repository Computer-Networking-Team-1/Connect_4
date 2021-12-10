package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: PWBar
 * description: ��й�ȣó�� ȭ�鿡 ����Ǹ� �� �Ǵ� ������ �Է��ϴ� â�� ����
 * @author 201937402 ������
 */
public class PasswordBar extends JPanel{
	JLabel infoLabe;
	JPasswordField infoField;
	Box infoBox;

	public PasswordBar(String info) {
		super();
		
		// �Է¹��� String�� ���� ������ �Է��ؾ� �ϴ��� ������ ��
		infoLabe = new JLabel(info);

		// ����Ǹ� �� �Ǵ� ������ �Է�
		infoField = new JPasswordField(20);

		// ������Ʈ���� ������ �Ʒ��� ��ġ�� �ڽ�
		infoBox = Box.createVerticalBox();

		infoBox.add(infoLabe);
		infoBox.add(infoField);
		add(infoBox);
	}
}