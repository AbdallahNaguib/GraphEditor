import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Main extends JComponent {

    static final int WIDE = 640;
    static final int HIGH = 480;
    static final int RADIUS = 20;
    private static final int rowHit = 30;
    private static final int colWidth = 30;
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Point mousePt = new Point(WIDE / 2, HIGH / 2);
    private Rectangle mouseRect = new Rectangle();
    private boolean selecting = false;
    static Random random=new Random(100);
    static private Main main=new Main();

    public static void main(String[] args){
        readEdges();
    }

    private static void readEdges(){
        JFrame frame=new JFrame("Reading the graph");
        frame.setSize(600,600);
        JTextField textField=new JTextField("Enter the vertices seperated by space(delete this text)");
        textField.setBounds(50,100,350,50);
        frame.add(textField);
        JTextArea textArea = new JTextArea("Enter the edges seperated by lines(delete this text):\n1 2\n2 3");
        textArea.setBounds(50,200,350,300);
        frame.add(textArea);
        JCheckBox isDir = new JCheckBox("is directed ?");
        isDir.setBounds(450,350,150,50);
        frame.add(isDir);
        JButton button=new JButton("Draw");
        button.addActionListener(e -> {
            main.drawGraph();
            main.nodes.clear();
            main.edges.clear();
            String[] nodes=textField.getText().split(" ");
            String[] edges=textArea.getText().split("\n");
            for(String str:nodes){
                main.addNode(str);
            }
            for(String edge:edges){
                String[] vertices=edge.split(" ");
                main.addEdge(vertices[0],vertices[1],isDir.isSelected());
            }
            main.getAdjacencyMatrix();
            main.getAdjacencyList();
        });
        button.setBounds(450,200,100,50);
        frame.add(button);

        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void getAdjacencyMatrix(){
        JFrame frame=new JFrame("Adjacency matrix");
        frame.setSize(600,600);
        int cnt=nodes.size()+1;
        JTable table=new JTable(cnt,cnt);
        for(int i=0 ; i<cnt ; i++) {
            table.getColumnModel().getColumn(i).setMinWidth(colWidth);
        }
        table.setRowHeight(rowHit);
        for(int i=1 ; i<cnt;i++){
            table.setValueAt(nodes.get(i-1).name,0,i);
        }
        for(int i=1 ; i<cnt;i++){
            table.setValueAt(nodes.get(i-1).name,i,0);
        }
        for(int i=1 ; i<cnt ; i++){
            List<Node> nbrs=nodes.get(i-1).nebours;
            for(int j=1 ; j<cnt ; j++){
                if(nbrs.contains(nodes.get(j-1)))
                    table.setValueAt(1,i,j);
                else
                    table.setValueAt(0,i,j);
            }
        }
        table.setBounds(30,30,500,500);
        frame.add(table);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    void getAdjacencyList(){
        JFrame frame=new JFrame("Adjacency list");
        frame.setSize(600,600);
        int cnt=nodes.size();
        JTable table=new JTable(cnt,cnt);
        for(int i=0 ; i<cnt ; i++) {
            table.getColumnModel().getColumn(i).setMinWidth(colWidth);
        }
        table.setRowHeight(rowHit);
        for(int i=0 ; i<cnt;i++){
            table.setValueAt(nodes.get(i).name,i,0);
        }
        for(int i=0 ; i<cnt ; i++){
            List<Node> nbrs=nodes.get(i).nebours;
            for(int j=0 ; j<nbrs.size() ; j++){
                table.setValueAt(nbrs.get(j).name,i,j+1);
            }
        }
        table.setBounds(30,30,500,500);
        frame.add(table);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void drawGraph(){
        Main main=this;
        EventQueue.invokeLater(() -> {
            JFrame f = new JFrame("GraphPanel");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new JScrollPane(main), BorderLayout.CENTER);
            f.pack();
            f.setLocationByPlatform(true);
            f.setVisible(true);
        });
    }
    private Main() {
        this.setOpaque(true);
        this.addMouseListener(new MouseHandler());
        this.addMouseMotionListener(new MouseMotionHandler());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDE, HIGH);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (Edge e : edges) {
            e.draw(g);
        }
        for (Node n : nodes) {
            n.draw(g);
        }
        if (selecting) {
            g.setColor(Color.darkGray);
            g.drawRect(mouseRect.x, mouseRect.y,
                    mouseRect.width, mouseRect.height);
        }
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            selecting = false;
            mouseRect.setBounds(0, 0, 0, 0);
            e.getComponent().repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mousePt = e.getPoint();
            if (e.isShiftDown()) {
                Node.selectToggle(nodes, mousePt);
            } else if (e.isPopupTrigger()) {
                Node.selectOne(nodes, mousePt);
            } else if (Node.selectOne(nodes, mousePt)) {
                selecting = false;
            } else {
                Node.selectNone(nodes);
                selecting = true;
            }
            e.getComponent().repaint();
        }

    }

    private class MouseMotionHandler extends MouseMotionAdapter {

        Point delta = new Point();

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selecting) {
                mouseRect.setBounds(
                        Math.min(mousePt.x, e.getX()),
                        Math.min(mousePt.y, e.getY()),
                        Math.abs(mousePt.x - e.getX()),
                        Math.abs(mousePt.y - e.getY()));
                Node.selectRect(nodes, mouseRect);
            } else {
                delta.setLocation(
                        e.getX() - mousePt.x,
                        e.getY() - mousePt.y);
                Node.updatePosition(nodes, delta);
                mousePt = e.getPoint();
            }
            e.getComponent().repaint();
        }
    }

    private void addNode(String name){
        Node.selectNone(nodes);
        Node n = new Node(name);
        nodes.add(n);
        repaint();
    }

    private void addEdge(String name1, String name2,boolean isDir){
        Node node1 = null,node2 = null;
        for(Node node:nodes){
            if(node.name.equals(name1)){
                node1=node;
            }
            if(node.name.equals(name2)){
                node2=node;
            }
        }
        edges.add(new Edge(node1,node2,isDir));
        repaint();
    }
}
// 1 2 3 4
/*
1 2
1 3
2 4
3 2
 */