package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import klaxon.klaxon.descala.Procedure;
import mrtjp.core.color.Colors;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import mrtjp.core.vec.Vec2;

import java.util.function.Supplier;

public class PanNode extends TNode {

    public Size size = Size.zeroSize;

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    public int clampSlack = 0;

    public Vec2 scrollModifier = new Vec2(1, 1);
    public int scrollBarThickness = 4;
    public int scrollBarBGColour = Colors.LIGHT_GREY.argb(0x66);
    public int scrollBarColour = Colors.GREY.argb(0x99);
    public boolean scrollBarVertical = true;
    public boolean scrollBarHorizontal = true;

    public Supplier<Boolean> dragTestFunction = () -> false;
    public Procedure panDelegate = () -> {};

    public boolean debugShowClampBox = false;

    private Rect cFrame = Rect.zeroRect;
    private boolean mouseDown = false;
    private boolean mouseDownRight = false;
    private boolean mouseDownBelow = false;
    private Point lastMousePos = Point.zeroPoint;

    private int raytestMode = 0;

    @Override
    public void frameUpdate_Impl(Point mouse, float rframe) {
        this.cFrame = calculateChildrenFrame();
        final Point delta = mouse.subtract(lastMousePos);
        this.lastMousePos = mouse;

        if (mouseDownRight || mouseDownBelow) {
            final Vec2 sf2 = size.vectorize().divide(cFrame.size.vectorize());
            final Vec2 modVec = (mouseDownRight) ? Vec2.up : Vec2.left;
            panChildren(delta.vectorize().multiply(scrollModifier).divide(sf2).multiply(modVec));
        } else if (mouseDown) {
            panChildren(delta.vectorize().multiply(scrollModifier));
        } else {
            // x disp
            final int l = 0 + clampSlack;
            final int lc = (cFrame.size.width > size.width) ? Math.max(cFrame.origin.x, l) : Math.min(cFrame.origin.x, l);
            final int ld = l - lc;
            final int r = size.width - clampSlack;
            final int rc = (cFrame.size.width > size.width) ? Math.min(cFrame.maxX(), r) : Math.max(cFrame.maxX(), r);
            final int rd = r - rc;

            // y disp
            final int t = 0 + clampSlack;
            final int tc =
            (cFrame.size.height > size.height) ? Math.max(cFrame.origin.y, t) : Math.min(cFrame.origin.y, t);
            final int td = t - tc;
            final int b = size.height - clampSlack;
            final int bc = (cFrame.size.height > size.height) ? Math.min(cFrame.maxY(), b) : Math.max(cFrame.maxY(), b);
            final int bd = b - bc;

            panChildren(new Vec2(ld + rd, td + bd).multiply(0.1).multiply(scrollModifier));
        }
    }

    public void panChildren(Vec2 d) {
        final Vec2 d2 = d;
        if (d2 != Vec2.zeroVec) {
            for (INode c : children) c.setPosition(new Point(c.getPosition().vectorize().add(d2)));
            panDelegate.apply();
        }
    }

    @Override
    public void drawBack_Impl(Point mouse, float rframe) {
        drawScrollBars();
    }

    @Override
    public void drawFront_Impl(Point mouse, float rframe) {}

    private void drawScrollBars() {
        if (scrollBarVertical) {
            GuiDraw.drawRect(
                position.x + size.width - scrollBarThickness,
                position.y,
                scrollBarThickness,
                size.height,
                scrollBarBGColour
            );
            final Rect s = getScrollBarRight();
            GuiDraw.drawRect(s.x(), s.y(), s.width(), s.height(), scrollBarColour);
        }
        if (scrollBarHorizontal) {
            GuiDraw.drawRect(
                position.x,
                position.y + size.height - scrollBarThickness,
                size.width,
                scrollBarThickness,
                scrollBarBGColour
            );
            final Rect s = getScrollBarBelow();
            GuiDraw.drawRect(s.x(), s.y(), s.width(), s.height(), scrollBarColour);
        }
    }

    public Rect getScrollBarRight() {
        if (cFrame.size.height == 0) return Rect.zeroRect;
        final double sf = (double) size.height / cFrame.height();
        final Size s = new Size(scrollBarThickness, (int) (size.height * sf));
        final Point p = new Point(
            position.x + size.width - scrollBarThickness,
            (int) ((position.y - cFrame.y()) * sf)
        );
        return new Rect(p, s);
    }

    public Rect getScrollBarBelow() {
        if (cFrame.size.width == 0) return Rect.zeroRect;
        final double sf = (double) size.width / cFrame.width();
        final Size s = new Size((int) (size.width * sf), scrollBarThickness);
        final Point p = new Point(
                (int) ((position.x - cFrame.x()) * sf),
            position.y + size.height - scrollBarThickness
        );
        return new Rect(p, s);
    }

    private boolean doRayTest(Point p, int mode) {
        raytestMode = mode;
        final boolean hit = rayTest(p);
        raytestMode = 0;
        return hit;
    }

    @Override
    public boolean mouseClicked_Impl(
        Point p,
        int button,
        boolean consumed
    ) {

        if (!consumed) {
            if (scrollBarVertical && doRayTest(p, 1)) mouseDownRight = true;
            else if (scrollBarHorizontal && doRayTest(p, 2)) mouseDownBelow = true;
            else if (doRayTest(p, 3)) mouseDown = true;
            else return false;

            lastMousePos = p;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased_Impl(Point p, int button, boolean consumed) {
        mouseDown = false;
        mouseDownRight = false;
        mouseDownBelow = false;
        return false;
    }

    @Override
    public boolean traceHit(Point absPoint) {
        switch (raytestMode) {
            case 0: return mouseDown || mouseDownRight || mouseDownBelow;
            case 1: return getScrollBarRight().contains(parent.convertPointFromScreen(absPoint));
            case 2: return getScrollBarBelow().contains(parent.convertPointFromScreen(absPoint));
            case 3: return dragTestFunction.get();
            default: throw new RuntimeException("Invalid raytestMode " + raytestMode + "!");
        }
    }
}
