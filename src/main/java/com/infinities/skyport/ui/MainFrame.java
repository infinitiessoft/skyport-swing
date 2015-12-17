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

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinities.skyport.cache.CachedServiceProvider;
import com.infinities.skyport.cache.service.CachedComputeServices.ComputeQuartzType;
import com.infinities.skyport.model.configuration.Configuration;
import com.infinities.skyport.service.ConfigurationHome;
import com.infinities.skyport.service.ConfigurationLifeCycleListener;
import com.infinities.skyport.service.DriverHome;
import com.infinities.skyport.util.Manifests;

public class MainFrame implements ConfigurationLifeCycleListener {

	public static final String PATTERN_RFC1123 = "dd MMM yyyy HH:mm:ss";
	public static final String IMAGES_FOLDER = "images";
	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
	private static final String[] CONFIG_COLUMN = { "ID", "Status", "CloudName", "ProviderName", "ProviderClass",
			"Endpoint", "Account", "RegionId", "Modify Date" };
	private static final String ACTIVE = "Active";
	private static final String INACTIVE = "Inactive";
	private static final int ID_INDEX = 0;
	private static final int STATUS_INDEX = 1;
	private static final int CLOUDNAME_INDEX = 2;
	private static final int PROVIDERNAME_INDEX = 3;
	private static final int PROVIDERCLASS_INDEX = 4;
	// private static final int JKS_INDEX = 6;
	private static final int ENDPOINT_INDEX = 5;
	private static final int ACCOUNT_INDEX = 6;
	private static final int REGIONID_INDEX = 7;
	// private static final int MODIFYDATE_INDEX = 8;
	private JFrame frame;
	private JTabbedPane tabs;
	private JToolBar toolbar;
	private JPanel buttonBarPanel, configPanel, southPanel; // , logPanel;
	// private TestingPanel testingPanel;
	private JTable configTable;
	private Vector<Vector<String>> rows;
	private Vector<String> columns;
	protected DefaultTableModel tableModel;
	private JButton addButton, removeButton, settingButton, closeButton, activeButton, flushCacheButton;
	private PopupMenu trayPopup;
	private TrayIcon trayIcon;
	private SystemTray tray;
	private MenuItem aboutItem, startItem, stopItem, configItem, exitItem;
	private ImageIcon iconOpen, iconConfig;// , iconTest; // , iconLog;

	private ConfigurationHome configurationHome;
	private DriverHome driverHome;
	private boolean isTrayActivate;
	// private final String protocol, ip, port;
	private ProgressDialog progressDialog;
	private ExcelAdapter excelAdapter;
	private String title, version;


	public MainFrame(DriverHome driverHome, ConfigurationHome configurationHome, String protocol, String ip, String port) {
		// this.protocol = protocol;
		// this.ip = ip;
		// this.port = port;
		this.driverHome = driverHome;
		this.configurationHome = configurationHome;
	}

	public void activate() throws InterruptedException {
		logger.info("skyport ui initializaion start");
		initResource();
		setUpVersion();
		setUpTableModel();
		setUpUIComponent();
		setUpTrayIcon();
		enableTray(true);
		setUpFrame();
		setUpEventListener();
		clear();
		configurationHome.addLifeCycleListener(this);
		logger.info("skyport ui initializaion complete");
	}

	private void setUpVersion() {
		try {
			title = Manifests.getAttribute(Manifests.INPLEMENTATION_TITLE);
			version = Manifests.getAttribute(Manifests.INPLEMENTATION_VERSION);
		} catch (IOException e) {
			logger.error("read META-INF/MANIFEST.MF file failed", e);
		}

	}

	public void deactivate() throws InterruptedException, InvocationTargetException {

	}

	public ConfigurationHome getConfigurationHome() {
		return configurationHome;
	}

	public void setConfigurationHome(ConfigurationHome configurationHome) {
		this.configurationHome = configurationHome;
	}

	private void initResource() {
		String prefix = "/" + IMAGES_FOLDER + "/";
		iconOpen = new ImageIcon(MainFrame.class.getResource(prefix + "skyport-24px.png"));
		iconConfig = new ImageIcon(MainFrame.class.getResource(prefix + "skyport-32px.png"));
		// iconTest = new ImageIcon(MainFrame.class.getResource(prefix +
		// "test.png"));
	}

