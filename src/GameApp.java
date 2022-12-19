import java.util.*;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
interface Updatable{
  void update();
}
interface Subject{
  public void Register(Observer obj);
  public void UnRegister(Observer obj);
  public void notifyObservers();
  public Object getUpdates(Observer obj);
}
interface Observer{
  public void Update();
  public void setSubject(Subject sub);
}
class GameBackground extends Pane{
  private Image background = new Image("file:lib/background-image.png", 800,
  800,false,false);
  public GameBackground(){
    super.setScaleY(-1);
    super.getChildren().add(new ImageView(background));
  }
}
class CloudPane extends Pane{

}
class Distance extends Pane{
  private List<Pond> pondList;
  private List<Cloud> cloudList;
  public Distance(List<Pond> pondList, List<Cloud>cloudList){
    this.pondList = pondList;
    this.cloudList = cloudList;
  }
}
class Game extends Pane{
  private Random rand = new Random();
  private boolean OnHelipad = false;
  private static int ignition = 25000;
  private static Helipad helipad = new Helipad();
  private static Helicopter helicopter = new Helicopter(ignition, helipad);
  private List<Cloud> cloudlist = new ArrayList<>();
  private CloudPane clouds = new CloudPane();
  private List<Pond> pondlist = new ArrayList<>();
  private int CloudCount=rand.nextInt(5-4)+4;
  private boolean conditionOne,conditionTwo;
  private static int winConditionOne;
  private boolean ShowBorder;
  private Blimp blimp = new Blimp();
  private static Rectangle InBoundBox=new Rectangle(
    GameApp.XValueGameWindow(),
    GameApp.YValueGameWindow()
  );
  private GameBackground background = new GameBackground();
  private Distance distance = new Distance(pondlist, cloudlist);
  public Game(){
    ShowBorder=false;
    conditionOne = false; conditionTwo = false; winConditionOne=0;
    this.setBackground(Background.fill(Color.BLACK));
    this.setScaleY(-1);
    this.getChildren().addAll(InBoundBox,background);
    createPonds();
    this.getChildren().addAll(clouds,distance,helipad,blimp,helicopter);
  }
  public void run(){
    HelicopterIntersections();
    changeToReadyState();
    helicopter.update();
    helicopter.decreaseFuel();
    helicopter.updateFuel();
    createClouds();
    cloudChangeState();
    CloudNotSeeded();
    DeleteClouds();
    distance();
    lose();
    win();
    blimp.update();
    blimp.UpdateFuel();
    blimp.reset();
    blimpChangeState();
  }
  public void reset(){
    helicopter.reset();
    blimp.resetBlimp();
    for(Cloud cloud:cloudlist){
      cloud.reset();
    }
    for(Pond pond:pondlist){
      pond.reset();
    }
  }
  public void MakeBorderVisible(){
    ShowBorder = !ShowBorder;
  }
  public void ShowBorder(boolean ShowBorder){
    if(ShowBorder){
      helicopter.setBorder(Border.stroke(Color.WHITE));
    }
  }
  public void win(){
    if(pondlist.get(0).returnPondPercentage() >=80
        && pondlist.get(1).returnPondPercentage() >=80
        && pondlist.get(2).returnPondPercentage()>=80
    ){
      conditionOne=true;
    }

    if(helicopter.currentState().equals("OffState")&&OnHelipad){
      conditionTwo=true;
    }
    if(conditionOne && conditionTwo){
      GameApp.showAlertWin();
    }
  }
  public void lose(){
    if(helicopter.HandleFuel() <= 0){
      GameApp.showAlertLost();
    }
  }
  public void distance(){
    for(Cloud cloud:cloudlist){
      
      for(Pond pond:pondlist){
        if(cloud.allowed(pond)&&cloud.IsSeed()==true){
          pond.rainfall();
          pond.increaseArea();
        }
        else{
          pond.norain();
        }
      }
    }
  } 
  public void CloudBeingSeeded(){
    for(Cloud cloud:cloudlist){
      for(Pond pond:pondlist){
        if(CloudIntersectHelicopter(cloud, helicopter)){
          cloud.beingSeeded();
        }
      }
    }
  }
  public void CloudNotSeeded(){
    for(Cloud cloud:cloudlist){
      cloud.notSeeded();
    }
  }
  public void cloudChangeState(){
    for(Cloud cloud:cloudlist){
      cloud.CloudStateChanged();
    }
  }
  public void createPonds(){
    for(int i = 0; i < 3; i++){
      Pond pond = new Pond(cloudlist);
      pondlist.add(pond);
      this.getChildren().add(pond);
    }
  }
  public void createClouds(){
    if(cloudlist.size() <= CloudCount){
      for(int i = cloudlist.size(); i<CloudCount;i++){
        Cloud cloud = new Cloud(pondlist);
        cloudlist.add(cloud);
        clouds.getChildren().add(cloud);
        for(Pond pond:pondlist){
          cloud.Register(pond);
          pond.setSubject(cloud);
        }
        cloud.Register(helicopter);
        helicopter.setSubject(cloud);
      }
    }
  }
  public void DeleteClouds(){
    for (Cloud clouds : cloudlist) {
      clouds.update();
      clouds.intersect();
      clouds.HandleState();
      if(clouds.CurrentState().equals("OffStateRight")){
        cloudlist.remove(clouds);
      }
    }
  }
  public static boolean BlimpIntersect(Blimp blimp){
    return blimp.getBoundsInParent().intersects(InBoundBox.getBoundsInParent());
  }
  public void blimpChangeState(){
    blimp.HandleState();
  }
  public static boolean CloudIntersect(Cloud cloud){
    return cloud.getBoundsInParent().intersects(InBoundBox.getBoundsInParent());
  }
  public static boolean BlimpIntersectHelicopteer(Blimp blimp, Helicopter helicopter){
    return blimp.getBoundsInParent().intersects(helicopter.getBoundsInParent());
  }
  public static boolean CloudIntersectHelicopter(Cloud cloud, Helicopter helicopter){
    return cloud.getBoundsInParent().intersects(helicopter.getBoundsInParent());
  }
  public void increaseHelicopterSpeed(){
    helicopter.increaseVelocity();
  }
  public void decreaseHelicopterSpeed(){
    helicopter.decreaseVelocity();
  }
  public void rotateHelicopterLeft(){
    helicopter.rotateLeft();
   }
  public void rotateHelicopterRight(){
    helicopter.rotateRight();
  }
  public void changeStateToStarting(){
    helicopter.toggleReset();
    if(helicopter.currentState().equalsIgnoreCase("OffState") 
      || helicopter.currentState().equalsIgnoreCase("StopState"))
    {
      helicopter.changeState(new StartingState(helicopter,ignition));
      helicopter.turnEngineOn();
    }
    else if(helicopter.currentState().equalsIgnoreCase("StartingState")){
      helicopter.turnEngineOn();
      helicopter.changeState(new StoppingState(helicopter,ignition));
    }
    else if(helicopter.currentState().equalsIgnoreCase("ReadyState") && OnHelipad){
      helicopter.changeState(new StoppingState(helicopter,ignition));
      helicopter.turnEngineOn();
    }
  }
  public void changeToReadyState(){
    if(helicopter.currentState().equalsIgnoreCase("StartingState") && helicopter.returnBladeSpeed()==25){
      helicopter.changeState(new ReadyState(helicopter,ignition));
    }
    else if(helicopter.currentState().equalsIgnoreCase("StopState") && helicopter.returnBladeSpeed() ==0){
      helicopter.changeState(new OffState(helicopter,ignition));
    }
  }
  public void HelicopterIntersections(){
    OnHelipad = helicopter.getBoundsInParent().intersects(helipad.getBoundsInParent());
  }
  public static int getFuel(){
    return ignition;
  }
  public int WinScore(){
    return helicopter.HandleFuel();
  }
  public static void stopBladeRotation(){
    helicopter.stopBladeRotation();
  }
}

