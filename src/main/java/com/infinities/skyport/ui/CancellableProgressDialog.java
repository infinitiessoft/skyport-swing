/*******************************************************************************
 * Copyright 2015 InfinitiesSoft Solutions Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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
