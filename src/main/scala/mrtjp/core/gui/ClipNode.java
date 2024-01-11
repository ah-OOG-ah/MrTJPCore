package mrtjp.core.gui;

import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import net.minecraft.client.gui.ScaledResolution;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_BIT;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glScissor;

public class ClipNode extends TNode {

    public Size size = Size.zeroSize;

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    @Override
    public void drawBack(Point mouse, float rframe) {
        if (!hidden) {
            final Point dp = mouse.subtract(position);
            for (INode n : familyByZ()) {
                if (n == this) drawBack_Impl(mouse, rframe);
                else {
                    onChildPredraw();
                    translateTo();
                    n.drawBack(dp, rframe);
                    translateFrom();
                    onChildPostdraw();
                }
            }
        }
    }

    @Override
    public void drawFront(Point mouse, float rframe) {
        if (!hidden) {
            final Point dp = mouse.subtract(position);
            for (INode n : familyByZ()) {
                if (n == this) drawFront_Impl(mouse, rframe);
                else {
                    onChildPredraw();
                    translateTo();
                    n.drawFront(dp, rframe);
                    translateFrom();
                    onChildPostdraw();
                }
            }
        }
    }

    private void onChildPredraw() {
        final ScaledResolution scaleRes =
            new ScaledResolution(mcInst(), mcInst().displayWidth, mcInst().displayHeight);
        final int scale = scaleRes.getScaleFactor();

        final Point absPos = parent.convertPointToScreen(position);
        final Rect sFrame = new Rect(
            absPos.x * scale,
            mcInst().displayHeight - (absPos.y * scale) - size.height * scale,
            size.width * scale,
            size.height * scale
        );

        glEnable(GL_SCISSOR_TEST);
        glScissor(sFrame.x(), sFrame.y(), sFrame.width(), sFrame.height());
    }

    private void onChildPostdraw() {
        glDisable(GL_SCISSOR_TEST);
    }

    @Override
    public boolean traceHit(Point absPoint) {
        return  !super.traceHit(absPoint); // only let hits within frame pass through
    }

    @Override
    public boolean mouseScrolled_Impl(Point p, int dir, boolean consumed) {
        return !frame().contains(p);
    }

    @Override
    public boolean mouseClicked_Impl(Point p, int button, boolean consumed) {
        return !frame().contains(p);
    }

    public static void tempDisableScissoring() {
        glPushAttrib(GL_SCISSOR_BIT);
        glDisable(GL_SCISSOR_TEST);
    }

    public static void tempEnableScissoring() {
        glPopAttrib();
    }
}
