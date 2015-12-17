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
