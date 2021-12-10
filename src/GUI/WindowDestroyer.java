package GUI;

import java.awt.event.*;

public class WindowDestroyer extends WindowAdapter {
	public WindowDestroyer( ) {}
	
	@Override
	public void windowClosing(WindowEvent e) {
		super.windowClosing(e);
	}
}