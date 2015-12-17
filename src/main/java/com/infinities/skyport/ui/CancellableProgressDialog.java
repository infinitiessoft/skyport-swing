package com.infinities.skyport.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class CancellableProgressDialog {

	private JDialog dialog;
	private JButton cancel;


	public CancellableProgressDialog(Window owner, String title, String text, ModalityType modalityType) {
		setUpDialog(owner, title, text, modalityType);
	}

	private void setUpDialog(Window owner, String title, String text, ModalityType modalityType) {
		cancel = new JButton("cancel");
		dialog = new JDialog(owner, title, modalityType);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(400, 20));
		progressBar.setIndeterminate(true);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(cancel, BorderLayout.LINE_END);
		panel.add(new JLabel(text), BorderLayout.PAGE_START);
		dialog.setUndecorated(true);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);

		dialog.setAlwaysOnTop(true);
		dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		dialog.setResizable(false);
	}

	public void setVisible(boolean visible) {
		dialog.setVisible(true);
	}

	public void dispose() {
		dialog.dispose();
	}

	public void addCancelActionListener(ActionListener listener) {
		cancel.addActionListener(listener);
	}
	
	public void setButtonText(String text){
		cancel.setText(text);
	}
}
