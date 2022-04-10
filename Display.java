import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This Display class is used in order to draw the simulation onto the screen.
 * It is looped in an animation timer in the Graphics class
 * This class implements the GraphicsInterface which is a collection of constants used in the graphics calculations
 */
public class Display implements GraphicsInterface{

    private Pane pane;
    private Graph graph;
    private double multiplier;
    private double offsetX, offsetY;

    /**
     * This is the constructor for the Display class which takes a pane to draw onto and a Graph to draw
     * @param pane
     * @param graph
     */
    public Display(Pane pane, Graph graph){
        this.pane = pane;
        this.graph = graph;

        //Calculate a multiplier to scale the graph to fit it onto the screen.
        double minMultiplierX = (WINDOWWIDTH - 2 * GRAPHOFFSETX - 200) / (1.0 * (graph.getMaxX() - graph.getMinX()));
        double minMultiplierY = (WIDOWHEIGHT - 2 * GRAPHOFFSETY) / (1.0 * (graph.getMaxY() - graph.getMinY()));

        System.out.println(minMultiplierX + " " + minMultiplierY);

        if(minMultiplierY < minMultiplierX){
            multiplier = minMultiplierY;
        }
        else{
            multiplier = minMultiplierX;
        }

        offsetX = multiplier * graph.getMinX();
        offsetY = multiplier * graph.getMinY();

    }

    /**
     * Method called in the animation timer in order to draw the Graph onto the screen
     */
    public void displayGraph(){

        Sensor working = graph.getRoot();
        Stack<Sensor> marked = new Stack<>();

        transverseNextNode(working, marked);

        List<Node> circles = new ArrayList<>();
        for(int i = 0; i < pane.getChildren().size(); i++){
            if(pane.getChildren().get(i) instanceof Circle){
                circles.add(pane.getChildren().get(i));
            }
        }
        for(Node c: circles){
            pane.getChildren().remove(c);
            pane.getChildren().add(c);
        }

    }

    /**
     * Recursive function that finds the neighboring nodes of a current working node and then draws it onto
     * the board and marks it as transversed so that it does not draw the node again. In addition, a line is drawn
     * between the working node and the neighboring nodes of that working node
     * @param working current node getting the neighbors of
     * @param marked a stack of sensors that defines which ones have been transversed already
     */
    private void transverseNextNode(Sensor working, Stack<Sensor> marked){
        if(!marked.contains(working)){
            drawSensor(working);
            marked.push(working);
            if(working instanceof BaseStation){
                displayLog(working);
            }
        }
        for(Sensor neighbors: graph.getNeighbors(working)){
            drawLine(working, neighbors);
            if(marked.size() < graph.getSizeOfGraph()){
                //drawLine(working, neighbors) if you want to draw one path from the first to the last node.
                if(!marked.contains(neighbors)){
                    transverseNextNode(neighbors, marked);
                }
            }
        }
    }

    /**
     * Display the base station log when the base station node has been transversed in
     * the method transverseNextNode()
     * @param station
     */
    private void displayLog(Sensor station){
        Label log = new Label();
        log.setTranslateX(WINDOWWIDTH - 210);
        log.setTranslateY(GRAPHOFFSETY);
        log.setFont(Font.font("News Gothic Standard", 14));
        log.setText("Base Station Log");

        List<String> thisLog = ((BaseStation)station).getLog();
        for(int i = 0; i < thisLog.size(); i++){
            Label line = new Label();
            line.setTranslateX(WINDOWWIDTH - 210);
            line.setTranslateY(GRAPHOFFSETY + i * 10 + 20);
            line.setFont(Font.font("News Gothic Standard", 9));
            line.setText(thisLog.get(i));
            pane.getChildren().add(line);
        }

        pane.getChildren().add(log);
    }

    /**
     * A function called in transverseNextNode() in order to draw a line between two different sensors in the graph
     * @param sensor1 a Sensor to draw from
     * @param sensor2 a Sensor to draw to
     */
    private void drawLine(Sensor sensor1, Sensor sensor2){
        Line line = new Line(getSensorCoordX(sensor1) - offsetX, getSensorCoordY(sensor1) - offsetY,
                                getSensorCoordX(sensor2) - offsetX, getSensorCoordY(sensor2) - offsetY);
        pane.getChildren().add(line);
    }

    /**
     * Method called in transverseNextNode() in order to draw a node onto the screen that has been transversed.
     * @param sensor
     */
    private void drawSensor(Sensor sensor){
        Circle circle;
        if(sensor instanceof BaseStation){
            if(!(sensor.reportStatus().equals("fire") || sensor.reportStatus().equals("dead"))){
                circle = new Circle(getSensorCoordX(sensor) - offsetX, getSensorCoordY(sensor) - offsetY, 7, Color.BLUE);
            }
            else{
                circle = new Circle(getSensorCoordX(sensor) - offsetX, getSensorCoordY(sensor) - offsetY, 7, Color.RED);
            }
        }
        else if(sensor.reportStatus().equals("fire") || sensor.reportStatus().equals("dead")){
            circle = new Circle(getSensorCoordX(sensor) - offsetX, getSensorCoordY(sensor) - offsetY, 7, Color.RED);
        }
        else if(sensor.reportStatus().equals("alert")){
            circle = new Circle(getSensorCoordX(sensor) - offsetX, getSensorCoordY(sensor) - offsetY, 7, Color.YELLOW);
        }
        else{
            circle = new Circle(getSensorCoordX(sensor)- offsetX, getSensorCoordY(sensor) - offsetY, 7);
        }

        Circle agent;
        if(sensor.agentPresent()){
            agent = new Circle(getSensorCoordX(sensor) - offsetX, getSensorCoordY(sensor) - offsetY, 10, Color.LIMEGREEN);
            //System.out.println(sensor + " has agent");
            pane.getChildren().add(agent);

        }

        pane.getChildren().add(circle);
    }

    /**
     * A method used to get a sensor coordinate x value, so that this chunk of code did not have to be written out so many times
     * @param sensor
     * @return
     */
    private double getSensorCoordX(Sensor sensor){
        return multiplier * sensor.getX() + GRAPHOFFSETX;
    }

    /**
     * A method used to get a sensor y coordinate to minimize the amount of times I would have to write this
     * @param sensor
     * @return
     */
    private double getSensorCoordY(Sensor sensor){
        return multiplier * sensor.getY() + GRAPHOFFSETY;
    }

    /**
     * Method called in the animation time in order to remove all of the children for this pane
     */
    public void removeAll(){
        pane.getChildren().removeAll(pane.getChildren());
    }

}
