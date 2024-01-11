package mrtjp.core.gui;

import mrtjp.core.resource.ResourceLib;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Size;
import org.lwjgl.opengl.GL11;

/**
 *  Button that is used for selection.
 */
public class DotSelectNode extends ButtonNode {

    public DotSelectNode() {
        this.size = new Size(8, 8);
    }

    @Override
    public void drawButtonBackground(boolean mouseover) {
        super.drawButtonBackground(mouseover);
        ResourceLib.guiExtras().bind();
        GL11.glColor4f(1, 1, 1, 1);
        drawTexturedModalRect(
            position.x,
            position.y,
            (mouseover) ? 11 : 1,
            1,
            8,
            8
        );
    }

    public static DotSelectNode centered(int x, int y) {
        final DotSelectNode b = new DotSelectNode();
        b.position = new Point(x, y).subtract(4);
        return b;
    }
}
