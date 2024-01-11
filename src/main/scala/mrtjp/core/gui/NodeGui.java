package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.input.Mouse;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTranslated;

public class NodeGui extends GuiContainer implements INode {

    private final TNode delegate = new TNode();

    public int w;
    public int h;

    public NodeGui(Container c) {
        this(c, 176, 166);
    }
    public NodeGui(int x, int y) {
        this(new NodeContainer(), x, y);
    }

    public NodeGui(Container c, int w, int h) {
        super(c);
        this.w = w;
        this.h = h;

        this.xSize = w;
        this.ySize = h;
    }

    public boolean debugDrawFrames = false;

    public Size size = Size.zeroSize;
    @Override
    public Rect frame() {
        return new Rect(getPosition(), size);
    }

    @Override
    public void initGui() {
        super.initGui();
        setPosition(new Point(guiLeft, guiTop));
        if (size == Size.zeroSize)
            size = new Size(xSize, ySize); // TODO Legacy (size should be set directly)
        else {
            xSize = size.width;
            ySize = size.height;
        }
    }

    @Override
    public final void updateScreen() {
        super.updateScreen();
        update();
    }

    @Override
    public final void setWorldAndResolution(Minecraft mc, int i, int j) {
        final boolean init = this.mc == null;
        super.setWorldAndResolution(mc, i, j);
        if (init) onAddedToParent_Impl();
    }

    @Override
    public final void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        mouseClicked(new Point(x, y), button, false);
    }

    @Override
    public final void mouseMovedOrUp(int x, int y, int button) {
        super.mouseMovedOrUp(x, y, button);
        if (button != -1) mouseReleased(new Point(x, y), button, false);
    }

    @Override
    public final void mouseClickMove(int x, int y, int button, long time) {
        super.mouseClickMove(x, y, button, time);
        mouseDragged(new Point(x, y), button, time, false);
    }

    @Override
    public final void handleMouseInput() {
        super.handleMouseInput();
        final int i = Mouse.getEventDWheel();
        if (i != 0) {
            final java.awt.Point p = GuiDraw.getMousePosition();
            mouseScrolled(new Point(p.x, p.y), (i > 0) ? 1 : -1, false);
        }
    }

    @Override
    public final void keyTyped(char c, int keycode) {
        if (keyPressed(c, keycode, false)) return;

        super.keyTyped(c, keycode);
    }

    public boolean isClosingKey(int keycode) {
        return keycode == 1 || keycode == mc.gameSettings.keyBindInventory.getKeyCode(); // esc or inv key
    }

    /** Front/back rendering overridden, because at root, we dont push the
     * children to our pos, because its zero.
     */
    private float lastFrame = 0.0f;
    @Override
    public final void drawGuiContainerBackgroundLayer(
        float f,
        int mx,
        int my
    ) {
        lastFrame = f;
        final Point mouse = new Point(mx, my);
        frameUpdate(mouse, f);
        glDisable(GL_DEPTH_TEST);
        glColor4d(1, 1, 1, 1);
        rootDrawBack(mouse, f);
        glColor4d(1, 1, 1, 1);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public final void drawGuiContainerForegroundLayer(int mx, int my) {
        final Point mouse = new Point(mx, my);
        glDisable(GL_DEPTH_TEST);
        glColor4d(1, 1, 1, 1);
        rootDrawFront(mouse, lastFrame);
        glColor4d(1, 1, 1, 1);
        glEnable(GL_DEPTH_TEST);

        if (debugDrawFrames) {
            glTranslated(-getPosition().x, -getPosition().y, 0);

            for (INode c : getChildren()) render(c);
            glTranslated(getPosition().x, getPosition().y, 0);
        }
    }

    private void render(INode node) {
        if (!node.isHidden()) {
            final Rect f = node.frame();
            final Rect absF = new Rect(node.getParent().convertPointToScreen(f.origin), f.size);
            GuiLib.drawLine(absF.x(), absF.y(), absF.x(), absF.maxY());
            GuiLib.drawLine(absF.x(), absF.maxY(), absF.maxX(), absF.maxY());
            GuiLib.drawLine(absF.maxX(), absF.maxY(), absF.maxX(), absF.y());
            GuiLib.drawLine(absF.maxX(), absF.y(), absF.x(), absF.y());
        }
        for (INode c : node.getChildren()) render(c);
    }

    @Override
    public List<INode> getChildren() {
        return delegate.getChildren();
    }

    @Override
    public void setChildren(List<INode> children) {
        delegate.setChildren(children);
    }

    @Override
    public INode getParent() {
        return delegate.getParent();
    }

    @Override
    public void setParent(INode parent) {
        delegate.setParent(parent);
    }

    @Override
    public Point getPosition() {
        return delegate.getPosition();
    }

    @Override
    public void setPosition(Point position) {
        delegate.setPosition(position);
    }

    @Override
    public double getZPosition() {
        return delegate.getZPosition();
    }

    @Override
    public void setZPosition(double zPosition) {
        delegate.setZPosition(zPosition);
    }

    @Override
    public boolean isHidden() {
        return delegate.isHidden();
    }

    @Override
    public void setHidden(boolean hidden) {
        delegate.setHidden(hidden);
    }

    @Override
    public boolean isUserInteractionEnabled() {
        return delegate.isUserInteractionEnabled();
    }
}
