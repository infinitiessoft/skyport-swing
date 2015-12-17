package com.infinities.skyport.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultTreeModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinities.skyport.model.FunctionConfiguration;
import com.infinities.skyport.model.PoolSize;
import com.infinities.skyport.model.Time;
import com.infinities.skyport.model.configuration.Configuration;
import com.infinities.skyport.model.configuration.service.AdminConfiguration;
import com.infinities.skyport.model.configuration.service.CIConfiguration;
import com.infinities.skyport.model.configuration.service.ComputeConfiguration;
import com.infinities.skyport.model.configuration.service.DataCenterConfiguration;
import com.infinities.skyport.model.configuration.service.IdentityConfiguration;
import com.infinities.skyport.model.configuration.service.NetworkConfiguration;
import com.infinities.skyport.model.configuration.service.PlatformConfiguration;
import com.infinities.skyport.model.configuration.service.StorageConfiguration;
import com.infinities.skyport.ui.testing.DisabledNode;
import com.infinities.skyport.util.PropertiesHolder;

public class FunctionSettingPanel extends JPanel {

	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(FunctionSettingPanel.class);
	private static final long serialVersionUID = 1L;
	private String title = "no title";
	private AdminConfiguration adminConfiguration;
	private CIConfiguration cIConfiguration;
	private ComputeConfiguration computeConfiguration;
	private IdentityConfiguration identityConfiguration;
	private NetworkConfiguration networkConfiguration;
	private PlatformConfiguration platformConfiguration;
	private StorageConfiguration storageConfiguration;
	private DataCenterConfiguration dataCenterConfiguration;
	private JPanel settingPanel;
	private JTree tree;
	private DisabledNode top;
	private DefaultTreeModel treeModel;


	public FunctionSettingPanel(String title, Configuration configuration) throws Exception {
		super(new BorderLayout());
		this.title = title;
		this.adminConfiguration = configuration.getAdminConfiguration().clone();
		this.cIConfiguration = configuration.getcIConfiguration().clone();
		this.computeConfiguration = configuration.getComputeConfiguration().clone();
		this.dataCenterConfiguration = configuration.getDataCenterConfiguration().clone();
		this.identityConfiguration = configuration.getIdentityConfiguration().clone();
		this.networkConfiguration = configuration.getNetworkConfiguration().clone();
		this.platformConfiguration = configuration.getPlatformConfiguration().clone();
		this.storageConfiguration = configuration.getStorageConfiguration().clone();
		setUpTreeModel();
		setUpUIComponent();
		setUpEventListener();
	}

