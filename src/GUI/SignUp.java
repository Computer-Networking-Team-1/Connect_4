package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: SignUp
 * description: ȸ������ ȭ�� (�α��� ȭ��� ����)
 */
public class SignUp extends JFrame {
	private final static int width = 300;
	private final static int height = 100;
	
	// JTextField: �Է��� ������ getText()�� �̿��Ͽ� String type���� return ����
	// JLabel�� ������ ��κ��� component: setText(String s)�� ������ ���� ����
	public Box mainBox, errorBox;
	
	public InformationBar idBar;		// ID �Է�â ���� ���: ID.infoField (JTextField)
	public InformationBar nameBar;	// name �Է�â ���� ���: name.infoField (JTextField)
	public InformationBar nicknameBar; 
	public InformationBar emailBar;	// e-mail �Է�â ���� ���: email.infoField (JTextField)
	public InformationBar siteBar;		// SNS or Ȩ������
	public PasswordBar passwordBar, rePasswordBar;	// PW �Է�â ���� ���: PW.infoField (JTextField)
	public JLabel error;			// error��  error ���� (JLabel)
	
	/* Button
	 * Confirm ��ư: btn.btn1 (JButton)
	 * Cancel ��ư: btn.btn2 (JButton) */
	public BtnPanel btn;
	
	public SignUp() {
	    super("Sign up");
	    setSize(300, 600);
	    
	    mainBox = Box.createVerticalBox(); // component�� ������ �Ʒ��� ��ġ�� ���� �ڽ�
	    setLocationRelativeTo(null); // ȭ���� ������� �߾ӿ� ��ġ�ϵ��� ����
	    
	    idBar = new InformationBar("ID");
	    passwordBar = new PasswordBar("password");
	    rePasswordBar = new PasswordBar("re-password");
	    nameBar = new InformationBar("name");
	    nicknameBar = new InformationBar("nickname");
	    emailBar = new InformationBar("e-mail");
	    siteBar = new InformationBar("SNS or Homepage");
	    
	    // Confirm(Ȯ��) ��ư�� Cancel(���) ��ư (�ʺ�: 300, ����: 40 ���� ���� ������ 2�� ����)
	    btn = new BtnPanel("Confirm", "Cancel", 300, 40);
	    
	    error = new JLabel("���⿡ ���� ���� ���"); // ���� ������ ǥ��
	    // error.setForeground(new Color(255, 0, 0)); // ���� ���� ���� Ȯ�ο�
	    errorBox = Box.createHorizontalBox();
	    errorBox.add(error, BorderLayout.CENTER); // ���� ������ ��� ��ġ
	    
	    // ���� �ڽ��� ID, PW, PW ��Ȯ��, �̸�, ����, ���� �Է�â, ���� ����, Ȯ�� �� ��� ��ư ����
	    mainBox.add(idBar);
	    mainBox.add(passwordBar);
	    mainBox.add(rePasswordBar);
	    mainBox.add(nameBar);
	    mainBox.add(nicknameBar);
	    mainBox.add(emailBar);
	    mainBox.add(siteBar);
	    mainBox.add(errorBox);
	    mainBox.add(btn);

	    add(mainBox); // ���� �ڽ��� �����ӿ� ����
	    setResizable(false); // ȭ���� ũ�� ����
	    
	    // x ��ư�� ������ �� ȭ���� ����, �ٸ� ��� ���� ����
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
	
	public static void main(String[] args) {
		new SignUp();
	}
}
