package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: UserList
 * description: 접속한 사용자들의 별명을 표시
 * comment: 구현할 때 사용자 파일에서 별명 뿐만 아니라 ID까지 가져와야 함
 * @author 201937402 강태훈
 */
public class UserList extends JPanel {
	public DefaultListModel content;
	Box userBox, statusBox;
	public JList users;
	public JLabel userStatus;
	public JScrollPane userScroll;
	public BtnPanel userBtn;
	public BtnPanel miscBtn;
	
	public UserList(int width, int height) { // 너비와 높이를 입력받아 사용
		super();
		
		// component를 위에서 아래로 배치하기 위한 박스
		userBox = Box.createVerticalBox();
		userBox.setSize(width, height);
		
		content = new DefaultListModel();
		
		
		// 데이터로 리스트 생성
		users = new JList(content);

		// 스크롤이 있는 리스트 생성
		userScroll = new JScrollPane(users);
		
		// 입력받은 높이의 2/3 정도만 사용
		userScroll.setPreferredSize(new Dimension(width, height * 2 / 3));

		// 박스에 스크롤이 있는 데이터의 리스트를 넣음
		userBox.add(userScroll);
		
		// 사용자를 선택하고 '전적 확인' 버튼을 눌렀을 때 승패 기록, 승률 등을 표시할 라벨
		userStatus = new JLabel("user's record displayed here");
		
		userStatus.setMinimumSize(new Dimension(width, height / 6));
		userStatus.setPreferredSize(new Dimension(width, height / 6));
		userStatus.setMaximumSize(new Dimension(width, height / 6));
		
		statusBox = Box.createHorizontalBox();
		
		// 사용자 전적 라벨을 가운데에 배치하기 위해 별도의 박스 사용
		statusBox.add(userStatus, BorderLayout.CENTER);
		
		// 박스에 사용자 전적 라벨 삽입
		userBox.add(statusBox);

		// 옆으로 배열된 대결 신청 버튼과 전적 확인 버튼을 만들고 박스에 넣음
		userBtn = new BtnPanel("대결 신청", "전적 확인", width, (height / 12) - 20);
		userBox.add(userBtn);
		
		// 옆으로 배열된 정보 수정 버튼과 나가기 버튼을 만들고 박스에 넣음
		miscBtn = new BtnPanel("정보 수정", "나가기", width, (height / 12) - 20);
		userBox.add(miscBtn);
		setSize(width, height);
		add(userBox);
	}
}