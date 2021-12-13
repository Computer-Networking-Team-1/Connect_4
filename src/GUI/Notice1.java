package GUI;
import java.awt.*;
import javax.swing.*;

public class Notice1 extends JFrame {
	Box mainbox, labelbox, btnbox;
	public JLabel Notice;
	public JButton Btn;
	
	public Notice1() {
		super("Notice");
	    setSize(300, 250);
	    Notice = new JLabel("test");
	    setLocationRelativeTo(null);
	    mainbox = Box.createVerticalBox();
	    labelbox = Box.createHorizontalBox();
	    labelbox.setMinimumSize(new Dimension(200, 170));
	    labelbox.setPreferredSize(new Dimension(200, 170));
	    labelbox.setMaximumSize(new Dimension(200, 170));
	    labelbox.add(Notice, BorderLayout.CENTER);
	    mainbox.add(labelbox);
	    Btn = new JButton("»Æ¿Œ");
	    Btn.setMinimumSize(new Dimension(200, 40));
	    Btn.setPreferredSize(new Dimension(200, 40));
	    Btn.setMaximumSize(new Dimension(200, 40));
	    btnbox = Box.createHorizontalBox();
	    btnbox.add(Btn);
	    mainbox.add(btnbox);
	    add(mainbox);
	    setResizable(false);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
}
