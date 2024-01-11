package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import klaxon.klaxon.descala.Procedure;
import mrtjp.core.resource.ResourceLib;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base button class with position and width/height. Doesn't render anything,
 * nor does it perform action when clicked.
 */
public class ButtonNode extends TNode {

    public Size size = Size.zeroSize;
    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    public Procedure clickDelegate = () -> {};
    public Consumer<List<String>> tooltipBuilder = strings -> {};
    public Procedure drawFunction = () -> {};

    public boolean mouseoverLock = false;

    @Override
    public boolean mouseClicked_Impl(Point p, int button, boolean consumed) {
        if (!consumed && rayTest(p)) {
            ResourceLib.soundButton().play();
            onButtonClicked();
            return true;
        }
        return false;
    }

    public void onButtonClicked() {
        clickDelegate.apply();
    }

    @Override
    public void drawBack_Impl(Point mouse, float rframe) {
        GL11.glColor4f(1, 1, 1, 1);
        final boolean mouseover = mouseoverLock || (frame().contains(mouse) && rayTest(mouse));
        drawButtonBackground(mouseover);
        drawButton(mouseover);
    }

    @Override
    public void drawFront_Impl(Point mouse, float rframe) {
        if (rayTest(mouse)) {
            final List<String> list = new ArrayList<>();
            tooltipBuilder.accept(list);

            // draw tooltip with absolute coords to allow it to force-fit on screen
            translateToScreen();
            final Point m = parent.convertPointToScreen(mouse);
            GuiDraw.drawMultilineTip(m.x + 12, m.y - 12, list);
            translateFromScreen();
        }
    }

    public void drawButtonBackground(boolean mouseover) {}
    public void drawButton(boolean mouseover) {}
}
