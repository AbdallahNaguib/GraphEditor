import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Algorithms {

    static Node getPar(Map<Node,Node> dsuPar,Node node){
        if(dsuPar.get(node)==node)return node;
        Node par=getPar(dsuPar,dsuPar.get(node));
        dsuPar.put(node,par);
        return par;
    }

    static void connect(Map<Node,Node> dsuPar,Node n1,Node n2){
        Node par1=getPar(dsuPar,n1);
        Node par2=getPar(dsuPar,n2);
        if(par1==par2)return;
        dsuPar.put(par1,par2);
    }

    public static ArrayList<Edge> minimumSpanningTree(ArrayList<Edge> edges
            ,ArrayList<Node> nodes){
        Map<Node,Node> dsuPar=new HashMap<>();
        for(Node node:nodes){
            dsuPar.put(node,node);
        }
        Collections.sort(edges);
        ArrayList<Edge> res=new ArrayList<>();
        for(Edge edge:edges){
            if(getPar(dsuPar,edge.n1)!=getPar(dsuPar,edge.n2)){
                connect(dsuPar,edge.n1,edge.n2);
                res.add(edge);
            }
        }
        return res;
    }
    static void colorTheGraph(ArrayList<Edge> edges
            ,ArrayList<Node> nodes){
        Color cols[] = {Color.RED,Color.GREEN,Color.BLACK,Color.YELLOW,Color.CYAN
                ,Color.BLUE};
        for(Node node:nodes){
            for(Color col:cols) {
                boolean ok=true;
                for (Edge edge : edges) {
                    if (node == edge.n1 || node == edge.n2) {
                        if(edge.n1.color == col || edge.n2.color == col ){
                            ok=false;
                            break;
                        }
                    }
                }
                if(ok){
                    node.color = col;
                    break;
                }
            }
        }
    }
    static ArrayList<Edge> getEuler(ArrayList<Edge> edges
            ,ArrayList<Node> nodes){
        for(Node node:nodes){
            ArrayList<Edge> res=runEuler(edges,node,new ArrayList<>());
            if(res!=null)return res;
        }
        // no euler path found
        return null;
    }
    private static ArrayList<Edge> runEuler(ArrayList<Edge> edges
            ,Node cur,ArrayList<Edge> vis){
        if(edges.isEmpty())return vis;
        ArrayList<Edge> res=null;
        for(Edge edge:edges){
            if((edge.isDirected && edge.n1==cur)||
                    (!edge.isDirected && (edge.n1==cur || edge.n2==cur ))){
                vis.add(edge);
                ArrayList<Edge> edges2 = new ArrayList<>(edges);
                edges2.remove(edge);
                Node n = edge.n1;
                if(n==cur){
                    n=edge.n2;
                }
                res = runEuler(edges2,n,vis);
                if(res!=null){
                    return res;
                }
                vis.remove(edge);
            }
        }
        return null;
    }
    static ArrayList<Node> hamiltonPath(ArrayList<Edge> edges
            ,ArrayList<Node> nodes){
        ArrayList<Node> res=null;
        for(Node node:nodes){
            ArrayList<Node> nodes2=new ArrayList<>(nodes);
            res = runHamilton(edges,nodes2,node,new ArrayList<>());
            if(res!=null)return res;
        }
        return res;
    }
    private static ArrayList<Node> runHamilton(ArrayList<Edge> edges
            , ArrayList<Node> nodes, Node cur, ArrayList<Node> vis){
        nodes.remove(cur);
        vis.add(cur);
        if(nodes.isEmpty())return vis;
        ArrayList<Node> res;
        for(Node node:nodes){
            if(nodeGoesToNode(cur,node,edges) != -1){
                ArrayList<Node> nodes2=new ArrayList<>(nodes);
                res=runHamilton(edges,nodes2,node,vis);
                if(res!=null)return res;
            }
        }
        nodes.add(cur);
        vis.remove(cur);
        return null;
    }
    private static int nodeGoesToNode(Node n1, Node n2, ArrayList<Edge> edges){
        for(Edge edge:edges){
            if(edge.n1 == n1 && edge.n2 == n2)return edge.wt;
            if(!edge.isDirected){
                if(edge.n2 == n1 && edge.n1 == n2)return edge.wt;
            }
        }
        return -1;
    }
    static Pair<ArrayList<Node>,Integer> getMinHamilton(ArrayList<Edge> edges
            ,ArrayList<Node> nodes){
        Pair<ArrayList<Node>,Integer> res = new Pair<>(null,1000000000);
        for(Node node:nodes){
            ArrayList<Node> nodes2=new ArrayList<>(nodes);
            ArrayList<Node> vis = new ArrayList<>();
            vis.add(node);
            nodes2.remove(node);
            Pair<ArrayList<Node>,Integer> res2=
                    runMinHamilton(edges,nodes2,node,node,vis
                            ,0);
            if(res2.first !=null && res2.second<=res.second){
                res = res2;
            }
        }
        return res;
    }

    private static Pair<ArrayList<Node>,Integer> runMinHamilton(ArrayList<Edge> edges
            , ArrayList<Node> nodes,Node start, Node cur, ArrayList<Node> vis,int cost){
        Pair<ArrayList<Node>,Integer> res=new Pair<>(null,1000000000);
        if(nodes.isEmpty()){
            // check if current is connected to the starting point
            // because we need a hamilton circuit
            int val=nodeGoesToNode(cur,start,edges);
            if(val != -1){
                vis.add(start);
                return new Pair<>(new ArrayList<>(vis),cost+val);
            }else{
                return res;
            }
        }
        for(Node node:nodes){
            int val = nodeGoesToNode(cur,node,edges);
            if(val != -1){
                ArrayList<Node> nodes2=new ArrayList<>(nodes);
                ArrayList<Node> vis2 = new ArrayList<>(vis);
                nodes2.remove(node);
                vis2.add(node);
                Pair<ArrayList<Node>,Integer> res2=runMinHamilton(edges,nodes2,start,node,vis2,cost+val);
                if(res2.first !=null && res2.second<=res.second){
                    res.first =new ArrayList<>(res2.first);
                    res.second = res2.second;
                }
            }
        }
        return res;
    }
}
class Pair<T,T2>{
    T first;
    T2 second;
    Pair(T first, T2 second) {
        this.first = first;
        this.second = second;
    }
}
