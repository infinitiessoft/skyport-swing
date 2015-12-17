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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import com.infinities.skyport.util.PropertiesHolder;

public class TaskParamPanel extends JPanel implements PropertyChangeListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int minCore, maxCore, minMax, maxMax, minCapacity, maxCapacity;
	private JLabel coreLabel, maxLabel, capacityLabel;
	private JFormattedTextField coreField, maxField, capacityField;
	private JSlider coreSlider, maxSlider, capacitySlider;
	private String title = "no title";
	private NumberFormat numberFormat;

	private int core, max, capacity;


	public int getCore() {
		return core;
	}

	public void setCore(int core) {
		this.core = core;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public TaskParamPanel(String title, int core, int max, int capacity) {
		super(new GridBagLayout());
		this.title = title;
		this.core = core;
		this.max = max;
		this.capacity = capacity;
		setUpResource();
		setUpUIComponent();
		setUpEventListener();
	}

	private void setUpResource() {
		minCore = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.THREADPOOL_CORE_MIN));
		maxCore = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.THREADPOOL_CORE_MAX));
		minMax = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.THREADPOOL_MAX_MIN));
		maxMax = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.THREADPOOL_MAX_MAX));
		minCapacity = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.THREADPOOL_CAPACITY_MIN));
		maxCapacity = Integer.parseInt(PropertiesHolder.getProperty(PropertiesHolder.THREADPOOL_CAPACITY_MAX));
	}

	private void setUpUIComponent() {
		this.setOpaque(true);

		TitledBorder titleBorder;
		titleBorder = BorderFactory.createTitledBorder(title);
		titleBorder.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titleBorder);

		GridBagConstraints c = new GridBagConstraints();

		numberFormat = NumberFormat.getIntegerInstance();
		NumberFormatter formatter = new NumberFormatter(numberFormat);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);

		coreLabel = new JLabel("Core Pool Size:");
		// c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		add(coreLabel, c);

		maxLabel = new JLabel("Max Pool Size:");
		// c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		add(maxLabel, c);

		capacityLabel = new JLabel("Queue Capacity:");
		// c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.EAST;
		add(capacityLabel, c);

		coreSlider = new JSlider();
		coreSlider.setMaximum(maxCore);
		coreSlider.setMinimum(minCore);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		add(coreSlider, c);

		maxSlider = new JSlider();
		maxSlider.setMaximum(maxMax);
		maxSlider.setMinimum(minMax);
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		add(maxSlider, c);

		capacitySlider = new JSlider();
		capacitySlider.setMaximum(maxCapacity);
		capacitySlider.setMinimum(minCapacity);
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 2;
		add(capacitySlider, c);

		coreSlider.setValue(core);
		maxSlider.setValue(max);
		capacitySlider.setValue(capacity);

		coreField = new JFormattedTextField(formatter);
		coreField.setColumns(3);
		coreField.setValue(core);
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 0;
		add(coreField, c);

		maxField = new JFormattedTextField(formatter);
		maxField.setColumns(3);
		maxField.setValue(max);
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 1;
		add(maxField, c);

		capacityField = new JFormattedTextField(formatter);
		capacityField.setColumns(3);
		capacityField.setValue(capacity);
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 2;
		add(capacityField, c);
	}

	private void setUpEventListener() {
		coreSlider.addChangeListener(this);
		maxSlider.addChangeListener(this);
		capacitySlider.addChangeListener(this);

		coreField.addPropertyChangeListener(this);
		maxField.addPropertyChangeListener(this);
		capacityField.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource() == coreField && "value".equals(evt.getPropertyName())) {
			Number value = (Number) evt.getNewValue();
			coreSlider.setValue(value.intValue());
			setCore(value.intValue());
		}

		if (evt.getSource() == maxField && "value".equals(evt.getPropertyName())) {
			Number value = (Number) evt.getNewValue();
			maxSlider.setValue(value.intValue());
			setMax(value.intValue());
		}

		if (evt.getSource() == capacityField && "value".equals(evt.getPropertyName())) {
			Number value = (Number) evt.getNewValue();
			capacitySlider.setValue(value.intValue());
			setCapacity(value.intValue());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		if (e.getSource() == coreSlider) {
			coreField.setValue(coreSlider.getValue());
			setCore(coreSlider.getValue());
		} else if (e.getSource() == maxSlider) {
			maxField.setValue(maxSlider.getValue());
			setMax(maxSlider.getValue());
		} else if (e.getSource() == capacitySlider) {
			capacityField.setValue(capacitySlider.getValue());
			setCapacity(capacitySlider.getValue());
		}
	}

	public void setDefault(int core, int max, int capacity) {
		coreSlider.setValue(core);
		maxSlider.setValue(max);
		capacitySlider.setValue(capacity);
		setCore(core);
		setMax(max);
		setCapacity(capacity);
	}

	// public static void main(String args[]){
	// JFrame frame = new JFrame();
	//
	//
	// TaskParamPanel panel = new TaskParamPanel("Short-Term Task",10,10,10);
	//
	// frame.add(panel);
	//
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
	// frame.pack();
	// frame.setSize(new Dimension(1000, 600));
	//
	//
	// frame.setVisible(true);
	//
	// }

}
