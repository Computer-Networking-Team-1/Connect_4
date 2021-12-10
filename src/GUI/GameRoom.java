package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
//대전 상대의 준비 상태 표시는 Opponent.OppoReady로 접근할 수 있습니다, type은 JLabel
//자신의 준비 상태를 변경하는 버튼은 User.ReadyBtn으로 접근할 수 있습니다, type은 JButton

/**
 * class: GameRoom
 * description: 게임이 진행 중일 때 보이는 화면
 * @author 201937402 강태훈
 */
public class GameRoom extends JFrame{
	
	public Box chatBox, playBox;
	
	/* 대전 상대
	 * 별명 접근: opponent.nickname (JLabel)
	 * 준비 상태 표시: opponent.readyStatus (JLabel)
	 * 전적 표시: opponent.profile (JLabel) */
	public UserInGame opponent;
	
	/* 본인
	 * 별명 접근: user.nickname (JLabel)
	 * 준비 상태 변경: user.readyBtn (JButton)
	 * 전적 표시: user.profile (JLabel)
	 * JLabel, JButton은 setText(String s)로 문구 변경 가능 */
	public UserInGame user;

	/* 채팅창
	 * 접근 방법: chatWindow.chatScroll (JScrollPane)
	 * 표시되는 내용: chatWindow.Contents (DefaultListModel) */
	public ChatWindow chatWindow;
	
	/* 채팅 입력창
	 * 접근 방법: chat.chatCon (JTextField)
	 * 입력된 내용 접근 방법: chat.chatCon.getText() (String)
	 * 엔터 버튼: chat.enter (JButton) */
	public ChatBar chat;
	
	/* 게임판
	 * 각 버튼들 접근 방법: gameBoard.btn[int index] (JButton)
	 * 각 칸들 접근 방법: gameBoard.cell[int row][int col] (JLabel)
	 * 아이콘 변경 방법: setIcon(Icon icon) (JLabel)
	 * 비어있는 아이콘: gameBoard.empty
	 * 빨간 말로 채워진 아이콘: gameBoard.red
	 * 노란 말로 채워진 아이콘: gameBoard.yellow */
	public Connect4 gameBoard;
	
	public GameRoom() throws IOException {
		super("Connect 4");
		
		// 채팅창과 채팅 입력창을 위아래로 담을 박스
		chatBox = Box.createVerticalBox();

		// 상대의 정보, 게임판, 나의 정보를 옆으로 담을 박스
		playBox = Box.createHorizontalBox();

		// 별명이 opponent nickname인 상대의 정보창
		// 세 번째 인자가 0일 경우 상대의 정보창
		opponent = new UserInGame(225, 490, 0, "opponent nickname");
		
		// 별명이 user nickname인 나의 정보창
		// 세 번째 인자가 0이 아닐 경우 자신의 정보창
		user = new UserInGame(225,490, 1, "user nickname");
		
		// 게임이 진행될 게임판
		gameBoard = new Connect4(550, 490);
		
		// 상대의 정보창, 게임판, 나의 정보창을 화면에 추가
		playBox.add(opponent);
		playBox.add(gameBoard);
		playBox.add(user);
		add(playBox);

		/* 채팅 입력창 */
		chat = new ChatBar(1000, 25);

		/* 채팅창 */
		chatWindow = new ChatWindow(970, 160);
		chatBox.add(chatWindow);
		chatBox.add(chat);
		
		add(chatBox, BorderLayout.SOUTH); // 채팅창과 채팅 입력창을 화면에 추가

		setSize(1000, 750);
		setResizable(false);
		setLocationRelativeTo(null); // 실행했을 때 모니터의 가운데에 오게끔
		setDefaultCloseOperation(EXIT_ON_CLOSE); // x 버튼 눌렀을 때 종료, 다른 행동 지정 가능
		setVisible(true); // 화면에 보이도록
	}
}