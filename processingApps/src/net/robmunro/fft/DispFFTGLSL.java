package net.robmunro.fft;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import net.robmunro.lib.Sphere;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.glgraphics.GLTextureFilterParameters;

import oscP5.OscIn;
import oscP5.OscP5;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class DispFFTGLSL extends PApplet {

	private static final int HEIGHT = 600;
	private static final int WIDTH = 800;
	private static final int DATA_LENGTH = 1024;
	private static final int DATA_MAX = 255;
	boolean drawSempahore = false;
	OscP5 oscP5 =null; 
	float rotateCtr=0.0f;
	PImage buffer;
	int buffWid=640;
	int buffHgt= 480;
	GLTextureFilter fftFilter;
	GLTexture  tex1;
	GLTexture  buffer1;
	Sphere s;
	float rotation = 0f;
	@Override
	public void setup() {
		  size(WIDTH,HEIGHT, GLConstants.GLGRAPHICS);
		  noStroke();
		  frameRate(15);
		  
		  oscP5= new OscP5(
					this,
					"localhost",
					10003,
					10013,
					"receiveOSC"
					
			);
		  
		  fftFilter = new GLTextureFilter(this, "resources/robmunro/fft/fft.xml");
		  tex1 = new GLTexture(this,buffWid,buffHgt);
		  buffer1 = new GLTexture( this, buffWid, buffHgt );
		  s = new Sphere();
		  s.initializeSphere(15);
	}

	@Override
	public void draw() {
		if (!drawSempahore) {	
			//test data
			//ArrayList<Integer> data = new ArrayList<Integer>();
			//for (int i=0;i<DATA_LENGTH;i++) { data.add((int)Math.round(Math.random()*DATA_MAX)); }
		
			drawSempahore = true;
			rotateCtr += 0.1;
			buffer = new PImage(buffWid, buffHgt);
			
			background(0);
			GLTextureFilterParameters params = new GLTextureFilterParameters(this);
			params.parFlt1=(float)mouseY/(float)HEIGHT*10f;
			buffer.set(1, 1, color( 255, 255, 255, 200 ));
			buffer.set(buffWid, 1, color( 255, 255, 255, 200 ));
			buffer.set(1, buffHgt, color( 255, 255, 255, 200 ));
			buffer.set(buffWid, buffHgt, color( 255, 255, 255, 200 ));
			/* *******************
			Image img = new BufferedImage(buffWid, buffHgt,BufferedImage.TYPE_INT_BGR);
			Graphics2D g2 = (Graphics2D)img.getGraphics();
			g2.setColor(Color.red);
			int[] last=new int[]{0,buffHgt/2};
			
			****************** */
			ArrayList<Integer> data = sndLoader.getData();
			for (int i=0;i<data.size();i++) {
				int left = i*buffWid/data.size() - buffWid/2;
				//int top=(buffHgt-data.get(i))/2+(buffHgt/2) -400;
				int top=(buffHgt-data.get(i))/2+(buffHgt/2) -200;
				buffer.set(left, top, color( 0, 255, 255, 200 ));
				/* *******************
				int left=(int)(i*((float)buffWid/(float)data.size()));
				int top=(int)( (float)buffHgt/2 - (float)data.get(i) *(float)buffHgt/(float)DATA_MAX*0.25f);
				g2.drawLine(last[0], last[1], left, top);
				last[0]=left; last[1]=top;
				****************** */
			}
			buffer1.putImage(buffer);

			//buffer1.putImage( new PImage(img) );
			buffer1.filter(fftFilter, tex1, params);
			
			//image(buffer1,20, 0);
			//image(tex1, 0, 0);
			
			translate(WIDTH/2f,HEIGHT/2f,0);
			rotateX(radians(0));
			rotateY(rotation);
			rotation+=0.01;
			s.texturedSphere(300, tex1, this);
			
			
			/*
			ArrayList<Integer> data = sndLoader.getData();
			for (int i=0;i<data.size();i++) {
				pushMatrix();
				int boxFill = color(255*data.get(i)/DATA_MAX ,0 ,255*(DATA_MAX-(data.get(i)/DATA_MAX)), 50);
				fill(boxFill);
				int left = i*width/data.size() - width/2;
				translate( left, (DATA_MAX-data.get(i))/2+height/4 -100 );
				  //translate(400,data.get(i));
				
				box(10,10,10);
				popMatrix();
			}
			data = fftLoader.getData();
			for (int i=0;i<data.size();i++) {
				pushMatrix();
				int boxFill = color(255*data.get(i)/DATA_MAX ,0 ,255*(DATA_MAX-(data.get(i)/DATA_MAX)), 50);
				fill(boxFill);
				int left = i*width/data.size() - width/2;
				translate( left, (DATA_MAX-data.get(i))*2+height/4-100);
				  //translate(400,data.get(i));
				
				box(10,10,10);
				popMatrix();
			}
			*/
		}
		drawSempahore = false;
	}
	BufferLoader sndLoader=new BufferLoader();
	BufferLoader fftLoader=new BufferLoader();
	public void receiveOSC(OscIn oscIn){
		if (oscIn.getAddrPattern().equals("/fft")) {
			fftLoader.process(oscIn);
		}else if (oscIn.getAddrPattern().equals("/snd")) {
			sndLoader.process(oscIn);
		}
	}
	
	class BufferLoader {
		private ArrayList<Integer> snd_buffer = new ArrayList<Integer>();
		private ArrayList<Integer> snd_data  = new ArrayList<Integer>();
		private HashMap<Integer,ArrayList> tmp  = new HashMap<Integer,ArrayList>();
		
		public void process(OscIn oscIn){
			Integer index = oscIn.getInt(0);
			String data = oscIn.getString(1);
			//System.out.println(index+ " --- "+data+":");
			String[] dataSplit=data.split(":");
			ArrayList<Integer> tmp_buf = new ArrayList<Integer>();
			for (int i=0;i<dataSplit.length;i++) {
				try {
					tmp_buf.add(Integer.parseInt(dataSplit[i]));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			tmp.put( index, tmp_buf );
			if (index==0) {
				for (int i=tmp.keySet().size()-1; i>0; i--) {
					if (tmp.get( i )!=null) {
						snd_buffer.addAll(tmp.get( i ));
					}
				}
				snd_data = snd_buffer;
				snd_buffer = new ArrayList<Integer>();
				System.out.println("rx frame:"+oscIn.getAddrPattern());
			}
		}
		public ArrayList<Integer> getData() {
			return snd_data;
		}
	}
	
	
}
