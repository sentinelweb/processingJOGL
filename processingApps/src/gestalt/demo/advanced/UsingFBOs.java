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


import gestalt.candidates.JoglTextureReader;
import gestalt.candidates.rendertotexture.JoglFrameBufferObject;
import gestalt.candidates.rendertotexture.JoglTexCreatorFBO_DepthRGBA;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.render.plugin.Camera;
import gestalt.render.plugin.FrameSetup;
import gestalt.shape.Cube;
import gestalt.shape.Plane;
import gestalt.util.CameraMover;
import gestalt.util.ImageUtil;


/**
 * this demo shows how to use framebuffer objects (FBO).
 * FBOs are the best way to create an offscreen rendercontext.
 *
 * this demo also uses the 'JoglTextureReader' which reads a
 * texture in OpenGL memory back into the CPU. this can
 * be very, very slow.
 */

public class UsingFBOs
    extends AnimatorRenderer {

    private Cube _myCube;

    private JoglFrameBufferObject _myFBO;

    private Plane _myPlane;

    private JoglTextureReader _myReader;

    public void setup() {
        /* g1 */
        framerate(60);
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create cube */
        _myCube = drawablefactory().cube();
        _myCube.material().color.set(0.75f);
        _myCube.scale().set(300, 100, 100);
        bin(BIN_3D).add(_myCube);

        /* create FBO and add cube */
        _myFBO = setupFBO();
        _myFBO.add(_myCube);
        bin(BIN_ARBITRARY).add(_myFBO);

        /* create plane to show FBO */
        _myPlane = drawablefactory().plane();
        _myPlane.material().addPlugin(_myFBO);
        _myPlane.scale().set(100, 100);
        _myPlane.position().set(10 + displaycapabilities().width / -2,
                                -10 + displaycapabilities().height / 2);
        _myPlane.origin(SHAPE_ORIGIN_TOP_LEFT);
        bin(BIN_3D).add(_myPlane);

        /* get texture data back from opengl memory */
        _myReader = new JoglTextureReader(_myFBO);
        _myReader.setActive(false);
        bin(BIN_ARBITRARY).add(_myReader);
    }


    private JoglFrameBufferObject setupFBO() {
        int myFBOWidth = 512;
        int myFBOHeight = 512;

        /* create a camera for the framebuffer object */
        Camera myCamera = drawablefactory().camera();
        myCamera.position().z = myFBOHeight;
        myCamera.viewport().width = myFBOWidth;
        myCamera.viewport().height = myFBOHeight;
        myCamera.farclipping = myFBOHeight * 2;

        /* create framebuffer object */
        JoglFrameBufferObject myFBO = new JoglFrameBufferObject(myFBOWidth,
                                                                myFBOHeight,
                                                                myCamera,
                                                                new JoglTexCreatorFBO_DepthRGBA());
        /* set backgroundcolor of FBO */
        myFBO.backgroundcolor().set(1, 0, 0, 0.5f);

        /* add camera to framebuffer object renderbin */
        myFBO.add(myCamera);

        /* create a framesetup for the FBO the clears the screen */
        FrameSetup myFrameSetup = drawablefactory().frameSetup();
        myFrameSetup.colorbufferclearing = true;
        myFrameSetup.depthbufferclearing = true;
        myFBO.add(myFrameSetup);

        return myFBO;
    }


    public void loop(float theDeltaTime) {
        /* move camera */
        CameraMover.handleKeyEvent(_myFBO.camera(), event(), theDeltaTime);

        /* move cube */
        _myCube.rotation().y -= 0.01f;
        _myCube.rotation().z -= 0.013f;

        /* en/disable texture reader */
        if (event().mouseClicked && event().mouseButton == MOUSEBUTTON_RIGHT) {
            _myReader.setActive(!_myReader.isActive());
        }

        /* display last read texture */
        if (_myReader.isActive()) {
            if (event().keyPressed && event().keyCode == KEYCODE_SPACE) {
                ImageUtil.displayBitmap(_myReader.bitmap(), "", false);
            }
        }
    }


    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 640;
        myDisplayCapabilities.height = 480;
        new UsingFBOs().init(myDisplayCapabilities);
    }
}
