import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.Random;
interface Updateable{
 void update();
}
class Game extends Pane {
 private Helicopter helicopter = new Helicopter();
 private Helipad helipad = new Helipad();
 private Cloud cloud = new Cloud();
 private Pond pond = new Pond();
 boolean OnHelipad = false;
 boolean overCloud = false;
 boolean showBorders = false;
 public Game(){
  this.setBackground(Background.fill(Color.BLACK));
  this.setScaleY(-1);
  this.getChildren().addAll(pond,cloud,helipad,helicopter);
 }
 public void run(){
  HelicopterIntersections();
  ShowBorders(showBorders);
  cloud.increaseSaturation();
 }
 public void restartGame(){
  pond.resetSpawn();
  cloud.resetSpawn();
  helicopter.resetSpawn();
 }

 public void makeVisible(){
  showBorders =!showBorders;
  System.out.println(showBorders);
 }
 private void ShowBorders(boolean show){
  if(show){
   helicopter.setBorder(Border.stroke(Color.WHITE));
   helipad.setBorder(Border.stroke(Color.WHITE));
   pond.setBorder(Border.stroke(Color.WHITE));
   cloud.setBorder(Border.stroke(Color.WHITE));
  }
  else if(show==false){
   helicopter.setBorder(Border.stroke(Color.TRANSPARENT));
   helipad.setBorder(Border.stroke(Color.TRANSPARENT));
   pond.setBorder(Border.stroke(Color.TRANSPARENT));
   cloud.setBorder(Border.stroke(Color.TRANSPARENT));

  }
 }

 public void decreaseSaturation(){
  cloud.decreaseSaturation();
  if(cloud.CloudPercentage>30){
   pond.area();
  }
 }

 public void HelicopterIntersections(){
  OnHelipad = helicopter.getBoundsInParent().intersects(helipad.getBoundsInParent());
  overCloud = helicopter.getBoundsInParent().intersects(cloud.getBoundsInParent());
  if(GameApp.EngineState()){
   helicopter.changeState(new RunningState(helicopter));
  }
 }
 public void increaseHelicopterVelocity(){
  helicopter.increaseVelocity();
 }
 public void decreaseHelicopterVelocity(){
  helicopter.decreaseVelocity();
 }

 public void rotateHelicopterLeft(){
  helicopter.rotateLeft();
 }
 public void rotateHelicopterRight(){
  helicopter.rotateRight();
 }
}
class Cloud extends GameObject{
 private Circle cloud;
 int min = 35;
 int max = 50;
 int CloudPercentage=0;
 int rgb1 = 255, rgb2 = 255, rgb3 = 255;
 Label percentage;
 Random rand = new Random();
 double radius, CloudRadius,upperBound,lowerBound,LocationX, LocationY;

 public Cloud(){
  resetSpawn();
 }

 public void resetSpawn(){
  radius=rand.nextInt(max-min)+min;
  cloud = new Circle(radius);
  cloud.setFill(Color.WHITE);
  CloudRadius = cloud.getRadius();
  percentage = new Label();
  percentage = new Label();
  percentage.setText(""+CloudRadius);
  percentage.setTextFill(Color.BLACK);
  super.flipLabel(percentage);
  upperBound=GameApp.YValueGameWindow()-CloudRadius;
  lowerBound=GameApp.YValueGameWindow()/2+CloudRadius;
  LocationX = rand.nextDouble( ((GameApp.XValueGameWindow()
    -CloudRadius)-CloudRadius)+CloudRadius);
  LocationY = rand.nextDouble(upperBound - lowerBound) + lowerBound;
  super.setLayoutX(LocationX);
  super.setLayoutY(LocationY);
  addToObject(cloud);
  addToObject(percentage);
  System.out.println("Pond radius: " + radius);

 }

 private void increasePercentage(){
  if(CloudPercentage <= 99){
   CloudPercentage++;
   percentage.setText(""+CloudPercentage);
  }
 }

