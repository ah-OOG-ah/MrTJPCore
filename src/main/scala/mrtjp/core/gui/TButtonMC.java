package mrtjp.core.gui;

import mrtjp.core.resource.ResourceLib;
import org.lwjgl.opengl.GL11;

/**
 * Trait for buttons that renders their background as a default MC button.
 */
public abstract class TButtonMC extends ButtonNode {

    @Override
    public void drawButtonBackground(boolean mouseover) {
        super.drawButtonBackground(mouseover);

        ResourceLib.guiTex().bind();
        GL11.glColor4f(1, 1, 1, 1);
        final int state = (mouseover) ? 2 : 1;

        drawTexturedModalRect(
            position.x,
            position.y,
            0,
            46 + state * 20,
            size.width / 2,
            size.height / 2
        );
        drawTexturedModalRect(
            position.x + size.width / 2,
            position.y,
            200 - size.width / 2,
            46 + state * 20,
            size.width / 2,
            size.height / 2
        );
        drawTexturedModalRect(
            position.x,
            position.y + size.height / 2,
            0,
            46 + state * 20 + 20 - size.height / 2,
            size.width / 2,
            size.height / 2
        );
        drawTexturedModalRect(
            position.x + size.width / 2,
            position.y + size.height / 2,
            200 - size.width / 2,
            46 + state * 20 + 20 - size.height / 2,
            size.width / 2,
            size.height / 2
        );
    }
}
