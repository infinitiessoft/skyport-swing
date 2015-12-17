package com.infinities.skyport.ui.verifier;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinities.skyport.cache.CachedServiceProvider;
import com.infinities.skyport.model.configuration.Configuration;
import com.infinities.skyport.service.ConfigurationHome;
import com.infinities.skyport.ui.validate.TextValidator;

public class NameVerifier extends AbstractWarnableVerifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(NameVerifier.class);
	private final Configuration configuration;
	private final ConfigurationHome configurationHome;


	public NameVerifier(JComponent component, Configuration configuration, ConfigurationHome configurationHome) {
		super(component);
		this.configuration = configuration;
		this.configurationHome = configurationHome;
	}

	private boolean isUnique(JTextField field) {
		Configuration configuration;
		try {
			CachedServiceProvider serviceProvider = configurationHome.findByName(field.getText());
			if (serviceProvider == null) {
				return true;
			}
			configuration = serviceProvider.getConfiguration();
		} catch (java.lang.IllegalArgumentException e) {
			logger.debug("cannot found identical name in home service");
			return true;
		}
		// update
		if (configuration != null && this.configuration.getId() != null
				&& configuration.getId().equals(this.configuration.getId())) {
			return true;
		}
		return false;
	}

	@Override
	protected Error getErrorDefinition(JComponent c) {
		if (!(c instanceof JTextField)) {
			throw new IllegalArgumentException("Not a JTextField");
		}

		TextValidator validator = new TextValidator();

		if (!validator.isValidText(((JTextField) c).getText())) {
			return new Error(Error.ERROR, "name length should between 3 and 15. only allow letters,numbers.");
		} else if (!isUnique((JTextField) c)) {
			return new Error(Error.ERROR, "duplicate name");
		} else {
			return new Error(Error.NO_ERROR, "");
		}
	}
}