 public void increaseSaturation(){
  if(rgb1<255){
   rgb1+=1;
   rgb2+=1;
   rgb3+=1;
  }
 }

 public void decreaseSaturation(){
  if(rgb1 >= 155){
   rgb1 -=2;
   rgb2-=2;
   rgb3-=2;
  }
  increasePercentage();
  System.out.println(rgb1);
  cloud.setFill(Color.rgb(rgb1,rgb2,rgb3));
 }

}
class Pond extends GameObject{
 private Circle pond;
 Random rand = new Random();
 int min=25;
 int max=35;
 double p;
 Label percentage;
 double upperBound, lowerBound, LocationX, LocationY, PondRadius;
 double radius;

 public Pond(){
  resetSpawn();
 }
 public void resetSpawn(){
  radius=rand.nextInt(max-min)+min;
  pond = new Circle(radius);
  pond.setFill(Color.BLUE);
  PondRadius = pond.getRadius();
  percentage = new Label();
  percentage.setText(""+PondRadius);
  percentage.setTextFill(Color.WHITE);
  super.flipLabel(percentage);
  upperBound=GameApp.YValueGameWindow()-PondRadius;
  lowerBound=GameApp.YValueGameWindow()/2+PondRadius;
  LocationX = rand.nextDouble( ((GameApp.XValueGameWindow()
    -PondRadius)-PondRadius)+PondRadius);
  LocationY = rand.nextDouble(upperBound - lowerBound) + lowerBound;
  super.setLayoutX(LocationX);
  super.setLayoutY(LocationY);
  addToObject(pond);
  addToObject(percentage);
  System.out.println("Pond radius: " + radius);

 }

 public void area(){
  Scale test = new Scale();
  double x = 1;
  double y=1;
  test.setX(x+=0.01);
  test.setY(y+=0.01);
  if(p<=99){
   pond.getTransforms().add(test);
   percentage.setText(""+(p+=1));
  }
 }
}
abstract class GameObject extends StackPane {
 private Translate translate;
 private Rotate rotation;
 private Scale scale;
 public GameObject(){
  translate = new Translate();
  rotation = new Rotate();
  scale = new Scale();
  this.getTransforms().addAll(translate, rotation, scale);
 }
 public void addToObject(Node node){this.getChildren().add(node);}
 public void flipLabel(Label label){
  label.setScaleY(-1);
 }

}
class Helipad extends GameObject{
 private Rectangle base;
 private Circle circle;
 public Helipad(){
  base = new Rectangle(75,75);
  circle = new Circle(25);
  base.setFill(Color.GRAY);
  circle.setFill(Color.GRAY);
  circle.setStroke(Color.WHITE);
  super.addToObject(base);
  super.addToObject(circle);
  super.setLayoutX(GameApp.XValueGameWindow()/2 - base.getWidth()/2);
  super.setLayoutY(GameApp.YValueGameWindow()/10);
 }
}
abstract class HelicopterState implements Updateable{
 protected Helicopter helicopter;
 public HelicopterState(Helicopter helicopter){
  this.helicopter = helicopter;
 }
 abstract void decreaseFuel();
 abstract boolean intersect();
}
class OffState extends HelicopterState{
 public OffState(Helicopter helicopter) {
  super(helicopter);
  update();
 }
 @Override
 void decreaseFuel() {

 }
 @Override
 boolean intersect() {
  return false;
 }

