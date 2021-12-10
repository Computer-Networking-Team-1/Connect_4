package GUI;

import java.io.*;
import java.awt.*;
import javax.swing.*;

/**
 * class: Test
 * description: 화면이 잘 생성되는지 확인
 * @author 201937402 강태훈
 */
public class Test extends JFrame {
	public static void main(String[] args) throws IOException {
		
		//WaitingRoom f = new WaitingRoom();
		//Sign s = new Sign();
		//SignUp su = new SignUp();
		
		/* 테스트용으로 cell의 Label의 아이콘 변경한 것 */
		GameRoom g = new GameRoom();
		g.gameBoard.cell[1][1].setIcon(g.gameBoard.red);
		g.gameBoard.cell[2][2].setIcon(g.gameBoard.yellow);
	}
}