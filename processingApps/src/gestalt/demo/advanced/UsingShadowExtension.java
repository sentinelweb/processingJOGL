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


import gestalt.candidates.shadow.JoglMaterialPluginShadowCombiner;
import gestalt.candidates.shadow.JoglMaterialPluginShadowController;
import gestalt.candidates.shadow.JoglShadowMap;
import gestalt.candidates.shadow.JoglShadowMapDisplay;
import gestalt.context.DisplayCapabilities;
import gestalt.impl.jogl.shape.JoglSphere;
import gestalt.render.AnimatorRenderer;
import gestalt.render.plugin.Camera;
import gestalt.shape.Plane;
import gestalt.shape.material.TexturePlugin;
import gestalt.util.CameraMover;

import data.Resource;


/**
 * this demo shows how to use the shadow extension.
 */

public class UsingShadowExtension
    extends AnimatorRenderer {

    /**
     * @todo -- move texture generation to texturemanager
     */

    private JoglShadowMap _myShadowMapExtension;

    private JoglMaterialPluginShadowController _myController;

    public DisplayCapabilities createDisplayCapabilities() {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.backgroundcolor.set(0.5f, 0.5f, 0.5f);
        myDisplayCapabilities.width = 800;
        myDisplayCapabilities.height = 600;
        return myDisplayCapabilities;
    }


    public void setup() {
        /* gestalt */
        framerate(60);

        /* setup shadow map */
        final int myShadowMapWidth = 400;
        final int myShadowMapHeight = 300;
        _myShadowMapExtension = new JoglShadowMap(light(), myShadowMapWidth, myShadowMapHeight, true, false);
        _myShadowMapExtension.shadowcolor.a = 0.5f;
        _myShadowMapExtension.lightcamera.nearclipping = 100;
        _myShadowMapExtension.lightcamera.farclipping = 5000;
        bin(BIN_FRAME_SETUP).add(_myShadowMapExtension);

        /* create plugin */
        _myController = new JoglMaterialPluginShadowController(_myShadowMapExtension);
        _myController.enableShadow();
        JoglMaterialPluginShadowCombiner myCombiner = new JoglMaterialPluginShadowCombiner(_myShadowMapExtension);

        /* create shapes */
        JoglSphere mySphereA = new JoglSphere();
        mySphereA.position().set(100, 50, -100);
        mySphereA.scale().set(100, 100, 100);
        mySphereA.material().lit = true;
        mySphereA.material().color.set(1, 0, 0, 1);
        mySphereA.material().addPlugin(myCombiner);

        JoglSphere mySphereB = new JoglSphere();
        mySphereB.position().set(0, 100, 0);
        mySphereB.scale().set(100, 100, 100);
        mySphereB.material().lit = true;
        mySphereB.material().color.set(1, 0, 0, 1);
        mySphereB.material().addPlugin(myCombiner);

        JoglSphere mySphereC = new JoglSphere();
        mySphereC.position().set(100, 350, 0);
        mySphereC.scale().set(100, 100, 100);
        mySphereC.material().lit = true;
        mySphereC.material().color.set(1, 0.5f, 0, 1);
        mySphereC.material().addPlugin(myCombiner);

        Plane myPlane = drawablefactory().plane();
        myPlane.scale().set(1000, 1000, 1);
        myPlane.rotation().x = -PI_HALF;
        myPlane.material().lit = true;
        myPlane.material().color.set(1, 1, 1, 1);
        myPlane.material().normalizenormals = true;

        TexturePlugin myTexture = drawablefactory().texture();
        myTexture.load(bitmapfactory().getBitmap(Resource.getStream("demo/common/mask256.png")));
        myPlane.material().addPlugin(myTexture);
        myPlane.material().transparent = true;

        /**
         * @todo
         * there is an order issue with the combiner and controller used in combination.
         * if the combiner is plugged before the controller, the effect of switch off
         * the controller yields undesirable combinder results.
         */

        myPlane.material().addPlugin(_myController);
        myPlane.material().addPlugin(myCombiner);

        /* add shapes to bins */
        bin(BIN_3D).add(mySphereA);
        bin(BIN_3D).add(mySphereB);
        bin(BIN_3D).add(mySphereC);
        bin(BIN_3D).add(myPlane);

        _myShadowMapExtension.addShape(mySphereA);
        _myShadowMapExtension.addShape(mySphereB);
        _myShadowMapExtension.addShape(mySphereC);

        /* light */
        light().enable = true;
        light().position().set(450, 720, 230);
        light().diffuse.set(1, 1, 1, 1);
        light().ambient.set(0, 0, 0, 1);

        /* camera() */
        camera().position().set( -400, 1000, 1000);
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* display shadowmap */
        JoglShadowMapDisplay myDisplay = new JoglShadowMapDisplay(_myShadowMapExtension,
                                                                  myShadowMapWidth,
                                                                  myShadowMapHeight);
        myDisplay.scale().scale(0.5f);
        myDisplay.material().color.a = 0.5f;
        myDisplay.position().set(displaycapabilities().width / -2,
                                 displaycapabilities().height / 2);
        myDisplay.position().x += myDisplay.scale().x / 2;
        myDisplay.position().y += myDisplay.scale().y / -2;
        bin(BIN_2D_FOREGROUND).add(myDisplay);
    }


    private boolean toggleCamera;

    public void loop(float theDeltaTime) {

        /* toggle camera()s */
        if (event().keyPressed && event().key == 'c') {
            System.out.println("toggled camera().");
            toggleCamera = !toggleCamera;
        }

        Camera myCamera;
        if (toggleCamera) {
            myCamera = _myShadowMapExtension.lightcamera;
        } else {
            myCamera = camera();
        }

        /* set shadow color */
        _myShadowMapExtension.shadowcolor.set( (float) event().mouseX / (float) displaycapabilities().width);
        _myShadowMapExtension.shadowcolor.a = (float) event().mouseY / (float) displaycapabilities().height;

        /* use controller plugin */
        if (event().mouseClicked) {
            if (event().mouseButton == MOUSEBUTTON_LEFT) {
                _myController.enableShadow();
            }
            if (event().mouseButton == MOUSEBUTTON_RIGHT) {
                _myController.disableShadow();
            }
        }

        /* move camera */
        CameraMover.handleKeyEvent(myCamera, event(), theDeltaTime);

        /* toggle camera modes */
        if (event().keyPressed) {
            if (event().key == '1') {
                myCamera.setMode(CAMERA_MODE_LOOK_AT);
            }
            if (event().key == '2') {
                myCamera.setMode(CAMERA_MODE_ROTATE_XYZ);
            }
            if (event().key == 'i') {
                myCamera.frustumoffset.y += 0.1f;
            }
            if (event().key == 'k') {
                myCamera.frustumoffset.y -= 0.1f;
            }
            if (event().key == 'u') {
                myCamera.farclipping += 100f;
            }
            if (event().key == 'j') {
                myCamera.farclipping -= 100f;
            }
            if (event().key == 'z') {
                myCamera.nearclipping += 10f;
            }
            if (event().key == 'h') {
                myCamera.nearclipping -= 10f;
            }
        }
    }


    public static void main(String[] arg) {
        new UsingShadowExtension().init();
    }
}
