package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Notice2 extends JFrame {
	Box mainbox, labelbox;
	public JLabel Notice;
	public BtnPanel Btns;
	
	public Notice2() {
		super("Notice");
	    setSize(300, 250);
	    Notice = new JLabel("test");
	    setLocationRelativeTo(null);
	    mainbox = Box.createVerticalBox();
	    labelbox = Box.createHorizontalBox();
	    labelbox.setMinimumSize(new Dimension(200, 160));
	    labelbox.setPreferredSize(new Dimension(200, 160));
	    labelbox.setMaximumSize(new Dimension(200, 160));
	    labelbox.add(Notice, BorderLayout.CENTER);
	    mainbox.add(labelbox);
	    Btns = new BtnPanel("수락", "거절", 300, 40);
	    mainbox.add(Btns);
	    add(mainbox);
	    setResizable(false);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}

}
