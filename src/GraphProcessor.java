import java.security.InvalidAlgorithmParameterException;
import java.io.*;
import java.util.*;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 * @author Owen Astrachan modified in Fall 2023
 *
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */

    private Map <Point, Set<Point>> myGraph;
    private Map<String, Point> pointName;
    private int numVert;
    private int numEdges;


 


    public GraphProcessor(){
        myGraph = new HashMap<>();
        pointName = new HashMap<>();
        numVert = 0;
        numEdges = 0;
    }

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws IOException if file not found or error reading
     */
    public void initialize(FileInputStream file) throws IOException {
        Scanner scan = new Scanner(file);
        if(!scan.hasNextInt()){
            scan.close();
            throw new IOException("Could not read .graph file");
        }
        numVert = scan.nextInt();
        numEdges = scan.nextInt();
        Point[] points = new Point[numVert];
        for (int i = 0; i < numVert; i++) {
            String name = scan.next();
            double lat = scan.nextDouble();
            double lon = scan.nextDouble();
            Point p = new Point(lat, lon);
            points[i] = p;
            pointName.put(name, p);
        }
        for (int k = 0; k < numEdges; k++) {
            Point p1 = points[scan.nextInt()];
            Point p2 = points[scan.nextInt()];
            myGraph.putIfAbsent(p1, new HashSet<Point>());
            myGraph.putIfAbsent(p2, new HashSet<Point>());
            myGraph.get(p1).add(p2);
            myGraph.get(p2).add(p1);
            if (!scan.hasNextInt() && scan.hasNext()) {
                scan.next();
            }
        }
        scan.close();
    }



    /**
     * NOT USED IN FALL 2023, no need to implement
     * @return list of all vertices in graph
     */

    public List<Point> getVertices(){
        return null;
    }

    /**
     * NOT USED IN FALL 2023, no need to implement
     * @return all edges in graph
     */
    public List<Point[]> getEdges(){
        return null;
    }

    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p is a point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        Point nearest = null;
        double min = Double.MAX_VALUE;
        for (Point v : myGraph.keySet()) {
            if (p.distance(v) < min){
                min = p.distance(v);
                nearest = v;
            }
        }
        return nearest;
    }



    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        double distance = 0;
        for (int i = 0; i < route.size() - 1; i++){
            distance += route.get(i).distance(route.get(i + 1));
        }
        return distance;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if and onlyu if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        if (p1.equals(p2)){
            return true;
        }
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(p1);
        while(!queue.isEmpty()){
            Point current = queue.remove();
            if(current.equals(p2)){
                return true;
            }
            if(!visited.contains(current)){
                visited.add(current);
                for(Point point : myGraph.get(current)){
                    queue.add(point);
                }
            }
        }
        return false;
    }

    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws IllegalArgumentException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws IllegalArgumentException {
        if (start.equals(end) || !connected(start, end)){
            throw new IllegalArgumentException();
        }
        List<Point> path = new ArrayList<>();
        Map<Point, Point> prev = new HashMap<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        while(!queue.isEmpty()){
            Point current = queue.remove();
            if(current.equals(end)){
                break;
            }
            for(Point point : myGraph.get(current)){
                if(!prev.containsKey(point)){
                    prev.put(point, current);
                    queue.add(point);
                }
            }
        }
        Point current = end;
        while(!current.equals(start)){
            path.add(0, current);
            current = prev.get(current);
        }
        path.add(0, start);
        return path;
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String name = "data/usa.graph";
        GraphProcessor gp = new GraphProcessor();
        gp.initialize(new FileInputStream(name));
        System.out.println("running GraphProcessor");
    }


    
}
