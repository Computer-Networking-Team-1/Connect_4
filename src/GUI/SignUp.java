package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: SignUp
 * description: ȸ������ ȭ�� (�α��� ȭ��� ����)
 * @author 201937402 ������
 */
public class SignUp extends JFrame {
	private final static int width = 300;
	private final static int height = 100;
	
	// JTextField: �Է��� ������ getText()�� �̿��Ͽ� String type���� return ����
	// JLabel�� ������ ��κ��� component: setText(String s)�� ������ ���� ����
	Box mainBox, errorBox;
	
	InformationBar ID; // ID �Է�â ���� ���: ID.infoField (JTextField)
	InformationBar name; // name �Է�â ���� ���: name.infoField (JTextField)
	InformationBar nickname; 
	InformationBar email; // e-mail �Է�â ���� ���: email.infoField (JTextField)
	PasswordBar PW, PWC; // PW �Է�â ���� ���: PW.infoField (JTextField)
	JLabel error; // error��  error ���� (JLabel)
	
	/* Button
	 * Confirm ��ư: btn.btn1 (JButton)
	 * Cancel ��ư: btn.btn2 (JButton) */
	BtnPanel btn;
	
	public SignUp() {
	    super("Sign up");
	    setSize(300, 600);
	    
	    mainBox = Box.createVerticalBox(); // component�� ������ �Ʒ��� ��ġ�� ���� �ڽ�
	    setLocationRelativeTo(null); // ȭ���� ������� �߾ӿ� ��ġ�ϵ��� ����
	    
	    ID = new InformationBar("ID"); // ID ���� + �Է�â
	    PW = new PasswordBar("PW"); // PW ���� + �Է�â
	    PWC = new PasswordBar("PW retype"); // PWC ���� + �Է�â, PW ��Ȯ�� �뵵
	    name = new InformationBar("Name"); // name ���� + �Է�â, �̸� �Է�
	    nickname = new InformationBar("Nickname"); // nickname ���� + �Է�â, ���� �Է�
	    email = new InformationBar("E-mail"); // e-mail ���� + �Է�â, ���� �Է�
	    
	    // Confirm(Ȯ��) ��ư�� Cancel(���) ��ư (�ʺ�: 300, ����: 40 ���� ���� ������ 2�� ����)
	    btn = new BtnPanel("Confirm", "Cancel", 300, 40);
	    
	    error = new JLabel("���⿡ ���� ���� ���"); // ���� ������ ǥ��
	    // error.setForeground(new Color(255, 0, 0)); // ���� ���� ���� Ȯ�ο�
	    errorBox = Box.createHorizontalBox();
	    errorBox.add(error, BorderLayout.CENTER); // ���� ������ ��� ��ġ
	    
	    // ���� �ڽ��� ID, PW, PW ��Ȯ��, �̸�, ����, ���� �Է�â, ���� ����, Ȯ�� �� ��� ��ư ����
	    mainBox.add(ID);
	    mainBox.add(PW);
	    mainBox.add(PWC);
	    mainBox.add(name);
	    mainBox.add(nickname);
	    mainBox.add(email);
	    mainBox.add(errorBox);
	    mainBox.add(btn);
	    
	    add(mainBox); // ���� �ڽ��� �����ӿ� ����
	    setResizable(false); // ȭ���� ũ�� ����
	    
	    // x ��ư�� ������ �� ȭ���� ����, �ٸ� ��� ���� ����
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
}