class GameObject extends StackPane{
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

class Pond extends GameObject implements Observer{
  private Circle Pond= new Circle();
  private double MaxRadius=30;
  private double MinRadius=10;
  private double MinXPosition=100;
  private double MaxXPosition = 700;
  private double MinYPosition = 300;
  private double MaxYPosition = 700;
  private Random rand = new Random();
  private List<Subject> cloud;
  private int pondPercentage;
  private Label Percentage;
  private List<Cloud>cloudlist;
  private double radius;
  private int counter=0;
  private boolean raining = false;
  public Pond(List<Cloud>cloudlist){
    this.cloudlist=cloudlist;
    cloud=new ArrayList<>();
    radius = RandomPondRadius();
    Pond.setRadius(radius);
    pondPercentage=(int)radius;
    Percentage = new Label();
    Percentage.setText(""+pondPercentage+"%");
    Percentage.setScaleY(-1);
    Pond.setFill(Color.web("#CDF0F9"));
    Pond.setScaleY(-1);
    addToObject(Pond);
    addToObject(Percentage);
    super.setLayoutX(RandomXPosition());
    super.setLayoutY(RandomYPosition());
  }
  public void reset(){
    super.getChildren().clear();
    radius = RandomPondRadius();
    Pond.setRadius(radius);
    pondPercentage=(int)radius;
    Percentage = new Label();
    Percentage.setText(""+pondPercentage+"%");
    Percentage.setScaleY(-1);
    Pond.setFill(Color.web("#CDF0F9"));
    Pond.setScaleY(-1);
    addToObject(Pond);
    addToObject(Percentage);
    super.setLayoutX(RandomXPosition());
    super.setLayoutY(RandomYPosition());
  }
  public int returnPondPercentage(){
    return pondPercentage;
  }
  public boolean returnRain(){
    return raining;
  }
  public boolean rainfall(){
    return raining=true;
  }
  public boolean norain(){
    return raining = false;
  }
  public double returnX(){
    return super.getLayoutX();
  }
  public double returnY(){
    return super.getLayoutY();
  }
  public Circle returnPond(){
    return Pond;
  }
  public double returnRadius(){
    return Pond.getRadius();
  }
  public double RandomPondRadius(){
    return rand.nextDouble(MaxRadius-MinRadius)+MinRadius;
  }
  public double RandomXPosition(){
    return rand.nextDouble(MaxXPosition-MinXPosition)+MinXPosition;
  }
  public double RandomYPosition(){
    return rand.nextDouble(MaxYPosition-MinYPosition)+MinYPosition;
  }
  @Override
  public void Update() {
    
  }
  @Override
  public void setSubject(Subject sub) {
    cloud.add(sub); 
  }
  public void increaseArea(){
    counter++;
    if(raining){
      if(counter%60==0){
        Scale test = new Scale();
        double x = 1;
        double y=1;
        test.setX(x+=0.01);
        test.setY(y+=0.01);
        if(pondPercentage<=99){
        Pond.getTransforms().add(test);
        Percentage.setText(""+(pondPercentage+=1)+"%");
        }
    }
    }
  }

}

abstract class CloudState implements Updatable{
  private Cloud cloud;
  private static boolean InPlay = false;
  private final static double WindSpeed = 1;
  private static int counter = 0;
  public CloudState(Cloud cloud){
    this.cloud = cloud;
  }

