package GUI;

import java.awt.*;
import javax.swing.*;

/**
 * class: PWBar
 * description: 비밀번호처럼 화면에 노출되면 안 되는 정보를 입력하는 창을 생성
 * @author 201937402 강태훈
 */
public class PasswordBar extends JPanel{
	JLabel infoLabe;
	JPasswordField infoField;
	Box infoBox;

	public PasswordBar(String info) {
		super();
		
		// 입력받은 String을 무슨 정보로 입력해야 하는지 밝히는 라벨
		infoLabe = new JLabel(info);

		// 노출되면 안 되는 정보를 입력
		infoField = new JPasswordField(20);

		// 컴포넌트들을 위에서 아래로 배치할 박스
		infoBox = Box.createVerticalBox();

		infoBox.add(infoLabe);
		infoBox.add(infoField);
		add(infoBox);
	}
}