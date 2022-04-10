import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class BaseStation extends Sensor{

    private List<String> log = new ArrayList<>();
    //private BlockingQueue q;

    /**
     * Constructor for the BaseStation
     * @param x x-coordinate of Sensor representing
     * the Base Station
     * @param y y-coordinate of Sensor representing
     * the Base Station
     */
    public BaseStation(int x, int y){
        super(x, y);
    }

    /**
     * Method to receive a message from a neighboring node.
     * @param message String to be added to the Base
     * Station log
     */
    public void addLog(String message){
        log.add(message);
    }

    /**
     * Adds message to Base Station log
     * when the Base Station is on fire
     */
    private void baseFire(){
        log.add("Base station on fire");
    }

    /**
     * Retrieves the Base Station log
     * @return List of String messages
     * held by the Base Station
     */
    public List<String> getLog(){
        return this.log;
    }

}