  public abstract String toString();
  public void DeleteCloud(){
    cloud = null;
  }
  public Cloud returnCloud(){
    return cloud;
  }
  public void increaseCounter(){
    counter++;
  }
  public int GetCounter(){
    return counter;
  }
  public double WINDSPEED(){
    return WindSpeed;
  }
  public abstract boolean intersect();
  public abstract void updateLabel();
  public abstract void beingSeeded();
  public abstract void notSeeded();
  public abstract double returnX();
  public abstract double returnY();
  
}
class OutOfPlayStateLeft extends CloudState{

  public OutOfPlayStateLeft(Cloud cloud) {
    super(cloud);
   
  }
  @Override
  public void update() {
    super.increaseCounter();
    if(super.GetCounter() % 5 == 0){
      returnCloud().setLayoutX(returnCloud().getLayoutX()+WINDSPEED());
    }
  }

  @Override
  public String toString() {
    return "OffStateLeft";
  }

  @Override
  public boolean intersect() {
    return Game.CloudIntersect(returnCloud());
  }
  @Override
  public void updateLabel() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void beingSeeded() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void notSeeded() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public double returnX() {
    // TODO Auto-generated method stub
    return 0;
  }
  @Override
  public double returnY() {
    // TODO Auto-generated method stub
    return 0;
  }

 
}
class InPlayState extends CloudState{
  public InPlayState(Cloud cloud) {
    super(cloud);
  }
  @Override
  public void update() {
    super.increaseCounter();
    if(super.GetCounter() % 5 == 0){
      returnCloud().setLayoutX(returnCloud().getLayoutX()+WINDSPEED());
    }
  }
  @Override
  public String toString(){
    return "InPlayState";
  }
  @Override
  public boolean intersect(){
    return Game.CloudIntersect(returnCloud());
  }
  @Override
  public void updateLabel(){
      
  }
  @Override
  public void beingSeeded() {
    if(returnCloud().returnRGB() > 155){
      returnCloud().decreaseRGB();
    }
  }
  @Override
  public void notSeeded() {
      if(returnCloud().returnRGB() > 255){
        returnCloud().increaseRGB();
      }
  }
  @Override
  public double returnX() {
    return super.returnCloud().getLayoutX();
  }
  @Override
  public double returnY() {
    return super.returnCloud().getLayoutY();
  }


}
class OutOfPlayStateRight extends CloudState{

  public OutOfPlayStateRight(Cloud cloud) {
    super(cloud);
    System.out.println("Out of play Right");
    super.DeleteCloud();
  }
  @Override
  public void update() {
    // super.increaseCounter();
    // if(super.GetCounter() % 1 == 0){
    //   returnCloud().setLayoutX(returnCloud().getLayoutX()+WINDSPEED());
    // }
  }

  @Override
  public String toString() {
    return "OffStateRight";
  }

