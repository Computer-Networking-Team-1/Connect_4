package GUI;

import java.io.*;
import java.awt.*;
import javax.swing.*;

/**
 * class: Test
 * description: ȭ���� �� �����Ǵ��� Ȯ��
 * @author 201937402 ������
 */
public class Test extends JFrame {
	public static void main(String[] args) throws IOException {
		
		//WaitingRoom f = new WaitingRoom();
		//Sign s = new Sign();
		//SignUp su = new SignUp();
		
		/* �׽�Ʈ������ cell�� Label�� ������ ������ �� */
		GameRoom g = new GameRoom();
		g.gameBoard.cell[1][1].setIcon(g.gameBoard.red);
		g.gameBoard.cell[2][2].setIcon(g.gameBoard.yellow);
	}
}