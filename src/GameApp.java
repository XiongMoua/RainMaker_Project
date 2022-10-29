import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.Random;

/**
 * game logic and object construction happens in this class
 * the layout of the objects will also be in here?
 */
class Game extends Pane {
 //take all the objects needed for the game
 Helipad helipad = new Helipad();
 Pond pond = new Pond();
 Random rand =new Random();
 Cloud cloud = new Cloud();
 Helicopter helicopter = new Helicopter();
 public Game() {

  this.setScaleY(-1);
  //translate the helipad
  helipad.setTranslateX(500/2 - 75/2);
  helipad.setTranslateY(100);

  //pond stuff ((GameApp.size.getY() - GameApp.size.getY() / 2) + GameApp.size.getY() / 2)
  double lY = rand.nextDouble( GameApp.size.getY() - GameApp.size.getY()/2) + GameApp.size.getY()/2;
  double lY2 = rand.nextDouble( GameApp.size.getY() - GameApp.size.getY()/2) + GameApp.size.getY()/2;
  pond.setTranslateY(lY);
  cloud.setTranslateY(lY2);

  //helicopter stuff
  helicopter.setTranslateX((500/2 - 75/2) + 75/2);
  helicopter.setTranslateY(100 + (75/2));

  this.getChildren().addAll(helipad, pond,cloud, helicopter);

 }
}

/**
 * No movement in the game object class
 * Any behaviour or state will be setup in this class
 */
abstract class GameObject extends Group implements Updatable{
 private Translate translation;
 private Rotate rotation;
 private Scale scale;
 public GameObject() {
  translation = new Translate();
  rotation = new Rotate();
  scale = new Scale();
  this.getTransforms().addAll(translation, rotation, scale);
 }
 void add(Node node){this.getChildren().add(node);}
}
 class Pond extends GameObject {
  public Pond(){
   Random rand = new Random();
   //random = (max - min) + min;
   Circle pond = new Circle(25);
   pond.setFill(Color.BLUE);
   double cX = pond.getRadius();
   double lX = rand.nextDouble( (GameApp.size.getX() - (int)cX + cX));

   pond.setTranslateX(lX);
   add(pond);
  }

  @Override
  public void update() {

  }

 }

 class Cloud extends GameObject {
 public Cloud(){
  Random rand = new Random();

  Circle cloud = new Circle(50);
  cloud.setFill(Color.WHITE);
  double cX = cloud.getRadius();
  double lX = rand.nextDouble( (GameApp.size.getX() - (int)cX)) + cX;
  cloud.setTranslateX(lX);
  add(cloud);

 }
  @Override
  public void update() {

  }

 }

 class Helipad extends GameObject {
  public Helipad(){
   Rectangle base = new Rectangle(75,75);
   Circle circle = new Circle(25);
   circle.setTranslateX(75/2);
   circle.setTranslateY(75/2);
   base.setFill(Color.GRAY);
   base.setStroke(Color.WHITE);
   circle.setFill(Color.GRAY);
   circle.setStroke(Color.WHITE);
   add(base);
   add(circle);
  }

  @Override
  public void update() {

  }

 }

 class Helicopter extends GameObject {
 //crice and a line
 public Helicopter(){
  Circle base = new Circle(10);
  base.setFill(Color.YELLOW);
  Line line = new Line();
  line.setStartY(0);
  line.setEndY(35);
  line.setStroke(Color.YELLOW);
  line.setStrokeWidth(2);
  add(base);
  add(line);
 }
  @Override
  public void update() {

  }
 }

 class PondAndCloud {

 }

 interface Updatable {
  void update();
 }

 /**
  * Sets up all the keyboard events  handlers to invoke public methods
  * in the game
  */
 public class GameApp extends Application {
  static Point2D size = new Point2D(500, 800);

  @Override
  public void start(Stage stage) throws Exception {
   Game gameWindow = new Game();
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

  public static void main(String[] args) {launch(args);}

 }



