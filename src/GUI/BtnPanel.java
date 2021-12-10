package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: BtnPanel
 * description: 이름이 될 String 2개, 너비, 높이를 입력받고 옆으로 배열된 버튼 2개를 생성
 * @author 201937402 강태훈
 */
public class BtnPanel extends JPanel {
	public JButton btn1;
	// JPanel rightPanel, leftPanel;
	public JButton btn2;

	public BtnPanel(String btn1, String btn2, int width, int height) {
		super();
		setSize(width, height);

		this.btn1 = new JButton(btn1);
		this.btn2 = new JButton(btn2);
		
		/* 입력받은 2개의 String을 각 버튼에 표시 */
		this.btn1.setPreferredSize(new Dimension(width / 2 - 50, height));
		this.btn2.setPreferredSize(new Dimension(width / 2 - 50, height));
		
		/* 버튼 2개가 들어갈 수 있도록 너비는 절반 이하로 축소 */
		add(this.btn1, BorderLayout.WEST); // 첫 번째 버튼을 왼쪽에 추가
		add(this.btn2, BorderLayout.EAST); // 두 번째 버튼을 오른쪽에 추가
	}
}