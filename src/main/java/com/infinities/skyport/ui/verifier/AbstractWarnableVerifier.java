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

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.border.Border;

public abstract class AbstractWarnableVerifier extends InputVerifier implements ActionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Border originalBorder;
	private Color originalBackgroundColor;
	private String originalTooltipText;


	public AbstractWarnableVerifier(JComponent c) {
		originalBorder = c.getBorder();
		if (originalBorder == null) {
			throw new IllegalArgumentException("originalBorder cannot be null");
		}
		originalBackgroundColor = c.getBackground();
		originalTooltipText = c.getToolTipText();

	}

	protected abstract Error getErrorDefinition(JComponent c);

	@Override
	public boolean verify(JComponent c) {
		Error error = getErrorDefinition(c);

		return !(error.getErrorType() == Error.ERROR);
	}

	@Override
	public boolean shouldYieldFocus(JComponent input) {
		boolean inputOK = verify(input);

		if (inputOK) {
			input.setBackground(originalBackgroundColor);
			input.setBorder(originalBorder);
			input.setToolTipText(originalTooltipText);
			return true;
		}
		Error error = getErrorDefinition(input);
		input.setBorder(new IconBorder(error.getImage(), originalBorder));
		input.setBackground(error.getColor());
		input.setToolTipText(error.getMessage());
		input.setInputVerifier(null);
		input.setInputVerifier(this);
		Toolkit.getDefaultToolkit().beep();

		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComponent source = (JComponent) e.getSource();
		shouldYieldFocus(source);
	}
}
