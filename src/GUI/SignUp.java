package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: SignUp
 * description: 회원가입 화면 (로그인 화면과 유사)
 * @author 201937402 강태훈
 */
public class SignUp extends JFrame {
	private final static int width = 300;
	private final static int height = 100;
	
	// JTextField: 입력한 문장을 getText()를 이용하여 String type으로 return 가능
	// JLabel을 포함한 대부분의 component: setText(String s)로 문구를 변경 가능
	Box mainBox, errorBox;
	
	InformationBar ID; // ID 입력창 접근 방법: ID.infoField (JTextField)
	InformationBar name; // name 입력창 접근 방법: name.infoField (JTextField)
	InformationBar nickname; 
	InformationBar email; // e-mail 입력창 접근 방법: email.infoField (JTextField)
	PasswordBar PW, PWC; // PW 입력창 접근 방법: PW.infoField (JTextField)
	JLabel error; // error로  error 접근 (JLabel)
	
	/* Button
	 * Confirm 버튼: btn.btn1 (JButton)
	 * Cancel 버튼: btn.btn2 (JButton) */
	BtnPanel btn;
	
	public SignUp() {
	    super("Sign up");
	    setSize(300, 600);
	    
	    mainBox = Box.createVerticalBox(); // component를 위에서 아래로 배치할 메인 박스
	    setLocationRelativeTo(null); // 화면이 모니터의 중앙에 위치하도록 설정
	    
	    ID = new InformationBar("ID"); // ID 문구 + 입력창
	    PW = new PasswordBar("PW"); // PW 문구 + 입력창
	    PWC = new PasswordBar("PW retype"); // PWC 문구 + 입력창, PW 재확인 용도
	    name = new InformationBar("Name"); // name 문구 + 입력창, 이름 입력
	    nickname = new InformationBar("Nickname"); // nickname 문구 + 입력창, 별명 입력
	    email = new InformationBar("E-mail"); // e-mail 문구 + 입력창, 메일 입력
	    
	    // Confirm(확인) 버튼과 Cancel(취소) 버튼 (너비: 300, 높이: 40 범위 내에 옆으로 2개 생성)
	    btn = new BtnPanel("Confirm", "Cancel", 300, 40);
	    
	    error = new JLabel("여기에 오류 문구 출력"); // 오류 문구를 표시
	    // error.setForeground(new Color(255, 0, 0)); // 오류 문구 공간 확인용
	    errorBox = Box.createHorizontalBox();
	    errorBox.add(error, BorderLayout.CENTER); // 오류 문구를 가운데 배치
	    
	    // 메인 박스에 ID, PW, PW 재확인, 이름, 별명, 메일 입력창, 에러 문구, 확인 및 취소 버튼 삽입
	    mainBox.add(ID);
	    mainBox.add(PW);
	    mainBox.add(PWC);
	    mainBox.add(name);
	    mainBox.add(nickname);
	    mainBox.add(email);
	    mainBox.add(errorBox);
	    mainBox.add(btn);
	    
	    add(mainBox); // 메인 박스를 프레임에 넣음
	    setResizable(false); // 화면의 크기 고정
	    
	    // x 버튼을 눌렀을 때 화면이 닫힘, 다른 기능 삽입 가능
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
}
