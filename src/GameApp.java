import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * game logic and object construction happens in this class
 */
class Game extends Pane {
 //take all the objects needed for the game
 Pond pond;
 Cloud cloud;
 Helipad helipad;
 Helicopter helicopter;
 public Game(Pond pond, Cloud cloud, Helipad helipad, Helicopter helicopter){
  this.pond = pond;
  this.cloud = cloud;
  this.helipad = helipad;
  this.helicopter = helicopter;
  setupLayout();
 }

 private void setupLayout(){
  super.getChildren().add(pond);
 }

}

/**
 * No movement in the game object class
 * Any behaviour or state will be setup in this class
 */
abstract class GameObject extends Group {
 Color color;
 Shape shape;
 public GameObject(Color color, Shape shape, Point2D size){
  this.color = color;
  this.shape = shape;
 }

}

class Pond extends GameObject{

 public Pond(Color color, Shape shape, Point2D size) {
  super(color, shape, size);
 }
}

class Cloud extends GameObject{

 public Cloud(Color color, Shape shape, Point2D size) {
  super(color,shape, size);
 }
}

class Helipad extends GameObject{

 public Helipad(Color color, Shape shape, Point2D size) {
  super(color, shape, size);
 }
}

class Helicopter extends GameObject{

 public Helicopter(Color color, Shape shape, Point2D size) {
  super(color, shape,size);
 }
}

class PondAndCloud{

}

interface Updatable{

}

/**
 * Sets up all the keyboard events  handlers to invoke public methods
 * in the game
 */
public class GameApp extends Application {
 Point2D size = new Point2D(500,800);

 @Override
 public void start(Stage primaryStage) throws Exception {

 }

 public static void main(String[] args){launch(args);}
}
