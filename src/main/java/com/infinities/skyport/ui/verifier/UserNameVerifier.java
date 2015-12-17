package com.infinities.skyport.ui.verifier;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.infinities.skyport.ui.validate.TextValidator;

public class UserNameVerifier extends AbstractWarnableVerifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNameVerifier(JComponent component) {
		super(component);
	}

	// TODO check duplicate username
	@Override
	protected Error getErrorDefinition(JComponent c) {
		if (!(c instanceof JTextField)) {
			throw new IllegalArgumentException("Not a JTextField");
		}

		TextValidator validator = new TextValidator();

		if (validator.isValidText(new String(((JTextField) c).getText()))) {
			return new Error(Error.NO_ERROR, "");
		} else {
			return new Error(Error.ERROR, "name length should between 3 and 15. only allow letters,numbers.");
		}
	}
}