  @Override
  public boolean intersect() {
    // return Game.CloudIntersect(returnCloud());
    return false;
  }
  @Override
  public void updateLabel() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void beingSeeded() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void notSeeded() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public double returnX() {
    // TODO Auto-generated method stub
    return 0;
  }
  @Override
  public double returnY() {
    // TODO Auto-generated method stub
    return 0;
  }

  

}
class Cloud extends GameObject implements Subject{
  private Ellipse cloud;
  private CloudState state;
  private Random rand= new Random();
  private double MaxRadius = 75;
  private double MinRadius = 35;
  private double MaxYRadius = 35;
  private double MinYRadius = 25;
  private double MinXPosition = 50;
  private double MaxXPosition = 400;
  private double MinYPosition = 200;
  private double MaxYPosition = 700;
  private List<Observer> observers;
  private final GameObject MUTEX = new GameObject();
  private boolean changeState;
  private Label percentage;
  private int CloudPercentage;  
  private int counter=0;
  private int rgb1=255,rgb2=255,rgb3=255;
  private List<Pond> pondList;
  private boolean seeded;
  public Cloud(List<Pond> pondList){
    this.pondList = pondList;
    seeded = false;
    state = new OutOfPlayStateLeft(this);
    CloudPercentage = 0;
    percentage = new Label();
    percentage.setText(""+CloudPercentage+"%");
    percentage.setScaleY(-1);
    this.observers = new ArrayList<>();
    cloud = new Ellipse();
    cloud.setCenterX(50);
    cloud.setCenterY(50);
    cloud.setRadiusX(randomXRadius());
    cloud.setRadiusY(randomYRadius());
    cloud.setFill(Color.WHITE);
    addToObject(cloud);
    addToObject(percentage);
    SetCloudPosition();
  }
  public void reset(){
    super.getChildren().clear();
    seeded = false;
    state = new OutOfPlayStateLeft(this);
    CloudPercentage = 0;
    percentage = new Label();
    percentage.setText(""+CloudPercentage+"%");
    percentage.setScaleY(-1);
    this.observers = new ArrayList<>();
    cloud = new Ellipse();
    cloud.setCenterX(50);
    cloud.setCenterY(50);
    cloud.setRadiusX(randomXRadius());
    cloud.setRadiusY(randomYRadius());
    cloud.setFill(Color.WHITE);
    addToObject(cloud);
    addToObject(percentage);
    SetCloudPosition();
  }
  public boolean IsSeed(){
    if(CloudPercentage > 0){
      seeded = true;
    }
    else if(CloudPercentage <=0){
      seeded = false;
    }
    return seeded;
  }
  public boolean allowed(Pond pond){
    double diameter = 2*pond.returnRadius();
    double accept = 4*diameter;
    double xdif = state.returnX()-pond.returnX();
    double ydif = state.returnY()-pond.returnY();
    xdif = xdif*xdif;
    ydif= ydif*ydif;
    double distance = Math.sqrt(xdif+ydif);
    if(distance < accept){
      return true;
    }
    else return false;
  }
  public double cloudX(){
    return state.returnX();
  }
  public double cloudY(){
    return state.returnY();
  }
  public void beingSeeded(){
    state.beingSeeded();
    if(CloudPercentage<100){
      CloudPercentage++;
    }
    cloud.setFill(Color.rgb(rgb1, rgb2, rgb3));
    percentage.setText(""+CloudPercentage);
  }
  public void notSeeded(){
    
    counter++;
    if(counter %60 ==0){
      state.notSeeded();
      if(CloudPercentage>0){
        CloudPercentage--;
      }
      cloud.setFill(Color.rgb(rgb1, rgb2, rgb3));
      percentage.setText(""+CloudPercentage);
    }
  }
  public int returnRGB(){
    return rgb1;
  }
  public void decreaseRGB(){
    rgb1--;
    rgb2--;
    rgb3--;
  }
  public void increaseRGB(){
    rgb1++;
    rgb2++;
    rgb3++;
  }
  public Ellipse returnCloudObject(){
    return cloud;
  }
  
