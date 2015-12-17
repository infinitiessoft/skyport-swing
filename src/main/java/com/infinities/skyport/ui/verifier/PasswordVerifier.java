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
