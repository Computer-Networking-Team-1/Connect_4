package GUI;

import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * class: Connect4
 * description: 게임판을 만드는 class
 * @author 201937402 강태훈
 */
public class Connect4 extends JPanel {
	private final static int row = 6;
	private final static int column = 7;
	
	/* 각 아이콘의 원본이 되는 사진 경로 지정 */
	String emptyPath = "./empty.png";
	String redPath = "./red.png";
	String yellowPath = "./yellow.png";

	Box btnRow, btnBox, mainBox;
	Box boardRow0, boardRow1, boardRow2, boardRow3, boardRow4, boardRow5;
	Box turnBox;
	public ImageIcon empty, red, yellow;
	
	
	public JButton[] btn = new JButton[column];
	public JLabel[][] cell = new JLabel[row][column]; // 게임판의 각 칸을 표현할 라벨
	
	public JLabel turn;
	
	public Connect4(int width, int height) throws IOException { // 호출 시 너비와 높이 입력받음
		super();

		mainBox = Box.createVerticalBox();
		
		/* empty icon */
		empty = new ImageIcon(ImageIO.read(new File(emptyPath)));

		/* red icon */
		red = new ImageIcon(ImageIO.read(new File(redPath)));

		/* yellow icon */
		yellow = new ImageIcon(ImageIO.read(new File(yellowPath)));

		/* 판에 말을 채우는 버튼을 옆으로 배열하는 박스 */
		btnRow = Box.createHorizontalBox();

		for(int i = 0; i < column; i++) {
			btn[i] = new JButton("↓");
			btn[i].setMinimumSize(new Dimension(width/column - 20, height/10));
			btn[i].setPreferredSize(new Dimension(width/column - 20, height/10));
			btn[i].setMaximumSize(new Dimension(width/column - 20, height/10));
			btnRow.add(btn[i]);
			
			for (int j = 0; j < row; j++) {
				cell[j][i] = new JLabel(empty);
				cell[j][i].setPreferredSize(new Dimension(width/column - 20, width/column - 20));
			}
		}
		
		/* column 수만큼 버튼을 만들고 옆으로 배열
		 * 동시에 게임판의 각 칸을 empty로 초기화 */
		btnBox = Box.createVerticalBox();
		btnBox.add(btnRow, BorderLayout.CENTER);
		mainBox.add(btnBox); // 한 쪽으로 치우치는 것을 방지하기 위해 박스에 한 번 더 삽입
		
		/* 게임판의 각 행을 표시할 박스 */
		boardRow0 = Box.createHorizontalBox();
		boardRow1 = Box.createHorizontalBox();
		boardRow2 = Box.createHorizontalBox();
		boardRow3 = Box.createHorizontalBox();
		boardRow4 = Box.createHorizontalBox();
		boardRow5 = Box.createHorizontalBox();
		
		/* 각 행에 맞는 라벨을 행에 추가 */
		for(int i = 0; i < column; i++) {
			boardRow0.add(cell[0][i]);
			boardRow1.add(cell[1][i]);
			boardRow2.add(cell[2][i]);
			boardRow3.add(cell[3][i]);
			boardRow4.add(cell[4][i]);
			boardRow5.add(cell[5][i]);
		}

		/* 게임판에 행들을 추가 */
		mainBox.add(boardRow5);
		mainBox.add(boardRow4);
		mainBox.add(boardRow3);
		mainBox.add(boardRow2);
		mainBox.add(boardRow1);
		mainBox.add(boardRow0);
		
		turn = new JLabel();
		turnBox = Box.createHorizontalBox();
		turnBox.add(turn, BorderLayout.CENTER);
		mainBox.add(turnBox);
		
		/* 버튼과 게임판 추가 */
		add(mainBox, BorderLayout.CENTER);
		setBackground(new Color(255,255,255));
		setSize(width, height);
	}
}