  public String CurrentState(){
    return state.toString();
  }
  public void HandleState(){
    if(state.intersect()){
      ChangeState(new InPlayState(this));
    }
    else if(state.intersect()==false&&CurrentState().equals("InPlayState")){
      ChangeState(new OutOfPlayStateRight(this));
    }
  }
  public void ChangeState(CloudState state){
    this.state = state;
  }
  public void update() {
    state.update();
  }
  public boolean intersect(){
    return state.intersect();
  }
  public double randomXRadius(){
    return rand.nextDouble(MaxRadius-MinRadius)+MinRadius;
  }
  public double randomYRadius(){
    return rand.nextDouble(MaxYRadius-MinYRadius)+MinYRadius;
  }
  public double RandomXPosition(){
    return -(rand.nextDouble(MaxXPosition-
      (cloud.getRadiusX()+MinXPosition)+MinXPosition+cloud.getRadiusX()));
  }
  public double RandomYPosition(){
    return rand.nextDouble(MaxYPosition-MinYPosition)+MinYPosition;
  }
  public void SetCloudPosition(){
    super.setLayoutX(RandomXPosition());
    super.setLayoutY(RandomYPosition());
  }
  @Override
  public void Register(Observer obj) {
    if(obj == null) throw new NullPointerException("Null Observer");
    synchronized (MUTEX) {
      if(!observers.contains(obj)){
        observers.add(obj);
      }
    }
  }
  @Override
  public void UnRegister(Observer obj) {
    synchronized (MUTEX){
      observers.remove(obj);
    }
    
  }
  @Override
  public void notifyObservers() {
    List<Observer> observersLocal = null;
    synchronized (MUTEX) {
			if (!changeState)
				return;
			observersLocal = new ArrayList<>(this.observers);
			this.changeState=false;
		}
		for (Observer obj : observersLocal) {
			obj.Update();
		}
  }
  @Override
  public Object getUpdates(Observer obj) {
    return 1;
  }
  public void CloudStateChanged(){
    if(CurrentState().equals("InPlayState")){
      
      this.changeState = true;
      notifyObservers();
    }
    else if(CurrentState().equals("OffStateRight")){
      this.changeState = true;
      notifyObservers();
    }
  }

}

class Helipad extends GameObject{
  private Rectangle base;
  private Circle circle;
  private double height=100;
  private double width=100;
  private Image helipad = new Image("file:lib/heli-pad.png",
  height,width,false,false
  );
  public Helipad(){
    ImageView Helipad = new ImageView(helipad);
    Helipad.setScaleY(-1);
    Helipad.setTranslateX(GameApp.XValueGameWindow()/2-width/2);
    Helipad.setTranslateY(height/2);
    addToObject(Helipad);
    
  }
  public double HelipadWidth(){
    return width;
  }
  public double HelipadHeight(){
    return height;
  }

}
class Helicopter_Blades extends GameObject{
  private double rotation = 0;
  private int counter = 0;
  private int counterTwo = 0;
  private Image heli_blades = new Image("file:lib/Helicopter-blades.png",
    75.44,75.28,true,false
  );
  public Helicopter_Blades(){
    ImageView heliblades = new ImageView(heli_blades);
    heliblades.setScaleY(-1);
    this.getChildren().add(heliblades);
  }
  public void resetRotation(){
    rotation = 0;
  }
  public double bladeSpeed(){
    return rotation;
  }
  public void increaseBladeSpeed(){
    counter++;
    if(counter % 10 == 0){
      if(rotation < 25){
        rotation+=1;
      }
    }
    super.setRotate(super.getRotate()+rotation);
  }
  public void decreaseBladeSpeed(){
    counterTwo++;
    if(counterTwo % 10 == 0){
      if(rotation > 0){
        rotation-=1;
      }
    }
    super.setRotate(super.getRotate() - rotation);
  }
}
abstract class HelicopterState implements Updatable{
  private Helicopter helicopter;
  private static boolean Engine = false;
  protected static int fuel = Game.getFuel();
  private static double rotation = 0;
  private static double velocity = 0;
  private static double posX = 0;
  private static double posY = 0;
  public HelicopterState(Helicopter helicopter,int fuel){
    this.helicopter = helicopter;
  }
  public double rotation(){
    return rotation;
  }
  public int getFuel(){
    return fuel;
  }
  public void resetRotation(){
    rotation=0;
  }
  public double velocity(){
    return velocity;
  }
  public double posX(){
    return posX;
  }
  public double posY(){
    return posY;
  }
  public void decreaseFuelOne(){
    fuel--;
  }
  public void decreaseFuelTwo(){
    fuel-=2;
  }
  public void decreaseFuelThree(){
    fuel-=3;
  }
  public Helicopter returnHelicopter(){
    return helicopter;
  }
  public void turnOffEngine(){
    Engine=false;
  }
  public abstract String toString();
  public abstract boolean currentEngine();
  public void turnEngineOn(){
    Engine = !Engine;
  }
  public boolean returnEngine(){
    return Engine;
  }
  public abstract void increaseVelocity();
  public abstract void decreaseVelocity();
  public abstract void rotateLeft();
  public abstract void rotateRight();
  public abstract void deacreaseFuel();
  public abstract int returnFuel();
}
class OffState extends HelicopterState{
  public OffState(Helicopter helicopter,int fuel) {
    super(helicopter,fuel);
    System.out.println("Off State");
  }
  
  @Override
  public String toString() {
    return "OffState";
  }

  public void turnEngineOn(){
    super.turnEngineOn();
  }
  @Override
  public boolean currentEngine(){
    return super.returnEngine();
  }
  public void turnOffEngine(){
    super.turnOffEngine();
  }

  @Override
  public void increaseVelocity() {}

  @Override
  public void decreaseVelocity() {}

  @Override
  public void rotateLeft() {}

  @Override
  public void rotateRight() {}

  @Override
  public void update() {}

  @Override
  public void deacreaseFuel() {
   
  }

  @Override
  public int returnFuel() {
    return super.fuel;
  }

}
class StartingState extends HelicopterState{

  public StartingState(Helicopter helicopter,int fuel) {
    super(helicopter,fuel);
    System.out.println("Starting State");
  }

  @Override
  public String toString() {
    return "StartingState";
  }

