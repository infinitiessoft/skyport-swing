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
package com.infinities.skyport.ui.verifier;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

public class IconBorder extends AbstractBorder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageIcon icon;
	private Border originalBorder;


	public IconBorder(ImageIcon icon, Border originalBorder) {
		this.icon = icon;
		this.originalBorder = originalBorder;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		originalBorder.paintBorder(c, g, x, y, width, height);
		Insets insets = getBorderInsets(c);
		int by = (c.getHeight() / 2) - (icon.getIconHeight() / 2);
		int w = Math.max(2, insets.left);
		int bx = x + width - (icon.getIconHeight() + (w * 2)) + 2;
		g.translate(bx, by);
		g.drawImage(icon.getImage(), x, y, icon.getImageObserver());

	}

	@Override
	public Insets getBorderInsets(Component c) {
		return originalBorder.getBorderInsets(c);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}
}
