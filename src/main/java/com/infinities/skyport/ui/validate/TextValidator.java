package com.infinities.skyport.ui.validate;

import java.io.Serializable;

import com.google.common.base.Strings;

public class TextValidator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	// private Pattern pattern;
	// private static final String PATTERN = "^[a-zA-Z0-9_-]{3,50}$";

	public TextValidator() {
		// pattern = Pattern.compile(PATTERN);
	}

	public boolean isValidText(String text) {
		// checkNotNull(text);
		// Matcher matcher = pattern.matcher(text);
		return !Strings.isNullOrEmpty(text);
		// return matcher.matches();
	}

}
