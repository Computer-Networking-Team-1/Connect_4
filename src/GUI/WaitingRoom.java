package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: WaitingRoom
 * description: 대기실 화면
 * @author 201937402 강태훈
 */
public class WaitingRoom extends JFrame {
	
	Box mainBox;
	
	/* 채팅 입력창
	 * 접근 방법: chat.chatCon (JTextField)
	 * 엔터 버튼: chat.enter (JButton)
	 * JTextField: 입력한 내용을 getText()를 이용해 String type으로 return 가능 */
	ChatBar chat;
	
	/* 채팅창
	 * 출력: chatWindow.chatScroll (JScrollPane)
	 * 출력할 내용 수정: chatWindow 안의 DefaultListModel 접근 (현재는 chatWindow.test) */
	ChatWindow chatWindow;

	/* 접속자들
	 * 접속자들 출력: currentUsers.useScroll (JScrollPane)
	 * 접속자들 명단 수정: currentUsers 안의 DefaultListModel 접근 (현재는 currentUsers.test)
	 * 사용자의 전적 표시: currentUsers.userStatus (JLabel)
	 * 대결 신청 버튼: currentUsers.userBtn.btn1 (JButton)
	 * 전적 확인 버튼: currentUsers.userBtn.btn2 (JButton)
	 * 정보 수정 버튼: currentUsers.miscBtn.btn1 (JButton)
	 * 나가기 버튼: currentUsers.miscBtn.btn2 (JButton)
	 * setText(String s): JLabel 내용 변경 가능 */
	UserList currentUsers;
	
	public WaitingRoom() {
		super("Waiting room");
		setSize(1000, 700);
		
		// 사용자가 채팅할 내용을 입력할 창과 그 내용을 채팅창으로 보낼 버튼
		chat = new ChatBar(1000, 25);

		// 사용자들의 채팅 내용을 표시할 채팅창
		chatWindow = new ChatWindow(670, 620);

		// 접속한 사용자들의 별명을 표시
		currentUsers = new UserList(300, 640);

		// component를 왼쪽에서 오른쪽으로 배치할 메인 박스
		mainBox = Box.createHorizontalBox();

		// 메인 박스에 채팅창을 넣음
		mainBox.add(chatWindow);

		// 메인 박스에 접속자 리스트와 버튼들 삽입
		mainBox.add(currentUsers);

		// 채팅창과 접속자 리스트 밑에 채팅 입력창 배치
		add(mainBox, BorderLayout.WEST);
		add(chat, BorderLayout.SOUTH);
		setLocationRelativeTo(null); // 화면이 모니터의 중앙에 위치
		setResizable(false); // 화면의 크기 고정
	    setDefaultCloseOperation(EXIT_ON_CLOSE); // x 버튼 누르면 화면 종료
		setVisible(true);
	}
}