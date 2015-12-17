package com.infinities.skyport.ui;

import javax.swing.table.DefaultTableModel;

public class UneditableTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public UneditableTableModel() {
		super();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
