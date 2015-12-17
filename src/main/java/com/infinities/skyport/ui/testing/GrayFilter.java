package com.infinities.skyport.ui.testing;

import java.awt.image.RGBImageFilter;
import java.io.Serializable;

public class GrayFilter extends RGBImageFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GrayFilter() {

	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0x00ff00) >> 8;
		int b = (rgb & 0x0000ff);
		int iy = (r + g + b) / 3;

		iy = Math.min(255, iy);

		return ((rgb & 0xff000000) | (iy << 16) | (iy << 8) | iy);
	}

}
