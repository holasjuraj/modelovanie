package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class SampleController implements Initializable {
	private GraphicsContext gc, gcGraf;
	private List<Box> boxes = new ArrayList<Box>();
	private int maxHeight = 200, maxWidth = 200;
	private int minHeight = 5, minWidth = 5;
	private int minRychlost = -10, maxRychlost = 10;
	private Timeline timel;
	private boolean playing = false;
	private MovingAverage timeAvgN = new MovingAverage(20);
	private MovingAverage timeAvgHG = new MovingAverage(20);
	private List<Integer> timesListN = new ArrayList<Integer>();
	private List<Integer> timesListHG = new ArrayList<Integer>();

	HierarchicalGrid hg = new HierarchicalGrid(boxes, 6);
	private Matrix kolizieN = new Matrix(0);
	
	//////// GUI Components ////////
	
	@FXML private GridPane gridPane;
	@FXML private Canvas canvas;
	@FXML private Pane pane;
    @FXML private Button animationButton;
    @FXML private CheckBox checkBoxNaive;
    @FXML private CheckBox checkBoxHG;
    @FXML private CheckBox checkBoxHGNarrow;
	@FXML private TextField textNumBoxes;
	@FXML private TextField textMaxH;
	@FXML private TextField textMaxW;
	@FXML private TextField textMaxV;
	@FXML private Button changeButton;
	@FXML private Button example1Button;
	@FXML private Button example2Button;
	@FXML private Button example3Button;
	@FXML private TextArea textInfo;
    @FXML private Label timeNaiveLabel;
    @FXML private Label timeHGLabel;
	@FXML private Canvas grafCanvas;
	
    
    //////// Methods ////////
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = canvas.getGraphicsContext2D();
		gcGraf = grafCanvas.getGraphicsContext2D();
		timesListN.add(0);
		timesListHG.add(0);
		
		onButtonChange(null);

		
		gridPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override 
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {          
				canvas.setWidth(0.75*(double)newSceneWidth);    
			}
		});
		gridPane.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				canvas.setHeight(0.85*(double)newSceneHeight);
			}
		});
		
		gridPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override 
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {          
				grafCanvas.setWidth(0.75*(double)newSceneWidth);    
			}
		});
		
		paint();
	}

	
	//// Buttons ////
	
    @FXML
    void onButtonAnimationStart(ActionEvent event) {
    	if (playing){
    		pause();
    		playing = false;
    		animationButton.setText("Animuj");
    	}
    	else{
    		resume();
    		playing = true;
    		animationButton.setText("Zastav");
    	}
    }
	
	@FXML
	void onButtonChange(ActionEvent event) {
//		textNumBoxes.commitValue();
//		textMaxW.commitValue();
//		textMaxH.commitValue();
//		textMaxV.commitValue();
		try{
			int newVal = new Integer(textNumBoxes.getText());
			int pocet = boxes.size();
			if(newVal >= 0 && newVal <= 1000){ pocet = newVal; }
			
			newVal = new Integer(textMaxW.getText());
			if(newVal >= 5 && newVal <= 250){ maxWidth = newVal; }
			
			newVal = new Integer(textMaxH.getText());
			if(newVal >= 5 && newVal <= 250){ maxHeight = newVal; }
			
			newVal = new Integer(textMaxV.getText());
			if(newVal >= 0 && newVal <= 10){ maxRychlost = newVal; }
			minRychlost = -maxRychlost;
		
			zmenPocetBoxov(pocet);
		}
		catch(NumberFormatException e){ System.out.println("Wrong input number!"); }
		
		paint();
	}
	
	@FXML
	void onButtonExample1(ActionEvent event){
		// veeela malych boxikov, malo kolizii
		zmenPocetBoxov(0);
		textNumBoxes.setText("1000");
		textMaxH.setText("15");
		textMaxW.setText("15");
		textMaxV.setText("2");
		checkBoxHGNarrow.setSelected(false);
		onButtonChange(null);
		textInfo.setText("Ukážka 1:\npri veľkom počte malých objektov, ktoré "
				+ "produkujú málo kolízií, vykazuje HG najlepšie výsledky, "
				+ "zhruba 10x rýchlejšie ako naivný algoritmus.");
	}
	
	@FXML
	void onButtonExample2(ActionEvent event){
		// vela vacsich boxov, velmi vela kolizii
		zmenPocetBoxov(0);
		textNumBoxes.setText("500");
		textMaxH.setText("150");
		textMaxW.setText("150");
		textMaxV.setText("8");
		checkBoxHGNarrow.setSelected(true);
		onButtonChange(null);
		textInfo.setText("Ukážka 2:\npokiaľ je v scéne veľa (väčších) objektov"
				+ " ktoré vytvárajú veľké množstvo kolízií, HG prekonáva  "
				+ "naivný algoritmus zhruba 8-násobne.");
	}
	
	@FXML
	void onButtonExample3(ActionEvent event){
		// malo boxov, malo kolizii - nie az tak efektivne
		zmenPocetBoxov(0);
		textNumBoxes.setText("20");
		textMaxH.setText("150");
		textMaxW.setText("150");
		textMaxV.setText("8");
		checkBoxHGNarrow.setSelected(false);
		onButtonChange(null);
		textInfo.setText("Ukážka 3:\npri malom počte objektov sa prejavuje "
				+ "zvýšená réžia dátových štruktúr v HG algoritme, kvôli čomu "
				+ "už dosahuje výsledky porovnateľné s naivným algoritmom, "
				+ "alebo aj o niečo horšie.");
	}
	
	private void zmenPocetBoxov(int newVal){
		Random rand = new Random();
		int w, h, dx, dy;
		if ( newVal > boxes.size()){
			for (int i=boxes.size(); i<newVal; i++){
				h = rand.nextInt(maxHeight-minHeight) + minHeight;
				w = rand.nextInt(maxWidth-minWidth) + minWidth;
				dx = rand.nextInt(maxRychlost-minRychlost)+minRychlost;
				dy = rand.nextInt(maxRychlost-minRychlost)+minRychlost;
				addBox(new Box(rand.nextDouble()*(Box.maxCoord-w),rand.nextDouble()*(Box.maxCoord-h),w,h,dx,dy));
			}
		}
		else if (newVal < boxes.size()){
			for(int i = boxes.size(); i > newVal; i--){
				removeBox(i-1);
			}
		}
		naiveCollision();
	}
	
	
	//// Vykreslovanie ////
	
	private void paint(){
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gcGraf.setFill(Color.WHITE);
		gcGraf.fillRect(0, 0, grafCanvas.getWidth(), grafCanvas.getHeight());
		
		int grafMax = Math.max(Collections.max(timesListN), Collections.max(timesListHG));
		drawBoxes();
		if (checkBoxHG.isSelected()){
			drawCollisionsHG();
			drawTimeGraph(timesListN, Color.RED, grafMax);
		}
		if (checkBoxNaive.isSelected()){
			drawCollisionsNaive();
			drawTimeGraph(timesListHG, Color.BLUE, grafMax);
		}
	}
	
	private void drawBoxes(){
		for(Box b:boxes){
			b.paint(gc);
		}
	}
	
	private void drawCollisionsNaive(){
		gc.setStroke(Color.RED);
		List<Line> lines = makeLineList(kolizieN);
		for (Line l:lines){
			gc.strokeLine(l.getStartX()-2, l.getStartY(), l.getEndX()-2, l.getEndY());
		}
	}
	
	private void drawCollisionsHG(){
		gc.setStroke(Color.BLUE);
		List<Line> lines = makeLineList(hg.getCollisions(checkBoxHGNarrow.isSelected()));
		for (Line l:lines){
			gc.strokeLine(l.getStartX()+2, l.getStartY(), l.getEndX()+2, l.getEndY());
		}
	}
	
	private void drawTimeGraph(List<Integer> timesList, Color col, double max){
		gcGraf.setStroke(col);
		double	width = grafCanvas.getWidth(),
				height = grafCanvas.getHeight();
		for(int i = 0; i < timesList.size()-1; i++){
			int timeIndex = timesList.size()-i-1;
			gcGraf.strokeLine(	width-i, height * (1-timesList.get(timeIndex)/max),
								width-i-1, height * (1-timesList.get(timeIndex-1)/max));
		}
	}
	
	private List<Line> makeLineList(Matrix mat){
		List<Line> lines = new ArrayList<>();
		for(int r = 0; r < mat.n; r++){
			for(int c = r+1; c < mat.n; c++){
				if(mat.get(r, c)){
					Box B1 = boxes.get(r), B2 = boxes.get(c);
					lines.add(new Line(B1.centerX(canvas),B1.centerY(canvas),B2.centerX(canvas),B2.centerY(canvas)));
				}
			}
		}
		return lines;
	}
	
	
	//// Animacia a kolizie ////
	
	private void resume() {
        timel = new Timeline(new KeyFrame(
					Duration.millis(50),
					ae -> pohybuj())
        		);
        timel.setCycleCount(Animation.INDEFINITE);
        timel.play();
    }
	
	private void pause() {
		timel.stop();
	}

	private void pohybuj(){
		long startTime;
		List<double[]> oldPos = new ArrayList<double[]>();
		for(int i = 0; i < boxes.size(); i++){
			oldPos.add(new double[]{boxes.get(i).x, boxes.get(i).y});
		}
		
		for (int i=0; i<boxes.size(); i++){
			boxes.get(i).move();
		}

		if (checkBoxNaive.isSelected()){
			startTime = System.nanoTime();
			naiveCollision();
			timeAvgN.add((System.nanoTime()-startTime)/1000);
			timeNaiveLabel.setText(timeAvgN.getAvg() + " microsec");
			timesListN.add(timeAvgN.getAvg());
			if(timesListN.size() > grafCanvas.getWidth()){ timesListN.remove(0); }
		}
		if (checkBoxHG.isSelected()){
			startTime = System.nanoTime();
			HGCollision(oldPos);
			timeAvgHG.add((System.nanoTime()-startTime)/1000);
			timeHGLabel.setText(timeAvgHG.getAvg() + " microsec");
			timesListHG.add(timeAvgHG.getAvg());
			if(timesListHG.size() > grafCanvas.getWidth()){ timesListHG.remove(0); }
		}
		
		paint();
	}
	  
	private void naiveCollision(){
		kolizieN.clear();
		for (int i=0; i<boxes.size(); i++){
			for (int j=0; j<boxes.size(); j++){
				Box B1 = boxes.get(i),
					B2 = boxes.get(j);
				if (!(B1.x>(B2.x+B2.width)) && !(B1.x+B1.width<B2.x) 
						&& !(B1.y>B2.y+B2.height) && !(B1.y+B1.height<B2.y)){
					kolizieN.set(i, j, true);
				}
			}
		}
	}
	 
	private void HGCollision(List<double[]> oldPos){
		for(int i = 0; i < boxes.size(); i++){
			hg.updateBox(i, oldPos.get(i));
		}
	}

    public void addBox(Box b){
		boxes.add(b);
		kolizieN.addRowCol();
		hg.addBox(b);
    }
    
    public void removeBox(int index){
		kolizieN.removeRowCol(index);  
		hg.removeBox(index);
		boxes.remove(index);
    }
    
}