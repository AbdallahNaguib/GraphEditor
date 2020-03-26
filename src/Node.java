import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Node {

    List<Node> nebours = new ArrayList<>();

    String name;
    private Point p;
    private int r;
    private boolean selected = false;
    private Rectangle b = new Rectangle();
    Color color =null;
    /**
     * Draw this node.
     */
    void draw(Graphics g) {
        if(color == null){
            color = Color.BLUE;
        }
        g.setColor(color);
        g.drawString(name,b.x,b.y);
        g.fillOval(b.x, b.y, b.width, b.height);
        if (selected) {
            g.setColor(Color.darkGray);
            g.drawRect(b.x, b.y, b.width, b.height);
        }
    }
    /**
     * Construct a new node.
     */
    Node(String str) {
        this.name=str;
        int x=(Math.abs(Main.random.nextInt()))%(Main.WIDE-2*Main.RADIUS)
                + Main.RADIUS;
        int y=(Math.abs(Main.random.nextInt()))%(Main.HIGH-2*Main.RADIUS) +
                Main.RADIUS;
        Point p = new Point(x,y);
        this.p = p;
        this.r = Main.RADIUS;
        setBoundary(b);
    }

    /**
     * Calculate this node's rectangular boundary.
     */
    private void setBoundary(Rectangle b) {
        b.setBounds(p.x - r, p.y - r, 2 * r, 2 * r);
    }



    /**
     * Return this node's location.
     */
    Point getLocation() {
        return p;
    }

    /**
     * Return true if this node contains p.
     */
    private boolean contains(Point p) {
        return b.contains(p);
    }

    boolean isSelected() {
        return selected;
    }

    /**
     * Mark this node as selected.
     */
    void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Collected all the selected nodes in list.
     */
    static void getSelected(List<Node> list, List<Node> selected) {
        selected.clear();
        for (Node n : list) {
            if (n.isSelected()) {
                selected.add(n);
            }
        }
    }

    /**
     * Select no nodes.
     */
    static void selectNone(java.util.List<Node> list) {
        for (Node n : list) {
            n.setSelected(false);
        }
    }

    /**
     * Select a single node; return true if not already selected.
     */
    static boolean selectOne(List<Node> list, Point p) {
        for (Node n : list) {
            if (n.contains(p)) {
                if (!n.isSelected()) {
                    Node.selectNone(list);
                    n.setSelected(true);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Select each node in r.
     */
    static void selectRect(List<Node> list, Rectangle r) {
        for (Node n : list) {
            n.setSelected(r.contains(n.p));
        }
    }

    /**
     * Toggle selected state of each node containing p.
     */
    static void selectToggle(List<Node> list, Point p) {
        for (Node n : list) {
            if (n.contains(p)) {
                n.setSelected(!n.isSelected());
            }
        }
    }


    static void updatePosition(List<Node> list, Point d) {
        for (Node n : list) {
            if (n.isSelected()) {
                n.p.x += d.x;
                n.p.y += d.y;
                n.setBoundary(n.b);
            }
        }
    }
}

