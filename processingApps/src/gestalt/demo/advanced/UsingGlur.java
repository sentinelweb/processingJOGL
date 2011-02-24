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


import gestalt.candidates.JoglGLUTBitmapFont;
import gestalt.candidates.glur.OffscreenBlurContext;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cube;
import gestalt.util.CameraMover;

import mathematik.Vector2i;

import data.Resource;


public class UsingGlur
    extends AnimatorRenderer {

    private OffscreenBlurContext _myBlurContext;

    private Cube[] _myCube;

    private JoglGLUTBitmapFont _myFont;

    private float _myCounter;

    public void setup() {
        /* g1 */
        framerate(120);

        /* create offscreen context */
        _myBlurContext = new OffscreenBlurContext(drawablefactory(),
                                                  bin(BIN_ARBITRARY),
                                                  bin(BIN_2D_FOREGROUND),
                                                  bin(BIN_FRAME_SETUP),
                                                  new Vector2i(1024, 512),
                                                  new Vector2i(512, 256),
                                                  Resource.getStream("demo/shader/simple.vsh"),
                                                  Resource.getStream("demo/shader/blur9x9.fsh"));

        /* create cube and add it to shape FBO */
        _myCube = new Cube[150];
        for (int i = 0; i < _myCube.length; i++) {
            _myCube[i] = drawablefactory().cube();
            _myCube[i].rotation().x = TWO_PI * -i / (float) _myCube.length;
            _myCube[i].rotation().y = TWO_PI * i / (float) _myCube.length;
            _myCube[i].rotation().z = TWO_PI * -i / (float) _myCube.length;
            _myCube[i].material().color.set(0, 0.5f, 1, 0.05f);
            _myCube[i].scale().set(500, 10, 10);
            _myBlurContext.bin().add(_myCube[i]);
        }

        /* create cube and add it to shape FBO */
        Cube myCube = drawablefactory().cube();
        myCube.material().color.set(1);
        myCube.scale().set(200, 200, 200);
        myCube.rotation().set(0.2, 0.5, 1.2);
        _myBlurContext.bin().add(myCube);

        /* osd */
        _myFont = new JoglGLUTBitmapFont();
        _myFont.color.set(1, 0.25f);
        _myFont.align = JoglGLUTBitmapFont.LEFT;
        _myFont.position.set(displaycapabilities().width / -2 + 20, displaycapabilities().height / 2 - 20);
        bin(BIN_2D_FOREGROUND).add(_myFont);
    }


    public void mousePressed(int x, int y, int thePressedMouseButton) {
        System.out.println("### blur shader");
        System.out.println("blursize " + _myBlurContext.blur().blursize);
        System.out.println("blurspread " + _myBlurContext.blur().blurspread);
        System.out.println("strength " + _myBlurContext.blur().strength);
    }


    public void loop(float theDeltaTime) {

        CameraMover.handleKeyEvent(_myBlurContext.camera(), event(), theDeltaTime);

        _myFont.text = "FRAMERATE: " + (int) (1 / theDeltaTime);

        /* move cube */
        for (int i = 0; i < _myCube.length; i++) {
            _myCube[i].rotation().x -= 0.01f + theDeltaTime * 0.13f * i / (float) _myCube.length;
            _myCube[i].rotation().y -= 0.01f + theDeltaTime * 0.25f * i / (float) _myCube.length;
            _myCube[i].rotation().z -= 0.01f + theDeltaTime * 0.33f * i / (float) _myCube.length;
        }

        /* blur */
        _myCounter += theDeltaTime * 2;
        _myBlurContext.blur().blurspread = 1f + (float) (Math.sin(_myCounter) + 1) * 0.25f;

        if (event().mouseDown) {
            _myBlurContext.blurdisplay().position().set(event().mouseX, event().mouseY);
        } else {
            _myBlurContext.blurdisplay().position().set(0, 0);
        }
        _myBlurContext.blurdisplay().material().color.set(1, 0.65f);
    }


    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 1024;
        myDisplayCapabilities.height = 512;
        myDisplayCapabilities.backgroundcolor.set(0.2f);
        myDisplayCapabilities.antialiasinglevel = 2;
        new UsingGlur().init(myDisplayCapabilities);
    }
}
