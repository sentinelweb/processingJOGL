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


import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;
import gestalt.util.CameraMover;

import data.Resource;


public class UsingANiceModel
    extends AnimatorRenderer {

    public void setup() {
        /* load model */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/person.obj"));
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);

        Model myModel = drawablefactory().model(myModelData, myModelMesh);
        myModel.mesh().material().createTexture(bitmapfactory().getBitmap(Resource.getPath("demo/common/person.png")));
        myModel.mesh().material().lit = true;

        bin(BIN_3D).add(myModel);

        /* camera */
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().lookat().set(0, 150, 0);

        /* setup light */
        light().enable = true;
        light().setPositionRef(camera().position());

        /* set background color */
        displaycapabilities().backgroundcolor.set(0.2f);
    }


    public void loop(final float theDeltaTime) {
        /* update the cameramover */
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
    }


    public static void main(String[] args) {
        new UsingANiceModel().init();
    }
}
