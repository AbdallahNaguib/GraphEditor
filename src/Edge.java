import java.awt.*;

class Edge implements Comparable<Edge> {

    Node n1;
    Node n2;
    int wt;
    boolean isDirected, isMinSpanTree;

    Edge(Node n1, Node n2, boolean isDir, int wt) {
        isDirected = isDir;
        this.n1 = n1;
        this.n2 = n2;
        n1.nebours.add(n2);
        if (!isDirected) n2.nebours.add(n1);
        this.wt = wt;
    }

    void draw(Graphics g) {
        Point p1 = n1.getLocation();
        Point p2 = n2.getLocation();
        int xMid = (p1.x + p2.x) / 2;
        int yMid = (p1.y + p2.y) / 2;
        if (isMinSpanTree) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.darkGray);
        }
        if (wt != -1) {
            g.drawString(Integer.toString(wt), xMid, yMid);
        }
        for (int i = 0; i < 5; i++) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        if (isDirected) {
            int d = 30;
            int h = 10;
            int dx = p2.x - p1.x, dy = p2.y - p1.y;
            double D = Math.sqrt(dx * dx + dy * dy);
            double xm = D - d, xn = xm, ym = h, yn = -h, x;
            double sin = dy / D, cos = dx / D;

            x = xm * cos - ym * sin + p1.x;
            ym = xm * sin + ym * cos + p1.y;
            xm = x;

            x = xn * cos - yn * sin + p1.x;
            yn = xn * sin + yn * cos + p1.y;
            xn = x;

            int[] xpoints = {p2.x, (int) xm, (int) xn};
            int[] ypoints = {p2.y, (int) ym, (int) yn};
            g.fillPolygon(xpoints, ypoints, 3);
        }
    }

    @Override
    public int compareTo(Edge e2) {
        if (this.wt < e2.wt) return -1;
        else if (this.wt > e2.wt) return 1;
        return 0;
    }
}