  @Override
  public boolean currentEngine() {
    return super.returnEngine();
  }

  @Override
  public void increaseVelocity() {}

  @Override
  public void decreaseVelocity() {}

  @Override
  public void rotateLeft() {}

  @Override
  public void rotateRight() {}

  @Override
  public void update() {}

  @Override
  public void deacreaseFuel() {
    super.decreaseFuelOne();
    
  }
  @Override
  public int returnFuel() {
    return super.fuel;
  }

}
class StoppingState extends HelicopterState{

  public StoppingState(Helicopter helicopter,int fuel) {
    super(helicopter,fuel);
    System.out.println("Stopping State");
  }

  @Override
  public String toString() {
    return "StopState";
  }

  @Override
  public boolean currentEngine() {
    return super.returnEngine();
  }

  public void turnEngineOn(){
    super.turnEngineOn();
  }

  @Override
  public void increaseVelocity() {}

  @Override
  public void decreaseVelocity() {}

  @Override
  public void rotateLeft() {}

  @Override
  public void rotateRight() {}

  @Override
  public void update() {}

  @Override
  public void deacreaseFuel() {
    
  }

  @Override
  public int returnFuel() {
    return super.fuel;
  }

}
class ReadyState extends HelicopterState implements Updatable{
  private double rotation = 0;
  private double velocity = 0;
  private double posX = 0;
  private double posY = 0;
  public ReadyState(Helicopter helicopter,int fuel) {
    super(helicopter,fuel);
    System.out.println("Ready State");
  }
  @Override
  public String toString() {
    return "ReadyState";
  }

  @Override
  public boolean currentEngine() {
    return super.returnEngine();
  }

  @Override
  public void update() {
    super.returnHelicopter().setRotate(rotation);
    super.returnHelicopter().setLayoutX(updateLocationX());
    super.returnHelicopter().setLayoutY(updateLocationY());
  }
  @Override
  public void rotateLeft(){
    rotation += 15;
  }
  @Override
  public void rotateRight(){
    rotation -=15;
  }

  public double updateLocationX(){
    posX = Math.sin(Math.toRadians(-rotation))*velocity;
    return super.returnHelicopter().getLayoutX()+posX;
  }
  public double updateLocationY(){
    posY = Math.cos(Math.toRadians(-rotation))*velocity;
    return super.returnHelicopter().getLayoutY()+posY;
  }

  @Override
  public void increaseVelocity(){
    if(velocity <= 10){
     velocity+=0.1;
    }
  }

  @Override
  public void decreaseVelocity(){
    if(velocity >= -2){
     velocity -= 0.1;
    }
  }

  @Override
  public void deacreaseFuel() {
    if(velocity==0 || velocity <4){
      super.decreaseFuelOne();
    }
    if(velocity > 4){
      super.decreaseFuelTwo();
    }
    else if(velocity > 6){
      super.decreaseFuelThree();
    }
    
  }

  @Override
  public int returnFuel() {
    return super.fuel;
  }

}
class Helicopter extends GameObject implements Updatable,Observer{
  private int ignition;
  private HelicopterState state;
  private Label Fuel;
  private Helipad helipad;
  private Helicopter_Blades blades = new Helicopter_Blades();
  private boolean Engine;
  private String string;
  private List<Subject> clouds;
  private double width = 81.28;
  private double height = 101.6;
  private Image helicopter = new Image("file:lib/helicopter.png",
  width, height,true,false
  );
  private boolean reset;
  public Helicopter(int ingnition, Helipad helipad){
    clouds = new ArrayList<>();
    this.helipad = helipad;
    reset=false;
    ImageView Heli = new ImageView(helicopter);
    Heli.setScaleY(-1);
    Heli.setTranslateY(-15);
    addToObject(Heli);
    super.setLayoutY(helipad.HelipadHeight()/2);
    super.setLayoutX(GameApp.XValueGameWindow()/2-width/2);
    addToObject(blades);
    Engine = false;
    this.ignition = ingnition;
    Fuel=new Label();
    Fuel.setScaleY(-1);
    Fuel.setTranslateY(-25);
    Fuel.setTextFill(Color.YELLOW);
    addToObject(Fuel);
    state=new OffState(this,ignition);
    bladesRotate.start();
  }
  public void toggleReset(){
    reset = false;
    bladesRotate.start();
  }
  public void reset(){
    reset=true;
    state=new OffState(this,ignition);
    System.out.println(state.currentEngine());
    state.resetRotation();
    blades.resetRotation();
    state.turnOffEngine();
    super.setRotate(0);
    super.setLayoutY(helipad.HelipadHeight()/2);
    super.setLayoutX(GameApp.XValueGameWindow()/2-width/2);

  }
  public void stopBladeRotation(){
    bladesRotate.stop();
  }
  public int HandleFuel(){
    return state.returnFuel();
  }
  public void decreaseFuel(){
    state.deacreaseFuel();
  }
  public void updateFuel(){
    Fuel.setText(""+state.returnFuel());
  }
  public double returnBladeSpeed(){
    return blades.bladeSpeed();
  }
  public void changeState(HelicopterState state){
    this.state = state;
  }
  public String currentState(){
    return state.toString();
  }

