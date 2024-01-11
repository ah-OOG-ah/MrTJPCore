package mrtjp.core.gui;

import mrtjp.core.resource.ResourceLib;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Size;

/**
 * Check box button that has either an on or off state.
 */
public class CheckBoxNode extends TButtonMC {

    public CheckBoxNode() {
        this.size = new Size(14, 14);
    }

    public boolean state = false;

    @Override
    public void drawButton(boolean mouseover) {
        super.drawButton(mouseover);
        ResourceLib.guiExtras().bind();
        final int u = (state) ? 17 : 1;
        drawTexturedModalRect(position.x, position.y, u, 134, 14, 14);
    }

    @Override
    public void onButtonClicked() {
        state = !state;
        super.onButtonClicked();
    }

    public static CheckBoxNode centered(int x, int y) {
        final CheckBoxNode b = new CheckBoxNode();
        b.position = new Point(x, y).subtract(4);
        return b;
    }
}
