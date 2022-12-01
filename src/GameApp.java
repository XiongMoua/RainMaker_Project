import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
/*
Class notes: Use random.nextGaussian
 */
interface Updateable{
 void update();
}
class Game extends Pane{
 private Helicopter helicopter = new Helicopter();
 private Helipad helipad = new Helipad();
 private Cloud cloud = new Cloud();
 private Pond pond = new Pond();
 boolean OnHelipad = false;
 boolean overCloud = false;
 boolean showBorders = false;
 double bladeSpeed = 0;
 int counter = 0;
 int counter2 = 0;
 public Game(){
  this.setBackground(Background.fill(Color.BLACK));
  this.setScaleY(-1);
  this.getChildren().addAll(pond,cloud,helipad,helicopter);
 }
 public void changeHelicopterStateToStart(){
  helicopter.changeState(new Starting(helicopter));
 }

 public void increaseBladeSpeed(){
  counter2++;
  if(counter2 % 5 == 0){
   bladeSpeed+=2;
   helicopter.rotateBlades(bladeSpeed);
  }
 }
 public void run(){
  //iteration make it work
  HelicopterIntersections();
  ShowBorders(showBorders);
  increaseSaturation();

 }

 public void rotateBlades(){
  if(GameApp.EngineState()){
    helicopter.helibladeTest();
  }

 }

