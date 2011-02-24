package net.robmunro.perform;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.robmunro.lib.TextTexture;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.KaleidoScope;
import net.robmunro.lib.ogl.tools.MotionBlur;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.lib.ogl.tools.VideoPlay;

import org.gstreamer.ClockTime;

import oscP5.OscIn;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PFont;
import procontroll.ControllDevice;
import procontroll.ControllIO;
/**
 * NOTE: when player 0 (right) has a mode higher than player 1 (left) the alpha chanel screws up and player 1 im doesnt show.
 * dont forget to trigger all used knobs or they are zero !!
 * 
 * PERFORMED AT : Sat Noght Beano, 100 club, 14.03.09. (800x600)
 * used pd patch Beano140309/audioProc.pd for beats sync
 * @author robm
 *
 */
public class Beano140309 extends PApplet {
	private static final int NUM_MODES = 9;
	private int HEIGHT = 600;
	GL gl;
	OpenGL ogl ;
	GLU glu;
	ControllIO controllIO;
	ControllDevice djConsole;
	boolean djConsoleOn = false;
	VideoPlay player0;
	VideoPlay player1;
	VideoPlay player2;
	OscP5 oscP5_10001 =null; 
	PFont metaBold;
	Shape shape;
	KaleidoScope kal;
	Vector3D kalRotMtx;
	float[] sliders;
	MotionBlur m;
	Method playerReady;
	TextTexture tt = new TextTexture();
	String text = null;
	String renderedText = null;
	BeanoControlFrame bcf;
	
	public void setup() {
		size(800,600, OPENGL);
		background(0);
		ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
		shape=new Shape(this.gl);
		kalRotMtx = new Vector3D();
		
		startFullscreen();						 //2
		
		kal = new KaleidoScope(this.gl,this);
		controllIO = ControllIO.getInstance(this);
		controllIO.printDevices();
		setVideo(movIndex0,0);
		setVideo(movIndex1,1);
		metaBold = loadFont("resources/gsmovie/UniversLTStd-Light-48.vlw");
		textFont(metaBold, 15); 
		djConsole = controllIO.getDevice("Hercules Hercules DJ Console");
		djConsole.printSticks();
		djConsole.printSliders();
		djConsole.printButtons();
		for (int i=0;i<djConsole.getNumberOfButtons();i++) {
			djConsole.plug(this, "handleButton"+i+"Press", ControllIO.ON_PRESS, i);
		}
		sliders=new float[djConsole.getNumberOfSliders()];
		djConsole.getSlider(2).setMultiplier(0.49f);//alpha0
		djConsole.getSlider(0).setMultiplier(0.49f);//alpha1
		djConsole.getSlider(4).setMultiplier(64);// circular segments
		djConsole.getSlider(8).setMultiplier(0.49f);//tex Pos 0
		djConsole.getSlider(9).setMultiplier(0.49f);//motion blur
		djConsole.getSlider(10).setMultiplier(1.99f);//distort factor
		m = new MotionBlur(this.gl);
		m.clearAccum();
		oscP5_10001= new OscP5(	this,	"localhost",	10001,	10011,	"receiveOSC"	);
		
		bcf= new BeanoControlFrame(this);
		bcf.setVisible(true);
		
	    //ogl.loadTexture("text", tex);
	    
	}
	
	public void destroy() {
		super.destroy();
		if (player0!=null) player0.dispose();
		if (player1!=null) player1.dispose();
		println("destroyed");
	}

