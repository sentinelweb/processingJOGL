package net.robmunro.algorithm.julia;

import java.awt.dnd.MouseDragGestureRecognizer;

import processing.core.PApplet;

public class Julia extends PApplet {

	
	private static final float HEIGHT = 400;
	private static final float WIDTH = 600;
	
	private Complex start ;
	private Complex finish ;
	public void setup () {
		size((int)WIDTH, (int)HEIGHT, P3D);
	
	}
	boolean semaphore = false;
	public void  draw() {
		if (semaphore) {return;}
		semaphore=true;
		Complex constant=scale(new int[]{mouseX,mouseY});
		for (int i=0;i<WIDTH;i++) {
			for (int j=0;j<HEIGHT;j++) {
				Complex point=scale(new int[]{i,j});
				int jhgt=juliaSolve(point,constant);
				//int jhgt=mandelSolve(point , constant);
				
				stroke(color(255,jhgt*10,0));
				point(i,j);
			}
		}
		semaphore=false;
	}
	
	public int juliaSolve(Complex c,Complex constant){
		int loopCtr=0;
		while(loopCtr<25) {
			c = c.square().sub(constant ); //y=z^2 -c
			//c = c.square().add(constant ); //y=z^2 +c
			//c = c.square().add(c.mul(new Complex(2f,0))).sub(constant ); //y=z^2 +2*z-c
			if (c.mod()>2) {return loopCtr;}
			loopCtr++;
		}
		return loopCtr;
	}
	public int mandelSolve(Complex c, Complex factor){/// the factor isnt required
		int loopCtr=0;
		Complex var = new Complex(0,0);
		while(loopCtr<25) {
			//var = var.square().add(c ); //y=z^2 +c
			var = var.square().mul(new Complex(2f,0f)).square().square().square().square().square().div(factor.square()).add( c ); //y=z^2 +c
			if (var.mod()>2) {return loopCtr;}
			loopCtr++;
		}
		return loopCtr;
	}
	
	private int[] unscale(Complex c){
		return new int[] {(int)(c.re*(WIDTH/2f)-(WIDTH/2f)) , (int)(c.im*(HEIGHT/2f)-(HEIGHT/2f))};
	}
	
	private Complex scale(int[] coords) {
		return new Complex((coords[0]-(WIDTH/2f))/(float)(WIDTH/2f),(coords[1]-(HEIGHT/2f))/(float)(HEIGHT/2f));
	}
	
	class Complex{
		public float re=0.0f, im=0.0f;

		public Complex(float re, float im) {
			super();
			this.re = re;
			this.im = im;
		}
		public Complex square(){
			return this.mul(this);
		}
		public Complex add(Complex c) {
			return new Complex(this.re+c.re,this.im+c.im);
		}
		public Complex  sub(Complex c){
			return new Complex(this.re-c.re,this.im-c.im);
		}
		public Complex mul(Complex c) {
			return new Complex((this.re*c.re-this.im*c.im),(this.re*c.im+this.im*c.re));
		}
		public Complex div(Complex c) {
			return new Complex(((this.re*c.re+this.im*c.im)/(c.re*c.re+c.im*c.im)), ((this.im*c.re-this.re*c.im)/(c.re*c.re+c.im*c.im)));
		}
		public float mod() {
			return (float)Math.sqrt(this.re*this.re+this.im+this.im);
		}
		public String toString(){return this.re+":"+this.im;}
	}
}