 @Override
 public void update() {

 }
}
class RunningState extends HelicopterState{
 public RunningState(Helicopter helicopter) {
  super(helicopter);
  decreaseFuel();
  update();
 }
 @Override
 void decreaseFuel() {
  helicopter.RunningOnFuel();
 }
 @Override
 boolean intersect() {
  return false;
 }
 @Override
 public void update() {
  super.helicopter.setRotate(super.helicopter.rotation);
  super.helicopter.setLayoutX(super.helicopter.updateLocationX());
  super.helicopter.setLayoutY(super.helicopter.updateLocationY());
 }
}
class Helicopter extends GameObject{
 private int ignition;
 private HelicopterState state;
 private Label fuel;
 private double velocity = 0;
 private double posX = 0;
 private double posY = 0;
 public double rotation = 0;
 private Helipad helipad = new Helipad();
 public Helicopter(){
  Circle base = new Circle(10);
  base.setFill(Color.YELLOW);
  Line line = new Line();
  line.setStartY(0);
  line.setEndY(35);
  line.setStroke(Color.YELLOW);
  line.setStrokeWidth(2);
  line.setTranslateY(10);
  addToObject(base);
  addToObject(line);
  super.setLayoutY(GameApp.YValueGameWindow()/8);
  super.setLayoutX(GameApp.XValueGameWindow()/2-16.5);
  ignition = 25000;
  fuel = new Label();
  fuel.setText(""+ignition);
  flipLabel(fuel);
  fuel.setTranslateY(-(base.getRadius()*2));
  fuel.setTextFill(Color.YELLOW);
  addToObject(fuel);
 }
 public void resetSpawn(){


 }
 public void changeState(HelicopterState state){
  this.state = state;
 }
 public void rotateLeft(){
  rotation += 15;
 }
 public void rotateRight(){
  rotation -= 15;
 }
 public double updateLocationX(){
  posX = Math.sin(Math.toRadians(-rotation))*velocity;
  return super.getLayoutX()+posX;
 }
 public double updateLocationY(){
  posY = Math.cos(Math.toRadians(-rotation))*velocity;
  return super.getLayoutY()+posY;
 }
 public void increaseVelocity(){
  if(velocity <= 10){
   velocity+=0.1;
  }
 }
 public void decreaseVelocity(){
  if(velocity >= -2){
   velocity -= 0.1;
  }
 }
 public void RunningOnFuel(){
  ignition -= 1;
  fuel.setText(""+ignition);
 }
}
public class GameApp extends Application {
 private static boolean heliEngineOn = false;
 private static Point2D size = new Point2D(500, 800);
 @Override
 public void start(Stage stage) throws Exception {
  Game gameWindow = new Game();
  Scene scene = new Scene(gameWindow, size.getX(), size.getY());
  stage.setTitle("RainMaker");
  stage.setResizable(false);
  stage.setScene(scene);
  scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
   @Override
   public void handle(KeyEvent event) {
    if(event.getCode() == KeyCode.I && gameWindow.OnHelipad){
     startEngine();
    }
    if(event.getCode() == KeyCode.W && heliEngineOn){
     gameWindow.increaseHelicopterVelocity();
    }
    if(event.getCode() == KeyCode.D && heliEngineOn){
     gameWindow.rotateHelicopterRight();
    }
    if(event.getCode() == KeyCode.A && heliEngineOn){
     gameWindow.rotateHelicopterLeft();
    }
    if(event.getCode() == KeyCode.S && heliEngineOn){
     gameWindow.decreaseHelicopterVelocity();
    }
    if (event.getCode() == KeyCode.B) {
     gameWindow.makeVisible();
    }
    if(event.getCode() == KeyCode.SPACE && gameWindow.overCloud) {
     gameWindow.decreaseSaturation();
    }
    if(event.getCode() == KeyCode.R){
     gameWindow.restartGame();
    }
   }
  });
  AnimationTimer loop = new AnimationTimer() {
   @Override
   public void handle(long now) {
    gameWindow.run();
   }
  };
  loop.start();
  stage.show();
 }
 private void startEngine(){
  heliEngineOn = !heliEngineOn;
 }
 public static boolean EngineState(){
  return heliEngineOn;
 }

 public static double XValueGameWindow(){
  return size.getX();
 }
 public static double YValueGameWindow(){
  return size.getY();
 }
}