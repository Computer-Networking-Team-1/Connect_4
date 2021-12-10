package GUI;

import java.awt.*;
import javax.swing.*;

public class Result extends JFrame {
	JLabel result;
	JButton back;
	Box btnBox, mainBox;
	public Result(boolean win) {
		super("Game Result");
		if(win) {
			result = new JLabel("승리했습니다");
		}
		else {
			result = new JLabel("패배했습니다");
		}
		mainBox = Box.createHorizontalBox();
		mainBox.setPreferredSize(new Dimension(400, 300));
		btnBox = Box.createVerticalBox();
		btnBox.add(result);
		back = new JButton("Go back");
		back.setMinimumSize(new Dimension(150, 40));
		back.setPreferredSize(new Dimension(150, 40));
		back.setMaximumSize(new Dimension(150, 40));
		btnBox.add(back, BorderLayout.CENTER);
		mainBox.add(btnBox, BorderLayout.CENTER);
		add(mainBox);
		setSize(400, 300);
		setResizable(false);
		setLocationRelativeTo(null); // 실행했을 때 모니터의 가운데에 오게끔
		setDefaultCloseOperation(EXIT_ON_CLOSE); // x 버튼 눌렀을 때 종료, 다른 행동 지정 가능
		setVisible(true); // 화면에 보이도록
	}
}