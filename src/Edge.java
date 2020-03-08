import java.awt.*;

class Edge {

    private Node n1;
    private Node n2;
    boolean isDirected;

    Edge(Node n1, Node n2,boolean isDir) {
        isDirected=isDir;
        this.n1 = n1;
        this.n2 = n2;
        n1.nebours.add(n2);
        if(!isDirected)n2.nebours.add(n1);
    }

    void draw(Graphics g) {
        Point p1 = n1.getLocation();
        Point p2 = n2.getLocation();


        g.setColor(Color.darkGray);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
        if(isDirected) {
            int d=30;
            int h=10;
            int dx = p2.x - p1.x, dy = p2.y - p1.y;
            double D = Math.sqrt(dx*dx + dy*dy);
            double xm = D - d, xn = xm, ym = h, yn = -h, x;
            double sin = dy / D, cos = dx / D;

            x = xm*cos - ym*sin + p1.x;
            ym = xm*sin + ym*cos + p1.y;
            xm = x;

            x = xn*cos - yn*sin + p1.x;
            yn = xn*sin + yn*cos + p1.y;
            xn = x;

            int[] xpoints = {p2.x, (int) xm, (int) xn};
            int[] ypoints = {p2.y, (int) ym, (int) yn};
            g.fillPolygon(xpoints, ypoints, 3);
        }
    }
}