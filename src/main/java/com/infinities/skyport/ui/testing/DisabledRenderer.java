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
package com.infinities.skyport.ui.testing;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DisabledRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Icon disabledLeafIcon;
	private Icon disabledOpenIcon;
	private Icon disabledClosedIcon;

	public DisabledRenderer() {
		this(new GraydIcon(UIManager.getIcon("Tree.leafIcon")), new GraydIcon(
				UIManager.getIcon("Tree.openIcon")), new GraydIcon(
				UIManager.getIcon("Tree.closedIcon")));

	}

	public DisabledRenderer(Icon leafIcon, Icon openIcon, Icon closedIcon) {
		setDisabledLeafIcon(leafIcon);
		setDisabledOpenIcon(openIcon);
		setDisabledClosedIcon(closedIcon);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, sel, expanded,
				leaf, row, hasFocus);

		setText(stringValue);

		if (sel) {
			setForeground(getTextSelectionColor());
		} else {
			setForeground(getTextNonSelectionColor());
		}

		boolean treeIsEnabled = tree.isEnabled();
		boolean nodeIsEnabled = ((DisabledNode) value).isEnabled();
		boolean isEnabled = (treeIsEnabled && nodeIsEnabled);
		setEnabled(isEnabled);

		if (isEnabled) {
			selected = sel;
			if (leaf) {
				setIcon(getLeafIcon());
			} else if (expanded) {
				setIcon(getOpenIcon());
			} else {
				setIcon(getClosedIcon());
			}
		} else {
			selected = false;
			if (leaf) {
				if (nodeIsEnabled) {
					setDisabledIcon(getLeafIcon());
				} else {

					setDisabledIcon(disabledLeafIcon);
				}

			} else if (expanded) {
				if (nodeIsEnabled) {
					setDisabledIcon(getOpenIcon());
				} else {
					setDisabledIcon(disabledOpenIcon);
				}
			} else {
				if (nodeIsEnabled) {
					setDisabledIcon(getClosedIcon());
				} else {
					setDisabledIcon(disabledClosedIcon);
				}
			}
		}
		return this;

	}

	public void setDisabledLeafIcon(Icon icon) {
		disabledLeafIcon = icon;
	}

	public void setDisabledOpenIcon(Icon icon) {
		disabledOpenIcon = icon;
	}

	public void setDisabledClosedIcon(Icon icon) {
		disabledClosedIcon = icon;
	}

}
