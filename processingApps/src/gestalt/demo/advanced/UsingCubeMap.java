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


import gestalt.candidates.materialplugin.JoglMaterialPluginCubeMap;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;

import data.Resource;


/**
 * this demo shows how to use cube maps.
 * cube mapping is a technique that takes a three dimensional texture coordinate
 * and returns a texel from a given cube map.
 *
 * here we used it, to create reflections on a model
 *
 * thanks to humus.ca for the textures
 */

public class UsingCubeMap
    extends AnimatorRenderer {

    private Model _myMesh;

    public void setup() {
        /*
         * create the cube map material plugin
         * you need to load six bitmaps. one for each side of the cube
         */
        JoglMaterialPluginCubeMap myCubeMap = new JoglMaterialPluginCubeMap();
        myCubeMap.load(bitmapfactory().getBitmap(Resource.getStream("demo/common/cube_negx.png")),
                       bitmapfactory().getBitmap(Resource.getStream("demo/common/cube_posx.png")),
                       bitmapfactory().getBitmap(Resource.getStream("demo/common/cube_negy.png")),
                       bitmapfactory().getBitmap(Resource.getStream("demo/common/cube_posy.png")),
                       bitmapfactory().getBitmap(Resource.getStream("demo/common/cube_negz.png")),
                       bitmapfactory().getBitmap(Resource.getStream("demo/common/cube_posz.png")));

        /* crate the model */
        _myMesh = createModel();

        /* add material plugin to the material of the mesh */
        _myMesh.mesh().material().addPlugin(myCubeMap);
        _myMesh.mesh().position().set(0, 0, -200);
        bin(BIN_3D).add(_myMesh);

        System.out.println("### INFO: move your mouse around to control the rotation of the shape");
    }


    public void loop(final float theDeltaTime) {
        /* rotation of the models depends on the mouse position */
        _myMesh.mesh().rotation().add( (float) event().mouseX / displaycapabilities().width / 10f,
                                      (float) event().mouseY / displaycapabilities().height / 10f,
                                      0f);
    }


    private Model createModel() {
        /* load model data from .obj file */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("demo/common/weirdobject.obj"));

        /*
         * this method calculates the normals in a way, that each same vertices have the same normal
         * NOTE: the algorithm is not very efficient. big models will take a while. seriously.
         */
        myModelData.averageNormals();

        /* get mesh */
        Mesh myModelMesh = drawablefactory().mesh(true,
                                                  myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);
        Model myModel = drawablefactory().model(myModelData, myModelMesh);

        /*
         * make the model not transparent. as the vertices came in random order, you can t apply
         * any depthsorting anyways.
         */
        myModelMesh.material().transparent = false;

        return myModel;
    }


    public static void main(String[] args) {
        new UsingCubeMap().init();
    }
}
