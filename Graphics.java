import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class Graphics extends Application implements GraphicsInterface{

    private Graph graph;

    /**
     * The extension of Application requires for an empty constructor of Graphics
     */
    public Graphics(){

    }

    /**
     * This method returns a parent to add to the scene
     *
     * In addition, this controls the logic for the input of the user and for the game
     * @return a Parent to add to the scene
     */
    private Parent createGame(){

        Parameters parameters = getParameters();
        List<String> strings = parameters.getRaw();
        Main main = new Main(strings);
        graph = main.getGraph();

        Pane pane = new Pane();
        pane.setPrefSize(WINDOWWIDTH, WIDOWHEIGHT);

        Display display = new Display(pane, graph);
        //System.out.println(graph.getRoot());

        AnimationTimer timer = new MyTimer(display);
        timer.start();

        return pane;
    }

    @Override
    /**
     * This method is part of the Application and is overridden in order to create the stage
     * In addition, this throws Exception and calls
     * @createGame()
     */
    public void start(Stage stage) throws Exception {

        stage.setTitle("Fire Simulation");
        stage.setScene(new Scene(createGame()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class MyTimer extends AnimationTimer {

        private Display display;

        public MyTimer(Display display){
            this.display = display;
        }

        @Override
        /**
         * Redraws the graph every 50 milliseconds
         */
        public void handle(long now){
            if(now % 20 == 0){
                display.removeAll();
                display.displayGraph();
            }
        }

    }

}
