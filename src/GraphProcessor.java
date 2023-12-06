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

     private Map <Point, Integer> connectedMap;
    private List<Point> info;
    private Map <Point, List<Point>> adjacent;
    private int numVert;
    private int numEdges;


 


    public GraphProcessor(){
        connectedMap = new HashMap<>();
        info = new ArrayList<>();
        adjacent = new HashMap<>();
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
        Scanner reader = new Scanner(file);
        
        //if the file isn't a .graph file, throw this error
        if (!reader.hasNextInt()){
            reader.close();
            throw new FileNotFoundException("Could not read .graph file");
        }

        String[] vertedge = reader.nextLine().split(" ");

        numVert = Integer.parseInt(vertedge[0]);
        numEdges = Integer.parseInt(vertedge[1]);

        info = new ArrayList<>();

        for (int i = 0; i < numVert; i++){
            String[] temp = reader.nextLine().split(" ");
            info.add(new Point(Double.parseDouble(temp[1]),Double.parseDouble(temp[2])));
        }

        

        //System.out.println(info.entrySet());

        adjacent = new HashMap<>();

        for (int i = 0; i < numEdges; i++){
            String[] temp = reader.nextLine().split(" ");
            Point a = info.get(Integer.parseInt(temp[0]));
            Point b = info.get(Integer.parseInt(temp[1]));
            if (!adjacent.containsKey(a)){
                adjacent.put(a, new ArrayList<>());
            }
            if (!adjacent.containsKey(b)){
                adjacent.put(b, new ArrayList<>());
            }
            adjacent.get(a).add(b);
            adjacent.get(b).add(a);
        }

        fillConnections();

        //System.out.println(adjacent.entrySet());

        reader.close();
    }

    private void fillConnections(){
        connectedMap = new HashMap<>();
        Set<Point> visited = new HashSet<>();
        int component = 0;
        for (Point p1: info){
            if (visited.contains(p1)){
                continue;
            }

            Stack<Point> toExplore = new Stack<>();
            Point current;
            toExplore.add(p1);
            visited.add(p1);
            connectedMap.put(p1, component);
            while (!toExplore.isEmpty()){
                current = toExplore.pop();
                for (Point neighbor : adjacent.get(current)){
                    if (!visited.contains(neighbor)){
                        visited.add(neighbor);
                        connectedMap.put(neighbor, component);
                        toExplore.push(neighbor);
                    }
                }
            }
            component += 1;
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
        Point closest = info.get(0);
        for (Point point : info){
            if (point.distance(p) < closest.distance(p)){
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
        if(p1.equals(p2)){
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
                for(Point point : adjacent.get(current)){
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
        if(!connected(start, end)){
            throw new IllegalArgumentException();
        }
        List<Point> route = new ArrayList<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Point> prev = new HashMap<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        while(!queue.isEmpty()){
            Point current = queue.remove();
            if(current.equals(end)){
                break;
            }
            if(!visited.contains(current)){
                visited.add(current);
                for(Point point : adjacent.get(current)){
                    queue.add(point);
                    prev.put(point, current);
                }
            }
        }
        Point current = end;
        while(current != null){
            route.add(0, current);
            current = prev.get(current);
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
