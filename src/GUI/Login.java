package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: Sign
 * description: �α��� ȭ��
 */
public class Login extends JFrame {
	
	public Box mainBox, errorBox;
	
	/* Button
	 * Sign in ��ư: btn.btn1 (JButton)
	 * Sign up ��ư: btn.btn2 (JButton) */
	public BtnPanel btn;
	
	/* ID
	 * ID �Է�â: ID.infoField (JTextField)
	 * JTextField: �Է��� ������ getText()�� �̿��� String type���� return */
	public InformationBar idBar;
	
	/* Password
	 * PW �Է�â: password.infoField (JTextFiled)
	 * JTextField: �Է��� ������ getText()�� �̿��� String type���� return */
	public PasswordBar passwordBar;

	/* Error
	 * Error ���� (JLabel)
	 * JLabel�� ������ ��κ��� component�� setText(String s)�� ���� ���� ���� */
	public JLabel error;
	
	public Login () {
	    // �α����ϴ� ������ ����
		super("Login");
	    setSize(300, 250);

	    // component���� ������ �Ʒ��� ��ġ�� ���� �ڽ�
	    mainBox = Box.createVerticalBox();

	    // ȭ���� ������� �߾ӿ� ��ġ�ϵ��� ����
	    setLocationRelativeTo(null);

	    // ID ���� + �Է�â
	    idBar = new InformationBar("ID");

	    // PW ���� + �Է�â
	    passwordBar = new PasswordBar("password");

	    // Sign in, Sign up ��ư (�ʺ�: 300, ����: 40, ������ ����) 2��
	    btn = new BtnPanel("Login", "Sign Up", 300, 40);

	    // ���� ���� ǥ��
	    error = new JLabel("���⿡ ���� ���� ���");
	    // error.setForeground(new Color(255, 0, 0)); // ������ ǥ���ϴ� ������ ���� ���� ���� ��, ���� ���� ���

	    errorBox = Box.createHorizontalBox();
	    errorBox.add(error, BorderLayout.CENTER); // ���� ������ ����� ��ġ

	    /* ���� �ڽ��� ID�� PW �Է�â, ���� ����, �α���, ȸ������ ��ư�� ������� �ֱ� */
	    mainBox.add(idBar);
	    mainBox.add(passwordBar);
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
	
	public static void main(String[] args) {
		new Login();
	}
}