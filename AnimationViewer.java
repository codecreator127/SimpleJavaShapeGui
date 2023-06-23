/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 *  UPI: jlin865	Name: John Lin
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field;

class AnimationViewer extends JComponent implements Runnable {
	private Thread animationThread = null; // the thread for animation
	private static int DELAY = 120; // the current animation speed
	//removed shapes list
	private ShapeType currentShapeType = Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType = Shape.DEFAULT_PATHTYPE; // the current path type
	private Color currentColor = Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private Color currentBorderColor = Shape.DEFAULT_BORDER_COLOR;
	private int currentPanelWidth = Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT,currentWidth = Shape.DEFAULT_WIDTH, currentHeight = Shape.DEFAULT_HEIGHT;
	private String currentLabel = Shape.DEFAULT_LABEL;
	protected MyModel model;
	protected NestedShape root;

	public AnimationViewer() {
		root = new NestedShape(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT);
		model = new MyModel();
		start();
		addMouseListener(new MyMouseAdapter());

	}

	public void setCurrentLabel(String text) {
		currentLabel = text;
		for (int i = 0; i < root.getSize(); i ++) {
			Shape currentShape = root.getInnerShapeAt(i);
			if (currentShape.isSelected()) {
				currentShape.setLabel(currentLabel);
			}
		}
	}
	public void setCurrentColor(Color bc) {
	    currentColor = bc;
		for (int i = 0; i < root.getSize(); i ++) {
			Shape currentShape = root.getInnerShapeAt(i);
			if (currentShape.isSelected()) {
				currentShape.setColor(currentColor);
			}
		}
	  }
	public void setCurrentBorderColor(Color bc) {
	    currentBorderColor = bc;
		for (int i = 0; i < root.getSize(); i ++) {
			Shape currentShape = root.getInnerShapeAt(i);
			if (currentShape.isSelected()) {
				currentShape.setBorderColor(currentBorderColor);
			}
		}
	 }
	public void setCurrentHeight(int h) {
	    currentHeight = h;
		for (int i = 0; i < root.getSize(); i ++) {
			Shape currentShape = root.getInnerShapeAt(i);
			if (currentShape.isSelected()) {
				currentShape.setHeight(currentHeight);
			}
		}
	 }
	public void setCurrentWidth(int w) {
	    currentWidth = w;
	    for (int i = 0; i < root.getSize(); i ++) {
			Shape currentShape = root.getInnerShapeAt(i);
			if (currentShape.isSelected()) {
				currentShape.setWidth(currentWidth);
			  }
		}

	 }
	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			boolean found = false;
			for (int i = 0; i < root.getSize(); i ++) {
				Shape currentShape = root.getInnerShapeAt(i);
				if (currentShape.contains(e.getPoint())) { // if the mousepoint is within a shape, then set the shape to
					currentShape.setSelected(!currentShape.isSelected());
					found = true;
				}

			}
			if (!found) {
				Shape child = root.createInnerShape(e.getX(), e.getY(), currentWidth, currentHeight, currentColor, currentBorderColor, currentPathType, currentShapeType);
				model.insertNodeInto(child, root);
			}
		}
	}
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < root.getSize(); i ++) {
			Shape currentShape = root.getInnerShapeAt(i);
			currentShape.move();
			currentShape.draw(g);
			currentShape.drawHandles(g);
			currentShape.drawString(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight();
	    for (int i = 0; i < root.getSize(); i ++) {
			Shape currentShape = root.getInnerShapeAt(i);
			currentShape.resetPanelSize(currentPanelWidth, currentPanelHeight);
		}
	}

	//added code
	

	class MyModel extends AbstractListModel<Shape> implements TreeModel {
		private ArrayList<Shape> selectedShapes;;
		public MyModel() {
			selectedShapes = root.getAllInnerShapes();
		}
		public int getSize() {
			return selectedShapes.size();
		}
		public Shape getElementAt(int index) {	
			return selectedShapes.get(index);
		}
		public void reload(NestedShape selected) {
		    selectedShapes = selected.getAllInnerShapes();
			fireContentsChanged(this, 0, selectedShapes.size() - 1);
		}
		private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
		public NestedShape getRoot() {
			return root;
		}
		public boolean isLeaf(Object node) {
			if (node instanceof NestedShape) {
				return false;
			}
			return true;
		}
		public boolean isRoot(Shape selectedNode) {
			if (root == selectedNode) {
				return true;
			}
			return false;
		}
		public Shape getChild(Object parent, int index) {
			if (parent instanceof NestedShape) {
				NestedShape n = (NestedShape)parent;
				if (index < n.getSize() && index >= 0) {
					return n.getInnerShapeAt(index);
				}
			}
			return null;
		}
		public int getChildCount(Object parent) {
			if (parent instanceof NestedShape) {
				NestedShape n = (NestedShape)parent;
				return n.getSize();
			}
			return 0;
		}
		public int getIndexOfChild(Object parent, Object child) {
			if (parent instanceof NestedShape) {
				NestedShape n = (NestedShape)parent;
				if (n.getAllInnerShapes().contains(child)) {
					return n.getAllInnerShapes().indexOf(child);
				}
			}
			return -1;
		}
		public void addTreeModelListener(final TreeModelListener tml) {
			treeModelListeners.add(tml);
		}
		public void removeTreeModelListener(final TreeModelListener tml) {
			treeModelListeners.remove(tml);
		}
		public void valueForPathChanged(TreePath path, Object newValue) {
		}
		public void fireTreeNodesInserted(Object source, Object[] path,int[] childIndices, Object[] children) {
			TreeModelEvent tEvent = new TreeModelEvent(source, path, childIndices, children);
			for (TreeModelListener listener: treeModelListeners) {
				listener.treeNodesInserted(tEvent);
			}
		}
		public void insertNodeInto(Shape newChild, NestedShape parent) {
			int[] childIndices = {parent.getSize() - 1};
			Object[] children = {newChild};
			fireTreeNodesInserted(this, parent.getPath(), childIndices, children);
			fireIntervalAdded(this, parent.indexOf(newChild), parent.indexOf(newChild));
		}
		public void addShapeNode(NestedShape selectedNode) {
			Shape nestedShape = null;
			if (selectedNode == root) {
				nestedShape = root.createInnerShape(0, 0, currentWidth, currentHeight, currentColor, currentBorderColor, currentPathType, currentShapeType);
			}
			else {
				nestedShape = selectedNode.createInnerShape(currentPathType, currentShapeType);
			}
			insertNodeInto(nestedShape, selectedNode);

		}
		public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children) {
			TreeModelEvent tEvent = new TreeModelEvent(source, path, childIndices, children);
			for (TreeModelListener listener: treeModelListeners) {
				listener.treeNodesRemoved(tEvent);
			}
		}
		public void removeNodeFromParent(Shape selectedNode) {
			NestedShape parent = selectedNode.getParent();
			int index = parent.indexOf(selectedNode);
			
			fireIntervalRemoved(this, parent.indexOf(selectedNode), parent.indexOf(selectedNode));
			
			parent.removeInnerShape(selectedNode);
			int[] childIndices = {index};
			Object[] children = {selectedNode};
			fireTreeNodesRemoved(this, parent.getPath(), childIndices, children);
			
		}

	}

	// you don't need to make any changes after this line ______________
	public String getCurrentLabel() {return currentLabel;}
	public int getCurrentHeight() { return currentHeight; }
	public int getCurrentWidth() { return currentWidth; }
	public Color getCurrentColor() { return currentColor; }
	public Color getCurrentBorderColor() { return currentBorderColor; }
	public void setCurrentShapeType(ShapeType value) {currentShapeType = value;}
	public void setCurrentPathType(PathType value) {currentPathType = value;}
	public ShapeType getCurrentShapeType() {return currentShapeType;}
	public PathType getCurrentPathType() {return currentPathType;}
	public void update(Graphics g) {
		paint(g);
	}
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while (animationThread == myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long) milliseconds);
		} catch (InterruptedException ie) {}
	}
}
