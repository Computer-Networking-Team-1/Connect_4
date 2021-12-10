package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: Sign
 * description: �α��� ȭ��
 * @author 201937402 ������
 */
public class Sign extends JFrame {
	
	Box mainBox, errorBox;
	
	/* Button
	 * Sign in ��ư: btn.btn1 (JButton)
	 * Sign up ��ư: btn.btn2 (JButton) */
	BtnPanel btn;
	
	/* ID
	 * ID �Է�â: ID.infoField (JTextField)
	 * JTextField: �Է��� ������ getText()�� �̿��� String type���� return */
	InformationBar ID;
	
	/* Password
	 * PW �Է�â: password.infoField (JTextFiled)
	 * JTextField: �Է��� ������ getText()�� �̿��� String type���� return */
	PasswordBar password;

	/* Error
	 * Error ���� (JLabel)
	 * JLabel�� ������ ��κ��� component�� setText(String s)�� ���� ���� ���� */
	JLabel error;
	
	public Sign() {
	    // �α����ϴ� ������ ����
		super("Sign");
	    setSize(300, 250);

	    // component���� ������ �Ʒ��� ��ġ�� ���� �ڽ�
	    mainBox = Box.createVerticalBox();

	    // ȭ���� ������� �߾ӿ� ��ġ�ϵ��� ����
	    setLocationRelativeTo(null);

	    // ID ���� + �Է�â
	    ID = new InformationBar("ID");

	    // PW ���� + �Է�â
	    password = new PasswordBar("PW");

	    // Sign in, Sign up ��ư (�ʺ�: 300, ����: 40, ������ ����) 2��
	    btn = new BtnPanel("Sign in", "Sign up", 300, 40);

	    // ���� ���� ǥ��
	    error = new JLabel("���⿡ ���� ���� ���");
	    // error.setForeground(new Color(255, 0, 0)); // ������ ǥ���ϴ� ������ ���� ���� ���� ��, ���� ���� ���

	    errorBox = Box.createHorizontalBox();
	    errorBox.add(error, BorderLayout.CENTER); // ���� ������ ����� ��ġ

	    /* ���� �ڽ��� ID�� PW �Է�â, ���� ����, �α���, ȸ������ ��ư�� ������� �ֱ� */
	    mainBox.add(ID);
	    mainBox.add(password);
	    mainBox.add(errorBox);
	    mainBox.add(btn);

	    // �����ӿ� ���� �ڽ� �ֱ�
	    add(mainBox);
	   
	    // ȭ�� ũ�� ����
	    setResizable(false);
	    
	    // x ��ư ������ �� �������� ����, �ٸ� ��� ���� ����
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
}