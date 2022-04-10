import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Main {

    private static String inputFileName;
    private Graph graph = new Graph();
    private List<String> args;
    //event log used specifically for the playback. This log does NOT affect the simulation
    private Queue<String> eventLog = new LinkedList<>();
    private Timer timer = new Timer();


    public Main(List<String> args){
        this.args = args;
        doStuff();
    }

    private void doStuff(){
        BaseStation baseStation;
        List<Sensor> sensors = new ArrayList<>();

        if(args.size() > 0){
            inputFileName = args.get(0);
        }

        Stack<Integer> sensorsToCreate = new Stack<>();
        Stack<Integer> edgesToCreate = new Stack<>();
        int[] baseStationNode = new int[2];
        int[] fireStart = new int[2];

        try{

            File file = new File(inputFileName);

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String st;
            while ((st = bufferedReader.readLine()) != null) {

                if(st.charAt(0) == 'n'){
                    List<Integer> numbers = parseLine(st.substring(5));
                    sensorsToCreate.push(numbers.get(0));
                    sensorsToCreate.push(numbers.get(1));
                }
                else if(st.charAt(0) == 'e'){
                    List<Integer> numbers = parseLine(st.substring(5));
                    for(int i = 0; i < numbers.size(); i++){
                        edgesToCreate.push(numbers.get(i));
                    }
                }
                else if(st.charAt(0) == 's'){
                    List<Integer> numbers = parseLine(st.substring(8));
                    baseStationNode[0] = numbers.get(0);
                    baseStationNode[1] = numbers.get(1);
                }
                else if(st.charAt(0) == 'f'){
                    List<Integer> numbers = parseLine(st.substring(5));
                    fireStart[0] = numbers.get(0);
                    fireStart[1] = numbers.get(1);
                }
            }


        } catch(IOException io){
            System.err.println(io);
        }

        //Graph graph = new Graph();

        //pop y then pop x
        //random agent with crazy values so we know if something went wrong
        Agent agent = new Agent("-100", new Sensor(0, 0), false);
        int num = sensorsToCreate.size();
        for(int i = 0; i < num; i+= 2){
            int y = sensorsToCreate.pop();
            int x = sensorsToCreate.pop();

            Sensor sensor;

            if(x == baseStationNode[0] && y == baseStationNode[1]){
                baseStation = new BaseStation(x, y);
                sensor = baseStation;
                //graph.addSensor(sensor);
                agent = new Agent("1", sensor, false);
                sensor.agentMove(agent);
                sensors.add(sensor);
            }
            else{
                sensor = new Sensor(x, y);
                sensors.add(sensor);
            }

            /*if(x == fireStart[0] && y == fireStart[1]){
                sensor.setStatus("fire");
            }*/
        }

        for(Sensor s : sensors){
            graph.addSensor(s);
        }

        //graph.adjSensors.keySet().stream().forEach(e -> System.out.println(e.getCoordinates()));

        num = edgesToCreate.size();
        for(int i = 0; i < num; i += 4){
            int y2 = edgesToCreate.pop();
            int x2 = edgesToCreate.pop();
            int y1 = edgesToCreate.pop();
            int x1 = edgesToCreate.pop();

            Sensor sensor1 = new Sensor(-1, -1);
            Sensor sensor2 = new Sensor(-1, -1);
            for(Sensor sensor : sensors){
                String coord = sensor.getCoordinates();
                if(coord.equals(x1 + " " + y1)){
                    sensor1 = sensor;
                }
                else if(coord.equals(x2 + " " + y2)){
                    sensor2 = sensor;
                }
                else{
                    //do nothing
                }
            }

            //System.out.println(sensor1.getCoordinates() + " " + sensor2.getCoordinates());

            graph.addEdge(sensor1, sensor2);
        }

        //start simulation
        //Graph simulationGraph = graph;
        //simulationGraph.startThreadsForAllSensors();
        graph.startThreadsForAllSensors();
        //start agent thread
        new Thread(agent).start();

        //start the fire
        Sensor start = graph.getSensor(fireStart[0], fireStart[1]);
        try{
            start.getQ().put("fire");
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        //return log for base station when it catches on fire
            //graph.getBaseStation().getLog()

    }



    /**
     * This static method is used in the main method in order to parse the configuration files to return a list of integers
     * @param s the string with just integers and spaces
     * @return the list of integers in the order they appear on the string
     */
    private static List<Integer> parseLine(String s){

        List<Integer> list = new ArrayList<>();

        String integerToAdd = "";
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == ' '){
                list.add(Integer.parseInt(integerToAdd));
                integerToAdd = "";
            }
            else{
                integerToAdd += s.charAt(i);
                if(i == s.length() - 1){
                    list.add(Integer.parseInt(integerToAdd));
                }
            }
        }

        return list;
    }

    public Graph getGraph(){
        return this.graph;
    }
}
