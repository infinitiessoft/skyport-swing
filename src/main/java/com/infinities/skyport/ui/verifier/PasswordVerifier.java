package com.infinities.skyport.ui.verifier;

import javax.swing.JComponent;
import javax.swing.JPasswordField;

import com.infinities.skyport.ui.validate.TextValidator;

public class PasswordVerifier extends AbstractWarnableVerifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordVerifier(JComponent component) {
		super(component);
	}

	@Override
	protected Error getErrorDefinition(JComponent c) {
		if (!(c instanceof JPasswordField)) {
			throw new IllegalArgumentException("Not a JPasswordField");
		}

		TextValidator validator = new TextValidator();

		if (validator.isValidText(new String(((JPasswordField) c).getPassword()))) {
			return new Error(Error.NO_ERROR, "");
		} else {
			return new Error(Error.ERROR, "name length should between 3 and 15. only allow letters,numbers.");
		}

	}
}
