package com.infinities.skyport.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class ExcelAdapter implements ActionListener {

	private String rowstring, value;
	private Clipboard system;
	private StringSelection stsel;
	private JTable jTable1;
	private static final String lineSepartor = System.getProperty("line.separator");


	// private static final Logger logger =
	// LoggerFactory.getLogger(ExcelAdapter.class);

	/**
	 * The Excel Adapter is constructed with a JTable on which it enables
	 * Copy-Paste and acts as a Clipboard listener.
	 */
	public ExcelAdapter(final JTable myJTable) {
		jTable1 = myJTable;
		KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.
		final KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		// Identifying the Paste KeyStroke user can modify this
		// to copy on some other Key combination.
		// jTable1.registerKeyboardAction(this, "Copy", copy,
		// JComponent.WHEN_FOCUSED);
		jTable1.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);

		system = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * Public Accessor methods for the Table on which this adapter acts.
	 */
	public JTable getJTable() {
		return jTable1;
	}

	public void setJTable(final JTable aJTable1) {
		jTable1 = aJTable1;
	}

	/**
	 * This method is activated on the Keystrokes we are listening to in this
	 * implementation. Here it listens for Copy and Paste ActionCommands.
	 * Selections comprising non-adjacent cells result in invalid selection and
	 * then copy action cannot be performed. Paste is done by aligning the upper
	 * left corner of the selection with the 1st element in the current
	 * selection of the JTable.
	 */
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().compareTo("Copy") == 0) {
			copy();
		}
		if (e.getActionCommand().compareTo("Paste") == 0) {
			// System.out.println("Trying to Paste");
			final int selectRow[] = (jTable1.getSelectedRows());
			final int selectCol[] = (jTable1.getSelectedColumns());

			final int startRow = selectRow[0];
			final int startCol = jTable1.convertColumnIndexToModel(selectCol[0]);
			try {
				final String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
				// System.out.println("String is:" + trstring);
				int curRowIdx = 0;
				for (int rowIdx = curRowIdx; rowIdx < selectRow.length; rowIdx += curRowIdx) {
					final StringTokenizer st1 = new StringTokenizer(trstring, lineSepartor);
					for (curRowIdx = 0; st1.hasMoreTokens(); curRowIdx++) {
						rowstring = st1.nextToken();
						final StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
						// System.out.println("Cell is:'" + rowstring+"'");
						for (int colIdx = 0; st2.hasMoreTokens(); colIdx++) {
							value = st2.nextToken();
							if ((startRow + rowIdx + curRowIdx < jTable1.getRowCount())
									&& (startCol + colIdx < jTable1.getColumnCount())) {
								final Class<?> cellType = jTable1.getColumnClass(startCol + colIdx);
								// System.out.println("row: "+(startRow +
								// rowIdx+
								// curRowIdx)+" col: "+(startCol+colIdx)+" class: "+cellType.getName());
								if (String.class.isAssignableFrom(cellType))
									jTable1.setValueAt(value, startRow + rowIdx + curRowIdx, startCol + colIdx);
								else if (Boolean.class.isAssignableFrom(cellType))
									jTable1.setValueAt(Boolean.parseBoolean(value), startRow + rowIdx + curRowIdx, startCol
											+ colIdx);
								else if (Number.class.isAssignableFrom(cellType)) {
									if (Integer.class.isAssignableFrom(cellType))
										jTable1.setValueAt(Integer.parseInt(value), startRow + rowIdx + curRowIdx, startCol
												+ colIdx);
									else if (Double.class.isAssignableFrom(cellType))
										jTable1.setValueAt((Double) DecimalFormat.getInstance().parse(value), startRow
												+ rowIdx + curRowIdx, startCol + colIdx);
									else if (Float.class.isAssignableFrom(cellType))
										jTable1.setValueAt(Float.parseFloat(value), startRow + rowIdx + curRowIdx, startCol
												+ colIdx);
									else if (BigDecimal.class.isAssignableFrom(cellType))
										jTable1.setValueAt(new BigDecimal(value), startRow + rowIdx + curRowIdx, startCol
												+ colIdx);
								} else if (Date.class.isAssignableFrom(cellType))
									jTable1.setValueAt(DateFormat.getInstance().parse(value), startRow + rowIdx + curRowIdx,
											startCol + colIdx);
								else
									System.out.println("unsuported type copy for value: " + value + " into "
											+ cellType.getName());
							}
							// System.out.println("Putting " + value +
							// " at row=" + (startRow + r) + " column=" +
							// (startCol + j));
						}
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void copy() {
		final StringBuffer sbf = new StringBuffer();
		// Check to ensure we have selected only a contiguous block of
		// cells
		final int numcols = jTable1.getSelectedColumnCount();
		final int numrows = jTable1.getSelectedRowCount();
		final int[] rowsselected = jTable1.getSelectedRows();
		final int[] colsselected = jTable1.getSelectedColumns();
		if (!(((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0]) && (numrows == rowsselected.length)) && ((numcols - 1 == colsselected[colsselected.length - 1]
				- colsselected[0]) && (numcols == colsselected.length)))) {
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {
				sbf.append(jTable1.getValueAt(rowsselected[i], colsselected[j]));
				if (j < numcols - 1)
					sbf.append("\t");
			}
			sbf.append("\n");
		}
		stsel = new StringSelection(sbf.toString());
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
		system.setContents(stsel, stsel);
	}

	public void copyRow() {
		final StringBuffer sbf = new StringBuffer();
		// Check to ensure we have selected only a contiguous block of
		// cells
		final int numcols = jTable1.getColumnCount();
		final int numrows = jTable1.getSelectedRowCount();
		final int[] rowsselected = jTable1.getSelectedRows();
		final int[] colsselected = new int[numcols];
		for (int i = 0; i < numcols; i++) {
			colsselected[i] = i;
		}
		if (!(((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0]) && (numrows == rowsselected.length)) && ((numcols - 1 == colsselected[colsselected.length - 1]
				- colsselected[0]) && (numcols == colsselected.length)))) {
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {
				sbf.append(jTable1.getValueAt(rowsselected[i], colsselected[j]));
				if (j < numcols - 1)
					sbf.append("\t");
			}
			sbf.append("\n");
		}
		stsel = new StringSelection(sbf.toString());
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
		system.setContents(stsel, stsel);
	}

}
