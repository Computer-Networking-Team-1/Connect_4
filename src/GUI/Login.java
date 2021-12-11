package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: Sign
 * description: 로그인 화면
 */
public class Login extends JFrame {
	
	public Box mainBox, errorBox;
	
	/* Button
	 * Sign in 버튼: btn.btn1 (JButton)
	 * Sign up 버튼: btn.btn2 (JButton) */
	public BtnPanel btn;
	
	/* ID
	 * ID 입력창: ID.infoField (JTextField)
	 * JTextField: 입력한 문장을 getText()를 이용해 String type으로 return */
	public InformationBar idBar;
	
	/* Password
	 * PW 입력창: password.infoField (JTextFiled)
	 * JTextField: 입력한 문장을 getText()를 이용해 String type으로 return */
	public PasswordBar passwordBar;

	/* Error
	 * Error 접근 (JLabel)
	 * JLabel을 포함한 대부분의 component는 setText(String s)로 문구 변경 가능 */
	public JLabel error;
	
	public Login () {
	    // 로그인하는 프레임 생성
		super("Login");
	    setSize(300, 250);

	    // component들을 위에서 아래로 배치할 메인 박스
	    mainBox = Box.createVerticalBox();

	    // 화면이 모니터의 중앙에 위치하도록 설정
	    setLocationRelativeTo(null);

	    // ID 문구 + 입력창
	    idBar = new InformationBar("ID");

	    // PW 문구 + 입력창
	    passwordBar = new PasswordBar("password");

	    // Sign in, Sign up 버튼 (너비: 300, 높이: 40, 옆으로 나열) 2개
	    btn = new BtnPanel("Login", "Sign Up", 300, 40);

	    // 오류 문구 표시
	    error = new JLabel("여기에 오류 문구 출력");
	    // error.setForeground(new Color(255, 0, 0)); // 오류를 표시하는 공간을 보기 위해 넣은 것, 추후 삭제 요망

	    errorBox = Box.createHorizontalBox();
	    errorBox.add(error, BorderLayout.CENTER); // 오류 문구를 가운데에 배치

	    /* 메인 박스에 ID와 PW 입력창, 오류 문구, 로그인, 회원가입 버튼을 순서대로 넣기 */
	    mainBox.add(idBar);
	    mainBox.add(passwordBar);
	    mainBox.add(errorBox);
	    mainBox.add(btn);

	    // 프레임에 메인 박스 넣기
	    add(mainBox);
	   
	    // 화면 크기 고정
	    setResizable(false);
	    
	    // x 버튼 눌렀을 때 닫히도록 설정, 다른 기능 삽입 가능
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
	
	public static void main(String[] args) {
		new Login();
	}
}