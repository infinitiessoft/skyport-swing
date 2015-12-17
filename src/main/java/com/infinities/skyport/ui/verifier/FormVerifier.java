package com.infinities.skyport.ui.verifier;

import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.JTextComponent;

import com.infinities.skyport.ui.listener.AccessConfigFormListener;

public class FormVerifier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<JTextComponent> textComponents = new ArrayList<JTextComponent>();
	private List<Component> componentsToEnable = new ArrayList<Component>();
	private AccessConfigFormListener formListener = new AccessConfigFormListener(this);


	public void addTextComponent(JTextComponent textComponent) {
		textComponents.add(textComponent);
		textComponent.getDocument().addDocumentListener(formListener);
	}

	public void addComponentsToEnable(Component comp) {
		componentsToEnable.add(comp);
	}

	public void docsChanged() {
		boolean textVerified = true;
		for (JTextComponent textComp : textComponents) {
			textVerified &= textComp.getInputVerifier().verify(textComp);
		}

		for (Component comp : componentsToEnable) {
			comp.setEnabled(textVerified);
		}
	}
}
