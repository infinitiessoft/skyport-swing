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
