package GUI;

import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * class: Connect4
 * description: �������� ����� class
 * @author 201937402 ������
 */
public class Connect4 extends JPanel {
	private final static int row = 6;
	private final static int column = 7;
	
	/* �� �������� ������ �Ǵ� ���� ��� ���� */
	String emptyPath = "./empty.png";
	String redPath = "./red.png";
	String yellowPath = "./yellow.png";

	Box btnRow, btnBox, mainBox;
	Box boardRow0, boardRow1, boardRow2, boardRow3, boardRow4, boardRow5;
	Box turnBox;
	public ImageIcon empty, red, yellow;
	
	
	public JButton[] btn = new JButton[column];
	public JLabel[][] cell = new JLabel[row][column]; // �������� �� ĭ�� ǥ���� ��
	
	public JLabel turn;
	
	public Connect4(int width, int height) throws IOException { // ȣ�� �� �ʺ�� ���� �Է¹���
		super();

		mainBox = Box.createVerticalBox();
		
		/* empty icon */
		empty = new ImageIcon(ImageIO.read(new File(emptyPath)));

		/* red icon */
		red = new ImageIcon(ImageIO.read(new File(redPath)));

		/* yellow icon */
		yellow = new ImageIcon(ImageIO.read(new File(yellowPath)));

		/* �ǿ� ���� ä��� ��ư�� ������ �迭�ϴ� �ڽ� */
		btnRow = Box.createHorizontalBox();

		for(int i = 0; i < column; i++) {
			btn[i] = new JButton("��");
			btn[i].setMinimumSize(new Dimension(width/column - 20, height/10));
			btn[i].setPreferredSize(new Dimension(width/column - 20, height/10));
			btn[i].setMaximumSize(new Dimension(width/column - 20, height/10));
			btnRow.add(btn[i]);
			
			for (int j = 0; j < row; j++) {
				cell[j][i] = new JLabel(empty);
				cell[j][i].setPreferredSize(new Dimension(width/column - 20, width/column - 20));
			}
		}
		
		/* column ����ŭ ��ư�� ����� ������ �迭
		 * ���ÿ� �������� �� ĭ�� empty�� �ʱ�ȭ */
		btnBox = Box.createVerticalBox();
		btnBox.add(btnRow, BorderLayout.CENTER);
		mainBox.add(btnBox); // �� ������ ġ��ġ�� ���� �����ϱ� ���� �ڽ��� �� �� �� ����
		
		/* �������� �� ���� ǥ���� �ڽ� */
		boardRow0 = Box.createHorizontalBox();
		boardRow1 = Box.createHorizontalBox();
		boardRow2 = Box.createHorizontalBox();
		boardRow3 = Box.createHorizontalBox();
		boardRow4 = Box.createHorizontalBox();
		boardRow5 = Box.createHorizontalBox();
		
		/* �� �࿡ �´� ���� �࿡ �߰� */
		for(int i = 0; i < column; i++) {
			boardRow0.add(cell[0][i]);
			boardRow1.add(cell[1][i]);
			boardRow2.add(cell[2][i]);
			boardRow3.add(cell[3][i]);
			boardRow4.add(cell[4][i]);
			boardRow5.add(cell[5][i]);
		}

		/* �����ǿ� ����� �߰� */
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
		
		/* ��ư�� ������ �߰� */
		add(mainBox, BorderLayout.CENTER);
		setBackground(new Color(255,255,255));
		setSize(width, height);
	}
}