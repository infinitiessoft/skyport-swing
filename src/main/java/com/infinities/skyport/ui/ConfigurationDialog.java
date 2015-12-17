package com.infinities.skyport.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.dasein.cloud.ContextRequirements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.infinities.skyport.ServiceProvider;
import com.infinities.skyport.annotation.Provider;
import com.infinities.skyport.model.configuration.Configuration;
import com.infinities.skyport.service.ConfigurationHome;
import com.infinities.skyport.service.DriverHome;
import com.infinities.skyport.ui.verifier.ComboboxVerifier;
import com.infinities.skyport.ui.verifier.FormVerifier;
import com.infinities.skyport.ui.verifier.IdVerifier;
import com.infinities.skyport.ui.verifier.NameVerifier;
import com.infinities.skyport.ui.verifier.TextVerifier;

public class ConfigurationDialog extends JDialog {

	private static final int DEFAULT_WIDTH = 380;
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationDialog.class);
	private final ConfigurationHome configurationHome;
	private Configuration configuration;
	private final DriverHome driverHome;
	private final JFrame frame;
	private JTextField idField, cloudNameField, endpointField, accountField, regionIdField;
	private final FormVerifier formVerifier = new FormVerifier();
	private JComboBox<String> providerClassList, providerNameList;
	private JCheckBox cacheBox, timeoutBox;
	private JButton testButton;
	private AdvanceDialog advanceDialog;
	private CancellableProgressDialog progressDialog;
	private JPanel mainPanel;
	private GridBagConstraints c;
	private boolean returnVal = true;
	private int count = 0;
	private Map<String, JTextField> others;


	public ConfigurationDialog(JFrame parent, Configuration configuration, ConfigurationHome configurationHome,
			DriverHome driverHome) throws Exception {
		super(parent, ModalityType.DOCUMENT_MODAL);
		this.configuration = configuration;
		this.configurationHome = configurationHome;
		this.driverHome = driverHome;
		this.frame = parent;
		this.others = new HashMap<String, JTextField>();
		initResource();
		setUpUIComponent();
		setUpComponentValue(configuration);
	}

	private void setUpComponentValue(Configuration configuration) throws Exception {
		idField.setText(configuration.getId());
		if (!Strings.isNullOrEmpty(configuration.getId())) {
			idField.setEditable(false);
		}
		cloudNameField.setText(configuration.getCloudName());
		endpointField.setText(configuration.getEndpoint());
		accountField.setText(configuration.getAccount());
		regionIdField.setText(configuration.getRegionId());
		cacheBox.setSelected(configuration.getCacheable());
		timeoutBox.setSelected(configuration.getTimeoutable());
		String selectedItem = (String) providerClassList.getSelectedItem();
		if (!selectedItem.equals(configuration.getProviderClass())
				&& !Strings.isNullOrEmpty(configuration.getProviderClass())) {
			providerClassList.setSelectedItem(configuration.getProviderClass());
		} else {
			extendParameter(selectedItem);
		}
		for (Entry<String, JTextField> entry : others.entrySet()) {
			String name = entry.getKey();
			JTextField field = entry.getValue();
			field.setText(configuration.getProperties().getProperty(name, ""));
		}
		formVerifier.docsChanged();

		advanceDialog = new AdvanceDialog(this, configuration);
		advanceDialog.setLocationRelativeTo(null);
	}

	private void initResource() {

	}

	private void setUpUIComponent() {
		setLayout(new BorderLayout());
		setTitle("Configuration Dialog");

		mainPanel = new JPanel();
		c = new GridBagConstraints();
		mainPanel.setLayout(new GridBagLayout());

		initIdComponent(mainPanel, c);
		initCloudNameComponent(mainPanel, c);
		initDriverComponent(mainPanel, c);
		initProviderNameComponent(mainPanel, c);
		initEndpointComponent(mainPanel, c);
		initAccountComponent(mainPanel, c);
		initRegionIdComponent(mainPanel, c);
		addButtonComponent();
		JScrollPane mainScrollPane = new JScrollPane(mainPanel);
		mainScrollPane.setPreferredSize(new Dimension(DEFAULT_WIDTH, 410));
		add(mainScrollPane, BorderLayout.CENTER);

	}

	private void initIdComponent(JPanel panel, GridBagConstraints c) {
		final JLabel idDesc = new JLabel("Enter the id of the profile.");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0.5;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(idDesc, c);

		final JLabel idLabel = new JLabel("Id:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(idLabel, c);

		idField = new JTextField();
		idLabel.setLabelFor(idField);
		idField.setName("id");
		IdVerifier verifier = new IdVerifier(idField, configuration.getId(), configurationHome);
		idField.setInputVerifier(verifier);
		idField.addActionListener(verifier);
		formVerifier.addTextComponent(idField);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 1;
		panel.add(idField, c);
	}

	private void initCloudNameComponent(JPanel panel, GridBagConstraints c) {
		final JLabel nameDesc = new JLabel("Enter the name of the profile.");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 2;
		panel.add(nameDesc, c);

		final JLabel cloudNameLabel = new JLabel("*Name:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
		panel.add(cloudNameLabel, c);

		cloudNameField = new JTextField();
		cloudNameLabel.setLabelFor(cloudNameField);
		cloudNameField.setName("cloudName");
		NameVerifier verifier = new NameVerifier(cloudNameField, configuration, configurationHome);
		cloudNameField.setInputVerifier(verifier);
		cloudNameField.addActionListener(verifier);
		formVerifier.addTextComponent(cloudNameField);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 3;
		panel.add(cloudNameField, c);
	}

	private void initDriverComponent(JPanel panel, GridBagConstraints c) {
		final JLabel providerClassDesc = new JLabel("Select the ProviderClass to use for this connection.");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		panel.add(providerClassDesc, c);

		final JLabel providerClassLabel = new JLabel("*ProviderClass:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 5;
		panel.add(providerClassLabel, c);

		final Vector<String> providerClasses = new Vector<String>();
		providerClasses.addAll(driverHome.findAll().keySet());

		providerClassList = new JComboBox<String>(providerClasses);
		providerClassList.setName("providerClass");
		// driverList.setSelectedIndex(0);
		providerClassList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
		providerClassLabel.setLabelFor(providerClassList);
		ComboboxVerifier verifier = new ComboboxVerifier(providerClassList);
		providerClassList.setInputVerifier(verifier);
		providerClassList.addActionListener(verifier);
		providerClassList.addItemListener(getProviderClassSelectedListener());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 5;
		// driverList.addActionListener(getDriverListActionListener());
		panel.add(providerClassList, c);

	}

	private void initProviderNameComponent(JPanel panel, GridBagConstraints c) {
		final JLabel providerNameDesc = new JLabel("Enter the provider of the cloud.");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 6;
		panel.add(providerNameDesc, c);

		final JLabel providerNameLabel = new JLabel("*Provider:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 7;
		panel.add(providerNameLabel, c);

		providerNameList = new JComboBox<String>();
		providerNameList.setName("providerName");
		// driverList.setSelectedIndex(0);
		providerNameList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
		providerNameLabel.setLabelFor(providerNameList);
		ComboboxVerifier verifier = new ComboboxVerifier(providerNameList);
		providerNameList.setInputVerifier(verifier);
		providerNameList.addActionListener(verifier);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 7;
		// driverList.addActionListener(getDriverListActionListener());
		panel.add(providerNameList, c);

	}

	private void initEndpointComponent(JPanel panel, GridBagConstraints c) {
		final JLabel endpointDesc = new JLabel("Enter the bootstrap URL for talking to the cloud API");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 8;
		panel.add(endpointDesc, c);

		final JLabel endpointLabel = new JLabel("*Endpoint:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 9;
		panel.add(endpointLabel, c);

		endpointField = new JTextField();
		endpointLabel.setLabelFor(endpointField);
		endpointField.setName("endpoint");
		TextVerifier verifier = new TextVerifier(endpointField);
		endpointField.setInputVerifier(verifier);
		endpointField.addActionListener(verifier);
		formVerifier.addTextComponent(endpointField);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 9;
		panel.add(endpointField, c);
	}

	private void initAccountComponent(JPanel panel, GridBagConstraints c) {
		final JLabel accountDesc = new JLabel("Enter the account number of the cloud.");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 10;
		panel.add(accountDesc, c);

		final JLabel accountLabel = new JLabel("*Account:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 11;
		panel.add(accountLabel, c);

		accountField = new JTextField();
		accountLabel.setLabelFor(accountField);
		accountField.setName("account");
		TextVerifier verifier = new TextVerifier(accountField);
		accountField.setInputVerifier(verifier);
		accountField.addActionListener(verifier);
		formVerifier.addTextComponent(accountField);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 11;
		panel.add(accountField, c);
	}

	private void initRegionIdComponent(JPanel panel, GridBagConstraints c) {
		final JLabel regionIdDesc = new JLabel("Enter the ID for the cloud region.");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 12;
		panel.add(regionIdDesc, c);

		final JLabel regionIdLabel = new JLabel("*Region ID:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 13;
		panel.add(regionIdLabel, c);

		regionIdField = new JTextField();
		regionIdLabel.setLabelFor(regionIdField);
		regionIdField.setName("regionId");
		TextVerifier verifier = new TextVerifier(regionIdField);
		regionIdField.setInputVerifier(verifier);
		regionIdField.addActionListener(verifier);
		formVerifier.addTextComponent(regionIdField);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 13;
		panel.add(regionIdField, c);
	}

	private void initOtherComponent(JPanel panel, GridBagConstraints c, String name, String desc, boolean required) {
		final JLabel paramDesc = new JLabel(desc);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		// c.weighty = 0.5;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 14 + count * 2;
		panel.add(paramDesc, c);

		JLabel paramLabel;
		if (required)
			paramLabel = new JLabel("*" + name + ":");
		else
			paramLabel = new JLabel(" " + name + ":");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 15 + count * 2;
		panel.add(paramLabel, c);

		JTextField paramField = new JTextField();
		paramLabel.setLabelFor(paramField);
		paramField.setName(name);
		if (required) {
			TextVerifier verifier = new TextVerifier(paramField);
			paramField.setInputVerifier(verifier);
			paramField.addActionListener(verifier);
		}
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 15 + count * 2;
		panel.add(paramField, c);
		others.put(name, paramField);
	}

	private ItemListener getProviderClassSelectedListener() {
		return new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					removeParamComponent();
					others.clear();
					mainPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, (410)));
					String providerClassName = (String) evt.getItem();
					if (Strings.isNullOrEmpty(providerClassName)) {
						mainPanel.validate();
						pack();
						mainPanel.repaint();
						return;
					}
					extendParameter(providerClassName);
				}
			}
		};
	}

	private void extendParameter(String providerClassName) {
		Class<? extends ServiceProvider> serviceProvider = driverHome.findByName(providerClassName);
		if (serviceProvider != null) {
			try {
				if (providerNameList.getItemCount() != 0) {
					providerNameList.removeAllItems();
				}
				if (serviceProvider.getAnnotation(Provider.class) != null) {
					String[] values = serviceProvider.getAnnotation(Provider.class).enumeration();
					for (String value : values) {
						providerNameList.addItem(value);
					}
				} else {
					providerNameList.addItem(" ");
				}

				ContextRequirements requirements = serviceProvider.newInstance().getContextRequirements();
				List<ContextRequirements.Field> fields = requirements.getConfigurableValues();

				if (fields.size() > 0) {
					count = 0;
					for (ContextRequirements.Field f : fields) {
						if (f.type.equals(ContextRequirements.FieldType.KEYPAIR)) {
							initOtherComponent(mainPanel, c, f.name + "_SHARED", f.description, f.required);
							count++;
							initOtherComponent(mainPanel, c, f.name + "_SECRET", f.description, f.required);
						} else {
							initOtherComponent(mainPanel, c, f.name, f.description, f.required);
						}
						count++;
					}
					// addButtonComponent();
					mainPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, (410 + 40 * (count + 1))));

				}

			} catch (IllegalAccessException e) {
				logger.error("class:" + providerClassName + " connot be acxess");
			} catch (InstantiationException e) {
				logger.error("class:" + providerClassName + " connot be instantiated");
			}
			mainPanel.validate();
			mainPanel.repaint();
			pack();
		}
	}

	private void removeParamComponent() {
		for (int i = (mainPanel.getComponentCount() - 1); i >= 21; i--) {
			mainPanel.remove(i);
		}
	}

	private void addButtonComponent() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		initCacheCheckbox(buttonPanel);
		initTimeoutCheckbox(buttonPanel);
		initAdvanceButtonComponent(buttonPanel);
		initTestButtonComponent(buttonPanel);
		initOkButton(buttonPanel);
		initCancelButton(buttonPanel);
		this.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				doReturn(true);
			}
		});
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void initCacheCheckbox(JPanel panel) {
		cacheBox = new JCheckBox("Cache");
		panel.add(cacheBox);
	}

	private void initTimeoutCheckbox(JPanel panel) {
		timeoutBox = new JCheckBox("Timeout");
		panel.add(timeoutBox);
	}

	private void initAdvanceButtonComponent(JPanel panel) {
		JButton advanceButton = new JButton("Advance..");
		advanceButton.addActionListener(getAdvanceButtonActionListener());
		panel.add(advanceButton);
	}

	private void initTestButtonComponent(JPanel panel) {
		testButton = new JButton("Test");
		Window win = SwingUtilities.getWindowAncestor(testButton);
		progressDialog = new CancellableProgressDialog(win, "Dialog", "Please wait.......", ModalityType.APPLICATION_MODAL);
		testButton.addActionListener(getTestButtonActionListener());
		formVerifier.addComponentsToEnable(testButton);
		panel.add(testButton);
	}

	private void initOkButton(JPanel panel) {
		JButton okButton = new JButton("OK");
		okButton.addActionListener(getOkButtonActionListener());
		formVerifier.addComponentsToEnable(okButton);
		panel.add(okButton);
	}

	private void initCancelButton(JPanel panel) {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(getCancelButtonActionListener());
		panel.add(cancelButton);
	}

	private ActionListener getOkButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					mergeAccessConfig();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Merge AccessConfig fail");
				}
				setVisible(false);
				dispose();
			}

		};
	}

	private ActionListener getCancelButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				doReturn(true);
				setVisible(false);
				dispose();
			}

		};
	}

	private ActionListener getTestButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent t) {
				testButton.setEnabled(false);
				final SwingWorker<Boolean, Void> mySwingWorker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						// mimic some long-running process here...
						try {
							return testConnection();
						} catch (Exception e) {
							logger.warn("testing exception", e);
						}
						return false;

					}
				};

				mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals("state")) {
							if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
								progressDialog.dispose();
								testButton.setEnabled(true);
							}
						}
					}
				});
				progressDialog.addCancelActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						mySwingWorker.cancel(true);
					}
				});
				mySwingWorker.execute();
				progressDialog.setVisible(true);

				try {
					boolean ret = mySwingWorker.get();
					if (ret) {
						JOptionPane.showMessageDialog(null, "Connection succssfully.");
					} else {
						JOptionPane.showMessageDialog(null, "Connection fail");
					}
				} catch (Exception e) {
					logger.warn("testing fail", e);
					JOptionPane.showMessageDialog(null, "Connection fail");
				}
			}
		};
	}

	private ActionListener getAdvanceButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent t) {
				advanceDialog.setVisible(true);
			}
		};
	}

	private void doReturn(boolean cancel) {
		returnVal = !cancel;
		this.dispose();
	}

	private boolean testConnection() {
		try {
			Configuration configuration = new Configuration();
			String cloudName = cloudNameField.getText();
			String providerName = (String) providerNameList.getSelectedItem();
			String endpoint = endpointField.getText();
			String account = accountField.getText();
			String regionId = regionIdField.getText();
			String providerClass = (String) providerClassList.getSelectedItem();

			configuration.setCloudName(cloudName);
			configuration.setProviderName(providerName);
			configuration.setEndpoint(endpoint);
			configuration.setProviderClass(providerClass);
			configuration.setAccount(account);
			configuration.setRegionId(regionId);

			for (Entry<String, JTextField> entry : others.entrySet()) {
				logger.debug("property key:{}, value:{}", new Object[] { entry.getKey(), entry.getValue().getText() });
				configuration.getProperties().put(entry.getKey(), entry.getValue().getText());
			}

			String context = configurationHome.testContext(configuration);
			logger.debug("textContext:{}", context);
			return !Strings.isNullOrEmpty(context);
		} catch (Exception e) {
			logger.error("encounter error when testing connection", e);
		}

		return false;
	}

	private void mergeAccessConfig() throws Exception {
		String id = idField.getText();
		String cloudName = cloudNameField.getText();
		String providerName = (String) providerNameList.getSelectedItem();
		String endpoint = endpointField.getText();
		String account = accountField.getText();
		String regionId = regionIdField.getText();
		String providerClass = (String) providerClassList.getSelectedItem();
		boolean cache = cacheBox.isSelected();
		boolean timeout = timeoutBox.isSelected();

		configuration.setId(id);
		configuration.setCloudName(cloudName);
		configuration.setProviderName(providerName);
		configuration.setEndpoint(endpoint);
		configuration.setProviderClass(providerClass);
		configuration.setAccount(account);
		configuration.setRegionId(regionId);
		configuration.setCacheable(cache);
		configuration.setTimeoutable(timeout);
		configuration.getProperties().clear();
		for (Entry<String, JTextField> entry : others.entrySet()) {
			String key = entry.getKey();
			JTextField value = entry.getValue();
			configuration.getProperties().setProperty(key, value.getText());
		}

		configuration.setShortPoolConfig(advanceDialog.getShortPoolConfig());
		configuration.setMediumPoolConfig(advanceDialog.getMediumPoolConfig());
		configuration.setLongPoolConfig(advanceDialog.getLongPoolConfig());
		configuration.setAdminConfiguration(advanceDialog.getAdminConfiguration());
		configuration.setcIConfiguration(advanceDialog.getcIConfiguration());
		configuration.setComputeConfiguration(advanceDialog.getComputeConfiguration());
		configuration.setDataCenterConfiguration(advanceDialog.getDataCenterConfiguration());
		configuration.setIdentityConfiguration(advanceDialog.getIdentityConfiguration());
		configuration.setNetworkConfiguration(advanceDialog.getNetworkConfiguration());
		configuration.setPlatformConfiguration(advanceDialog.getPlatformConfiguration());
		configuration.setStorageConfiguration(advanceDialog.getStorageConfiguration());
	}

	public boolean showDialog() {
		this.pack();
		this.setLocationRelativeTo(frame);
		this.setVisible(true);
		return this.returnVal;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
