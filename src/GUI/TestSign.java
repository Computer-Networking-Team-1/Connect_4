package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Server.Protocol;

import java.io.*;
import java.net.Socket;

public class TestSign extends JFrame{
	/*임시로 만든 로그인/회원가입/대기실 화면*/
	Protocol response = null;
	public InformationBar text;
	public BtnPanel btns;
	Box mainbox;
	public TestSign() throws Exception {
		super("Sign");
	    setSize(300, 250);
	    mainbox = Box.createVerticalBox();
	    setLocationRelativeTo(null);
	    text = new InformationBar("Name");
	    btns = new BtnPanel("sign up", "challenge", 300, 40);
	    mainbox.add(text);
	    mainbox.add(btns);
	    add(mainbox);
	    setResizable(false);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
}
