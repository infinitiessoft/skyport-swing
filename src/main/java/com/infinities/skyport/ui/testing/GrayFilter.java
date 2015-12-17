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
