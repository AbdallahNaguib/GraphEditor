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
    private static final int buttonWidth = 250;
    private static final int buttonHeight = 50;
    private static final int rowHit = 30;
    private static final int colWidth = 30;

    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private Point mousePt = new Point(WIDE / 2, HIGH / 2);
    private Rectangle mouseRect = new Rectangle();
    private boolean selecting = false;
    static Random random = new Random(100);
    static private Main main = new Main();

    public static void main(String[] args) {
        readEdges();
    }

    private static void readEdges() {
        JFrame frame = new JFrame("Reading the graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(1000, 600);
        JTextField textField = new JTextField("Enter the vertices seperated by space(delete this text)");
        textField.setBounds(50, 100, 350, 50);
        frame.add(textField);
        JTextArea textArea = new JTextArea("Enter the edges seperated by lines(delete this text):\n1 2 (weight if exists)\n2 3 10");
        textArea.setBounds(50, 200, 350, 300);
        frame.add(textArea);
        JCheckBox isDir = new JCheckBox("is directed ?");
        isDir.setBounds(50, 150, 150, 50);
        frame.add(isDir);
        JButton draw = new JButton("Draw");
        draw.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
            main.drawGraph();
        });
        int firstCol=420;
        draw.setBounds(firstCol, 200, buttonWidth, buttonHeight);
        frame.add(draw);

        JButton adjacencyMat = new JButton("adjacencyMat");
        adjacencyMat.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
            main.getAdjacencyMatrix();
        });
        adjacencyMat.setBounds(firstCol, 300, buttonWidth, buttonHeight);
        frame.add(adjacencyMat);

        JButton adjacencyList = new JButton("adjacencyList");
        adjacencyList.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
            main.getAdjacencyList();
        });
        adjacencyList.setBounds(firstCol, 400, buttonWidth, buttonHeight);
        frame.add(adjacencyList);

        JButton minimumSpanningTree = new JButton("minimumSpanningTree");
        minimumSpanningTree.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
            main.getMinimumSpanningTree();
        });
        minimumSpanningTree.setBounds(firstCol, 500, buttonWidth, buttonHeight);
        frame.add(minimumSpanningTree);

        JButton color = new JButton("color the graph");
        color.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
            main.colorTheGraph();
        });
        color.setBounds(700, 200, buttonWidth, buttonHeight);
        frame.add(color);

        JButton eulerPath = new JButton("get Euler path/circuit");
        eulerPath.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
            main.getEuler();
        });
        eulerPath.setBounds(700, 300, buttonWidth, buttonHeight);
        frame.add(eulerPath);

        JButton hamiltonPath = new JButton("get hamilton path");
        hamiltonPath.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
                main.getHamilton();
        });
        hamiltonPath.setBounds(700, 400, buttonWidth, buttonHeight);
        frame.add(hamiltonPath);

        JButton minimumHamiltonPath = new JButton("get minimum hamilton circuit");
        minimumHamiltonPath.addActionListener(e -> {
            if(main.prepareGraphData(textField, textArea, isDir))
                main.getMinHamilton();
        });
        minimumHamiltonPath.setBounds(700, 500, buttonWidth, buttonHeight);
        frame.add(minimumHamiltonPath);

        frame.setLayout(null);
        frame.setVisible(true);

    }

    private void getMinHamilton(){
        if(!edges.isEmpty() && edges.get(0).wt<0){
            JOptionPane.showMessageDialog(new JFrame()
                    , "graph must be weighted");
            return;
        }
        var res = Algorithms.getMinHamilton(edges,nodes);
        if(res.first==null){
            JOptionPane.showMessageDialog(new JFrame()
                    , "graph doesn't has hamilton circuit");
            return;
        }
        JOptionPane.showMessageDialog(new JFrame()
                , "minimum hamilton circuit = "+res.second);
        int cnt=1;
        for(Node node:res.first){
            node.name += " (hamilton path number = "+cnt+")";
            cnt++;
        }
        drawGraph();
    }

    private void getEuler(){
        ArrayList<Edge> res = Algorithms.getEuler(edges,nodes);
        if(res==null){
            JOptionPane.showMessageDialog(new JFrame()
                    , "graph doesn't has euler path/circuit");
            return;
        }
        int cnt=1;
        for(Edge edge:res){
            edge.wt = cnt++;
        }
        edges = res;
        repaint();
        drawGraph();
    }

    private void getHamilton(){
        ArrayList<Node> res = Algorithms.hamiltonPath(edges,nodes);
        if(res==null){
            JOptionPane.showMessageDialog(new JFrame()
                    , "graph doesn't has hamilton path");
            return;
        }
        int cnt=1;
        for(Node node:res){
            node.name += " (hamilton path number = "+cnt+")";
            cnt++;
        }
        drawGraph();
    }

    private void colorTheGraph(){
        Algorithms.colorTheGraph(edges,nodes);
        drawGraph();
    }

    private void getMinimumSpanningTree() {
        ArrayList<Edge> res = Algorithms.minimumSpanningTree(edges, nodes);
        boolean dir=false;
        for(Edge edge:edges){
            if(res.contains(edge)){
                edge.isMinSpanTree=true;
            }
            if(edge.isDirected){
                dir = true;
                break;
            }
        }
        if(dir){
            JOptionPane.showMessageDialog(new JFrame()
                    , "can't get MST for a directed graph");
        }else {
            drawGraph();
        }
    }

    private boolean prepareGraphData(JTextField textField, JTextArea textArea, JCheckBox isDir) {
        nodes.clear();
        edges.clear();
        String[] nodes = textField.getText().split(" ");
        String[] edges = textArea.getText().split("\n");
        for (String str : nodes) {
            addNode(str);
        }
        boolean wt=false , notwt = false;
        for (String edge : edges) {
            String[] vertices = edge.split(" ");
            if (vertices.length == 2) {
                // graph not weighted
                notwt = true;
                addEdge(vertices[0], vertices[1], isDir.isSelected(), -1);
            } else {
                // graph is weighted
                wt=true;
                addEdge(vertices[0], vertices[1], isDir.isSelected()
                        , Integer.parseInt(vertices[2]));
            }
        }
        if(wt && notwt){
            JOptionPane.showMessageDialog(new JFrame()
                    , "graph can't have weighted and non weighted edges at the same time");
            return false;
        }
        return true;
    }

    private void getAdjacencyMatrix() {
        JFrame frame = new JFrame("Adjacency matrix");
        frame.setSize(600, 600);
        int cnt = nodes.size() + 1;
        JTable table = new JTable(cnt, cnt);
        for (int i = 0; i < cnt; i++) {
            table.getColumnModel().getColumn(i).setMinWidth(colWidth);
        }
        table.setRowHeight(rowHit);
        for (int i = 1; i < cnt; i++) {
            table.setValueAt(nodes.get(i - 1).name, 0, i);
        }
        for (int i = 1; i < cnt; i++) {
            table.setValueAt(nodes.get(i - 1).name, i, 0);
        }
        for (int i = 1; i < cnt; i++) {
            Node n1 = nodes.get(i - 1);
            for (int j = 1; j < cnt; j++) {
                Node n2 = nodes.get(j - 1);
                table.setValueAt(getWeight(n1, n2), i, j);
            }
        }
        table.setBounds(30, 30, 500, 500);
        frame.add(table);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private int getWeight(Node u, Node v) {
        for (Edge e : edges) {
            if ((e.n1 == u && e.n2 == v) ||
                    (!e.isDirected && e.n2 == u && e.n1 == v)) {
                return e.wt == -1 ? 0 : e.wt;
            }
        }
        return -1;
    }

    private void getAdjacencyList() {
        JFrame adjacency_list = new JFrame("Adjacency list");
        adjacency_list.setSize(600, 600);
        int cnt = nodes.size();
        JTable adjacencyListTable = new JTable(cnt, cnt);
        for (int i = 0; i < cnt; i++) {
            adjacencyListTable.getColumnModel().getColumn(i).setMinWidth(colWidth);
        }
        adjacencyListTable.setRowHeight(rowHit);
        for (int i = 0; i < cnt; i++) {
            adjacencyListTable.setValueAt(nodes.get(i).name, i, 0);
        }
        for (int i = 0; i < cnt; i++) {
            List<Node> nbrs = nodes.get(i).nebours;
            for (int j = 0; j < nbrs.size(); j++) {
                adjacencyListTable.setValueAt(nbrs.get(j).name, i, j + 1);
            }
        }
        adjacencyListTable.setBounds(30, 30, 500, 500);
        adjacency_list.add(adjacencyListTable);
        adjacency_list.setLayout(null);
        adjacency_list.setVisible(true);
    }

    private void drawGraph() {
        Main main = this;
        EventQueue.invokeLater(() -> {
            JFrame f = new JFrame("GraphPanel");
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

    private void addNode(String name) {
        Node.selectNone(nodes);
        Node n = new Node(name);
        nodes.add(n);
        repaint();
    }

    private void addEdge(String name1, String name2, boolean isDir, int wt) {
        Node node1 = null, node2 = null;
        for (Node node : nodes) {
            if (node.name.equals(name1)) {
                node1 = node;
            }
            if (node.name.equals(name2)) {
                node2 = node;
            }
        }
        if (node1 == null || node2 == null) {
            JOptionPane.showMessageDialog(new JFrame(), "error with the input");
            return;
        }
        edges.add(new Edge(node1, node2, isDir, wt));
        repaint();
    }
}
// 1 2 3 4 5 6
/*
1 2
2 3
2 4
3 4
3 5
3 6
4 5
4 6


1 2
2 3
3 4
4 5
1 4


A B C D
A B 40
B C 24
C D 10
D A 14
A C 20
D B 12

 */