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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.Serializable;

import javax.swing.Icon;

public class GraydIcon implements Icon, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Icon icon;

	private Image image;


	public GraydIcon(Icon icon) {
		this.icon = icon;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (image == null) {
			Image orgImage = c.createImage(getIconWidth(), getIconHeight());
			Graphics imageG = orgImage.getGraphics();
			Color background = c.getBackground();
			imageG.setColor(background);
			imageG.fillRect(0, 0, getIconWidth(), getIconHeight());

			icon.paintIcon(c, imageG, x, y);

			ImageFilter colorfilter = new GrayFilter();
			image = c.createImage(new FilteredImageSource(orgImage.getSource(), colorfilter));
		}
		g.drawImage(image, x, y, null);
	}

	@Override
	public int getIconWidth() {
		return icon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return icon.getIconHeight();
	}

}
