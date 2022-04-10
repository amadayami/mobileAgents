import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Sensor implements Runnable{
    //Consumer Thread implementation

    /*
    status can be: normal, alert, dead
     */
    private String status = "normal";
    private boolean isDead = false;

    private List<Sensor> neighbors = new ArrayList<>();
    private BlockingQueue<String> q = new ArrayBlockingQueue<String>(500);
    private List<String> receivedMessages = new ArrayList<>();
    //this blocking Queue is unique

    private int x;
    private int y;

    //Agent on the Sensor
    private Agent agent = null;

    /**
     * Constructor for the Sensor object
     * @param x the x-coordinate of the Sensor
     * @param y the y-coordinate of the Sensor
     */
    public Sensor(int x, int y){
        this.x = x;
        this.y = y;

        //q = new PriorityBlockingQueue<>();
    }


    /**
     * Receives and processes message passed to the method. Can
     * handle messages for fire, Sensor death, and requests
     * for status.
     * @param message String message to be processed by the current
     * Sensor
     * @param receive the Sensor in which the message was received,
     * in case of needed replies
     */
    public void receiveMessage(String message, Sensor receive){
        if(isDead){
            //do nothing, can't receive messages
        }
        else{
            if(message.equals("fire")){
                //placeholder status, feel free to change
                this.status = "alert";
            }
            else if(message.equals("kill")){
                this.killSensor();
            }
            else if(message.equals("status")){
                //send status to the asking sensor
                receive.receiveMessage(this.status, this);
            }
            //not sure what other messages are involved
            else if(message.contains("base")){
                //thinking we could mark any message intended for base
            }
        }
    }

    /**
     * Retrieves the status for the current Sensor
     * @return String representing the status of
     * the current Sensor
     */
    public synchronized String reportStatus(){
        return status;
    }

    /**
     * Changes the status of the current node
     * @param status String representation of the
     * status of the node, indicating whether or
     * not it is on fire
     */
    public void setStatus(String status){
        //add to event log new status
        //String "node statusupdate statusName
        //System.out.println(this + " " + "is " + status);
        this.status = status;
        try{
            if(agentPresent()){
                this.agent.getQueue().put(status);
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds an agent to the current node after checking
     * for an Agent on the current node
     * @param agent Agent object to be placed on
     * on the node location
     */
    public synchronized void agentMove(Agent agent){
        if(agentPresent() == false){
            this.agent = agent;
        }
        else{
            System.out.println("Agent already present on sensor");
        }
    }

    /**
     * Sets current agent to null for when an agent
     * moves randomly to another node
     */
    public synchronized void agentLeave(){
        this.agent = null;
    }

    /**
     * Checks to see if an agent is present on the
     * current node. Returns true if an agent is on the
     * current node.
     * @return boolean value representing presence of
     * Agent object
     */
    public synchronized boolean agentPresent(){
        if(this.agent == null){
            return false;
        }
        return true;
    }

    /**
     * Adds a neighbor to the list of neighbors of
     * this Sensor based on the edges provided in
     * the text file passed in by the user. If the
     * Sensor to be added doesn't exist in the
     * LinkedList, then it is added.
     * @param neighbor Sensor that has an edge
     * with the current Sensor
     */
    public void addNeighbor(Sensor neighbor){
        if(!this.neighbors.contains(neighbor)){
            this.neighbors.add(neighbor);
        }
    }

    /**
     * Returns a string representation of the coordinates
     * of this sensor.
     * @return a String containing the x and y values of
     * the coordinates of the current Sensor
     */
    public String getCoordinates(){
        //i do it this way since i'm paranoid about errors
        String coord = "" + this.x + " " + this.y;
        return coord;
    }

    /**
     * Retrieves the x-coordinate of the Sensor location
     * @return int value of Sensor location x
     */
    public int getX(){
        return this.x;
    }

    /**
     * Retrieves the y-coordinate of the Sensor location
     * @return int value of Sensor location y
     */
    public int getY(){
        return this.y;
    }

    /**
     * Called when the fire has spread to the current
     * Sensor. Messages all neighbors about status update.
     * Marks self as dead since Sensor is now unable to
     * do anything.
     */
    private void killSensor(){
        Iterator<Sensor> iter = this.neighbors.iterator();
        while(iter.hasNext()){
            Sensor active = iter.next();
            //this is just a temp message, cool to change
            active.receiveMessage("fire", this);
        }
        //this.status = "dead";
        isDead = true;
    }

    /**
     * Retrieves the list of neighboring Sensors
     * @return List representation of Sensors that
     * share an edge with the current Sensor
     */
    public List<Sensor> getNeighbors(){
        return this.neighbors;
    }

    /**
     * Returns the BlockingQueue for the current Sensor
     * @return
     */
    public BlockingQueue<String> getQ() {
        return q;
    }

    /**
     * Handles the messages placed in the Sensor BlockingQueue, sending
     * messages to the Base Station log, and fire spreading.
     */
    public void run(){
        try {
            while (!isDead) {
                //handle blocking queue
                while(!q.isEmpty()){
                    String message = q.take();
                    //System.out.println(this + " received " + message);

                    if(message.equals("fire")){
                        this.setStatus("fire");

                        if(this instanceof BaseStation){
                            System.out.println("Basestation on fire");
                            System.out.println(((BaseStation)this).getLog());
                            isDead = true;
                            for(Sensor sensor: getNeighbors()){
                                sensor.getQ().put("dead");
                            }
                        }
                        //set neighboring nodes to alert
                        /*for (Sensor sensor : getNeighbors()) {
                            if(sensor.status.equals("normal")) {
                                sensor.getQ().put("alert");
                            }
                        }*/
                        for(Sensor sensor: getNeighbors()){
                            System.out.println(sensor.getCoordinates());
                            if(!sensor.status.equals("fire")){
                                System.out.println("here");
                                sensor.getQ().put("alert");
                            }
                        }
                    }
                    else if(message.equals("alert")) {
                        this.setStatus("alert");
                    }
                    else if(message.equals("dead")){
                        this.isDead = true;
                        if(agentPresent()){
                            this.agent.getQueue().put("fire");
                        }
                        for(Sensor sensor: getNeighbors()){
                            sensor.getQ().put("dead");
                        }
                    }
                    else{
                        if(this instanceof BaseStation){
                            ((BaseStation)this).addLog(message);
                        }
                        else if(!receivedMessages.contains(message)){
                            System.out.println(message + " This is not base station");
                            for(Sensor sensor: getNeighbors()){
                                sensor.getQ().put(message);
                            }
                            receivedMessages.add(message);
                        }
                        //send the message to neighboring nodes to get to the base station
                    }
                }

                if(this.status.equals("fire") && getNeighbors().size() > 0) {

                    List<Sensor> neighborsNotOnFire = new ArrayList<>();
                    for(Sensor sensor: getNeighbors()){
                        if(sensor.reportStatus().equals("alert") || sensor.reportStatus().equals("normal")){
                            neighborsNotOnFire.add(sensor);
                        }
                    }

                    //System.out.println(this + " " + neighborsNotOnFire);

                    if(neighborsNotOnFire.size() == 0){
                        //this.setStatus("dead");
                        //isDead = true;
                    }
                    else{

                        Thread.sleep(3000);

                        Random rand = new Random();
                        int randomNumber = rand.nextInt(neighborsNotOnFire.size());
                        System.out.println(this + " turned "+ neighborsNotOnFire.get(randomNumber));
                        neighborsNotOnFire.get(randomNumber).getQ().put("fire");
                        //This thread sleeps so that the thread it caught on fire has time to process that it is on fire before
                        //this thread would make another decision on who to catch on fire
                        Thread.sleep(10);
                    }
                }
            }
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}