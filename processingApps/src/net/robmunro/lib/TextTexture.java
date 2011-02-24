package net.robmunro.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TextTexture {
	private static final int TEXT_HEIGHT = 45;
	public Font thisFont = new Font("Arial", Font.BOLD, TEXT_HEIGHT);
	public FontMetrics thisFontMetrics;
	
	public TextTexture() {
		
	}

	public  BufferedImage doText(String text,int width, int height) {
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)img.getGraphics();
		g2.setFont(thisFont);
		FontMetrics thisFontMetrics = g2.getFontMetrics(thisFont);
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, width, height);
		g2.setColor(Color.WHITE);
		int strWid = thisFontMetrics.stringWidth(text);
		g2.drawString(text, (width-strWid)/2, (height+TEXT_HEIGHT)/2);
		return  makeTransparent(img,Color.black);
		
	}
	
	public static BufferedImage makeTransparent(BufferedImage image, Color col){
        int width = image.getWidth();
        int height = image.getHeight();
        Color temp = null;
        BufferedImage newImage = new BufferedImage(width,
              height, BufferedImage.TYPE_4BYTE_ABGR);
        int color = -1;
        if(col != null) color = col.getRGB();
        for(int i = 0; i < width; i++){
              for(int j = 0; j < height; j++){
                    int k = image.getRGB(i, j);
                    if(col != null){
                          if(k == color){      
                                newImage.setRGB(i, j, 0x00000000);
                          }
                          else{
                                newImage.setRGB(i, j, k);
                          }
                    }
                    else newImage.setRGB(i, j, k);
              }
        }      
        return newImage;
  }
}
