/*
 * Gestalt
 *
 * Copyright (C) 2007 Patrick Kochlik + Dennis Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package gestalt.demo.advanced;


import java.text.AttributedString;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;

import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.shape.material.TexturePlugin;
import gestalt.texture.Bitmap;
import gestalt.util.FontTextureCreator;

import data.Resource;


public class UsingFontTextureCreator
    extends AnimatorRenderer {

    private Plane _myFontPlane;

    private TexturePlugin myFontTexture;

    public void setup() {

        displaycapabilities().backgroundcolor.set(0.2f);

        /* create a plane */
        _myFontPlane = drawablefactory().plane();

        /* create a texture */
        myFontTexture = drawablefactory().texture();
        _myFontPlane.material().addPlugin(myFontTexture);
        bin(BIN_3D).add(_myFontPlane);
        getRenderedText(1.2f);
    }


    public void loop(float theDeltaTime) {
        _myFontPlane.rotation().z = event().mouseX / (float) displaycapabilities().width * PI;
        if (event().mouseClicked) {
            final float linewidth = 1 + 2 * event().mouseX / (float) displaycapabilities().width;
            getRenderedText(linewidth);
        }
    }


    private void getRenderedText(float theLinewidth) {
        String theText = "Gestalt\na very small render engine toolboxtoolboxtoolboxtoolbox.";

        Font myFont = FontTextureCreator.getFont(Resource.getStream("demo/font/exmouth/exmouth_.ttf"), 36);
        Font myOtherFont = FontTextureCreator.getFont(Resource.getStream("demo/font/silkscreen/slkscr.ttf"), 8);

        AttributedString myAttributedString = new AttributedString(theText);
        myAttributedString.addAttribute(TextAttribute.FONT, myFont, 0, 7);
        myAttributedString.addAttribute(TextAttribute.FONT, myOtherFont, 7 + 1, theText.length());
        myAttributedString.addAttribute(TextAttribute.FOREGROUND, Color.BLACK, 7 + 1, theText.length());
        myAttributedString.addAttribute(TextAttribute.BACKGROUND, Color.WHITE, 7 + 1, theText.length());
        myAttributedString.addAttribute(TextAttribute.STRIKETHROUGH, Boolean.TRUE, theText.length() - 15, theText.length() - 9);

        FontTextureCreator.background = new Color(1f, 0f, 0f, 0.75f);
        FontTextureCreator.alignment = FontTextureCreator.CENTERED;
        FontTextureCreator.linewidth = theLinewidth;
        FontTextureCreator.padding = 20;

        Bitmap myBitmap = FontTextureCreator.getBitmap(myAttributedString, 256, true);
        myFontTexture.load(myBitmap);
        _myFontPlane.setPlaneSizeToTextureSize();
    }


    public static void main(String[] arg) {
        new UsingFontTextureCreator().init();
    }
}
