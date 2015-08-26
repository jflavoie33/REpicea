/*
 * This file is part of the repicea library.
 *
 * Copyright (C) 2009-2014 Mathieu Fortin for Rouge-Epicea
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package repicea.simulation.processsystem;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import repicea.gui.CommonGuiUtility;
import repicea.gui.REpiceaDialog;
import repicea.gui.REpiceaPanel;
import repicea.gui.UIControlManager;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

@SuppressWarnings("serial")
public class ProcessorInternalDialog extends REpiceaDialog {


	public static enum MessageID implements TextableEnum {
		ProcessorTitle("Processor features", "Caract\u00E9ristiques du processeur"),
		ProcessorName("Processor name", "Nom du processeur"),
		ProcessorIntake("Processor intake", "Intrant au processeur"),
		ProcessorYield("Processor yield", "Rendement du processeur"),
		SendToAnotherOutletLabel("Send to another outlet", "Envoyer vers un autre d\u00E9bouch\u00E9"),
		AvailableOutlets("Available outlets", "D\u00E9bouch\u00E9s disponibles"),
		GeneralFeatures("General features", "Caract\u00E9ristiques g\u00E9n\u00E9rales"),
		SpecificFeatures("Specific features", "Caract\u00E9ristiques sp\u00E9cifiques"),
		;

		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}

		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}

		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
	}
	
	private final Processor caller;

	protected JTextField processorTextField;
	
	private JPanel bottomComponent;



	/**
	 * The constructor is called by its underlying class WoodProductProcessor.
	 * @param parent a Window instance
	 * @param callerButton a ProcessorButton instance
	 */
	protected ProcessorInternalDialog(Window parent, ProcessorButton callerButton) {
		super(parent);
		setCancelOnClose(false);
		caller = callerButton.getOwner();
		
		initializeComponents();
		
		initUI();
		pack();
	}

	protected void initializeComponents() {
		processorTextField = new JTextField();
		processorTextField.setColumns(25);
		processorTextField.setText(getCaller().getName());
		processorTextField.setPreferredSize(new Dimension(100, processorTextField.getFontMetrics(processorTextField.getFont()).getHeight() + 2));
	}
	
	protected Processor getCaller() {return caller;}

	@Override
	protected void initUI() {
		setTitle(MessageID.ProcessorTitle.toString());

		setLayout(new BorderLayout());
				
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		getContentPane().add(mainPanel, BorderLayout.NORTH);
		
		mainPanel.add(createUpperPartPanel());
		
		bottomComponent = new JPanel();
		bottomComponent.setLayout(new BorderLayout());
		mainPanel.add(bottomComponent);
	}


	private void setBottomComponent(JPanel bottomPanel) {
		bottomComponent.removeAll();
		if (bottomPanel.getComponents().length > 0 && bottomPanel.getBorder() == null) {
			bottomPanel.setBorder(UIControlManager.getTitledBorder(MessageID.SpecificFeatures.toString()));
		}
		bottomComponent.add(bottomPanel, BorderLayout.CENTER);
		pack();
		validate();
		repaint();
	}


	@Override
	public void listenTo() {
		processorTextField.addCaretListener(getCaller());
	}

	@Override
	public void doNotListenToAnymore() {
		processorTextField.removeCaretListener(getCaller());
	}

	@Override
	public void setVisible(boolean bool) {
		if (!isVisible() && bool) {
			REpiceaPanel featurePanel = caller.getProcessFeaturesPanel();
			if (featurePanel != null) {
				if (caller.hasSubProcessors()) {
					setBottomComponent(new JPanel());
				} else {
					SystemManagerDialog dlg = (SystemManagerDialog) CommonGuiUtility.getParentComponent(this, SystemManagerDialog.class);
					boolean isEnablingGranted = dlg.getCaller().getGUIPermission().isEnablingGranted();
					if (!isEnablingGranted) {
						CommonGuiUtility.enableThoseComponents(featurePanel, JTextComponent.class, isEnablingGranted);
						CommonGuiUtility.enableThoseComponents(featurePanel, AbstractButton.class, isEnablingGranted);
						CommonGuiUtility.enableThoseComponents(featurePanel, JComboBox.class, isEnablingGranted);
						CommonGuiUtility.enableThoseComponents(featurePanel, JSlider.class, isEnablingGranted);
					}
					setBottomComponent(featurePanel);
				}
			}
		}
		super.setVisible(bool);
	}

	/**
	 * This panel contains the information that are always displayed. Typically, the processor name appears in this panel.
	 * @return a JPanel instance
	 */
	protected JPanel createUpperPartPanel() {
		JPanel upperPart = new JPanel();
		upperPart.setLayout(new BoxLayout(upperPart, BoxLayout.Y_AXIS));
		upperPart.setBorder(UIControlManager.getTitledBorder(MessageID.GeneralFeatures.toString()));
		
		JPanel processorNameSubPanel = UIControlManager.createSimpleHorizontalPanel(UIControlManager.getLabel(MessageID.ProcessorName),
				processorTextField, 
				5);

		Component verticalStrut = Box.createVerticalStrut(10);
		upperPart.add(verticalStrut);
		upperPart.add(processorNameSubPanel);
		upperPart.add(Box.createVerticalStrut(10));
		return upperPart;
	}
	
}
