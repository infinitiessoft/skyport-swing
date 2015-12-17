package com.infinities.skyport.ui.verifier;

import javax.swing.JComboBox;
import javax.swing.JComponent;

public class ComboboxVerifier extends AbstractWarnableVerifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ComboboxVerifier(JComponent component) {
		super(component);
		if (!(component instanceof JComboBox)) {
			throw new IllegalArgumentException("Not a JComboBox");
		}
	}

	private boolean isValid(JComboBox<String> field) {
		return field.getSelectedItem() != null && !"".equals(field.getSelectedItem());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Error getErrorDefinition(JComponent c) {
		if (isValid((JComboBox<String>) c)) {
			return new Error(Error.NO_ERROR, "");
		} else {
			return new Error(Error.ERROR, "field cannot be empty");
		}

	}
}
