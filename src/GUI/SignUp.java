package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: SignUp
 * description: 회원가입 화면 (로그인 화면과 유사)
 */
public class SignUp extends JFrame {
	private final static int width = 300;
	private final static int height = 100;
	
	// JTextField: 입력한 문장을 getText()를 이용하여 String type으로 return 가능
	// JLabel을 포함한 대부분의 component: setText(String s)로 문구를 변경 가능
	public Box mainBox, errorBox;
	
	public InformationBar idBar;		// ID 입력창 접근 방법: ID.infoField (JTextField)
	public InformationBar nameBar;	// name 입력창 접근 방법: name.infoField (JTextField)
	public InformationBar nicknameBar; 
	public InformationBar emailBar;	// e-mail 입력창 접근 방법: email.infoField (JTextField)
	public InformationBar siteBar;		// SNS or 홈페이지
	public PasswordBar passwordBar, rePasswordBar;	// PW 입력창 접근 방법: PW.infoField (JTextField)
	public JLabel error;			// error로  error 접근 (JLabel)
	
	/* Button
	 * Confirm 버튼: btn.btn1 (JButton)
	 * Cancel 버튼: btn.btn2 (JButton) */
	public BtnPanel btn;
	
	public SignUp() {
	    super("Sign up");
	    setSize(300, 600);
	    
	    mainBox = Box.createVerticalBox(); // component를 위에서 아래로 배치할 메인 박스
	    setLocationRelativeTo(null); // 화면이 모니터의 중앙에 위치하도록 설정
	    
	    idBar = new InformationBar("ID");
	    passwordBar = new PasswordBar("password");
	    rePasswordBar = new PasswordBar("re-password");
	    nameBar = new InformationBar("name");
	    nicknameBar = new InformationBar("nickname");
	    emailBar = new InformationBar("e-mail");
	    siteBar = new InformationBar("SNS or Homepage");
	    
	    // Confirm(확인) 버튼과 Cancel(취소) 버튼 (너비: 300, 높이: 40 범위 내에 옆으로 2개 생성)
	    btn = new BtnPanel("Confirm", "Cancel", 300, 40);
	    
	    error = new JLabel("여기에 오류 문구 출력"); // 오류 문구를 표시
	    // error.setForeground(new Color(255, 0, 0)); // 오류 문구 공간 확인용
	    errorBox = Box.createHorizontalBox();
	    errorBox.add(error, BorderLayout.CENTER); // 오류 문구를 가운데 배치
	    
	    // 메인 박스에 ID, PW, PW 재확인, 이름, 별명, 메일 입력창, 에러 문구, 확인 및 취소 버튼 삽입
	    mainBox.add(idBar);
	    mainBox.add(passwordBar);
	    mainBox.add(rePasswordBar);
	    mainBox.add(nameBar);
	    mainBox.add(nicknameBar);
	    mainBox.add(emailBar);
	    mainBox.add(siteBar);
	    mainBox.add(errorBox);
	    mainBox.add(btn);

	    add(mainBox); // 메인 박스를 프레임에 넣음
	    setResizable(false); // 화면의 크기 고정
	    
	    // x 버튼을 눌렀을 때 화면이 닫힘, 다른 기능 삽입 가능
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
	
	public static void main(String[] args) {
		new SignUp();
	}
}
