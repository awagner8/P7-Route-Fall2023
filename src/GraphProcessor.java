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

    // include instance variables here
    private Map<Point, List<Point>> graph;
    private Map<Point, Double> distances;
    private Map<Point, Point> previous;
    private PriorityQueue<Point> pq;
    private Set<Point> visited;


    public GraphProcessor(){
        graph = new HashMap<>();
        distances = new HashMap<>();
        previous = new HashMap<>();
        pq = new PriorityQueue<>();
        visited = new HashSet<>();



    }

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws IOException if file not found or error reading
     */

    public void initialize(FileInputStream file) throws IOException {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(file));
        String line = br.readLine();
        while(line != null){
            String[] split = line.split(" ");
            Point p1 = new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
            Point p2 = new Point(Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            if(!graph.containsKey(p1)){
                graph.put(p1, new ArrayList<>());
            }
            if(!graph.containsKey(p2)){
                graph.put(p2, new ArrayList<>());
            }
            graph.get(p1).add(p2);
            graph.get(p2).add(p1);
            line = br.readLine();
        }


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
        
        Point closest = null;
        double min = Double.MAX_VALUE;
        for(Point point : graph.keySet()){
            double distance = point.distance(p);
            if(distance < min){
                min = distance;
                closest = point;
            }
        }
        return closest;
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
        double distance = 0.0;
        for(int i = 0; i < route.size() - 1; i++){
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
        if(p1.equals(p2)){
            return true;
        }
        if(!graph.containsKey(p1) || !graph.containsKey(p2)){
            return false;
        }
        Set<Point> visited = new HashSet<>();
        Queue<Point> q = new LinkedList<>();
        q.add(p1);
        while(!q.isEmpty()){
            Point current = q.remove();
            if(current.equals(p2)){
                return true;
            }
            if(!visited.contains(current)){
                visited.add(current);
                for(Point point : graph.get(current)){
                    q.add(point);
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
        if(start.equals(end)){
            throw new IllegalArgumentException();
        }
        if(!connected(start, end)){
            throw new IllegalArgumentException();
        }
        pq.add(start);
        distances.put(start, 0.0);
        while(!pq.isEmpty()){
            Point current = pq.remove();
            if(current.equals(end)){
                break;
            }
            if(!visited.contains(current)){
                visited.add(current);
                for(Point point : graph.get(current)){
                    double distance = distances.get(current) + current.distance(point);
                    if(!distances.containsKey(point) || distance < distances.get(point)){
                        distances.put(point, distance);
                        previous.put(point, current);
                        pq.add(point);
                    }
                }
            }
        }
        List<Point> route = new ArrayList<>();
        Point current = end;
        while(current != null){
            route.add(0, current);
            current = previous.get(current);
        }
        return route;
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String name = "data/usa.graph";
        GraphProcessor gp = new GraphProcessor();
        gp.initialize(new FileInputStream(name));
        System.out.println("running GraphProcessor");
    }


    
}
