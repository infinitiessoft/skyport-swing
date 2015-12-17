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
public class AdvanceDialogTest {

	private JDialog parent;
	private AdvanceDialog dialog;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNewForm() throws Exception {
		parent = new JDialog();
		Configuration configuration = new Configuration();
		dialog = new AdvanceDialog(parent, configuration);
		dialog.setVisible(true);
	}

	@Test
	public void testOldForm() throws Exception {
		parent = new JDialog();
		Configuration configuration = new Configuration();
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getDelay().setNumber(60);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getDelay().setUnit(TimeUnit.MINUTES);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getTimeout().setNumber(5);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getTimeout().setUnit(TimeUnit.HOURS);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().setPoolSize(PoolSize.MEDIUM);
		configuration.getShortPoolConfig().setCoreSize(1);
		configuration.getShortPoolConfig().setKeepAlive(2);
		configuration.getShortPoolConfig().setMaxSize(3);
		configuration.getShortPoolConfig().setQueueCapacity(4);
		configuration.getMediumPoolConfig().setCoreSize(5);
		configuration.getMediumPoolConfig().setKeepAlive(6);
		configuration.getMediumPoolConfig().setMaxSize(7);
		configuration.getMediumPoolConfig().setQueueCapacity(8);
		configuration.getLongPoolConfig().setCoreSize(9);
		configuration.getLongPoolConfig().setKeepAlive(10);
		configuration.getLongPoolConfig().setMaxSize(11);
		configuration.getLongPoolConfig().setQueueCapacity(12);
		dialog = new AdvanceDialog(parent, configuration);
		dialog.setVisible(true);
	}

}
