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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.infinities.skyport.model.PoolConfig;
import com.infinities.skyport.model.configuration.Configuration;
import com.infinities.skyport.model.configuration.service.AdminConfiguration;
import com.infinities.skyport.model.configuration.service.CIConfiguration;
import com.infinities.skyport.model.configuration.service.ComputeConfiguration;
import com.infinities.skyport.model.configuration.service.DataCenterConfiguration;
import com.infinities.skyport.model.configuration.service.IdentityConfiguration;
import com.infinities.skyport.model.configuration.service.NetworkConfiguration;
import com.infinities.skyport.model.configuration.service.PlatformConfiguration;
import com.infinities.skyport.model.configuration.service.StorageConfiguration;

public class AdvanceDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TaskParamPanel shortTermPanel, midTermPanel, longTermPanel;
	private FunctionSettingPanel functionSettingPanel;
	private JButton okButton, cancelButton, defaultButton;
	private final Configuration configuration;


	public AdvanceDialog(Dialog parent, Configuration configuration) throws Exception {
		super(parent, true);
		this.configuration = configuration;
		setUpUIComponent();
		setUpEventListener();
	}

	private void setUpUIComponent() throws Exception {
		setLayout(new BorderLayout());
		setTitle("Advance Dialog");

		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel();
		panel.setLayout((new GridBagLayout()));

		PoolConfig shortPool = configuration.getShortPoolConfig();
		shortTermPanel = new TaskParamPanel("Short-Term Task", shortPool.getCoreSize(), shortPool.getMaxSize(),
				shortPool.getQueueCapacity());
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 8;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(shortTermPanel, c);

		PoolConfig mediumPool = configuration.getMediumPoolConfig();
		midTermPanel = new TaskParamPanel("Mid-Term Task", mediumPool.getCoreSize(), mediumPool.getMaxSize(),
				mediumPool.getQueueCapacity());
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 8;
		c.gridx = 8;
		c.gridy = 0;

		panel.add(midTermPanel, c);

		PoolConfig longPool = configuration.getLongPoolConfig();
		longTermPanel = new TaskParamPanel("Long-Term Task", longPool.getCoreSize(), longPool.getMaxSize(),
				longPool.getQueueCapacity());
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 8;
		c.gridx = 16;
		c.gridy = 0;

		panel.add(longTermPanel, c);

		functionSettingPanel = new FunctionSettingPanel("Function(0 means infinitie)", configuration);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 24;
		c.gridx = 0;
		c.gridy = 1;

		panel.add(functionSettingPanel, c);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout((new GridBagLayout()));

		defaultButton = new JButton("Default");
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		buttonPanel.add(defaultButton, c);

		okButton = new JButton("OK");
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		buttonPanel.add(okButton, c);

		cancelButton = new JButton("Cancel");
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 0;
		buttonPanel.add(cancelButton, c);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 8;
		c.gridx = 16;
		c.gridy = 4;

		panel.add(buttonPanel, c);

		panel.setPreferredSize(new Dimension(1100, 550));
		panel.setSize(new Dimension(1100, 550));
		add(panel);
		setLocationRelativeTo(getParent());
		this.setResizable(false);
		this.setVisible(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
	}

	private void setUpEventListener() {
		okButton.addActionListener(getOkButtonActionListener());
		cancelButton.addActionListener(getCancelButtonActionListener());
		defaultButton.addActionListener(getDefaultButtonActionListener());
	}

	private ActionListener getOkButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
	}

	private ActionListener getCancelButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				PoolConfig shortPool = configuration.getShortPoolConfig();
				shortTermPanel.setDefault(shortPool.getCoreSize(), shortPool.getMaxSize(), shortPool.getQueueCapacity());
				PoolConfig mediumPool = configuration.getMediumPoolConfig();
				midTermPanel.setDefault(mediumPool.getCoreSize(), mediumPool.getMaxSize(), mediumPool.getQueueCapacity());
				PoolConfig longPool = configuration.getLongPoolConfig();
				longTermPanel.setDefault(longPool.getCoreSize(), longPool.getMaxSize(), longPool.getQueueCapacity());
				setVisible(false);
			}

		};
	}

	private ActionListener getDefaultButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				shortTermPanel.setDefault(PoolConfig.DEFAULT_SHORT_MIN, PoolConfig.DEFAULT_SHORT_MAX,
						PoolConfig.DEFAULT_SHORT_QUEUE);
				midTermPanel.setDefault(PoolConfig.DEFAULT_MEDIUM_MIN, PoolConfig.DEFAULT_MEDIUM_MAX,
						PoolConfig.DEFAULT_MEDIUM_QUEUE);
				longTermPanel.setDefault(PoolConfig.DEFAULT_LONG_MIN, PoolConfig.DEFAULT_LONG_MAX,
						PoolConfig.DEFAULT_LONG_QUEUE);
				try {
					functionSettingPanel.setDefault();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		};
	}

	public PoolConfig getShortPoolConfig() {
		return new PoolConfig(shortTermPanel.getCore(), shortTermPanel.getMax(), shortTermPanel.getCapacity());
	}

	public PoolConfig getMediumPoolConfig() {
		return new PoolConfig(midTermPanel.getCore(), midTermPanel.getMax(), midTermPanel.getCapacity());
	}

	public PoolConfig getLongPoolConfig() {
		return new PoolConfig(longTermPanel.getCore(), longTermPanel.getMax(), longTermPanel.getCapacity());
	}

	public AdminConfiguration getAdminConfiguration() {
		return functionSettingPanel.getAdminConfiguration();
	}

	public CIConfiguration getcIConfiguration() {
		return functionSettingPanel.getcIConfiguration();
	}

	public ComputeConfiguration getComputeConfiguration() {
		return functionSettingPanel.getComputeConfiguration();
	}

	public IdentityConfiguration getIdentityConfiguration() {
		return functionSettingPanel.getIdentityConfiguration();
	}

	public NetworkConfiguration getNetworkConfiguration() {
		return functionSettingPanel.getNetworkConfiguration();
	}

	public PlatformConfiguration getPlatformConfiguration() {
		return functionSettingPanel.getPlatformConfiguration();
	}

	public StorageConfiguration getStorageConfiguration() {
		return functionSettingPanel.getStorageConfiguration();
	}

	public DataCenterConfiguration getDataCenterConfiguration() {
		return functionSettingPanel.getDataCenterConfiguration();
	}

}
