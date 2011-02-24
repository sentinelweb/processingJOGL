package net.processing.glgraphics.ex.hdmovfilter;

import processing.core.PApplet;
import processing.core.PFont;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.gsvideo.GSMovie;

public class HDMovieFilter extends PApplet {
	GSMovie movie;
	GLTexture texSrc, texFiltered;

	GLTextureFilter blur, emboss, edges, poster, currentFilter;
	String filterStr;

	PFont font;

	public void setup()
	{
	    size(screen.width, screen.height, GLConstants.GLGRAPHICS);
	   
	    movie = new GSMovie(this, "resources/glgraphics/ex/hdmovfilter/HD_CLIP.mov"); // need to find this
	    movie.loop();
	   
	    texSrc = new GLTexture(this);
	    texFiltered = new GLTexture(this);
	   
	    blur = new GLTextureFilter(this, "resources/glgraphics/ex/hdmovfilter/gaussBlur.xml");
	    emboss = new GLTextureFilter(this, "resources/glgraphics/ex/hdmovfilter/emboss.xml");
	    edges = new GLTextureFilter(this, "resources/glgraphics/ex/hdmovfilter/edgeDetect.xml");
	    poster = new GLTextureFilter(this, "resources/glgraphics/ex/hdmovfilter/posterize.xml");
	    
	    font = loadFont("resources/glgraphics/ex/hdmovfilter/EstrangeloEdessa-24.vlw");
	    textFont(font, 24);      
	    
	    currentFilter = edges;
	    filterStr = "edges";
	}

	public void movieEvent(GSMovie movie)
	{
	    movie.read();
	}

	public void draw()
	{
	    background(0);
	    
	    if ((1 < movie.width) && (1 < movie.height))
	    {
	        texSrc.putPixelsIntoTexture(movie);

	        // Calculating height to keep aspect ratio.      
	        float h = width * texSrc.height / texSrc.width;
	        float b = 0.5f * (height - h);

	        if (currentFilter == null) image(texSrc, 0, b, width, h);
	        else 
	        {
	            texSrc.filter(currentFilter, texFiltered);
	            image(texFiltered, 0, b, width, h);            
	        }          
	    }

	    text("Movie resolution: " + movie.width + "x" + movie.height + " | Screen resolution: " + width + "x" + height + " | Filter: " + filterStr, 10, 30);
	    text("B - bur filter | E - emboss filter | D - edges filter | P - posterize filter | X - disable filter", 10, height - 30); 
	    text("FPS: " + frameRate, width - 200, height - 30);
	}

	public void keyPressed()
	{
	    if ((key == 'B') || (key == 'b')) { currentFilter = blur; filterStr = "blur"; }
	    else if ((key == 'E') || (key == 'e')) { currentFilter = emboss; filterStr = "emboss"; }
	    else if ((key == 'D') || (key == 'd')) { currentFilter = edges; filterStr = "edges"; }  
	    else if ((key == 'P') || (key == 'p')) { currentFilter = poster; filterStr = "posterize"; }
	    else if ((key == 'X') || (key == 'x')) { currentFilter = null; filterStr = "none"; }
	}

}
