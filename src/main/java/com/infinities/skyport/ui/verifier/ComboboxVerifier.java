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