	void checkSliders() {
		 for (int num=0 ;num<djConsole.getNumberOfSliders();num++) {
			 float val= djConsole.getSlider(num).getValue();
			 if (val!=sliders[num]) {
				 println(num+":"+val);
			 }
			 sliders[num]=val;
		 }
	
	}
	String movieBase = "/home/robm/";
	String[] movies = {
			"koyaanisqatsi.avi",
			"media/video/london/avi/london_blue_step_lights_cpk_ky5.mpg.avi",
			"media/video/london/avi/london_oxo_fluro_2_cpk_ky5.mpg.avi",
			"media/video/london/avi/carousel_far.mpg.avi",
			"media/video/london/avi/london_eye_night_across_cpk_ky5.mpg.avi",
			"media/video/london/avi/london_oxo_fluro_4_cpk_ky5.mpg.avi",
			"media/video/london/avi/london_tree_eye_cpk_ky1_400x300.mpg.avi",
			"media/video/london/avi/london_treelights_blue2_cpk_ky5.mpg.avi",
			"media/video/london/avi/london_oxo_fluro_3_cpk_ky5.mpg.avi",
			"media/video/london/avi/london_treelights_blue3_cpk_ky5.mpg.avi",
			"media/video/london/avi/london_vauxall_bridge_close_320_divx.mpg.avi",
			"media/video/london/avi/london_eye_night_forward_cpk_ky5.mpg.avi",
			"media/video/london/avi/london_vauxall_bridge_far_320_divx.mpg.avi",
			"media/video/london/avi/mass_attack_lcd.mpg.avi",
			//"Videos/stuff/city/SDV_0009.AVI",
			//"media/video/stuff/greekflag.AVI.avi",
			"media/video/drive/IMGP0197.AVI.avi",
			"media/video/drive/loire_valley_drive.avi.avi",
			//"media/video/stuff/SDV_0039.AVI.avi",
			"media/video/robots/cockroach_robot.avi",
			"media/video/robots/fighting_robots1.avi",
			"media/video/robots/fighting_robots2.avi",
			"media/video/robots/fighting_robots.avi",
			"media/video/robots/jellyfish_robots1.avi",
			"media/video/robots/jellyfish_robots2.avi",
			"media/video/robots/speaking_robot.avi",
			"media/video/london/can_wharf/avi/cw_fireworks_edit_400_divx.avi",
			"media/video/immersion/anime_cpk_ky1_434x340.avi.avi",
			"media/video/immersion/CloretsXP_cpk_ky1_434x340.avi.avi",
			"media/video/immersion/e-collect_cpk_ky1_434x340.avi.avi",
			"media/video/immersion/gamecube_cpk_ky1_434x340.avi.avi",
			"media/video/immersion/suntory2_cpk_ky1_434x340.avi.avi",
			"media/video/immersion/tarako_cpk_ky1_434x340.avi.avi",
			"media/video/immersion/nuc_damage_ky5.avi.avi",
			"media/video/immersion/nuc_doorstep_ky5.avi.avi",
			"media/video/immersion/nuc_high_alt2_ky5.avi.avi",
			"media/video/immersion/nuc_ivyflats_ky5.avi.avi",
			"media/video/immersion/nuc_orange_ky5.avi.avi"

	};
	
	int movIndex1=movies.length-12;
	int movIndex0=0;
	// set the video file.
	private void setVideo(int index,int playerIndex) {
		String file =movieBase+movies[index];
		setVideo( file, playerIndex) ;
	}
	