	public void setUpEventListener() {
		if (frame == null) {
			return;
		}

		logger.debug("Setup EventListener");

		// frame.addWindowStateListener(new WindowStateListener() {
		//
		// @Override
		// public void windowStateChanged(WindowEvent e) {
		// if (e.getNewState() == JFrame.ICONIFIED) {
		// setVisible(false);
		// } else {
		// setVisible(true);
		// }
		// }
		// });

		// frame.addWindowListener(new WindowAdapter() {
		//
		// @Override
		// public void windowClosing(WindowEvent e) {
		// setVisible(false);
		//
		// }
		// });

		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		addButton.addActionListener(getAddAction());

		removeButton.addActionListener(getRemoveAction());

		settingButton.addActionListener(getSettingAction());

		activeButton.addActionListener(getActiveAction());

		flushCacheButton.addActionListener(new FlushCacheActionListener());

		configItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setExtendedState(JFrame.NORMAL);
				frame.setVisible(true);
			}
		});

		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String uri = "http://infinitiessoft.com/";
				final String mail = "pohsun@infinitiessoft.com";
				JLabel label = new JLabel();
				Font font = label.getFont();
				StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
				style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
				style.append("letter-spacing:" + 8 + "px;");
				style.append("font-size:" + 14 + "pt;");
				JEditorPane ep =
						new JEditorPane(
								"text/html",
								"<html><body style=\""
										+ style
										+ "\">"
										+ "<font line-height=\"20px\" color=\"008B8B\">About Skyport<br />by pohsun</font><br /><a href=\""
										+ uri
										+ "\">"
										+ uri
										+ "</a><br /><p><b>Version:</b>"
										+ version
										+ "<br /><b>Feedback:</b><a href=\"mailto:"
										+ mail
										+ "\">"
										+ mail
										+ "</a><br /><b>Copyright(c) 2011 InfinitiesSoft Solutions Inc.</b><p>The goal of skyport is to make it possible to interact with any virtual machines<br />"
										+ "from any application, regardless of which virtualization platform the virtual machines<br />"
										+ "is provision on.<br />"
										+ "For this to work, skyport handles user's http request and coverts it into APIs that<br />"
										+ "the virtualization platform understands.<br />"
										+ "In other words, the skyport's function, which is very similar to a human translator,<br />"
										+ "is to bridge the gap between parties that would not otherwise understand each other.<br /></body></html>");
				// handle link events
				ep.addHyperlinkListener(new HyperlinkListener() {

					@Override
					public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
							if (Desktop.isDesktopSupported()) {
								try {
									Desktop.getDesktop().browse(e.getURL().toURI());
								} catch (Exception ex) { /*
														 * TODO: error handling
														 */
								}
							} else { /* TODO: error handling */
							}
						}
					}
				});
				ep.setEditable(false);
				ep.setBackground(label.getBackground());
				// "by Po-Hsun, Huang\nhttp://infinitiessoft.com\nVersion:" +
				// version
				JOptionPane.showMessageDialog(null, ep, "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// mimic some long-running process here...
						tray.remove(trayIcon);
						close();
						return null;

					}
				};

				final ProgressDialog exitDialog =
						new ProgressDialog(null, "Dialog", "Please wait.......", ModalityType.APPLICATION_MODAL);
				mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals("state")) {
							if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
								exitDialog.dispose();
							}
						}
					}
				});
				mySwingWorker.execute();
				exitDialog.setVisible(true);
			}
		});

	}

	private void updateButtons() {
		if (configTable.getSelectedRow() != -1) {
			Vector<String> r = rows.get(configTable.getSelectedRow());
			String id = r.get(ID_INDEX);
			CachedServiceProvider serviceProvider = getConfigurationHome().findById(id);
			String status = r.get(STATUS_INDEX);
			addButton.setEnabled(true);
			settingButton.setEnabled(true);
			removeButton.setEnabled(true);
			activeButton.setEnabled(true);
			activeButton.setText(ACTIVE.equals(status) ? INACTIVE : ACTIVE);
			flushCacheButton.setEnabled(true && serviceProvider.getConfiguration().getCacheable());
		} else {
			activeButton.setEnabled(false);
			removeButton.setEnabled(false);
			settingButton.setEnabled(false);
			addButton.setEnabled(true);
			flushCacheButton.setEnabled(false);
		}

	}

	private void disableButton() {
		addButton.setEnabled(false);
		settingButton.setEnabled(false);
		removeButton.setEnabled(false);
		activeButton.setEnabled(false);
		flushCacheButton.setEnabled(false);
	}

	private void setUpTableModel() {
		rows = new Vector<Vector<String>>();
		columns = new Vector<String>();

		for (String name : CONFIG_COLUMN) {
			columns.addElement(name);
		}

		tableModel = new UneditableTableModel();
		tableModel.setDataVector(rows, columns);
	}

	private void setUpUIComponent() {
		configPanel = new JPanel(new BorderLayout());
		configTable = new JTable(tableModel);
		excelAdapter = new ExcelAdapter(configTable);

		final JPopupMenu menu = new JPopupMenu();

		configTable.setComponentPopupMenu(menu);
		configTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					menu.removeAll();
					JMenuItem item;
					item = new JMenuItem();
					item.setText("Add");
					item.setEnabled(false);
					menu.add(item);
					item = new JMenuItem();
					item.setText("Remove");
					item.setEnabled(true);
					item.addActionListener(getRemoveAction());
					menu.add(item);
					item = new JMenuItem();
					item.setText("Setting");
					item.setEnabled(true);
					item.addActionListener(getSettingAction());
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.PasteAction());
					item.setText(ACTIVE);
					if (configTable.getSelectedRow() != -1) {
						Vector<String> r = rows.get(configTable.getSelectedRow());
						try {
							String id = r.get(ID_INDEX);
							CachedServiceProvider serviceProvider = getConfigurationHome().findById(id);

							item.setText(serviceProvider.getConfiguration().getStatus() ? INACTIVE : ACTIVE);
						} catch (Exception ex) {
							logger.error("encounter error when find config", ex);
						}
					}
					item.setEnabled(true);
					item.addActionListener(getActiveAction());
					menu.add(item);
					item = new JMenuItem();
					item.setText("Copy");
					item.setEnabled(true);
					item.addActionListener(getCopyAction());
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());

				}
			}
		});

		configTable.setPreferredScrollableViewportSize(new Dimension(220, 70));
		configTable.getColumn(configTable.getColumnName(ACCOUNT_INDEX)).setCellRenderer(new DefaultTableCellRenderer() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;


			@Override
			public void setValue(Object value) {
				String password = "";
				int wordLong = value == null ? 0 : value.toString().length();

				for (int i = 0; i < wordLong; i++) {
					password += "*";

				}
				super.setValue(password);
			}
		});

		// testingPanel = new TestingPanel(protocol, ip, port);
		// configurationHome.addLifeCycleListener(testingPanel);
		// testingPanel.activate();

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(configTable);
		scrollPane.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					menu.removeAll();
					JMenuItem item;
					item = new JMenuItem();
					item.setText("Add");
					item.setEnabled(true);
					item.addActionListener(getAddAction());
					menu.add(item);
					item = new JMenuItem();
					item.setText("Remove");
					item.setEnabled(false);
					menu.add(item);
					item = new JMenuItem();
					item.setText("Setting");
					item.setEnabled(false);
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.PasteAction());
					item.setText(ACTIVE);
					item.setEnabled(false);
					menu.add(item);
					item = new JMenuItem();
					item.setText("Copy");
					item.setEnabled(false);
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());

				}
			}
		});

		JLabel top = new JLabel("Configuration");
		top.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		top.setFont(top.getFont().deriveFont(Font.BOLD));
		top.setOpaque(true);
		top.setBackground(configPanel.getBackground());
		configPanel.add("North", top);
		configPanel.setPreferredSize(new Dimension(400, 100));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		JPanel rightTopPanel = new JPanel();
		rightTopPanel.setLayout(new BoxLayout(rightTopPanel, BoxLayout.Y_AXIS));
		addButton = new JButton("Add");
		removeButton = new JButton("Remove");
		settingButton = new JButton("Setting");
		activeButton = new JButton("Active");
		flushCacheButton = new JButton("Flush");

		addButton.setMaximumSize(new Dimension(80, 30));
		// addButton.setLayout(null);
		removeButton.setMaximumSize(new Dimension(80, 30));
		// removeButton.setLayout(null);
		settingButton.setMaximumSize(new Dimension(80, 30));
		// settingButton.setLayout(null);
		activeButton.setMaximumSize(new Dimension(80, 30));
		// activeButton.setLayout(null);
		flushCacheButton.setMaximumSize(new Dimension(80, 30));
		// flushCacheButton.setLayout(null);
		settingButton.setEnabled(false);
		removeButton.setEnabled(false);
		activeButton.setEnabled(false);
		flushCacheButton.setEnabled(false);
		addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		activeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		flushCacheButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		rightTopPanel.add(addButton);
		rightTopPanel.add(removeButton);
		rightTopPanel.add(settingButton);
		rightTopPanel.add(activeButton);
		rightPanel.add(rightTopPanel);
		JPanel rightBottonPanel = new JPanel();
		rightBottonPanel.setLayout(new BoxLayout(rightBottonPanel, BoxLayout.Y_AXIS));
		rightBottonPanel.add(flushCacheButton);
		rightPanel.add(rightBottonPanel);

		southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
		southPanel.add(Box.createHorizontalGlue());
		// saveButton = new JButton("Save");
		closeButton = new JButton("Close");
		// saveButton.setMaximumSize(new Dimension(80, 30));
		// saveButton.setLayout(null);
		closeButton.setMaximumSize(new Dimension(80, 30));
		closeButton.setLayout(null);

		// southPanel.add(saveButton);
		southPanel.add(closeButton);

		ListSelectionModel cellSelectionModel = configTable.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateButtons();
			}

		});

		configPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		// Add the scroll pane to this panel.
		configPanel.add("Center", scrollPane);
		configPanel.add("East", rightPanel);

		tabs = new JTabbedPane();

		// with the mozilla L&F
		toolbar = new JToolBar(); // JButtonBar(JButtonBar.HORIZONTAL);
		// toolbar.setUI(new IconPackagerButtonBarUI());
		toolbar.setPreferredSize(new Dimension(200, 70));
		tabs.addTab("Configuration", configPanel);
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		// toolbar.setBackground(Color.GRAY);
		// tabs.addTab("Testing", testPanel);
		buttonBarPanel = new ButtonBarPanel(toolbar, new JPanel[] { configPanel });// ,
																					// testingPanel.getPanel()
																					// });
																					// //
																					// ,
		// logPanel
		// });

		aboutItem = new MenuItem("About");
		startItem = new MenuItem("Start service");
		stopItem = new MenuItem("Stop service");
		configItem = new MenuItem("Configure...");
		exitItem = new MenuItem("Exit");
	}

	private ActionListener getAddAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					newConfiguration();
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}

		};
	}

	private ActionListener getRemoveAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				disableButton();
				SwingWorker<Boolean, Void> mySwingWorker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						// mimic some long-running process here...
						boolean ret = true;
						try {
							int index = configTable.getSelectedRow();

							if (index != -1) { // At least one Row in Table
								DefaultTableModel model = (DefaultTableModel) configTable.getModel();
								Object value = model.getValueAt(index, ID_INDEX);

								try {
									String id = String.valueOf(value);
									getConfigurationHome().remove(id);
								} catch (Exception e1) {
									logger.error("encounter error when removing config", e1);
									ret = false;
								}
							}
						} catch (Exception e) {
							logger.warn("remove exception", e);
						}
						return ret;

					}
				};

				mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals("state")) {
							if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
								progressDialog.dispose();
								updateButtons();
							}
						}
					}
				});
				mySwingWorker.execute();

				progressDialog.setVisible(true);
				try {
					if (!mySwingWorker.get()) {
						JOptionPane.showMessageDialog(null, "encounter error when remove config");
					}
				} catch (Exception e1) {

				}
			}

		};
	}


	private class FlushCacheActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent t) {
			List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
			JPanel al = new JPanel(new GridLayout(0, 2));
			for (ComputeQuartzType type : ComputeQuartzType.values()) {
				JCheckBox box = new JCheckBox(type.name());
				checkBoxList.add(box);
				al.add(box);
			}
			int n = JOptionPane.showConfirmDialog(null, al, "Select caches to be flush...", JOptionPane.OK_CANCEL_OPTION);

			final List<ComputeQuartzType> selected = new ArrayList<ComputeQuartzType>();

			for (JCheckBox box : checkBoxList) {
				if (box.isSelected()) {
					selected.add(ComputeQuartzType.valueOf(box.getText()));
				}
			}
			checkBoxList.clear();

			if (JOptionPane.CANCEL_OPTION == n || selected.isEmpty()) {
				return;
			}

			Window win = SwingUtilities.getWindowAncestor(flushCacheButton);
			final CancellableProgressDialog progressDialog =
					new CancellableProgressDialog(win, "Dialog", "Please wait.......", ModalityType.APPLICATION_MODAL);
			progressDialog.setButtonText("Run in background");
			flushCacheButton.setEnabled(false);
			final SwingWorker<Boolean, Void> mySwingWorker = new SwingWorker<Boolean, Void>() {

				@Override
				protected Boolean doInBackground() throws Exception {
					// mimic some long-running process here...
					boolean ret = true;
					Vector<String> r = rows.get(configTable.getSelectedRow());

					try {
						String id = r.get(ID_INDEX);
						CachedServiceProvider serviceProvider = getConfigurationHome().findById(id);
						for (ComputeQuartzType type : selected) {
							serviceProvider.getComputeServices().flushCache(type).get();
						}
					} catch (Exception e1) {
						logger.error("encounter error when update config", e1);
						ret = false;
					}
					return ret;

				}
			};

			mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("state")) {
						if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
							progressDialog.dispose();
							updateButtons();
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
				if (!mySwingWorker.get()) {
					JOptionPane.showMessageDialog(null, "encounter error when update config");
				}
			} catch (Exception e1) {

			}
		}
	}


	private ActionListener getActiveAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				disableButton();
				SwingWorker<Boolean, Void> mySwingWorker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						// mimic some long-running process here...
						boolean ret = true;
						Vector<String> r = rows.get(configTable.getSelectedRow());

						try {
							String id = r.get(ID_INDEX);
							CachedServiceProvider serviceProvider = getConfigurationHome().findById(id);
							Configuration configuration = serviceProvider.getConfiguration();
							logger.debug("accessconfig status changed from {} to {}",
									new Object[] { configuration.getStatus(), !configuration.getStatus() });
							configuration.setStatus(!configuration.getStatus());
							getConfigurationHome().merge(id, configuration);
						} catch (Exception e1) {
							logger.error("encounter error when update config", e1);
							ret = false;
						}
						return ret;

					}
				};

				mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals("state")) {
							if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
								progressDialog.dispose();
								updateButtons();
							}
						}
					}
				});
				mySwingWorker.execute();
				progressDialog.setVisible(true);
				try {
					if (!mySwingWorker.get()) {
						JOptionPane.showMessageDialog(null, "encounter error when update config");
					}
				} catch (Exception e1) {

				}
			}

		};
	}

	private ActionListener getSettingAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<String> r = rows.get(configTable.getSelectedRow());
				try {
					String id = r.get(ID_INDEX);
					CachedServiceProvider serviceProvider = getConfigurationHome().findById(id);
					updateForm(serviceProvider.getConfiguration());
				} catch (Exception e1) {
					logger.error("encounter error when update config", e1);
					JOptionPane.showMessageDialog(null, "encounter error when set config");
				}
				configTable.clearSelection();
			}

		};
	}

	private ActionListener getCopyAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				excelAdapter.copyRow();
			}

		};
	}

	public void setUpFrame() {
		if (frame != null) {
			return;
		}

		frame = new JFrame(title);
		frame.setLayout(new BorderLayout());
		frame.add("Center", buttonBarPanel);
		frame.add("South", southPanel);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1000, 600));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setIconImage(iconOpen.getImage());
		Window win = SwingUtilities.getWindowAncestor(frame);
		progressDialog = new ProgressDialog(win, "Dialog", "Please wait.......", ModalityType.APPLICATION_MODAL);
	}

	private void setUpTrayIcon() {
		if (!SystemTray.isSupported()) {
			logger.warn("SystemTray is not supported");
			return;
		}

		trayPopup = new PopupMenu();
		trayIcon = new TrayIcon((iconOpen).getImage());
		tray = SystemTray.getSystemTray();

		trayPopup.add(configItem);
		trayPopup.add(startItem);
		trayPopup.add(stopItem);
		trayPopup.add(exitItem);
		trayPopup.addSeparator();
		trayPopup.add(aboutItem);

		trayIcon.setPopupMenu(trayPopup);
		stopItem.setEnabled(true);
		startItem.setEnabled(false);
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip(title);
	}

	public void enableTray(boolean enable) {
		if (!SystemTray.isSupported()) {
			logger.warn("SystemTray is not supported");
			return;
		}

		try {
			if (enable) {
				if (!isTrayActivate) {
					tray.add(trayIcon);
					isTrayActivate = true;
				}
			} else {
				tray.remove(trayIcon);
				isTrayActivate = false;
			}
			logger.debug("Tray enable? {}", enable);

		} catch (AWTException e) {
			logger.error("TrayIcon could not be added", e);
			return;
		}

	}

	public void setVisible(boolean visible) {
		if (frame == null) {
			return;
		}

		frame.setVisible(visible);
		if (visible) {
			frame.setState(JFrame.NORMAL);
		} else if (SystemTray.isSupported()) {
			frame.setState(JFrame.ICONIFIED);
		}
		logger.debug("MainFrame visible? {}", visible);
	}

	public JTable getConfigTable() {
		return configTable;
	}

	private void close() {
		setVisible(false);
		dispose();
		System.exit(0);
	}

	@Override
	public synchronized void persist(Configuration configuration) {
		Vector<String> r = new Vector<String>();
		r.addElement(configuration.getId());
		r.addElement(configuration.getStatus() ? ACTIVE : INACTIVE);
		r.addElement(configuration.getCloudName());
		r.addElement(configuration.getProviderName());
		r.addElement(configuration.getProviderClass());
		r.addElement(configuration.getEndpoint());
		r.addElement(configuration.getAccount());
		r.addElement(configuration.getRegionId());
		final SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_RFC1123);
		r.addElement(formatter.format(configuration.getModifiedDate().getTime()));
		int index = getRow(configuration);
		tableModel.insertRow(index, r);
		if (configTable != null) {
			configTable.addNotify();
		}
		logger.info("Register Configuration: config name={}, driver={}, row={}", new Object[] {
				configuration.getCloudName(), configuration.getProviderClass(), index });
	}

	private int getRow(Configuration configuration) {
		return tableModel.getRowCount();
	}

	@Override
	public synchronized void remove(Configuration configuration) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (tableModel.getValueAt(i, ID_INDEX).equals(configuration.getId())) {
				tableModel.removeRow(i);
				if (configTable != null) {
					configTable.addNotify();
					configTable.clearSelection();
				}
				logger.info("deregister configuration: {}", configuration.getCloudName());
			}
		}
	}

	@Override
	public synchronized void lightMerge(Configuration configuration) {
		merge(configuration);
	}

	@Override
	public synchronized void heavyMerge(Configuration configuration) {
		merge(configuration);
	}

	public synchronized void merge(Configuration configuration) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (tableModel.getValueAt(i, ID_INDEX).equals(configuration.getId())) {
				tableModel.setValueAt(configuration.getStatus() ? ACTIVE : INACTIVE, i, STATUS_INDEX);
				tableModel.setValueAt(configuration.getCloudName(), i, CLOUDNAME_INDEX);
				tableModel.setValueAt(configuration.getProviderName(), i, PROVIDERNAME_INDEX);
				tableModel.setValueAt(configuration.getProviderClass(), i, PROVIDERCLASS_INDEX);
				// tableModel.setValueAt(accessConfig.getJks(), i, JKS_INDEX);
				tableModel.setValueAt(configuration.getEndpoint(), i, ENDPOINT_INDEX);
				tableModel.setValueAt(configuration.getAccount(), i, ACCOUNT_INDEX);
				tableModel.setValueAt(configuration.getRegionId(), i, REGIONID_INDEX);

				if (configTable != null) {
					configTable.addNotify();
					// configTable.clearSelection();
				}

				logger.info("update Configuration: {}", configuration.getCloudName());
			}
		}
	}

	@Override
	public synchronized void clear() {
		logger.debug("clear TableModel: {}", tableModel.getRowCount());
		final int rows = tableModel.getRowCount();
		for (int i = 0; i < rows; i++) {
			logger.debug("TableModel remove Row: {}", i);
			tableModel.removeRow(0);
		}

		if (configTable != null) {
			configTable.addNotify();
			configTable.clearSelection();
		}
	}

	private void updateForm(final Configuration configuration) throws Exception {
		Configuration original = configuration.clone();
		ConfigurationDialog dialog = new ConfigurationDialog(frame, configuration, getConfigurationHome(), getDriverHome());
		boolean ret = dialog.showDialog();
		final StringBuilder message = new StringBuilder();

		if (ret && !original.equalsTo(configuration)) {
			disableButton();
			SwingWorker<Boolean, Void> mySwingWorker = new SwingWorker<Boolean, Void>() {

				@Override
				protected Boolean doInBackground() throws Exception {
					// mimic some long-running process here...
					boolean ret = true;
					try {
						configurationHome.merge(configuration.getId(), configuration);
					} catch (Exception e) {
						ret = false;
						message.append(e.getMessage());
						logger.warn("persist fail", e);
					}
					return ret;

				}
			};

			mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("state")) {
						if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
							progressDialog.dispose();
							updateButtons();
						}
					}
				}
			});
			mySwingWorker.execute();
			progressDialog.setVisible(true);

			try {
				if (!mySwingWorker.get()) {
					JOptionPane.showMessageDialog(null, "encounter error when update config:" + message.toString());
				}
			} catch (Exception e1) {

			}
		} else {
			updateButtons();
		}
	}

	private void newConfiguration() throws Exception {
		final Configuration configuration = new Configuration();
		ConfigurationDialog dialog = new ConfigurationDialog(frame, configuration, getConfigurationHome(), getDriverHome());
		boolean ret = dialog.showDialog();
		logger.debug("AccessConfigDialog return value: {}", ret);

		if (ret) {
			disableButton();
			SwingWorker<Boolean, Void> mySwingWorker = new SwingWorker<Boolean, Void>() {

				@Override
				protected Boolean doInBackground() throws Exception {
					// mimic some long-running process here...
					boolean ret = true;
					try {
						configurationHome.persist(configuration);
					} catch (Exception e) {
						ret = false;
						logger.warn("persist fail", e);
					}
					return ret;

				}
			};

			mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("state")) {
						if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
							progressDialog.dispose();
							updateButtons();
						}
					}
				}
			});
			mySwingWorker.execute();
			progressDialog.setVisible(true);

			try {
				if (!mySwingWorker.get()) {
					JOptionPane.showMessageDialog(null, "encounter error when add config");
				}
			} catch (Exception e1) {

			}
		} else {
			updateButtons();
		}
	}

	public void dispose() {
		if (frame == null) {
			return;
		}
		frame.dispose();
		frame.invalidate();
		frame = null;
		// testingPanel.dispose();
		logger.debug("dispose JFrame");
	}

	public DriverHome getDriverHome() {
		return driverHome;
	}

	public void setDriverHome(DriverHome driverHome) {
		this.driverHome = driverHome;
	}

	// public TestingPanel getTestingPanel() {
	// return testingPanel;
	// }
	//
	// public void setTestingPanel(TestingPanel testingPanel) {
	// this.testingPanel = testingPanel;
	// }

	public JFrame getFrame() {
		return frame;
	}


	private class ButtonBarPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Component currentComponent;


		public ButtonBarPanel(JToolBar toolbar, JComponent[] panel) {
			setLayout(new BorderLayout());

			add("North", toolbar);

			ButtonGroup group = new ButtonGroup();

			addButton("Configure", iconConfig, panel[0], toolbar, group);

			// addButton("Testing", iconTest, panel[1], toolbar, group);

			// addButton("Event", iconLog, panel[2], toolbar, group);
		}

		private void show(Component component) {
			if (currentComponent != null) {
				remove(currentComponent);
			}
			currentComponent = component;
			add("Center", currentComponent);
			revalidate();
			repaint();
		}

		public void addButton(String title, ImageIcon icon, final Component component, JToolBar bar, ButtonGroup group) {
			Action action = new AbstractAction(title, icon) {

				/**
						 * 
						 */
				private static final long serialVersionUID = 1L;


				@Override
				public void actionPerformed(ActionEvent e) {
					show(component);
				}
			};

			JToggleButton button = new JToggleButton(action);
			bar.add(button);

			group.add(button);

			if (group.getSelection() == null) {
				button.setSelected(true);
				show(component);
			}
		}
	}

}
