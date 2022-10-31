import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

interface Updatable {
 void update();
}

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
  this.setBackground(new Background(new BackgroundFill(Color.BLACK,new CornerRadii(0), Insets.EMPTY)));
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

 public void startIgnition(boolean start){
  if(start){
   helicopter.startIgnition();
  }else{

  }
 }
 public void moving(boolean move){
  if(move){
   helicopter.setTranslateY(helicopter.getTranslateY() + 1.25);
  }

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
 //cricle and a line
  private boolean onFuel = false;
  private int ignition;
  private Label fuel;
  //positins
  double pY;
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
   ignition = 20000;
   fuel = new Label();
   int amountofFuel = 20000;
   fuel.setText(""+amountofFuel);
   flipLabel(fuel);
   fuel.setTranslateX(-15);
   fuel.setTranslateY(-(base.getRadius()*2));
   fuel.setTextFill(Color.YELLOW);
   add(fuel);
   pY = this.getLayoutY();
  }

  private void flipLabel(Label label){
   label.setScaleY(-1);
  }

  public void startIgnition(){
   onFuel = true;
   ignition -=1;
   fuel.setText(""+ignition);
  }
  public void stopIgnition(){
   onFuel = false;
  }

   @Override
   public void update() {
    pY = pY * 1.25;
    super.getTransforms().clear();
    super.getTransforms().addAll(new Translate(this.getLayoutX(), pY));

   }

 }

 class PondAndCloud {

 }



 /**
  * Sets up all the keyboard events  handlers to invoke public methods
  * in the game
  */
 public class GameApp extends Application {
  static Point2D size = new Point2D(500, 800);
  //setting up the keys so that it won't keep pressing when a key is
  //pressed down
  Set<KeyCode> keysDown= new HashSet<>();
  int key(KeyCode k){
    return keysDown.contains(k) ? 1:0;
  }
  private boolean start = false;
  private boolean move = false;

  @Override
  public void start(Stage stage) throws Exception {
   Game gameWindow = new Game();
   Scene scene = new Scene(gameWindow, size.getX(), size.getY());
   scene.setFill(Color.BLACK);
   stage.setTitle("RainMaker");
   stage.setScene(scene);

   //setup the keys pressed when inside of the scene
   scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
    @Override
    public void handle(KeyEvent event) {
     keysDown.add(event.getCode());
     System.out.println("pressing on "+ event.getCode());
     if(event.getCode() == KeyCode.I){
      start();
     }
     else if(event.getCode() == KeyCode.W && start){
      move();
     }
    }
   });

   scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
    @Override
    public void handle(KeyEvent event) {
     keysDown.remove(event.getCode());
     System.out.println("removed "+ event.getCode());
    }
   });

   //animation starter
   AnimationTimer loop = new AnimationTimer() {
    @Override
    public void handle(long now) {
     gameWindow.startIgnition(start);
     if(move()){
      gameWindow.moving(move);
     }

    }
   };
   loop.start();
   stage.show();
  }

  private boolean start(){
   return start = !start;
  }

  private boolean move(){
   return move = !move;
  }


  public static void main(String[] args) {launch(args);}

 }



