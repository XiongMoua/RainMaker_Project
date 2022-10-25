import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.Random;

/**
 * game logic and object construction happens in this class
 */
class Game extends Pane {
 //take all the objects needed for the game
 Pond pond;
 Cloud cloud;
 Helipad helipad;
 Helicopter helicopter;
 int borders = 25;
 public Game(Pond pond, Cloud cloud, Helipad helipad, Helicopter helicopter){
  this.pond = pond;
  this.cloud = cloud;
  this.helipad = helipad;
  this.helicopter = helicopter;
  setupLayout();
 }

 private void setupLayout(){
  Random rand = new Random();
  double xPosition = rand.nextDouble(GameApp.size.getX());
  double yPosition = rand.nextDouble(GameApp.size.getY());
  double xPosition2 = rand.nextDouble(GameApp.size.getX());
  double yPosition2 = rand.nextDouble(GameApp.size.getY());
  pond.shape.setLayoutX(xPosition);
  pond.shape.setLayoutY(yPosition);
  cloud.shape.setLayoutY(yPosition2);
  cloud.shape.setLayoutX(xPosition2);
  helipad.shape.setLayoutX(GameApp.size.getX()/2 - helipad.getWidth()/2);
  helipad.shape.setLayoutY(GameApp.size.getY()-helipad.getHeight()-borders);
  super.getChildren().addAll(helipad.shape, pond.shape, cloud.shape);
 }

}

/**
 * No movement in the game object class
 * Any behaviour or state will be setup in this class
 */
abstract class GameObject extends Group {
 Shape shape;
 Point2D size;
 public GameObject(Color color, Shape shape, Point2D size){
  this.shape = shape;
  this.shape.setFill(color);
  this.size = size;
 }

 public double getHeight(){
  return size.getY();
 }

 public double getWidth(){
  return size.getX();
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
 static Point2D size = new Point2D(500,800);
 public double sizeX(){
  return size.getX();
 }
 public double sizeY(){
  return size.getY();
 }
 Pond pond = new Pond(Color.BLUE, new Circle(25), new Point2D(25,25));
 Cloud cloud = new Cloud(Color.WHITE, new Circle(50), new Point2D(50,50));
 Helipad helipad = new Helipad(Color.GRAY, new Rectangle (100,100),new Point2D(100,100));
 Helicopter helicopter = new Helicopter(Color.GRAY, new Rectangle(20,20), new Point2D(5,5));
 @Override
 public void start(Stage stage) throws Exception {
  Game gameWindow = new Game(pond, cloud, helipad, helicopter);
  Scene scene = new Scene(gameWindow, size.getX(), size.getY());
  scene.setFill(Color.BLACK);
  stage.setTitle("RainMaker");
  stage.setScene(scene);

  //animation starter
  AnimationTimer loop = new AnimationTimer() {
   @Override
   public void handle(long now) {

   }
  };
  stage.show();
 }

 public static void main(String[] args){launch(args);}
}
