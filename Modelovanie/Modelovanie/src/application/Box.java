package application;

import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Box {
	static final double maxCoord = 1000;
	
	double x, y;
	double width, height;
	Color color;
	double dx, dy;
	
	public Box(double xx, double yy, double w, double h, double ddx, double ddy){
		x = xx;
		y = yy;
		width = w;
		height = h;
		dx = ddx;
		dy = ddy;
		Random rand = new Random();
		color =  Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
	}
	
	public void paint(GraphicsContext gc){
		gc.setFill(color);
		double  gcH = gc.getCanvas().getHeight(),
				gcW = gc.getCanvas().getWidth();
		gc.fillRect(x/maxCoord*gcW, y/maxCoord*gcH, width/maxCoord*gcW, height/maxCoord*gcH);
	}
	
	public double centerX(Canvas c){
		double cx = (x+x+width)/2;
		return cx/maxCoord*c.getWidth();
	}
	
	public double centerY(Canvas c){
		double cy = (y+y+height)/2;
		return cy/maxCoord*c.getHeight();
	}
	
	public void move(){
		boolean posunX=false, posunY=false;
		if (x+width+dx>maxCoord){
			x = Math.min(maxCoord-width, x+dx);
			dx *= -1;
			posunX = true;
		}
		if (y+height+dy>maxCoord){
			y = Math.min(maxCoord-height, y+dy);
			dy *= -1;
			posunY = true;
		}
		if (x+dx<0){
			x = Math.max(x+dx, 0);
			dx *= -1;
			posunX = true;
		}
		if (y+dy<0){	
			y = Math.max(y+dy, 0);
			dy *= -1; 
			posunY = true;
		}
		if (!posunX){
			x += dx;
		}
		if(!posunY){
			y += dy;
		}
	}

}