	private void setVideo(String file,int playerIndex) {
		player2 = new  VideoPlay(this,file);
		player2.loop();
		if (playerIndex==0) {
			if (player0!=null) player0.dispose();
			player0=player2;
		} else {
			if (player1!=null) player1.dispose();
			player1=player2;
		}
	}
	int rot=0;
	float wid = 800;
	float hgt = 600;
	Vector3D addFactor = new Vector3D(1,1,1);
	int mode0 = 0;
	int mode1 = 0;
	boolean beatJump0=false;
	boolean beatJump1=false;
	int segments = 8;
	boolean mapWholeTex=false;
	boolean texShift=false;
	float distortFactor =1;
	boolean clearAccum = false;
	float textAlpha=0.8f;
	public void draw() {//checkSliders();
		background(0);
		gl.glTranslatef(-120f,0,0); // mysterious left shift
		if (clearAccum) {
			m.clearAccum();
			clearAccum = false;
		}
		segments =67+(int)djConsole.getSlider(4).getValue();  
		
		changeDistort();
		
		float alpha0 = 0.5f+djConsole.getSlider(0).getValue();
		float alpha1 = 0.5f+djConsole.getSlider(2).getValue();

		if (mapWholeTex) {
			kal.setTexMap(new float[][]{{0,0},{1,1}});
		} else {
			kal.setTexMap(new float[][]{{0.1f,0.1f},{0.9f,0.9f}});
		}
		
		gl.glPushMatrix();
		gl.glColor4f(1f, 1f, 1f, alpha0);
		if (player0!=null) player0.setTexture();
		drawMode(mode0);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glColor4f(1f, 1f, 1f, alpha1);
		if (player1!=null) player1.setTexture();
		drawMode(mode1);
		gl.glPopMatrix();
		
		rot++;
		translate.add(addFactor.copy().add(distortFactor));
		m.blur(0.5f+djConsole.getSlider(9).getValue());
		
		/* *********************
		gl.glLoadIdentity();
		gl.glTranslatef(0,0,-20);
		color(1,1,1);
		fill(1,1,1);
		text("hello",10,10);
		******************** */
		if (text!=null) {
			if (!ogl.texExist("text") || !text.equals(renderedText))  {
				BufferedImage tex = tt.doText(text, 820, 160);
				ogl.loadTexture("text", tex);
				renderedText=text;
			}
			gl.glPushMatrix();
			gl.glColor4f(1f, 1f, 1f,textAlpha);
			gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("text"));
			Vector3D v = new Vector3D(  0,0,-800.0f);
			gl.glTranslatef( v.x, v.y, v.z);
			shape.drawSquare(820, 160);
			gl.glPopMatrix();
		} else {
			ogl.unloadTexture("text");
			renderedText=null;
		}
	}

	private void changeDistort() {
		float oldDistort = distortFactor;  
		distortFactor = (float)Math.pow(djConsole.getSlider(10).getValue(),3);
		//if (oldDistort!=distortFactor) {println(distortFactor);}
	}

	private void drawMode(int mode) {
		if (mode==1) {
			Vector3D v = new Vector3D(  0,0,-3100.0f);
			gl.glTranslatef( v.x, v.y, v.z);
			kal.drawKal36(wid, hgt);
		}else if (mode==2) {
			Vector3D v = new Vector3D(  800,-600,-2100.0f);
			gl.glTranslatef( v.x, v.y, v.z);
			kal.drawKal16(wid, hgt);
		}else if (mode==3) {
			Vector3D v = new Vector3D(  0,0,-1050.0f);
			gl.glTranslatef( v.x, v.y, v.z);
			kal.drawKal4(wid, hgt);
		}else if (mode==4) {
			Vector3D v = new Vector3D(  -800,0,-1050.0f);
			gl.glTranslatef( v.x, v.y, v.z);
			kal.drawKal2h(wid*2, hgt);
		}else if (mode==5) {
			Vector3D v = new Vector3D(  0,-600,-1050.0f);
			gl.glTranslatef( v.x, v.y, v.z);
			kal.drawKal2v(wid, hgt*2);
		} else if (mode==6) {
			Vector3D v = new Vector3D(  0,0,-1010.0f);
			gl.glTranslatef( v.x, v.y, v.z);
			shape.drawSquare(wid, hgt);
		} else {
			float[][] texMap = kal.getTexMap();
			float texWid=1f/segments;
			float[][] texMap2=new float[2][2];
			texMap2[0][0] = 0.48f+djConsole.getSlider(8).getValue();
			texMap2[1][0] = texMap2[0][0]+texWid;
			texMap2[0][1] = texMap[0][1];
			texMap2[1][1] = texMap[1][1];
			if (mode==7) {
				Vector3D v = new Vector3D(  0,0,-400.0f);
				gl.glTranslatef( v.x, v.y, v.z);
				drawCircularRepeat(segments, 250, texMap2);  
			} else if (mode==8) {
				Vector3D v = new Vector3D(  -400,0,-360.0f);
				gl.glTranslatef( v.x, v.y, v.z);
				drawLines(segments, wid*2, hgt,texMap2);
			}else if (mode==9) {
				Vector3D v = new Vector3D(  0,-80,-180.0f);
				gl.glTranslatef( v.x, v.y, v.z);
				drawRing(segments, wid*2, hgt,texMap2);
			}
		}
	}
	
	Vector3D translate = new Vector3D();
	int rotation =0;
	public void kalDistort() {
		rotation+=(djConsole.getSlider(10).getValue()-1);
		//Vector3D rot1 = kalRotMtx.copy().add(distortFactor);
		gl.glRotatef(rot, rotation,rotation, rotation);
	}
	
	public void kalDistort1() {
		gl.glTranslatef( translate.x, translate.y, translate.z );
	}
	
	private void drawCircularRepeat(int segments, float length, float[][] texMap) {
		for (int i = 0; i<segments; i++) {
			gl.glPushMatrix();
			if (kal.getDistort()!=null) {
				try {kal.getDistort().invoke(this, null);	} catch (Exception e) {	e.printStackTrace();	} 
			}
			shape.drawSegment(length, (float)(2f*Math.PI/segments),new float[][]{{0.1f,0.1f},{0.9f,5f/segments}});// not sure what this 8 shoudl be
			//new float[][]{{0.1f,0.1f},{0.9f,5f/segments}}
			gl.glPopMatrix();
			gl.glRotatef(360f/segments,  0,0,1);
		}
	}
	
	private void drawLines(int rows, float width, float height, float[][] texMap) {
		for (int i=0;i<rows;i++) {
			//gl.glPushMatrix();
			if (kal.getDistort()!=null) {
				try {kal.getDistort().invoke(this, null);	} catch (Exception e) {	e.printStackTrace();	} 
			}
			shape.drawSquare(width/rows, height, texMap) ;
			//gl.glPopMatrix();
			gl.glTranslatef(width/rows,0,0);
		}
	}
	
	private int ringRot=0;
	private void drawRing(int rows, float width, float height, float[][] texMap) {
		ringRot+=2;
		gl.glPushMatrix();
		for (int i=0;i<rows;i++) {
			//
			if (kal.getDistort()!=null) {  
				try {kal.getDistort().invoke(this, null);	} catch (Exception e) {	e.printStackTrace();	} 
			}
			gl.glRotatef((float)(420/segments), 0,  0, 1);
			gl.glPushMatrix();
			gl.glRotatef((float)(ringRot), 0, 1, 0);
			
			shape.drawSquare(width/rows, height/rows) ;  // , texMap
			gl.glPopMatrix();
			gl.glTranslatef(width/3/rows,0,0);  
		}  
		gl.glPopMatrix();
	}
	
	boolean shift = false;
	boolean ctrl = false;
	boolean alt = false;
	public void keyPressed() {
		if (key>='0' && key<='9') {
			println("alt:"+alt);
			if (!alt) {
				mode0=Integer.parseInt(""+key);
				showController(0) ;
			}else {
				mode1=Integer.parseInt(""+key);
				showController(1) ;
			}
		}
		switch (key) {
			case '-':  setVideo(movIndex0,0) ; break;
			case '=': 
					//player.jump((float)Math.random()*5f);
					//player.play(); 
				
					//long timeSecs = Math.round(Math.random()*20);
					//println(timeSecs);
					long movLength = player0.getPlayer().queryDuration(TimeUnit.MILLISECONDS);
					long movPos = player0.getPlayer().queryPosition(TimeUnit.MILLISECONDS);
					double pc=Math.random()*100;
					long timeMillis = Math.round(pc*movLength/100.0);
					//println(movLength+" : " +movPos+" : " +pc+" : " +timeMillis);
					player0.getPlayer().seek(ClockTime.fromMillis(timeMillis));
					break;
			case 'b':if (!alt) {
						beatJump0=!beatJump0;
						showController(0) ;
					}else {
						beatJump1=!beatJump1;
						showController(1) ;
					}
					break;
			case 'o':bcf.setVisible(true);
			default: break;
		}
		 if (key == CODED) {
		    if (keyCode == SHIFT) {
		    	shift=true;
		    } else if (keyCode == CONTROL) {
		    	ctrl=true;
		    }else if (keyCode == ALT) {
		    	alt=true;
		    }
		  }
	}
	
	public void setDisplayText(String text) {
		if (text==null || !"".equals(text)) {
			this.text = text;
		} else {
			this.text = null;
		}
		
	}
	@Override
	public void keyReleased() {
		if (key == CODED) {
		    if (keyCode == SHIFT) {
		    	shift=false;
		    } else if (keyCode == CONTROL) {
		    	ctrl=false;
		    }else if (keyCode == ALT) {
		    	alt=false;
		    }
		  }
	}

	private void showController(int player) {//showController(0);
		int index = player==0?movIndex0:movIndex1;
		int mode = player==0?mode0:mode1;
		boolean bj = player==0?beatJump0:beatJump1;
		int start = movies[index].lastIndexOf("/");
		if (start==-1) {start=0;}
		String s = player+">> mode:"+mode+" bj:"+bj+" mov:"+movies[index].substring(start,movies[index].length());
		println(s);
		bcf.setStatus(s, player);
	}
	
	public void receiveOSC(OscIn oscIn){
		try{
			if (oscIn.getAddrPattern().equals("/bass")) {
				println("beat");
				if (beatJump0 || beatJump1) {
					rotation += 30;
					translate.add(50);
				}
				if (beatJump0) {
					long pos = player0.getPosMilli();
					long dur = player0.getLength();
					pos = (pos+10000)%dur;
					player0.setPosMilli(pos);

				}
				if (beatJump1) {
					long pos = player1.getPosMilli();
					long dur = player1.getLength();
					pos = (pos+10000)%dur;
					player1.setPosMilli(pos);
				}
			} else if (oscIn.getAddrPattern().equals("/treb")) {
				
			} 
		}catch (Exception e) {
			println(e.getMessage());
		}
	}
	
	
	public void handleButton0Press() {	}
	public void handleButton1Press() {player0.setRandomPosition();	}
	public void handleButton2Press() {mode0 = (mode0!=0?0:1);showController(0); }
	public void handleButton3Press() {	beatJump0=!beatJump0;showController(0);	}
	public void handleButton4Press() {	
		movIndex0--; 
		movIndex0=movIndex0<0?movies.length-1:movIndex0;   
		setVideo(movIndex0,0);
		showController(0);
	}
	public void handleButton5Press() {
		movIndex0++; 
		movIndex0%=movies.length; 
		setVideo(movIndex0,0);
		showController(0);
	}
	public void handleButton6Press() {}
	public void handleButton7Press() {player1.setRandomPosition();		}
	public void handleButton8Press() {mode1 = (mode1!=0?0:1);showController(1);	}
	public void handleButton9Press() {beatJump1=!beatJump1;showController(1);		}
	public void handleButton10Press() {	
		movIndex1--; 
		movIndex1=movIndex1<0?movies.length-1:movIndex1; 
		setVideo(movIndex1,1);
		showController(1);
	}
	public void handleButton11Press() {
		movIndex1++; 
		movIndex1%=movies.length; 
		setVideo(movIndex1,1);
		showController(1);
	}
	public void handleButton12Press() {println("button 12");	}
	public void handleButton13Press() {println("button 13");	}
	public void handleButton14Press() {println("button 14");	}
	public void handleButton15Press() {kal.setDistort(null);	}
	public void handleButton16Press() {
		rot=0;kalRotMtx.setXYZ(1, 1, 1);
		try{kal.setDistort(this.getClass().getDeclaredMethod("kalDistort", new Class[] {}));}catch (Exception e){}	
	}
	public void handleButton17Press() {
		translate.setXYZ(0,0,0);	
		addFactor.setXYZ((float)(-1+Math.random()*2f),(float)(-1+Math.random()*2f),(float)(+Math.random()*2f));
		try{kal.setDistort(this.getClass().getDeclaredMethod("kalDistort1", new Class[] {}));}catch (Exception e){}	
	}
	public void handleButton18Press() {++mode1;mode1%=NUM_MODES;	showController(1);	}
	public void handleButton19Press() {--mode1;mode1 = mode1<0?NUM_MODES-1:mode1;	showController(1);	}
	public void handleButton20Press() {clearAccum = true;	}
	public void handleButton21Press() {println("button 21");	}
	public void handleButton22Press() {++mode0;mode0%=NUM_MODES;	showController(0);}
	public void handleButton23Press() {--mode0;mode0 = mode0<0?NUM_MODES-1:mode0;	showController(0);	}
	public void handleButton24Press() {mapWholeTex = !mapWholeTex	;}
	public void handleButton25Press() {println("button 25");	}
	public void handleButton26Press() {println("button 26");	}
	public void handleButton27Press() {println("button 27");	}
	public void handleButton28Press() {println("button 28");	}
	public void handleButton29Press() {println("button 29");	}
	public void handleButton30Press() {println("button 30");	}
	/*
	public void handleButton0Press() {	println("button 0");	}
	public void handleButton1Press() {println("1"); 	}
	public void handleButton2Press() {println("2"); }
	public void handleButton3Press() {	println("3"); 	}
	public void handleButton4Press() {	println("4"); }
	public void handleButton5Press() {println("button 5");	}
	public void handleButton6Press() {println("button 6");	}
	public void handleButton7Press() {println("button 7");	}
	public void handleButton8Press() {println("button 8");	}
	public void handleButton9Press() {println("button 9");	}
	public void handleButton10Press() {println("button 10");	}
	public void handleButton11Press() {println("button 11");	}
	public void handleButton12Press() {println("button 12");	}
	public void handleButton13Press() {println("button 13");	}
	public void handleButton14Press() {println("button 14");	}
	public void handleButton15Press() {println("button 15");	}
	public void handleButton16Press() {println("button 16");		}
	public void handleButton17Press() {println("button 17");	}
	public void handleButton18Press() {println("button 18");	}
	public void handleButton19Press() {println("button 19");	}
	public void handleButton20Press() {wid=300;hgt=200;	}
	public void handleButton21Press() {println("button 21");	}
	public void handleButton22Press() {println("button 22");	}
	public void handleButton23Press() {println("button 23");	}
	public void handleButton24Press() {println("button 24");	}
	public void handleButton25Press() {println("button 25");	}
	public void handleButton26Press() {println("button 26");	}
	public void handleButton27Press() {println("button 27");	}
	public void handleButton28Press() {println("button 28");	}
	public void handleButton29Press() {println("button 29");	}
	public void handleButton30Press() {println("button 30");	}
	 */
	public static void main (String[] args) {
		println( Beano140309.class.getCanonicalName());
		PApplet.main(new String[] { Beano140309.class.getCanonicalName() });
	}
	
	void startFullscreen() {
		Frame oldFrame = frame;
		frame = new Frame(); 
		frame.add(this);
		frame.setUndecorated(true);  
		frame.setLocation(0, 0);
		oldFrame.setVisible(false);
		frame.setAlwaysOnTop(true);
		oldFrame.setAlwaysOnTop(false);
		GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice myGraphicsDevice = screenDevices[1];  // the second screen
		for (int i=0;i<screenDevices.length;i++) {
			  System.out.println(screenDevices[i].getIDstring()+":"+screenDevices[i].getDisplayMode().getWidth()+"x"+screenDevices[i].getDisplayMode().getHeight());
		}
		myGraphicsDevice.setFullScreenWindow(frame);  
		if (myGraphicsDevice.isDisplayChangeSupported()) {  
		    DisplayMode myDisplayMode = new DisplayMode(  
		    width,  
		    height,  
		    32,  
		    DisplayMode.REFRESH_RATE_UNKNOWN);  
		    myGraphicsDevice.setDisplayMode(myDisplayMode);  
		  }  
	}  
		 
	void stopFullscreen() {  
	  GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  ();  
	  myGraphicsDevice.setFullScreenWindow(null);  
	} 
	
	public class BeanoControlFrame extends JFrame {

		JTextField textFld = null;
		JLabel status0=null;
		JSlider posVal0Slider = null;
		JLabel posVal0Label =null;
		JLabel status1=null;
		JSlider posVal1Slider = null;
		JLabel posVal1Label =null;
		PApplet papp = null;
		JTextField parentKeyInput=null;
		JFileChooser fc;
		JFrame ctlFrame = this;
		BeanoControlFrame(PApplet p) {
			this.papp=p;
			fc=new JFileChooser();
			fc.setMultiSelectionEnabled(false);
			fc.setCurrentDirectory(new File("/home/robm"));
			
			Container container = getContentPane();
			container.setLayout(new GridLayout(-1,1));
			setSize(new Dimension(240, 320));
			JLabel jlbHelloWorld = new JLabel("Beano Control");
			add(jlbHelloWorld);
			this.setSize(100, 100);
			
			// pack();
			setVisible(true);
			// text display
			Container c = new JPanel();	add(c);
			textFld = new JTextField(30);
			c.add(textFld);
			JButton but = new JButton("Set Text");
			c.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setDisplayText(textFld.getText());
						};
					}
			);
			// text alpha slider.
			JSlider textAlphaSlider = new JSlider(0, 100);
			textAlphaSlider.setValue(8);
			add(textAlphaSlider);
			textAlphaSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					textAlpha = ((float)((JSlider)e.getSource()).getValue())/100f;
				}
			});
			status0 = new JLabel("Player 0 status");
			add(status0);
			// set pos for player 0 - percentage
			c = new JPanel();	add(c);
			posVal0Slider = new JSlider(0, 100);
			posVal0Slider.setValue(0);
			c.add(posVal0Slider);
			posVal0Label = new JLabel();
			posVal0Label.setText(""+posVal0Slider.getValue());
			c.add(posVal0Label);
			but = new JButton("Pos 0");
			c.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								
								player0.setPosPC(posVal0Slider.getValue());
							} catch(Exception ex) {}
						};
					}
			);
			posVal0Slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					posVal0Label.setText(""+((JSlider)e.getSource()).getValue());
				}
			});
			but = new JButton("File 0");
			c.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								FileChooserThread fct = new FileChooserThread(0);
								fct.start();
							} catch(Exception ex) {}
						};
					}
			);
			status1 = new JLabel("Player 1 status");
			add(status1);
			// set position for player 1
			c = new JPanel();	add(c);
			posVal1Slider = new JSlider(0, 100);
			posVal1Slider.setValue(0);
			c.add(posVal1Slider);
			posVal1Label = new JLabel();
			posVal1Label.setText(""+posVal1Slider.getValue());
			c.add(posVal1Label);
			posVal1Slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					posVal1Label.setText(""+((JSlider)e.getSource()).getValue());
				}
			});
			but = new JButton("Pos 1");
			c.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								player1.setPosPC(posVal1Slider.getValue());
							} catch(Exception ex) {}
						};
					}
			);
			//set filename button.
			but = new JButton("File 1");
			c.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								FileChooserThread fct = new FileChooserThread(1);
								fct.start();
							} catch(Exception ex) {}
						};
					}
			);

			KeyListener k=new KeyListener() {
				public void keyPressed(KeyEvent e) {
					papp.keyPressed(e);
					parentKeyInput.setText("");
				}
				public void keyReleased(KeyEvent e) {
					papp.keyReleased(e);
					parentKeyInput.setText("");
				}
				public void keyTyped(KeyEvent e) {
					papp.keyTyped(e);
					parentKeyInput.setText("");
				}
			};
			parentKeyInput = new JTextField(10);
			add(parentKeyInput);
			parentKeyInput.addKeyListener(k);

			pack();
		}
		public void setStatus(String str,int player) {
			if (player==0) {
				status0.setText(str);
			} else {
				status1.setText(str);
			}
			
		}
		
		private class FileChooserThread extends Thread{
			int player = 0;
			public FileChooserThread(int player){
				
				this.player=player;
			}
			public void run() {
				this.setName("FileChooserThread");
				int returnVal = fc.showOpenDialog(ctlFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String fileName=fc.getSelectedFile().getAbsolutePath();
					setVideo(fileName, this.player);
				}
			}
		}
	}
}
