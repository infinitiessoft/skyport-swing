package com.infinities.skyport.ui;

import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.infinities.skyport.model.PoolSize;
import com.infinities.skyport.model.configuration.Configuration;
import com.infinities.skyport.testcase.IntegrationTest;

@Category(IntegrationTest.class)
public class FunctionSettingPanelTest {

	private FunctionSettingPanel panel;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNewForm() throws Exception {
		Configuration configuration = new Configuration();
		panel = new FunctionSettingPanel("Settings", configuration);
		JDialog d = new JDialog(); // Creates dialog
		d.setModal(true); // Means it will wait
		d.add(panel); // Add your panel
		d.setSize(750, 400); // Set size (probably want this relating to your
								// panel
		d.setVisible(true);
	}

	@Test
	public void testUpdatedForm() throws Exception {
		Configuration configuration = new Configuration();
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getDelay().setNumber(60);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getDelay().setUnit(TimeUnit.MINUTES);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getTimeout().setNumber(5);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getTimeout().setUnit(TimeUnit.HOURS);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().setPoolSize(PoolSize.MEDIUM);
		panel = new FunctionSettingPanel("Settings", configuration);
		JDialog d = new JDialog(); // Creates dialog
		d.setModal(true); // Means it will wait
		d.add(panel); // Add your panel
		d.setSize(750, 400); // Set size (probably want this relating to your
								// panel
		d.setVisible(true);
	}

}