	private void setUpTreeModel() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		top = new DisabledNode("Functions");
		treeModel = new DefaultTreeModel(top);
		setUpService(adminConfiguration, top);
		setUpService(cIConfiguration, top);
		setUpService(computeConfiguration, top);
		setUpService(dataCenterConfiguration, top);
		setUpService(identityConfiguration, top);
		setUpService(networkConfiguration, top);
		setUpService(platformConfiguration, top);
		setUpService(storageConfiguration, top);
	}

	private void setUpService(Object configuration, DisabledNode parent) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DisabledNode childNode = new DisabledNode(configuration.getClass().getSimpleName());
		parent.add(childNode);

		Method[] methods = configuration.getClass().getMethods();
		for (Method method : methods) {

			// make directory
			if (method.getName().matches("^get[a-zA-Z]*Configuration$")) {
				DisabledNode dirNode = new DisabledNode(method.getReturnType().getSimpleName());
				childNode.add(dirNode);
				Object supportConfiguration = method.invoke(configuration);
				setUpConfiguration(supportConfiguration, dirNode);
			} else if (method.getName().matches("^get[a-zA-Z]*")
					&& method.getReturnType().equals(FunctionConfiguration.class)) {
				FunctionConfiguration functionConfiguration = (FunctionConfiguration) method.invoke(configuration);
				setUpFunctionConfiguration(method.getName().replaceFirst("get", ""), functionConfiguration, childNode);
			}
		}
	}

	private void setUpConfiguration(Object configuration, DisabledNode parent) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method[] methods = configuration.getClass().getMethods();
		for (Method method : methods) {
			// make node
			if (method.getName().matches("^get[a-zA-Z]*") && method.getReturnType().equals(FunctionConfiguration.class)) {
				FunctionConfiguration functionConfiguration = (FunctionConfiguration) method.invoke(configuration);
				setUpFunctionConfiguration(method.getName().replaceFirst("get", ""), functionConfiguration, parent);
			}
		}
	}

	private void setUpFunctionConfiguration(String name, FunctionConfiguration functionConfiguration, DisabledNode parent) {
		DisabledNode childNode = new DisabledNode(new FunctionInfo(name, functionConfiguration));
		parent.add(childNode);
	}

	private void setUpUIComponent() throws Exception {
		this.setOpaque(true);
		tree = new JTree(treeModel);
		JScrollPane treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension(300, 100));
		add("West", treeView);
		settingPanel = new JPanel(new FlowLayout());
		TitledBorder titleBorder;
		titleBorder = BorderFactory.createTitledBorder(title);
		titleBorder.setTitleJustification(TitledBorder.CENTER);
		settingPanel.setBorder(titleBorder);
		add("Center", settingPanel);
		setPreferredSize(new Dimension(1000, 400));
		setSize(new Dimension(1000, 400));
	}

	private void setUpEventListener() {
		tree.addTreeSelectionListener(new SelectTreeSelectionListener());
	}

	public void setDefault() throws Exception {

	}

	public AdminConfiguration getAdminConfiguration() {
		return adminConfiguration;
	}

	public CIConfiguration getcIConfiguration() {
		return cIConfiguration;
	}

	public ComputeConfiguration getComputeConfiguration() {
		return computeConfiguration;
	}

	public IdentityConfiguration getIdentityConfiguration() {
		return identityConfiguration;
	}

	public NetworkConfiguration getNetworkConfiguration() {
		return networkConfiguration;
	}

	public PlatformConfiguration getPlatformConfiguration() {
		return platformConfiguration;
	}

	public StorageConfiguration getStorageConfiguration() {
		return storageConfiguration;
	}

	public DataCenterConfiguration getDataCenterConfiguration() {
		return dataCenterConfiguration;
	}

	private void displayFunction(FunctionInfo functionInfo) throws Exception {
		SettingPanel inner = new SettingPanel(functionInfo.getConfiguration());
		settingPanel.add(inner);
	}


	private class SelectTreeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DisabledNode node = (DisabledNode) tree.getLastSelectedPathComponent();

			if (node == null) {
				return;
			}
			settingPanel.removeAll();
			Object nodeInfo = node.getUserObject();
			if (node.isLeaf() && nodeInfo instanceof FunctionInfo) {
				FunctionInfo functionInfo = (FunctionInfo) nodeInfo;
				try {
					displayFunction(functionInfo);
				} catch (Exception e1) {
					logger.warn("unexpected error", e1);
				}
			}
			settingPanel.validate();
			settingPanel.repaint();
		}
	}

	private class FunctionInfo {

		private String name;
		private FunctionConfiguration configuration;


		public FunctionInfo(String name, FunctionConfiguration configuration) {
			this.name = name;
			this.configuration = configuration;
		}

		public FunctionConfiguration getConfiguration() {
			return configuration;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	private class SettingPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final TimeUnit[] units = new TimeUnit[] { TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS };
		private int minDelay, maxDelay, minTimeout, maxTimeout;
		private JPanel inner;
		private JRadioButton longButton, mediumButton, shortButton;
		private JSlider timeoutSlider, delaySlider;
		private JFormattedTextField timeoutField, delayField;
		private JComboBox<TimeUnit> timeoutList, delayList;
		private final FunctionConfiguration configuration;


		// private final Action action;

		private SettingPanel(FunctionConfiguration configuration) {
			super(new FlowLayout());
			this.configuration = configuration;
			// this.action = action;
			setUpResource();
			setUpUI();
			setUpComponentValue();
		}

		private void setUpComponentValue() {
			PoolSize poolSize = configuration.getPoolSize();
			if (poolSize.equals(PoolSize.SHORT)) {
				shortButton.setSelected(true);
			} else if (poolSize.equals(PoolSize.MEDIUM)) {
				mediumButton.setSelected(true);
			} else if (poolSize.equals(PoolSize.LONG)) {
				longButton.setSelected(true);
			}

			long delay = configuration.getDelay().getNumber();
			long timeout = configuration.getTimeout().getNumber();

			delaySlider.setValue((int) delay);
			timeoutSlider.setValue((int) timeout);
			delayField.setValue(delay);
			timeoutField.setValue(timeout);
			delayList.setSelectedItem(configuration.getDelay().getUnit());
			timeoutList.setSelectedItem(configuration.getTimeout().getUnit());
		}

		private void setUpResource() {
			minDelay = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.DELAY_MIN));
			maxDelay = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.DELAY_MAX));
			minTimeout = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.TIMEOUT_MIN));
			maxTimeout = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.TIMEOUT_MAX));
		}

		private void setUpUI() {
			JLabel poolLabel = new JLabel("ThreadPool:");
			poolLabel.setPreferredSize(new Dimension(100, 20));
			JLabel delayLabel = new JLabel("Delay:");
			delayLabel.setPreferredSize(new Dimension(100, 20));
			JLabel timeoutLabel = new JLabel("Timeout:");
			timeoutLabel.setPreferredSize(new Dimension(100, 20));

			shortButton = new JRadioButton("Short");
			mediumButton = new JRadioButton("Medium");
			longButton = new JRadioButton("Long");

			ButtonGroup group = new ButtonGroup();
			group.add(shortButton);
			group.add(mediumButton);
			group.add(longButton);
			RadioButtonListener radioButtonListener = new RadioButtonListener(configuration);
			shortButton.addChangeListener(radioButtonListener);
			mediumButton.addChangeListener(radioButtonListener);
			longButton.addChangeListener(radioButtonListener);

			NumberFormat numberFormat = NumberFormat.getIntegerInstance();
			NumberFormatter formatter = new NumberFormatter(numberFormat);
			formatter.setAllowsInvalid(false);
			formatter.setCommitsOnValidEdit(true);

			delaySlider = new JSlider();
			delaySlider.setMaximum(maxDelay);
			delaySlider.setMinimum(minDelay);
			delaySlider.setPreferredSize(new Dimension(270, 20));

			delayField = new JFormattedTextField(formatter);
			delayField.setColumns(3);
			SliderListener delayListener = new SliderListener(delaySlider, delayField, configuration.getDelay());
			delaySlider.addChangeListener(delayListener);
			delayField.addPropertyChangeListener(delayListener);

			delayList = new JComboBox<TimeUnit>(units);
			delayList.setName("delayList");
			delayList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
			delayList.addItemListener(new ComboBoxListener(configuration.getDelay()));

			timeoutSlider = new JSlider();
			timeoutSlider.setMaximum(maxTimeout);
			timeoutSlider.setMinimum(minTimeout);
			timeoutSlider.setPreferredSize(new Dimension(270, 20));

			timeoutField = new JFormattedTextField(formatter);
			timeoutField.setColumns(3);
			SliderListener timeoutListener = new SliderListener(timeoutSlider, timeoutField, configuration.getTimeout());
			timeoutSlider.addChangeListener(timeoutListener);
			timeoutField.addPropertyChangeListener(timeoutListener);

			timeoutList = new JComboBox<TimeUnit>(units);
			timeoutList.setName("timeoutList");
			timeoutList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
			timeoutList.addItemListener(new ComboBoxListener(configuration.getTimeout()));

			this.inner = new JPanel();
			GroupLayout layout = new GroupLayout(inner);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			inner.setLayout(layout);
			layout.setHorizontalGroup(layout
					.createSequentialGroup()
					.addGroup(
							layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(poolLabel)
									.addComponent(delayLabel).addComponent(timeoutLabel))
					.addGroup(
							layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addGroup(
											layout.createSequentialGroup().addComponent(shortButton)
													.addComponent(mediumButton).addComponent(longButton))
									.addGroup(
											layout.createSequentialGroup().addComponent(delaySlider)
													.addComponent(delayField).addComponent(delayList))
									.addGroup(
											layout.createSequentialGroup().addComponent(timeoutSlider)
													.addComponent(timeoutField).addComponent(timeoutList))));
			layout.linkSize(SwingConstants.HORIZONTAL, delaySlider, timeoutSlider);
			layout.linkSize(SwingConstants.VERTICAL, delaySlider, timeoutSlider);

			layout.setVerticalGroup(layout
					.createSequentialGroup()
					.addGroup(
							layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(poolLabel)
									.addComponent(shortButton).addComponent(mediumButton).addComponent(longButton))
					.addGroup(
							layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(delayLabel)
									.addComponent(delaySlider).addComponent(delayField).addComponent(delayList))
					.addGroup(
							layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(timeoutLabel)
									.addComponent(timeoutSlider).addComponent(timeoutField).addComponent(timeoutList)));
			add(inner);
			setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		}
	}

	private class SliderListener implements PropertyChangeListener, ChangeListener {

		private JSlider slider;
		private JFormattedTextField field;
		private Time time;


		public SliderListener(JSlider slider, JFormattedTextField field, Time time) {
			super();
			this.slider = slider;
			this.field = field;
			this.time = time;
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
			field.setValue(slider.getValue());
			time.setNumber(slider.getValue());
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("value".equals(evt.getPropertyName())) {
				Number value = (Number) evt.getNewValue();
				slider.setValue(value.intValue());
				time.setNumber(value.intValue());
			}
		}
	}

	private class RadioButtonListener implements ChangeListener {

		private FunctionConfiguration configuration;


		public RadioButtonListener(FunctionConfiguration configuration) {
			this.configuration = configuration;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JRadioButton button = (JRadioButton) e.getSource();
			if (button.isSelected()) {
				if ("Short".equals(button.getText())) {
					configuration.setPoolSize(PoolSize.SHORT);
				} else if ("Medium".equals(button.getText())) {
					configuration.setPoolSize(PoolSize.MEDIUM);
				} else if ("Long".equals(button.getText())) {
					configuration.setPoolSize(PoolSize.LONG);
				}
			}
		}

	}

	private class ComboBoxListener implements ItemListener {

		private Time time;


		public ComboBoxListener(Time time) {
			this.time = time;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			TimeUnit timeUnit = (TimeUnit) e.getItem();
			time.setUnit(timeUnit);
		}

	}

}