 public void lose(){
  if(helicopter.returnFuel() ==0){
   GameApp.showAlertLost();
  }
 }
 public void win(){
  if(pond.PondPercentage == 100 && helicopter.returnFuel() > 0 && GameApp.EngineState() == false){
   System.out.println("You won");
   GameApp.showAlertWin();
  }
 }
 public void increaseSaturation(){
  cloud.Saturation();
 }
 public void decreaseSaturation(){
  cloud.decreaseSaturation();
  if(cloud.CloudPercentage > 30){
   pond.area();
  }
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

 public void HelicopterIntersections(){
  OnHelipad = helicopter.getBoundsInParent().intersects(helipad.getBoundsInParent());
  overCloud = helicopter.getBoundsInParent().intersects(cloud.getBoundsInParent());
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
 boolean seeded = false;
 int counter = 0;

 double radius, CloudRadius,upperBound,lowerBound,LocationX, LocationY;
 public Cloud(){
  resetSpawn();
 }
 public void resetSpawn(){
  radius=rand.nextInt(max-min)+min;
  cloud = new Circle(radius);
  cloud.setFill(Color.WHITE);
  CloudPercentage=0;
  CloudRadius = cloud.getRadius();
  percentage = new Label();
  percentage.setText(""+CloudPercentage);
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
 public void Saturation(){
  counter++;
  if(counter %60 == 0){
   increaseSaturation();
  }
 }
 public void increaseSaturation(){
  if(rgb1 < 255){
   rgb1++;
   rgb2++;
   rgb3++;
  }
  if(CloudPercentage>0){
   CloudPercentage--;
  }
  cloud.setFill(Color.rgb(rgb1,rgb2,rgb3));
  percentage.setText(""+CloudPercentage);
 }
 public void decreaseSaturation(){
  if(rgb1 > 155){
   rgb1--;
   rgb2--;
   rgb3--;
  }
  if(CloudPercentage<100){
   CloudPercentage++;
  }
  System.out.println(rgb1);
  System.out.println("reducing saturation");
  cloud.setFill(Color.rgb(rgb1,rgb2,rgb3));
  percentage.setText(""+CloudPercentage);
 }
}
class Pond extends GameObject{
 private Circle pond;
 Random rand = new Random();
 int min=25;
 int max=35;
 double p;
 Label percentage;
 double upperBound, lowerBound, LocationX,
   LocationY, PondRadius, PondPercentage;
 double radius;
 public Pond(){
  resetSpawn();
 }
 public void resetSpawn(){
  radius=rand.nextInt(max-min)+min;
  pond = new Circle(radius);
  pond.setFill(Color.BLUE);
  PondRadius = pond.getRadius();
  PondPercentage=PondRadius;
  percentage = new Label();
  percentage.setText(""+PondPercentage);
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
  Scale test = new Scale();
  double x = 1;
  double y=1;
  pond.getTransforms().add(test);
 }
 public void area(){
  Scale test = new Scale();
  double x = 1;
  double y=1;
  test.setX(x+=0.01);
  test.setY(y+=0.01);
  if(PondPercentage<=99){
   pond.getTransforms().add(test);
   percentage.setText(""+(PondPercentage+=1));
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
 protected double rotationSpeed = 0;
 protected double velocity;
 public HelicopterState(Helicopter helicopter){
  this.helicopter = helicopter;
 }
 abstract void decreaseFuel();
 abstract boolean intersect();
}
class Helicopter_Blades extends GameObject implements Updateable{
  double rotation = 0;
  int counter = 0;
 public Helicopter_Blades(){
  Line blade1 = new Line();
  blade1.setStartX(0);
  blade1.setStartY(0);
  blade1.setEndX(25);
  blade1.setEndY(25);
  blade1.setStrokeWidth(2);
  Line blade2 = new Line();
  blade2.setStartX(0);
  blade2.setStartY(0);
  blade2.setEndX(25);
  blade2.setEndY(-25);
  blade2.setStrokeWidth(2);
  this.getChildren().addAll(blade1,blade2);
 }


 @Override
 public void update() {
  counter++;
  //counter % 5 ==0
  if(counter % 10 == 0){
    if(rotation < 50)
      rotation+=1;
    
  }
  super.setRotate(super.getRotate()+rotation);
 }
}
class OffState extends HelicopterState{
 public OffState(Helicopter helicopter) {
  super(helicopter);
  update();
  System.out.println("currently in off state");
  System.out.println(GameApp.EngineState());
 }

 @Override
 void decreaseFuel() {
 }
 @Override
 boolean intersect() {
  return false;
 }
 public String toString(){
  return "OffState";
 }

 @Override
 public void update() {

 }
}
class Starting extends HelicopterState{

 public Starting(Helicopter helicopter) {
  super(helicopter);
  update();
  System.out.println("Currently in starting state");
 }
 public String toString(){
  return "StartingState";
 }

 @Override
 public void update() {

 }
 @Override
 void decreaseFuel() {

 }
 @Override
 boolean intersect() {
  return false;
 }
}
class Stopping extends HelicopterState{
 public Stopping(Helicopter helicopter) {
  super(helicopter);
 }
 @Override
 public void update() {

 }
 @Override
 void decreaseFuel() {

 }
 @Override
 boolean intersect() {
  return false;
 }
}
class Ready extends HelicopterState{
 double velocity;
 public Ready(Helicopter helicopter) {
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
  super.helicopter.setLayoutX(super.helicopter.updateLocationX(velocity));
  super.helicopter.setLayoutY(super.helicopter.updateLocationY(velocity));
 }
}
class Helicopter extends GameObject{
 private int ignition;
HelicopterState state;
 private Label fuel;
 private double posX = 0;
 boolean reset = false;
 private double posY = 0;
 public double rotation = 0;
 private Helipad helipad = new Helipad();
 double bladeRotate = 0;
 boolean onFuel = false;
 double velocity = 0;
 Helicopter_Blades blades = new Helicopter_Blades();
 public Helicopter(){
  Circle base = new Circle(10);
  base.setFill(Color.YELLOW);
  Line line = new Line();
  line.setStartY(0);
  line.setEndY(35);
  line.setStroke(Color.YELLOW);
  line.setStrokeWidth(2);
  line.setTranslateY(-10);
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
  addToObject(blades);
  state = new OffState(this);
  System.out.println(state);
 }
 public int returnFuel() {
  return ignition;
 }

 public boolean reset(){
  return reset = true;
 }
 public void helibladeTest(){
  blades.update();
 }
 public void resetSpawn(){
  reset();
  super.setLayoutY(GameApp.YValueGameWindow()/8);
  super.setLayoutX(GameApp.XValueGameWindow()/2-16.5);
  rotation =0;
  super.setRotate(rotation);
  ignition = 25000;
  fuel.setText(""+ignition);
  onFuel=false;

 }

 public void changeState(HelicopterState state){
  this.state = state;
 }
 public String returnState(){
  return state.toString();
 }
 public void rotateLeft(){
  rotation += 15;
 }
 public void rotateRight(){
  rotation -= 15;
 }
 public double rotateBlades(double speed){
  if(blades.getRotate() < 2000){
   blades.setRotate(blades.getRotate()+speed);
  }
  else{
   blades.setRotate(2000);
  }

  System.out.println(bladeRotate);
  return bladeRotate;
 }
 public double updateLocationX(double velocity){
  posX = Math.sin(Math.toRadians(-rotation))*velocity;
  return super.getLayoutX()+posX;
 }
 public double updateLocationY(double velocity){
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
  if(GameApp.EngineState()){
   if(ignition>0){
    ignition -= 2;
   }
   fuel.setText(""+ignition);
  }

 }
}
public class GameApp extends Application {
 private static boolean heliEngineOn = false;
 private static Point2D size = new Point2D(500, 800);
 private static Alert LostAlert;
 private static Alert WinAlert;
 private static AnimationTimer loop;
 public static void main(String[] args){launch(args);}
 @Override
 public void start(Stage stage) throws Exception {
  Game gameWindow = new Game();
  Scene scene = new Scene(gameWindow, size.getX(), size.getY());
  stage.setTitle("RainMaker");
  stage.setResizable(false);
  stage.setScene(scene);
  LostAlert = new Alert(Alert.AlertType.CONFIRMATION);
  LostAlert.setTitle("Confirmation");
  LostAlert.setContentText("You have Lost! Play Again?");
  LostAlert.setHeaderText("Confirmation");
  ButtonType buttonPlayAgain = new ButtonType("Yes");
  ButtonType NotPlayAgain = new ButtonType("No");
  LostAlert.getButtonTypes().set(0,buttonPlayAgain);
  LostAlert.getButtonTypes().set(1,NotPlayAgain);
  LostAlert.setOnCloseRequest(e->{
   ButtonType result = LostAlert.getResult();
   if (result != null && result == buttonPlayAgain) {
    System.out.println("Play Again!");
    gameWindow.restartGame();
    LostAlert.close();
    heliEngineOn=false;
    loop.start();
   } else {
    System.out.println("CLosing");
    heliEngineOn=false;
    LostAlert.close();
    stage.close();
    System.exit(0);
   }
  });

  WinAlert = new Alert(Alert.AlertType.CONFIRMATION);
  WinAlert.setTitle("Confirmation");
  WinAlert.setContentText("You Won! Play Again?");
  WinAlert.setHeaderText("Confirmation");
  WinAlert.getButtonTypes().set(0,buttonPlayAgain);
  WinAlert.getButtonTypes().set(1,NotPlayAgain);
  WinAlert.setOnCloseRequest(e->{
   ButtonType result = WinAlert.getResult();
   if (result != null && result == buttonPlayAgain) {
    System.out.println("Play Again!");
    gameWindow.restartGame();
    WinAlert.close();
    heliEngineOn=false;
    loop.start();
   } else {
    System.out.println("CLosing");
    heliEngineOn=false;
    WinAlert.close();
    stage.close();
    System.exit(0);
   }
  });
  AnimationTimer bladesRotate = new AnimationTimer() {

    @Override
    public void handle(long now) {
      gameWindow.rotateBlades();
      
    }
    
  };


  scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
   @Override
   public void handle(KeyEvent event) {
    if(event.getCode() == KeyCode.I && gameWindow.OnHelipad){
     System.out.println("Clicked on I");
     bladesRotate.start();
     gameWindow.changeHelicopterStateToStart();
//     if(Math.round(gameWindow.HelicopterSpeed()) == 0){
      startEngine();
      
     //}
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
     startEngine();
    }
   }
  });
  scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
   @Override
   public void handle(KeyEvent event) {
    if(event.getCode() == KeyCode.SPACE){

    }
   }
  });
  gameWindow.increaseSaturation();

  //idea for timeline: have multiple timelines to take care of the speed
  //or just have only 1 and the speed of the blades depend on the how many seconds have passed
  //try the second one first

  loop = new AnimationTimer() {
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
 public static void showAlertLost(){
  loop.stop();
  LostAlert.show();
 }
 public static void showAlertWin(){
  loop.stop();
  WinAlert.show();
 }
}