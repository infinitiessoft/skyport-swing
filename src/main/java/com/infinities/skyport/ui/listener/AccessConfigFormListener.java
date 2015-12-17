package com.infinities.skyport.ui.listener;

import java.io.Serializable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.infinities.skyport.ui.verifier.FormVerifier;

public class AccessConfigFormListener implements DocumentListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FormVerifier formVerifier;


	public AccessConfigFormListener(FormVerifier formVerifier) {
		this.formVerifier = formVerifier;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		formVerifier.docsChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		formVerifier.docsChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		formVerifier.docsChanged();
	}

}
