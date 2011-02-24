package net.robmunro.lib.ogl.tools;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL;

import org.gstreamer.ClockTime;
import org.gstreamer.Format;
import org.gstreamer.SeekType;
import org.gstreamer.elements.PlayBin;

import processing.core.PApplet;
import sun.security.action.GetLongAction;
import codeanticode.gsvideo.GSMovie;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

public class VideoPlay extends GSMovie{
	public TextureData videoData;
	Texture videoTex=null;
	
	public VideoPlay(PApplet arg0, String arg1) {
		super(arg0, arg1);
		//TextureData(int internalFormat, int width, int height, int border, int pixelFormat, int pixelType, boolean mipmap, boolean dataIsCompressed, boolean mustFlipVertically, Buffer buffer, TextureData.Flusher flusher) 
		gplayer.setVolume(0);
	}
	
	protected void invokeEvent(int w, int h, IntBuffer buffer) {
		if (videoData==null) {
			//videoData = new TextureData( GL.GL_RGB, w, h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, false, false, false, null, null );
			videoData = new TextureData( GL.GL_RGBA, w, h, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, false, false, false, null, null );
		}
		videoData.setBuffer(buffer);
	}
	
	public void setTexture() {
		if (this.videoData!=null) {
			if (this.videoTex!=null) {this.videoTex.dispose();}
			this.videoTex = TextureIO.newTexture( this.videoData);
			this.videoTex.bind();
			this.videoTex.enable();
		}
	}
	
	public void doStop(){
		this.videoData=null;
	}
	public  PlayBin getPlayer(){
		return gplayer;
	}
	public void destroy(){
		this.getPlayer().dispose();
		
	}
	
	public void setRandomPosition() {
		//long movPos = this.getPlayer().queryPosition(TimeUnit.MILLISECONDS);
		double pc=Math.random()*100;
		setPosPC(pc);
	}
	public long getPosPC(double pc) {
		long movLength = this.getPlayer().queryDuration(TimeUnit.MILLISECONDS);
		long timeMillis = Math.round(pc*movLength/100.0);
		System.out.println("pc:"+pc+" len:"+movLength+" = "+timeMillis);
		return timeMillis;
		
	}
	public void setPosPC(double pc) {
		setPosMilli(getPosPC( pc));
	}
	/**
	 * @param timeMillis
	 * PROBLEM: seek doesnt work - or at least it is very innaccurate.
	 * 
	 */
	public void setPosMilli(long timeMillis) {
		this.getPlayer().seek(ClockTime.fromMillis(timeMillis));
		//this.getPlayer().seek(ClockTime.fromNanos(timeMillis*1000));
		//System.out.println("playing:"+this.gplayer.isPlaying());
		//this.gplayer.pause();
		//this.getPlayer().seek(timeMillis,Format.TIME,0,SeekType.SET,timeMillis*1000,SeekType.NONE,-1);
		//this.gplayer.play();
	}

	public long getPosMilli() {
		return this.getPlayer().queryPosition(TimeUnit.MILLISECONDS);
	}
	
	public long getLength() {
		return this.getPlayer().queryDuration(TimeUnit.MILLISECONDS);
	}
}