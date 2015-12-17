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
