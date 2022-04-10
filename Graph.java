import java.util.*;

public class Graph {

    private List<Sensor> sensorsInGraph = new ArrayList<>();
    private Map<Sensor, List<Sensor>> adjSensors = new HashMap<>();
    private Sensor root = null;
    private Sensor baseStation = null;
    private int sizeOfGraph;
    private double minX, minY, maxX, maxY;

    /**
     * Creates empty Graph
     */
    public Graph(){
        sizeOfGraph = 0;
        minX = 1000;
        minY = 1000;
        maxX = -100;
        maxY = -100;
    }

    /**
     * Add sensor when given a sensor
     * Check if the graph was empty or not and create the root if it was
     * @param sensor sensor to put onto the Graph
     */
    protected void addSensor(Sensor sensor){
        if(root == null){
            root = sensor;
        }
        if(sensor instanceof BaseStation){
            baseStation = sensor;
        }
        //check for min and max X values
        if(sensor.getX() < minX){
            minX = sensor.getX();
        }
        else if(sensor.getX() > maxX){
            maxX = sensor.getX();
        }
        //check for min and maxY values
        if(sensor.getY() < minY){
            minY = sensor.getY();
        }
        else if(sensor.getY() > maxY){
            maxY = sensor.getY();
        }
        adjSensors.putIfAbsent(sensor, new ArrayList<>());
        sensorsInGraph.add(sensor);
        sizeOfGraph++;
        System.out.println(sensorsInGraph);
    }

    /**
     * remove a sensor on the graph when given a sensor to remove
     * @param sensor to remove
     */
    protected void removeSensor(Sensor sensor){
        adjSensors.values().stream().forEach(e -> e.remove(sensor));
        adjSensors.remove(sensor);
        sizeOfGraph--;
    }

    /**
     * Add edge given two sensors
     * @param sensor1
     * @param sensor2
     */
    protected void addEdge(Sensor sensor1, Sensor sensor2){
        adjSensors.get(sensor1).add(sensor2);
        adjSensors.get(sensor2).add(sensor1);
        sensor1.addNeighbor(sensor2);
        sensor2.addNeighbor(sensor1);
    }

    /**
     * Remove an edge given two sensors
     * @param sensor1 first sensor that has an edge with sensor2 to remove edge
     * @param sensor2 second sensor that shares an edge with sensor 1 to remove
     */
    protected void removeEdge(Sensor sensor1, Sensor sensor2) {
        List<Sensor> eS1 = adjSensors.get(sensor1);
        List<Sensor> eS2 = adjSensors.get(sensor2);
        if (eS1 != null) {
            eS1.remove(sensor1);
        }
        if (eS2 != null) {
            eS2.remove(sensor2);
        }
    }

    /**
     * Get the all nodes connected to a given node by edges
     * @param sensor sensor to check for neighbors
     * @return a List of sensors
     */
    protected List<Sensor> getNeighbors(Sensor sensor){
        return adjSensors.get(sensor);
    }

    /**
     * Starts each thread for the sensors in the graph
     */
    protected void startThreadsForAllSensors(){
        for(Sensor sensor: sensorsInGraph){
            System.out.println("Thread started");
            new Thread(sensor).start();
        }
    }

    /**
     * Returns true if the given sensor has adjacent
     * or neighboring Sensors
     * @param sensor Sensor to be checked for neighbors
     * @return boolean representing whether the Sensor
     * has any neighboring Sensors
     */
    protected boolean hasNeighbors(Sensor sensor){
        if(adjSensors.get(sensor) != null){
            return true;
        }
        return false;
    }

    /**
     * Retrieves the root node of the Graph
     * @return Sensor representing the root
     */
    protected Sensor getRoot(){
        return this.root;
    }

    /**
     * Retrieves the Sensor labeled as the Base Station
     * within the current Graph structure
     * @return Sensor representing the Base Station
     */
    protected Sensor getBaseStation(){
        return this.baseStation;
    }

    /**
     * Method used in main to start fireSpread
     * Does not really account for when the given x and y values are not in the graph, but that would mean the input
     * for the configuration file was incorrect
     * @param x x-coordinate of the chosen Sensor
     * @param y y-coordinate of the chosen Sensor
     * @return Sensor at the given location
     */
    protected Sensor getSensor(int x, int y){
        Sensor toReturn = new Sensor(0, 0);

        for(Sensor sensor: sensorsInGraph){
            if(sensor.getX() == x && sensor.getY() == y){
                toReturn = sensor;
            }
        }

        return toReturn;
    }

    /**
     * Returns the number of Sensors contained
     * within the Graph structure
     * @return int representation of number
     * of Sensors in Graph
     */
    protected int getSizeOfGraph(){
        return this.sizeOfGraph;
    }

    /**
     * Returns the minimum value that the
     * x-coordinate of a Sensor can be
     * @return int representing min x
     */
    protected double getMinX(){
        return this.minX;
    }

    /**
     * Returns the minimum value that the
     * y-coordinate of a Sensor can be
     * @return int representing min y
     */
    protected double getMinY(){
        return this.minY;
    }

    /**
     * Returns the maximum value that the
     * x-coordinate of a Sensor can be
     * @return int representing max x
     */
    protected double getMaxX(){
        return this.maxX;
    }

    /**
     * Returns the maximum value that the
     * y-coordinate of a Sensor can be
     * @return int representing max y
     */
    protected double getMaxY(){
        return this.maxY;
    }

}
