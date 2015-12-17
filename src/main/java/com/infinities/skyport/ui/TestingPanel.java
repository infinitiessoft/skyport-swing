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
//package com.infinities.skyport.ui;
//
//import java.awt.BorderLayout;
//import java.awt.Component;
//import java.awt.Container;
//import java.awt.Dialog.ModalityType;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.Window;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.io.BufferedReader;
//import java.io.Closeable;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.Arrays;
//import java.util.EventObject;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Vector;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import javax.swing.BorderFactory;
//import javax.swing.DefaultCellEditor;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JMenuItem;
//import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//import javax.swing.JTree;
//import javax.swing.ScrollPaneConstants;
//import javax.swing.SwingUtilities;
//import javax.swing.SwingWorker;
//import javax.swing.event.CellEditorListener;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
//import javax.swing.event.TreeSelectionEvent;
//import javax.swing.event.TreeSelectionListener;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableCellEditor;
//import javax.swing.table.TableCellRenderer;
//import javax.swing.table.TableColumn;
//import javax.swing.text.DefaultEditorKit;
//import javax.swing.text.JTextComponent;
//import javax.swing.tree.DefaultTreeModel;
//import javax.swing.tree.TreeModel;
//
//import org.dasein.cloud.ci.TopologyProvisionOptions.AccessConfig;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.base.Strings;
//import com.infinities.skyport.annotation.Cmd;
//import com.infinities.skyport.annotation.Param;
//import com.infinities.skyport.service.ConfigurationLifeCycleListener;
//import com.infinities.skyport.ui.testing.DisabledNode;
//import com.infinities.skyport.util.FormatUtil;
//
//public class TestingPanel implements ConfigurationLifeCycleListener, Closeable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	private static final Logger logger = LoggerFactory.getLogger(TestingPanel.class);
//	private static final String[] API_COLUMN = { "Name", "Data Type", "Description", "Constraints", "Value" };
//	private static final String TESTING_ERROR = "Testing failed";
//	private static final String SEPERATOR = ":";
//
//	private JPanel panel;
//	private JTree tree;
//	private DisabledNode top;
//	private DefaultTreeModel treeModel;
//	private JTextField urlField;
//	private JTable paraTable;
//	private DefaultTableModel tableModel;
//	private JTextArea resultArea, descArea;
//	private JButton submit;
//	private Vector<String> rows, columns;
//	private final String protocol;
//	private final String port;
//	private final String ip;
//	private final String context;
//
//
//	public TestingPanel(String protocol, String ip, String port) {
//		this.protocol = protocol;
//		this.ip = ip;
//		this.port = port;
//		this.context = "/skyport";
//		setUpTreeModel();
//
//	}
//
//	public void activate() {
//		initResource();
//		setUpUIComponent();
//		setUpEventListener();
//	}
//
//	public void deactivate() {
//
//	}
//
//	private void setUpTreeModel() {
//		top = new DisabledNode("API Usages");
//		treeModel = new DefaultTreeModel(top);
//	}
//
//	private void initResource() {
//
//	}
//
//	private void setUpUIComponent() {
//		panel = new JPanel(new BorderLayout());
//		tree = new JTree(treeModel);
//
//		JScrollPane treeView = new JScrollPane(tree);
//		treeView.setPreferredSize(new Dimension(200, 100));
//		panel.add("West", treeView);
//
//		JPanel rightPanel = new JPanel(new BorderLayout());
//
//		JPanel rightTopPanel = new JPanel(new BorderLayout());
//		JPanel rightCentralPanel = new JPanel(new BorderLayout());
//		JPanel rightBottomPanel = new JPanel(new BorderLayout());
//
//		JLabel request = new JLabel("Request");
//		request.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
//		request.setFont(request.getFont().deriveFont(Font.BOLD));
//		request.setOpaque(true);
//		request.setBackground(rightPanel.getBackground());
//
//		rightTopPanel.add(request, BorderLayout.PAGE_START);
//
//		JLabel urlName = new JLabel("URL:");
//
//		rightTopPanel.add(urlName, BorderLayout.LINE_START);
//
//		urlField = new JTextField();
//		installContextMenu(urlField);
//		rightTopPanel.add(urlField, BorderLayout.CENTER);
//
//		submit = new JButton("Submit");
//		rightTopPanel.add(submit, BorderLayout.LINE_END);
//
//		rows = new Vector<String>();
//		columns = new Vector<String>();
//
//		for (String name : API_COLUMN) {
//			columns.addElement(name);
//		}
//
//		tableModel = new DefaultTableModel() {
//
//			private static final long serialVersionUID = 1L;
//
//
//			@Override
//			public boolean isCellEditable(int row, int column) {
//				if (column == 4) {
//					return true;
//				}
//				return false;
//			}
//		};
//
//		tableModel.setDataVector(rows, columns);
//		tableModel.addTableModelListener(new ParaTableListener());
//
//		JPanel descPanel = new JPanel(new BorderLayout());
//		// JLabel descName = new JLabel("Description:");
//
//		descArea = new JTextArea(2, 2);
//		descArea.setEditable(false);
//		descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
//
//		paraTable = new JTable(tableModel);
//		paraTable.setPreferredScrollableViewportSize(new Dimension(200, 70));
//		paraTable.getColumn(API_COLUMN[4]).setCellRenderer(new EachRowRenderer());
//		paraTable.getColumn(API_COLUMN[4]).setCellEditor(new EachRowEditor(paraTable));
//		paraTable.setShowHorizontalLines(true);
//		new ExcelAdapter(paraTable);
//
//		TableColumn col = paraTable.getColumnModel().getColumn(0);
//		col.setPreferredWidth(100);
//		col = paraTable.getColumnModel().getColumn(2);
//		col.setPreferredWidth(400);
//		col = paraTable.getColumnModel().getColumn(3);
//		col.setPreferredWidth(215);
//
//		rightCentralPanel.add(descPanel, BorderLayout.PAGE_START);
//		rightCentralPanel.add(new JScrollPane(paraTable), BorderLayout.CENTER);
//
//		JLabel response = new JLabel("Response");
//		response.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
//		response.setFont(response.getFont().deriveFont(Font.BOLD));
//		response.setOpaque(true);
//		response.setBackground(rightPanel.getBackground());
//		rightBottomPanel.add(response, BorderLayout.PAGE_START);
//		// JLabel resultName = new JLabel("Result:");
//
//		resultArea = new JTextArea(10, 10);
//		resultArea.setLineWrap(true);
//		JScrollPane scrollPane = new JScrollPane(resultArea);
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		rightBottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
//
//		rightPanel.add(rightTopPanel, BorderLayout.PAGE_START);
//		rightPanel.add(rightCentralPanel, BorderLayout.CENTER);
//		rightPanel.add(rightBottomPanel, BorderLayout.PAGE_END);
//
//		panel.add("Center", rightPanel);
//
//		panel.setPreferredSize(new Dimension(400, 100));
//		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
//		installContextMenu(panel);
//	}
//
//	private void setUpEventListener() {
//		tree.addTreeSelectionListener(new SelectTreeSelectionListener());
//		submit.addActionListener(new SubmitActionListener());
//	}
//
//	private void clean() {
//		urlField.setText("");
//		descArea.setText("");
//		// resultArea.setText("");
//		tableModel.setRowCount(0);
//	}
//
//	private void register(AsyncDriver asyncDriver) {
//		AccessConfig config = asyncDriver.getConfiguration();
//		int index = getRow(config.getId());
//		register(asyncDriver, index);
//	}
//
//	private void register(AsyncDriver asyncDriver, int index) {
//		AccessConfig config = asyncDriver.getConfiguration();
//		DisabledNode access = new DisabledNode(config.getName() + SEPERATOR + config.getId());
//		logger.debug("top:{}, index:{}", new Object[] { top.getChildCount(), index });
//
//		treeModel.insertNodeInto(access, top, index);
//		List<Cmd> cmds = asyncDriver.getCmds();
//		logger.debug("register AccessConfig: {}, cmds: {}", new Object[] { config.getName(), cmds.size() });
//		for (Cmd annotation : cmds) {
//			DisabledNode cmd = new DisabledNode(new CmdInfo(annotation, config.getUsername(), config.getPassword(),
//					config.getDomain(), config.getName()));
//			// cmd.setEnabled(config.getStatus());
//			access.add(cmd);
//		}
//
//		if (tree != null) {
//			tree.expandRow(0);
//		}
//	}
//
//	private int getRow(String aid) {
//		for (int i = 0; i < top.getChildCount(); i++) {
//			DisabledNode node = (DisabledNode) top.getChildAt(i);
//			String name = (String) node.getUserObject();
//			name = name.split(SEPERATOR)[0];
//			String id = name;
//			if (aid.equals(id)) {
//				return i;
//			}
//		}
//		return top.getChildCount();
//	}
//
//	private void displayCmd(Cmd cmd, String username, String password, String domain, String config) {
//		tableModel.fireTableRowsDeleted(0, tableModel.getRowCount());
//		tableModel.getDataVector().clear();
//		tableModel.setRowCount(0);
//		if (cmd != null) {
//			descArea.setText(cmd.name() + ":" + cmd.description());
//
//			Param[] paras = cmd.param();
//			// for(Param para:paras){
//			for (int i = 0; i < paras.length; i++) {
//				final int row = i;
//				Param para = paras[i];
//				Vector<String> r = new Vector<String>();
//				String name = (para.required() ? para.name() + "*" : para.name());
//				r.addElement(name);
//				r.addElement(para.type());
//				r.addElement(para.description());
//				String constraint = Arrays.deepToString(para.constraints()).replace("[", "").replace("]", "");
//				r.addElement(constraint);
//				// int vColIndex = 0;
//				String[] values = para.enumeration();
//
//				if (values != null && values.length > 0) {
//					JComboBox<String> comboBox = new JComboBox<String>(values);
//					comboBox.insertItemAt("", 0);
//					comboBox.setSelectedIndex(0);
//					DefaultCellEditor comboBoxEditor = new DefaultCellEditor(comboBox);
//					comboBoxEditor.setClickCountToStart(1);
//					r.addElement("");
//					EachRowEditor rowEditor = (EachRowEditor) paraTable.getColumn(API_COLUMN[4]).getCellEditor();
//					rowEditor.setEditorAt(i, comboBoxEditor);
//				} else {
//					JTextField textField = new JTextField();
//					installContextMenu(textField);
//					final DefaultCellEditor textFieldEditor = new DefaultCellEditor(textField);
//					textFieldEditor.setClickCountToStart(1);
//
//					textField.addKeyListener(new KeyListener() {
//
//						@Override
//						public void keyTyped(KeyEvent e) {
//
//						}
//
//						@Override
//						public void keyPressed(KeyEvent e) {
//
//						}
//
//						@Override
//						public void keyReleased(KeyEvent e) {
//							tableModel.setValueAt(textFieldEditor.getCellEditorValue(), row, 4);
//							// tableModel.fireTableDataChanged();
//
//						}
//					});
//
//					if ("username".equals(para.name())) {
//						r.addElement(username);
//					} else if ("password".equals(para.name())) {
//						r.addElement(password);
//					} else if ("domain".equals(para.name())) {
//						r.addElement(domain);
//					} else {
//						r.addElement("");
//					}
//
//					EachRowEditor rowEditor = (EachRowEditor) paraTable.getColumn(API_COLUMN[4]).getCellEditor();
//					rowEditor.setEditorAt(i, textFieldEditor);
//
//				}
//				tableModel.addRow(r);
//			}
//
//			urlField.setText(protocol + "://" + ip + ":" + port + context + "?config=" + config + "&cmd="
//					+ FormatUtil.toHex(cmd.id()));
//
//		} else {
//			return;
//		}
//	}
//
//	public TreeModel getTreeModel() {
//		return treeModel;
//	}
//
//	public DisabledNode getTopNode() {
//		return top;
//	}
//
//
//	private class ParaTableListener implements TableModelListener {
//
//		@Override
//		public void tableChanged(TableModelEvent te) {
//
//			if (TableModelEvent.INSERT == te.getType()) {
//				logger.debug("A new row was added");
//			}
//			if (TableModelEvent.DELETE == te.getType()) {
//				logger.debug("A row was removed");
//			}
//
//			if (TableModelEvent.UPDATE == te.getType()) {
//				logger.debug("A row was updated");
//
//				@SuppressWarnings("unchecked")
//				Vector<Vector<String>> rows = tableModel.getDataVector();
//				StringBuilder url = new StringBuilder();
//				String oldUrl = urlField.getText();
//				if (oldUrl.contains("&")) {
//					String[] urls = oldUrl.split("&");
//					oldUrl = urls[0] + "&" + urls[1];
//
//				}
//				url.append(oldUrl);
//
//				for (Vector<String> row : rows) {
//
//					if (!Strings.isNullOrEmpty(row.elementAt(4))) {
//						url.append("&" + row.elementAt(0).replace("*", "") + "=" + row.elementAt(4));
//
//					}
//				}
//
//				urlField.setText(url.toString());
//
//			}
//
//		}
//	}
//
//	private class CmdInfo {
//
//		private final Cmd cmd;
//		private final String username;
//		private final String password;
//		private final String domain;
//		private final String config;
//
//
//		public CmdInfo(Cmd cmd, String username, String password, String domain, String config) {
//			this.cmd = cmd;
//			this.username = username;
//			this.password = password;
//			this.domain = domain;
//			this.config = config;
//		}
//
//		@Override
//		public String toString() {
//			return cmd.name();
//		}
//
//		public Cmd getCmd() {
//			return cmd;
//		}
//
//		public String getUsername() {
//			return username;
//		}
//
//		public String getPassword() {
//			return password;
//		}
//
//		public String getDomain() {
//			return domain;
//		}
//
//		public String getConfig() {
//			return config;
//		}
//	}
//
//	private class SelectTreeSelectionListener implements TreeSelectionListener {
//
//		@Override
//		public void valueChanged(TreeSelectionEvent e) {
//			DisabledNode node = (DisabledNode) tree.getLastSelectedPathComponent();
//
//			if (node == null) {
//				return;
//			}
//			Object nodeInfo = node.getUserObject();
//			clean();
//			if (node.isLeaf() && nodeInfo instanceof CmdInfo) {
//
//				CmdInfo cmd = (CmdInfo) nodeInfo;
//				displayCmd(cmd.getCmd(), cmd.getUsername(), cmd.getPassword(), cmd.getDomain(), cmd.getConfig());
//
//			} else {
//				return;
//			}
//			logger.debug("You selected node: {}", node);
//		}
//	}
//
//	private class SubmitActionListener implements ActionListener {
//
//		@Override
//		public void actionPerformed(ActionEvent t) {
//			Window win = SwingUtilities.getWindowAncestor(submit);
//			final CancellableProgressDialog progressDialog = new CancellableProgressDialog(win, "Dialog",
//					"Please wait.......", ModalityType.APPLICATION_MODAL);
//			submit.setEnabled(false);
//			final SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {
//
//				@Override
//				protected Void doInBackground() throws Exception {
//					// mimic some long-running process here...
//					DisabledNode node = (DisabledNode) tree.getLastSelectedPathComponent();
//
//					if (node == null) {
//						return null;
//					}
//
//					Object nodeInfo = node.getUserObject();
//
//					if (node.isLeaf()) {
//						CmdInfo cmd = (CmdInfo) nodeInfo;
//						String url = urlField.getText();
//
//						logger.trace("invoke command: {}" + cmd.getCmd());
//						String output = invokeCommand(url);
//						resultArea.setText(output);
//
//						logger.debug("Request Enter From: Testing, Config: {}, Cmd: {}, URL: {}, Result: {}", new Object[] {
//								cmd.getConfig(), cmd, url, output });
//					}
//					return null;
//				}
//			};
//
//			mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {
//
//				@Override
//				public void propertyChange(PropertyChangeEvent evt) {
//					if (evt.getPropertyName().equals("state")) {
//						if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
//							progressDialog.dispose();
//							submit.setEnabled(true);
//						}
//					}
//				}
//			});
//			progressDialog.addCancelActionListener(new ActionListener() {
//
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					mySwingWorker.cancel(true);
//				}
//			});
//			mySwingWorker.execute();
//			progressDialog.setVisible(true);
//		}
//	}
//
//
//	private String invokeCommand(String url) {
//		InputStream im = null;
//		InputStreamReader isr = null;
//		BufferedReader br = null;
//		HttpURLConnection uc = null;
//		StringBuffer mess = new StringBuffer();
//		try {
//			uc = getConnection(url);
//			// uc.setRequestMethod("GET");
//			// uc.setDoOutput(true);
//			// uc.setDoInput(true);
//			uc.connect();
//			im = uc.getInputStream();
//			isr = new InputStreamReader(im);
//			br = new BufferedReader(isr);
//
//			int c = 0;
//			while ((c = br.read()) != -1) {
//				mess.append((char) c);
//			}
//
//			// while ((line = br.readLine()) != null) {
//			// mess.append(line);
//			// }
//		} catch (Exception e) {
//			logger.warn("testing exception", e);
//			return TESTING_ERROR;
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					logger.error("Unexpected exception when close BufferedReader", e);
//				}
//			}
//			if (isr != null) {
//				try {
//					isr.close();
//				} catch (IOException e) {
//					logger.error("Unexpected exception when close InputStreamReader", e);
//				}
//			}
//			if (im != null) {
//				try {
//					im.close();
//				} catch (IOException e) {
//					logger.error("Unexpected exception when close InputStream", e);
//				}
//			}
//			if (uc != null) {
//				uc.disconnect();
//			}
//		}
//
//		return mess.toString();
//	}
//
//	private HttpURLConnection getConnection(String url) throws Exception {
//		if (url.contains("https")) {
//			HostnameVerifier hv = new HostnameVerifier() {
//
//				@Override
//				public boolean verify(String urlHostName, SSLSession session) {
//					System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
//					return true;
//				}
//			};
//			HttpsURLConnection.setDefaultHostnameVerifier(hv);
//		}
//		URL ul = new URL(url);
//		URLConnection connection = ul.openConnection();
//
//		if (url.contains("https")) {
//			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//
//				@Override
//				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//
//				@Override
//				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//				}
//
//				@Override
//				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//				}
//			} };
//
//			SSLContext sc = SSLContext.getInstance("SSL");
//
//			sc.init(null, trustAllCerts, new java.security.SecureRandom());
//			final SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
//			((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
//		}
//		connection.setConnectTimeout(60000);
//		connection.setReadTimeout(60000);
//		return (HttpURLConnection) connection;
//
//	}
//
//
//	private class EachRowRenderer implements TableCellRenderer {
//
//		private final Hashtable<Integer, TableCellRenderer> renderers;
//
//		private TableCellRenderer renderer;
//
//		private final TableCellRenderer defaultRenderer;
//
//
//		public EachRowRenderer() {
//			renderers = new Hashtable<Integer, TableCellRenderer>();
//			defaultRenderer = new DefaultTableCellRenderer();
//		}
//
//		@Override
//		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
//				int row, int column) {
//			renderer = renderers.get(row);
//			if (renderer == null) {
//				renderer = defaultRenderer;
//			}
//			return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//
//		}
//	}
//
//	private class EachRowEditor implements TableCellEditor {
//
//		private final Hashtable<Integer, TableCellEditor> editors;
//
//		private TableCellEditor editor;
//
//		private final TableCellEditor defaultEditor;
//
//		private final JTable table;
//
//
//		public EachRowEditor(JTable table) {
//			this.table = table;
//			editors = new Hashtable<Integer, TableCellEditor>();
//			defaultEditor = new DefaultCellEditor(new JTextField());
//		}
//
//		public void setEditorAt(int row, TableCellEditor editor) {
//			editors.put(row, editor);
//		}
//
//		@Override
//		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//
//			return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
//		}
//
//		@Override
//		public Object getCellEditorValue() {
//			return editor.getCellEditorValue();
//		}
//
//		@Override
//		public boolean stopCellEditing() {
//			return editor.stopCellEditing();
//		}
//
//		@Override
//		public void cancelCellEditing() {
//			editor.cancelCellEditing();
//		}
//
//		@Override
//		public boolean isCellEditable(EventObject anEvent) {
//			if (anEvent instanceof MouseEvent) {
//				selectEditor((MouseEvent) anEvent);
//			}
//			return editor.isCellEditable(anEvent);
//		}
//
//		@Override
//		public void addCellEditorListener(CellEditorListener l) {
//			editor.addCellEditorListener(l);
//		}
//
//		@Override
//		public void removeCellEditorListener(CellEditorListener l) {
//			editor.removeCellEditorListener(l);
//		}
//
//		@Override
//		public boolean shouldSelectCell(EventObject anEvent) {
//			if (anEvent instanceof MouseEvent) {
//				selectEditor((MouseEvent) anEvent);
//			}
//			return editor.shouldSelectCell(anEvent);
//		}
//
//		protected void selectEditor(MouseEvent e) {
//			int row;
//			if (e == null) {
//				row = table.getSelectionModel().getAnchorSelectionIndex();
//			} else {
//				row = table.rowAtPoint(e.getPoint());
//			}
//			editor = editors.get(row);
//			if (editor == null) {
//				editor = defaultEditor;
//			}
//		}
//	}
//
//
//	@Override
//	public void close() throws IOException {
//	}
//
//	@Override
//	public synchronized void persist(AsyncDriver asyncDriver) {
//		register(asyncDriver);
//		logger.debug("persist AccessConfig: {}", asyncDriver.getConfiguration().getName());
//	}
//
//	@Override
//	public synchronized void merge(AsyncDriver asyncDriver) {
//		AccessConfig accessConfig = asyncDriver.getConfiguration();
//		for (int i = 0; i < getTopNode().getChildCount(); i++) {
//			String nodeName = getTopNode().getChildAt(i).toString();
//			String[] seps = nodeName.split(SEPERATOR);
//			if (seps[1].equals(accessConfig.getId())) {
//				DisabledNode selectedNode = (DisabledNode) treeModel.getChild(getTopNode(), i);
//				treeModel.removeNodeFromParent(selectedNode);
//				logger.debug("remove AccessConfig from tree: {}", accessConfig.getName());
//				register(asyncDriver, i);
//			}
//		}
//		logger.debug("merge AccessConfig: {}", accessConfig.getName());
//
//	}
//
//	@Override
//	public synchronized void remove(AsyncDriver asyncDriver) {
//		AccessConfig accessConfig = asyncDriver.getConfiguration();
//		for (int i = 0; i < getTopNode().getChildCount(); i++) {
//			if (getTopNode().getChildAt(i).toString().equals(accessConfig.getName() + SEPERATOR + accessConfig.getId())) {
//				DisabledNode selectedNode = (DisabledNode) treeModel.getChild(getTopNode(), i);
//				treeModel.removeNodeFromParent(selectedNode);
//				logger.debug("remove AccessConfig from tree: {}", accessConfig.getName());
//			}
//		}
//		logger.debug("remove AccessConfig: {}", accessConfig.getName());
//	}
//
//	@Override
//	public void clear() {
//		getTopNode().removeAllChildren();
//		treeModel.reload(getTopNode());
//		logger.debug("clear TreeModel");
//	}
//
//	public JPanel getPanel() {
//		return panel;
//	}
//
//	public void dispose() {
//		if (panel == null) {
//			return;
//		}
//		panel.invalidate();
//		panel = null;
//		logger.debug("dispose JPanel");
//	}
//
//	private void installContextMenu(Container comp) {
//		if (comp instanceof JTextComponent) {
//
//			comp.addMouseListener(new MouseAdapter() {
//
//				@Override
//				public void mousePressed(MouseEvent e) {
//					maybeShowPopup(e);
//				}
//
//				@Override
//				public void mouseReleased(MouseEvent e) {
//					maybeShowPopup(e);
//				}
//
//				private void maybeShowPopup(MouseEvent e) {
//					if (e.isPopupTrigger()) {
//						final JTextComponent component = (JTextComponent) e.getComponent();
//						final JPopupMenu menu = new JPopupMenu();
//						JMenuItem item;
//						item = new JMenuItem(new DefaultEditorKit.CopyAction());
//						item.setText("Copy");
//						item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
//						menu.add(item);
//						item = new JMenuItem(new DefaultEditorKit.CutAction());
//						item.setText("Cut");
//						item.setEnabled(component.isEditable()
//								&& component.getSelectionStart() != component.getSelectionEnd());
//						menu.add(item);
//						item = new JMenuItem(new DefaultEditorKit.PasteAction());
//						item.setText("Paste");
//						item.setEnabled(component.isEditable());
//						menu.add(item);
//						menu.show(e.getComponent(), e.getX(), e.getY());
//
//					}
//				}
//			});
//
//		}
//
//		for (Component c : comp.getComponents()) {
//			if (c instanceof JTextComponent) {
//				c.addMouseListener(new MouseAdapter() {
//
//					@Override
//					public void mousePressed(MouseEvent e) {
//						maybeShowPopup(e);
//					}
//
//					@Override
//					public void mouseReleased(MouseEvent e) {
//						maybeShowPopup(e);
//					}
//
//					private void maybeShowPopup(MouseEvent e) {
//						if (e.isPopupTrigger()) {
//							final JTextComponent component = (JTextComponent) e.getComponent();
//							final JPopupMenu menu = new JPopupMenu();
//							JMenuItem item;
//							item = new JMenuItem(new DefaultEditorKit.CopyAction());
//							item.setText("Copy");
//							item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
//							menu.add(item);
//							item = new JMenuItem(new DefaultEditorKit.CutAction());
//							item.setText("Cut");
//							item.setEnabled(component.isEditable()
//									&& component.getSelectionStart() != component.getSelectionEnd());
//							menu.add(item);
//							item = new JMenuItem(new DefaultEditorKit.PasteAction());
//							item.setText("Paste");
//							item.setEnabled(component.isEditable());
//							menu.add(item);
//							menu.show(e.getComponent(), e.getX(), e.getY());
//
//						}
//					}
//				});
//			} else if (c instanceof Container)
//				installContextMenu((Container) c);
//		}
//	}
//
//}
