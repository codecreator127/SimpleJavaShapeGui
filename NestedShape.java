/*
 * ==========================================================================================
 * NestedShape.java : A shape that can contain other shapes inside it
 *  UPI: jlin865	Name: John Lin
 * ==========================================================================================
 */

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.util.*;
import java.awt.*;

public class NestedShape extends RectangleShape {
    private ArrayList<Shape> innerShapes = new ArrayList<Shape>();
    public NestedShape() {
        super();
        createInnerShape(PathType.BOUNCING, ShapeType.RECTANGLE);
    }
    public NestedShape(int x, int y, int width, int height, int panelWidth, int panelHeight, Color fillColor, Color bordColor, PathType pt) {
        super(x, y, width, height, panelWidth, panelHeight, fillColor, bordColor, pt);
        createInnerShape(PathType.BOUNCING, ShapeType.RECTANGLE);
    }
    public NestedShape(int width, int height) {
        super(0, 0, width, height, DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT, Color.black, Color.black, PathType.BOUNCING);
    }
    public Shape createInnerShape(int x, int y, int w, int h, Color c, Color bc, PathType pt, ShapeType st) {
        Shape n = null;
        switch (st) {
            case NESTED:
                n = new NestedShape(x, y, w, h, this.width, this.height, c, bc, pt);
                break;
        
            case RECTANGLE:
                n = new RectangleShape(x, y, w, h, this.width, this.height, c, bc, pt);
                break;
            case OVAL:
                n = new OvalShape(x, y, w, h, this.width, this.height, c, bc, pt);
                break;

        }
        n.setParent(this);
        innerShapes.add(n);
        return n;
    }
    public Shape createInnerShape(PathType pt, ShapeType st) {
        Shape n = null;
        switch (st) {
            case NESTED:
                n = new NestedShape(0, 0, (this.width/2), (this.height/2), this.width, this.height, this.color, this.borderColor, pt);
                break;
        
            case RECTANGLE:
                n = new RectangleShape(0, 0, (this.width/2), (this.height/2), this.width, this.height, this.color, this.borderColor, pt);
                break;
            case OVAL:
                n = new OvalShape(0, 0, (this.width/2), (this.height/2), this.width, this.height, this.color, this.borderColor, pt);
                break;

        }
        n.setParent(this);
        innerShapes.add(n);
        return n;
    }
    public Shape getInnerShapeAt(int index) {
        return innerShapes.get(index);
    }
    public int getSize() {
        return innerShapes.size();
    }

    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(this.x, this.y, this.width, this.height);;
        g.translate(this.x, this.y);
        for (Shape s: innerShapes) {
            s.draw(g);
            if (s.isSelected()) {
                s.drawHandles(g);
            }
            s.drawString(g);
        }
        g.translate(this.x * -1, this.y * -1    );
    }
    @Override
    public void move() {
        super.move();
        for (Shape s: innerShapes) {
            s.move();
        }
    }

    public int indexOf(Shape s) {
        return innerShapes.indexOf(s);
    }
    public void addInnerShape(Shape s) {
        s.setParent(this);
        innerShapes.add(s);
    }
    public void removeInnerShape(Shape s) {
        innerShapes.remove(s);
        s.setParent(null);
    }
    public void removeInnerShapeAt(int index) {
        Shape s = innerShapes.get(index);
        innerShapes.remove(index);
        s.setParent(null);
    }
    public ArrayList<Shape> getAllInnerShapes() {
        return innerShapes;
    }


}
