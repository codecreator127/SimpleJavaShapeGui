/*
 *  ============================================================================================
 *  A1.java : Extends JFrame and contains a panel where shapes move around on the screen.
 *  YOUR UPI: ANSWER
 *  ============================================================================================
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Scanner;

public class A2  extends JFrame {
	private AnimationViewer panel;  // panel for bouncing area
	JButton borderButton, loadButton;
	JComboBox<ShapeType> shapesComboBox;
	JComboBox<PathType> pathComboBox;
	JTextField labelTextField, filenameTextField;

	//insert inner member classes here
	class BorderListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Color selectedColor = JColorChooser.showDialog(null, "Border Color", null);
			if (selectedColor != null) {
				panel.setCurrentBorderColor(selectedColor);
			}
		}
		
	}
	class LabelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String text = labelTextField.getText();
			if (text != null && !text.isEmpty()) {
				panel.setCurrentLabel(text);
			}
		}
	}
	class LoadListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String filename = filenameTextField.getText();
			Scanner input = null;
			try {
				input = new Scanner(new File(filename));
				panel.loadShapesFromFile(filename);
			}
			catch (IOException e1) {
				System.out.printf("ERROR: The file %s does not exist.%n", filename);
				filenameTextField.setText("Invalid filename.");
			}
	
		}
	}


	// you don't need to make any changes after this line ______________
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new A2();
			}
		});
	}
	public A2() {
		super("Bouncing Application");
		panel = new AnimationViewer();
		add(panel, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT));
		add(setUpToolsPanel(), BorderLayout.NORTH);
		addComponentListener(
			new ComponentAdapter() { // resize the frame and reset all margins for all shapes
				public void componentResized(ComponentEvent componentEvent) {
					panel.resetMarginSize();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
		pack();
		setVisible(true);
	}
	public JPanel setUpToolsPanel() {
		shapesComboBox = new JComboBox<ShapeType>(new DefaultComboBoxModel<ShapeType>(ShapeType.values()));
		shapesComboBox.addActionListener( new ShapeActionListener()) ;
		pathComboBox = new JComboBox<PathType>(new DefaultComboBoxModel<PathType>(PathType.values()));
		pathComboBox.addActionListener( new PathActionListener());
		labelTextField = new JTextField("A2");
		labelTextField.addActionListener( new LabelListener());
		borderButton = new JButton("Border");
		borderButton.addActionListener( new BorderListener());
		loadButton = new JButton("Load");
		loadButton.addActionListener( new LoadListener());
		filenameTextField = new JTextField("A2.txt");
		JPanel toolsPanel = new JPanel();
		toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
		toolsPanel.add(new JLabel(" Shape: ", JLabel.RIGHT));
		toolsPanel.add(shapesComboBox);
		toolsPanel.add(new JLabel(" Path: ", JLabel.RIGHT));
		toolsPanel.add(pathComboBox);
		toolsPanel.add(borderButton);
		toolsPanel.add( new JLabel(" Text: ", JLabel.RIGHT));
		toolsPanel.add(labelTextField);
		toolsPanel.add( new JLabel(" Filename: ", JLabel.RIGHT));
		toolsPanel.add(filenameTextField);
		toolsPanel.add(loadButton);
		return toolsPanel;
	}
	class ShapeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel.setCurrentShapeType((ShapeType)shapesComboBox.getSelectedItem());
		}
	}
	class PathActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel.setCurrentPathType((PathType)pathComboBox.getSelectedItem());
		}
	}

}