  public boolean currentEngine(){
    return state.currentEngine();
  }

  public void increaseVelocity(){
    state.increaseVelocity();
  }
  public void decreaseVelocity(){
    state.decreaseVelocity();
  }
  public void rotateLeft(){
    state.rotateLeft();
  }
  public void rotateRight(){
    state.rotateRight();
  }
  public void turnEngineOn(){
    state.turnEngineOn();
  }

  AnimationTimer bladesRotate = new AnimationTimer() {

    @Override
    public void handle(long now) {

        if(currentEngine()){
          blades.increaseBladeSpeed();
        }
        if(currentEngine() == false){
          blades.decreaseBladeSpeed();
        }
      
    }
  };
  @Override
  public void update() {
    state.update();
  }

  public void setNews(String news){
    this.string=news;
  }
  @Override
  public void Update() {
  }
  @Override
  public void setSubject(Subject sub) {
    clouds.add(sub);
  }
}

abstract class BlimpState implements Updatable{
  private Blimp blimp;
  private static Random rand = new Random();
  private static boolean InPlay=false;
  private final static double WINDSPEED = 3;
  private static int counter = 0;
  private static int counter2=0;
  protected static int Fuel=rand.nextInt(10000-5000)+5000;;

  public BlimpState(Blimp blimp){
    
    this.blimp = blimp;
  }
  public void decreaseFuelOne(){
    Fuel--;
  }
  public void DeleteBlimp(){
    blimp = null;
  }
  public abstract void reset();
  public Blimp returnBlimp(){
    return blimp;
  }
  public void increaseCounter2(){
    counter2++;
  }
  public void increaseCounter(){
    counter++;
  }
  public int GetCounter2(){
    return counter2;
  }
  public int GetCounter(){
    return counter;
  }
  public double WINDSPEED(){
    return WINDSPEED;
  }
  public abstract String toString();
  public abstract boolean intersect();
  public abstract void updateLabel();
  public abstract void beingSeeded();
  public abstract void decreaseFuel();
  public abstract int returnFuel();

}
class Created extends BlimpState{

  public Created(Blimp blimp) {
    super(blimp);
    System.out.println("Created");
  }

  @Override
  public void update() {
    super.increaseCounter();
    if(super.GetCounter() % 5 == 0){
      returnBlimp().setLayoutX(returnBlimp().getLayoutX()+WINDSPEED());
    }
    
  }

  @Override
  public boolean intersect() {
    return Game.BlimpIntersect(returnBlimp());
  }

  @Override
  public void updateLabel() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void beingSeeded() {
    // TODO Auto-generated method stub
    
  }
  @Override
  public String toString() {
    return "Created";
  }

  @Override
  public void decreaseFuel() {
    super.decreaseFuelOne();
  }

  @Override
  public int returnFuel() {
    return super.Fuel;
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub
    
  }

}
class InView extends BlimpState{
  public InView(Blimp blimp) {
    super(blimp);
  }

  @Override
  public void update() {
    super.increaseCounter();
    if(super.GetCounter() % 5 == 0){
      returnBlimp().setLayoutX(returnBlimp().getLayoutX()+WINDSPEED());
    }
    
  }

  @Override
  public boolean intersect() {
    return Game.BlimpIntersect(returnBlimp());
  }

  @Override
  public void updateLabel() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void beingSeeded() {
    
  }
  @Override
  public String toString() {
    return "InView";
  }

  @Override
  public void decreaseFuel() {
    super.decreaseFuelOne();
    
  }

  @Override
  public int returnFuel() {
    return super.Fuel;
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub
    
  }

}
class Dead extends BlimpState{
  private Random rand =new Random();
  public Dead(Blimp blimp) {
    super(blimp);
    
  }

  @Override
  public void update() {}

  @Override
  public boolean intersect() {
    return false;
  }

  @Override
  public void updateLabel() {

  }

  @Override
  public void beingSeeded() {
    
  }

  @Override
  public String toString() {
    return "Dead";
  }

