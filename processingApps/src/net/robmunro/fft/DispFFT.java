package net.robmunro.fft;

import java.util.ArrayList;
import java.util.HashMap;

import oscP5.OscIn;
import oscP5.OscP5;

import processing.core.PApplet;

public class DispFFT extends PApplet {

	private static final int DATA_LENGTH = 1024;
	private static final int DATA_MAX = 255;
	boolean drawSempahore = false;
	OscP5 oscP5 =null; 
	float rotateCtr=0.0f;
	@Override
	public void setup() {
		  size(800,600, P3D);//OPENGL
		  noStroke();
		  frameRate(15);
		  
		  oscP5= new OscP5(
					this,
					"localhost",
					10003,
					10013,
					"receiveOSC"
			);
	}

	@Override
	public void draw() {
		if (!drawSempahore) {	
			//test data
			//ArrayList<Integer> data = new ArrayList<Integer>();
			//for (int i=0;i<DATA_LENGTH;i++) { data.add((int)Math.round(Math.random()*DATA_MAX)); }
		
			drawSempahore = true;
			rotateCtr += 0.1;
			
			background(0);
			translate(400,0);
			//rotateY(rotateCtr);
			
			
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
					snd_buffer.addAll(tmp.get( i ));
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
