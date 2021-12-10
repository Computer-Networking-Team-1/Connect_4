package GUI;

import java.awt.*;
import javax.swing.*;


/**
 * class: InfoBar
 * description: 특정한 정보를 입력하는 창을 생성
 * @author 201937402 강태훈
 */
public class InformationBar extends JPanel {
	JLabel infoLabel;
	public JTextField infoField;
	Box infoBox;

	public InformationBar(String info) {
		super();
	
		/* 입력받은 String을 무슨 정보로 입력해야 하는지 밝히는 라벨로 사용 */
		infoLabel = new JLabel(info);
		
		/* 정보를 입력할 필드 */
		infoField = new JTextField(20);

		/* component를 위에서 아래로 배치할 박스 */
		infoBox = Box.createVerticalBox();

		infoBox.add(infoLabel);
		infoBox.add(infoField);
		add(infoBox);
	}
}