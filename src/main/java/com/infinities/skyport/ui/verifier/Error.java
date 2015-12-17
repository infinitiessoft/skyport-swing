package com.infinities.skyport.ui.verifier;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.ImageIcon;

import com.infinities.skyport.ui.MainFrame;

public class Error implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int NO_ERROR = 0;
	public static final int INFO = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	private int errorType;
	private String message;
	private ImageIcon iconError, iconInfo, iconWarn;


	public Error(int errorType, String message) {
		this.errorType = errorType;
		this.message = message;
		initResource();
	}

	private void initResource() {
		String prefix = "/" + MainFrame.IMAGES_FOLDER + "/";
		iconInfo = new ImageIcon(MainFrame.class.getResource(prefix + "info.png"));
		iconWarn = new ImageIcon(MainFrame.class.getResource(prefix + "warning.png"));
		iconError = new ImageIcon(MainFrame.class.getResource(prefix + "error.png"));
	}

	protected int getErrorType() {
		return errorType;
	}

	protected String getMessage() {
		return message;
	}

	public Color getColor() {
		switch (errorType) {
		case ERROR:
			return Color.RED;
		case INFO:
			return Color.YELLOW;
		case NO_ERROR:
			return Color.WHITE; // any random color,as we'll be using the
								// original color from the component
		case WARNING:
			return Color.RED;
		default:
			throw new IllegalArgumentException("Not a valid error type");

		}
	}

	public ImageIcon getImage() {
		switch (errorType) {
		case ERROR:
			return iconError;
		case INFO:
			return iconInfo;
		case NO_ERROR:
			return null;
		case WARNING:
			return iconWarn;
		default:
			throw new IllegalArgumentException("Not a valid error type");
		}

	}

}
