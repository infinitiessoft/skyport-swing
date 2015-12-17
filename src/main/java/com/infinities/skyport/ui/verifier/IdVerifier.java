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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.infinities.skyport.cache.CachedServiceProvider;
import com.infinities.skyport.service.ConfigurationHome;

public class IdVerifier extends AbstractWarnableVerifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(IdVerifier.class);
	private final ConfigurationHome configurationHome;
	private final String id;


	public IdVerifier(JComponent component, String id, ConfigurationHome configurationHome) {
		super(component);
		this.id = id;
		this.configurationHome = configurationHome;
	}

	private boolean isUnique(JTextField field) {
		if (!Strings.isNullOrEmpty(id) && id.equals(field.getText())) {
			return true;
		}

		try {
			CachedServiceProvider serviceProvider = configurationHome.findById(field.getText());
			if (serviceProvider == null) {
				return true;
			}
		} catch (java.lang.IllegalArgumentException e) {
			logger.debug("cannot found identical name in home service");
		}
		return false;
	}

	@Override
	protected Error getErrorDefinition(JComponent c) {
		if (!(c instanceof JTextField)) {
			throw new IllegalArgumentException("Not a JTextField");
		}

		if (!isUnique((JTextField) c)) {
			return new Error(Error.ERROR, "duplicate id");
		} else {
			return new Error(Error.NO_ERROR, "");
		}
	}
}
