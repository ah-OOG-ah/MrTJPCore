package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import mrtjp.core.color.Colors;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class TabNode extends TNode {
    public int wMin;
    public int hMin;
    public int wMax;
    public int hMax;
    public final int color;

    public TabNode(int wMin, int hMin, int wMax, int hMax) {
        this(wMin, hMin, wMax, hMax, Colors.LIGHT_GREY.rgb);
    }

    public TabNode(int wMin, int hMin, int wMax, int hMax, int color) {

        this.wMin = wMin;
        this.hMin = hMin;
        this.wMax = wMax;
        this.hMax = hMax;
        this.color = color;
    }

    public double currentW = wMin;
    public double currentH = wMin;

    public TabControlNode getControl() {
        return (TabControlNode) parent;
    }

    public Size size = new Size(wMin, hMin);

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    private final Rect startBounds = frame();

    public boolean active = false;
    public boolean isOpen() {
        return active && size.width == wMax && size.height == hMax;
    }

    @Override
    public void drawBack_Impl(Point mouse, float rframe) {
        final int w = (active) ? wMax : wMin;
        final int h = (active) ? hMax : hMin;

        if (w != size.width) currentW += (w - currentW) / 8;
        if (h != size.height) currentH += (h - currentH) / 8;

        size = new Size((int) Math.round(currentW), (int) Math.round(currentH));

        drawBox();
        drawIcon();
        if (isOpen()) {
            drawTab();
            children.forEach(c -> c.setHidden(false));
        } else children.forEach(c -> c.setHidden(true));
    }

    @Override
    public void drawFront_Impl(Point mouse, float rframe) {
        if (rayTest(mouse)) {
            final List<String> list = new ArrayList<>();
            buildToolTip(list);
            GuiDraw.drawMultilineTip(
                mouse.x + 12,
                mouse.y - 12,
                list
            );
        }
    }

    public void drawTab() {}

    public void drawIcon() {}

    public void buildToolTip(List<String> list) {}

    public void drawBox() {
        final float r = (color >> 16 & 255) / 255.0f;
        final float g = (color >> 8 & 255) / 255.0f;
        final float b = (color & 255) / 255.0f;
        GL11.glColor4f(r, g, b, 1);

        GuiLib.drawGuiBox(position.x, position.y, size.width, size.height, 0);
    }

    @Override
    public boolean mouseClicked_Impl(Point p, int button, boolean consumed) {
        if (!consumed && startBounds.contains(p)) {
            getControl().onTabClicked(this);
            return true;
        }
        return false;
    }
}
