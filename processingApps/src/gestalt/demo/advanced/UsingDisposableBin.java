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


import gestalt.candidates.JoglDisposableBin;
import gestalt.render.AnimatorRenderer;

import com.sun.opengl.util.GLUT;


public class UsingDisposableBin
    extends AnimatorRenderer {

    private JoglDisposableBin g;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);

        g = new JoglDisposableBin();
        bin(BIN_3D).add(g);
    }


    public void loop(float theDeltaTime) {
        g.color(1, 0, 0, 1);
        g.font(GLUT.BITMAP_HELVETICA_18);
        g.text("001", event().mouseX, event().mouseY);

        g.color(1, 1);
        g.font(GLUT.BITMAP_HELVETICA_10);
        g.text("MY WORLD", event().mouseX, event().mouseY - 10);

        g.circle(event().mouseX, event().mouseY, 0, 50);
    }


    public static void main(String[] args) {
        new UsingDisposableBin().init();
    }
}
