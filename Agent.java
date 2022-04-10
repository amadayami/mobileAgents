import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Agent implements Runnable{

    private String agentId = "";
    private Sensor createLocation = null;

    //Sensors used during Agent random walk
    private Sensor current = null;
    private Sensor previous = null;

    //whether a fire has been found, controls random walk
    private boolean fireActive;

    //the number of children created by the current thread
    private int children = 0;

    private BlockingQueue<String> queue = new PriorityBlockingQueue<>();

    /**
     * Constructor for the Agent object. Sets agentId, creation
     * location, the current Sensor, and the status of fireActive
     * @param id String representing Agent id
     * @param creation Sensor location for which the Agent
     * was created
     * @param fire boolean for whether an initial fire was found
     */
    public Agent(String id, Sensor creation, boolean fire){
        this.agentId = id;
        this.createLocation = creation;
        this.current = creation;
        this.fireActive = fire;
    }

    /**
     * Creates a unique id for a child created by
     * the current Agent thread
     * @return unique String id for child
     */
    private synchronized String getChildId(){
        String childId = this.agentId + this.children;
        this.children++;
        return childId;
    }

    /**
     * Returns the blocking queue contained in the
     * current Agent thread
     * @return BlockingQueue of String messages
     */
    public synchronized BlockingQueue<String> getQueue(){
        return this.queue;
    }

    /**
     * Random traversal of the sensors by the first
     * Agent thread. Will randomly choose a neighbor
     * if there are more than two neighbors. For cases
     * of 0, 1, or 2 neighbors, they are treated as
     * special cases and have specific treatments.
     */
    private synchronized void randomWalk(){
        List<Sensor> possible;
        Random random = new Random();

        //special cases
        if(current != null){
            //shouldn't be if we set it at creation
            possible = current.getNeighbors();
        }
        else{
            System.out.println("Current sensor not set");
            return;
        }
        if(possible.isEmpty()){
            System.out.println("Node has no neighbors");
            return;
        }

        if(possible.size() == 1){
            //move to only option
            String status = possible.get(0).reportStatus();
            if(status.equals("alert")){
                this.moveAgent(possible.get(0));
                //should be the current node now
                this.fireProtocol(this.current);
            }
            else if(status.equals("fire")){
                this.fireProtocol(this.current);
            }
            else{
                this.moveAgent(possible.get(0));
            }
        }
        else if(possible.size() == 2){
            if(this.previous != null){
                possible.remove(this.previous);
                //move to only remaining neighbor
                String status = possible.get(0).reportStatus();
                if(status.equals("alert")){
                    this.moveAgent(possible.get(0));
                    this.fireProtocol(possible.get(0));
                }
                else if(status.equals("fire")){
                    //call fire protocol
                    this.fireProtocol(this.current);
                }
                else{
                    this.moveAgent(possible.get(0));
                }
            }
            else{
                //randomly choose a node
                int rand = random.nextInt(2);
                //move to index chosen if not on fire
                String status = possible.get(rand).reportStatus();
                if(status.equals("alert")){
                    this.moveAgent(possible.get(rand));
                    this.fireProtocol(possible.get(rand));
                }
                else if(status.equals("fire")){
                    //call fire protocol
                    this.fireProtocol(this.current);
                }
                else{
                    this.moveAgent(possible.get(rand));
                }
            }
        }
        else{
            if(this.previous != null){
                possible.remove(this.previous);
            }
            //choose a random neighbor
            int rand = random.nextInt(possible.size());
            //move to this index if not on fire
            String status = possible.get(rand).reportStatus();
            if(status.equals("alert")){
                this.moveAgent(possible.get(rand));
                this.fireProtocol(possible.get(rand));
            }
            else if(status.equals("fire")){
                this.fireProtocol(possible.get(rand));
            }
            else{
                this.moveAgent(possible.get(rand));
            }
        }
    }

    /**
     * Moves the Agent onto the Sensor passed to the method.
     * If the Sensor is dead or on fire, then the Agent is
     * blocked from moving.
     * @param move Sensor chosen as new location of Agent
     */
    private synchronized void moveAgent(Sensor move){
        String status = move.reportStatus();
        if(!status.equals("dead") || !status.equals("fire")){
            this.current.agentLeave();
            move.agentMove(this);
            this.previous = this.current;
            this.current = move;
        }
        else{
            System.out.println("Can't move agent to dead sensor");
        }
    }

    /**
     * Copies current Agent to available neighboring Sensors when called.
     * Changes status of fireActive so that the first Agent knows to end
     * its random walk once fireProtocol has already been called. Messages
     * the Sensor in which the child is created in order to pass it to
     * the Base Station log.
     * @param alerted the Sensor found to be alerted by the Agent
     */
    private synchronized void fireProtocol(Sensor alerted){
        //agent has found alerted sensor
        this.fireActive = true;

        for(Sensor sensor: alerted.getNeighbors()){
            if(!sensor.agentPresent() && !sensor.reportStatus().equals("fire")){
                String idChild = this.getChildId();
                //actual creation of the thread
                Agent child = new Agent(idChild, sensor, true);
                sensor.agentMove(child);
                new Thread(child).start();

                if(sensor.reportStatus().equals("alert")){
                    child.fireProtocol(sensor);
                }

                try{
                    String message = "Agent " + idChild  + " created at Sensor (" +
                            sensor.getX() + ", " + sensor.getY() + ")";
                    sensor.getQ().put(message);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                    System.out.println(this + " exited");
                }
            }
        }
    }

    /**
     * Retrieves the Sensor in which the current Agent
     * was created
     * @return Sensor location of Agent creation
     */
    public Sensor getCreateLocation(){
        return this.createLocation;
    }

    /**
     * Retrieves the String id for the current Agent
     * @return unique String id for Agent thread
     */
    public String getAgentId(){
        return this.agentId;
    }

    /**
     * The very first Agent does a random search until
     * it finds a neighbor that is either alerted or on
     * fire. Then the Agent copies itself on to the
     * neighboring Sensors that are still alive. If the
     * status of the current Sensor changes, then the
     * Agent copies itself to its neighbors again.
     * The Agent dies when the Sensor it is located on dies.
     */
    public void run(){
        try{
            while(!this.fireActive){
                Thread.sleep(1500);
                if(!this.fireActive){
                    this.randomWalk();
                }
            }
            boolean onFire = false;
            while(!onFire){
                while(!this.queue.isEmpty()){
                    String message = queue.take();
                    if(message.equals("alert")){
                        this.fireProtocol(this.current);
                    }
                    else if(message.equals("fire")){
                        this.current.agentLeave();
                        onFire = true;
                    }
                }
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

}
