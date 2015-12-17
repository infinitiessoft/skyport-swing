package com.infinities.skyport.ui.verifier;

import javax.swing.JComponent;
import javax.swing.JTextField;

public class TextVerifier extends AbstractWarnableVerifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TextVerifier(JComponent component) {
		super(component);
	}

	private boolean isValidText(JTextField field) {
		return field.getText() != null && !field.getText().trim().isEmpty();
	}

	@Override
	protected Error getErrorDefinition(JComponent c) {
		if (!(c instanceof JTextField)) {
			throw new IllegalArgumentException("Not a JTextField");
		}

		if (isValidText((JTextField) c)) {
			return new Error(Error.NO_ERROR, "");
		} else {
			return new Error(Error.ERROR, "field cannot be empty");
		}

	}
}