  @Override
  public void decreaseFuel() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int returnFuel() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void reset() {
    super.increaseCounter2();
    int random = rand.nextInt(1000-100)+100;
    if(GetCounter2()%random==0){
      super.returnBlimp().resetBlimp();
    }
    
  }

}
class Blimp extends GameObject implements Updatable{
  private Image blimp = new Image("file:lib/blimp.png",
    200.85,50.4,false,false
  );
  private Random rand = new Random();
  private double width = 111.92;
  private double height = 26.88;
  private double MinXPosition = 50;
  private double MaxXPosition = 400;
  private double MinYPosition = 200;
  private double MaxYPosition = 700;
  private int counter;
  private int counter2=0;
  private Label Fuel;
  private int amountOfFuel;
  private BlimpState state;
  public Blimp(){
    state = new Created(this);
    ImageView blimpy = new ImageView(blimp);
    blimpy.setScaleY(-1);
    addToObject(blimpy);
    counter=0;
    SetBlimpPosition();
    Fuel=new Label();
    Fuel.setText(""+amountOfFuel);
    Fuel.setTextFill(Color.GOLD);
    Fuel.setScaleY(-1);
    Fuel.setTranslateX(width/2);
    addToObject(Fuel);

  }  
  public String CurrentState(){
    return state.toString();
  }
  public void HandleState(){
    if(state.intersect()){
      ChangeState(new InView(this));
    }
    else if(state.intersect()==false&&CurrentState().equals("InView")){
      ChangeState(new Dead(this));
    }
  }
  public void ChangeState(BlimpState state){
    this.state = state;
  }

  public void reset(){
    state.reset();
  }
  public void resetBlimp(){
    super.getChildren().clear();
    state = new Created(this);
    ImageView blimpy = new ImageView(blimp);
    blimpy.setScaleY(-1);
    addToObject(blimpy);
    counter=0;
    SetBlimpPosition();
    Fuel=new Label();
    Fuel.setText(""+amountOfFuel);
    Fuel.setTextFill(Color.GOLD);
    Fuel.setScaleY(-1);
    Fuel.setTranslateX(width/2);
    addToObject(Fuel);
  }
  public double RandomXPosition(){
    return -(rand.nextDouble(MaxXPosition-
      (width+MinXPosition)+MinXPosition+width));
  }
  public double RandomYPosition(){
    return rand.nextDouble(MaxYPosition-MinYPosition)+MinYPosition;
  }
  public void SetBlimpPosition(){
    super.setLayoutX(RandomXPosition());
    super.setLayoutY(RandomYPosition());
  }
  public int HandleFuel(){
    return state.returnFuel();
  }
  public void DecreaseFuel(){
    state.decreaseFuel();
  }
  public void UpdateFuel(){
    Fuel.setText(""+state.returnFuel());
  }

  @Override
  public void update() {
    state.update();
  }
  public boolean intersect(){
    return state.intersect();
  }
  
}

public class GameApp extends Application{
  private static Point2D size = new Point2D(800, 800);
  private static Alert LostAlert;
  private static Alert WinAlert;
  private static AnimationTimer gameRun;
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
        LostAlert.close();
        gameRun.start();
        gameWindow.reset();
      } else {
        System.out.println("CLosing");
        
        LostAlert.close();
        stage.close();
        System.exit(0);
      }
    });

    WinAlert = new Alert(Alert.AlertType.CONFIRMATION);
    WinAlert.setTitle("Confirmation");
    WinAlert.setContentText("You Won! Play Again?"+
      "\nScore: "+gameWindow.WinScore());
    WinAlert.setHeaderText("Confirmation");
    WinAlert.getButtonTypes().set(0,buttonPlayAgain);
    WinAlert.getButtonTypes().set(1,NotPlayAgain);
    WinAlert.setOnCloseRequest(e->{
    ButtonType result = WinAlert.getResult();
    if (result != null && result == buttonPlayAgain){
        System.out.println("Play Again!");
        WinAlert.close();
        gameWindow.reset();
        gameRun.start();
      } else {
        System.out.println("Closing");
        
        WinAlert.close();
        stage.close();
        System.exit(0);
      }
    });
    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
    
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode()==KeyCode.I){
          System.out.println("Pressed on I");
          gameWindow.changeStateToStarting();
        }
        if(event.getCode()==KeyCode.W){
          gameWindow.increaseHelicopterSpeed();
        }
        if(event.getCode()==KeyCode.S){
          gameWindow.decreaseHelicopterSpeed();
        }
        if(event.getCode()==KeyCode.D){
          gameWindow.rotateHelicopterRight();
        }
        if(event.getCode()==KeyCode.A){
          gameWindow.rotateHelicopterLeft();
        }
        if(event.getCode()==KeyCode.SPACE){
          gameWindow.CloudBeingSeeded();
        }
        if(event.getCode()==KeyCode.B){
          gameWindow.MakeBorderVisible();
        }
        if(event.getCode()==KeyCode.R){
          gameWindow.reset();
        }
      }

    });
    gameRun = new AnimationTimer() {

      @Override
      public void handle(long now) {
        gameWindow.run();
        
      }

    };
    gameRun.start();
    stage.show();
  }

  public static void main(String[] args){launch(args);}
  public static double XValueGameWindow(){return size.getX();}
  public static double YValueGameWindow(){return size.getY();}
  public static void showAlertLost(){
    gameRun.stop();
    Game.stopBladeRotation();
    LostAlert.show();
    
   }
   public static void showAlertWin(){
    gameRun.stop();
    Game.stopBladeRotation();
    WinAlert.show();
   }
}