package mrtjp.core.gui;

import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static klaxon.klaxon.descala.Streams.foldLeft;

public interface INode {

    default Rect frame() {
        return new Rect(getPosition(), Size.zeroSize);
    }

    List<INode> getChildren();
    void setChildren(List<INode> children);

    INode getParent();
    void setParent(INode parent);

    Point getPosition();

    void setPosition(Point position);

    double getZPosition();
    void setZPosition(double z);

    boolean isHidden();
    void setHidden(boolean hidden);

    boolean isUserInteractionEnabled();

    default List<INode> buildParentHierarchy(INode to) {
        List<INode> hierarchy = new ArrayList<>();

        INode tmp = this;
        do {
            hierarchy.add(tmp);
            tmp = tmp.getParent();
        } while (tmp != null && !tmp.isRoot() && tmp != to);

        return hierarchy;
    }

    default Rect calculateAccumulatedFrame() {
        return frame().union(calculateChildrenFrame());
    }

    default Rect calculateChildrenFrame() {
        final Rect rect = (getChildren().isEmpty()) ? Rect.zeroRect :
            getChildren().stream().filter(c -> !c.isHidden()).map(INode::calculateAccumulatedFrame).reduce(Rect::union).get();
        return new Rect(convertPointTo(rect.origin, getParent()), rect.size);
    }

    default List<INode> childrenByZ() {
        return getChildren().stream().sorted(Comparator.comparingDouble(INode::getZPosition)).collect(Collectors.toList());
    }

    default Point convertPointTo(Point p, INode to) {

        if (this == to) return p;
        else if (this.isDecendantOf(to)) {

            final List<INode> list = buildParentHierarchy(to);
            list.remove(list.size() - 1); // dropRight(1)
            return foldLeft(p, list, (point, tNode) -> point.add(tNode.getPosition()));
        } else if (to.isDecendantOf(this)) {

            // TODO sibling conversion by conv to screen, then conv from screen on other node
            final List<INode> list = to.buildParentHierarchy(this);
            list.remove(list.size() - 1); // dropRight(1)
            return foldLeft(p, list, (point, tNode) -> point.subtract(tNode.getPosition()));
        } else
            throw new RuntimeException("Attempted to convert points between unrelated nodes.");
    }

    default Point convertPointFrom(Point p, INode from) {
        return from.convertPointTo(p, this);
    }

    default Point convertPointToScreen(Point p) {
        return getRoot().getPosition().add(convertPointTo(p, getRoot()));
    }

    default void drawBack(Point mouse, float rframe) {
        if (!isHidden()) {
            final Point dp = mouse.subtract(getPosition());
            for (INode n : familyByZ()) {
                if (n == this) drawBack_Impl(mouse, rframe);
                else {
                    translateTo();
                    n.drawBack(dp, rframe);
                    translateFrom();
                }
            }
        }
    }

    default void drawFront(Point mouse, float rframe) {
        if (!isHidden()) {
            final Point dp = mouse.subtract(getPosition());
            for (INode n : familyByZ()) {
                if (n == this) drawFront_Impl(mouse, rframe);
                else {
                    translateTo();
                    n.drawFront(dp, rframe);
                    translateFrom();
                }
            }
        }
    }

    default List<INode> familyByZ() {
        final List<INode> ret = new ArrayList<>();
        ret.add(this);
        ret.addAll(getChildren());
        ret.sort(Comparator.comparingDouble(INode::getZPosition));
        return ret;
    }

    default NodeGui getRoot() {

        INode root = this;
        while (root != null && !(root instanceof NodeGui)) {
            root = root.getParent();
        }

        if (root == null) throw new RuntimeException("Gui not found");
        return (NodeGui) root;
    }

    default List<INode> hitTest(Point point) {
        if (getParent() == null)
            throw new RuntimeException("Cannot hittest a node without a parent.");
        if (isRoot()) throw new RuntimeException("Cannot hittest a root node.");

        List<INode> test = new ArrayList<>();
        final Point ap = getParent().convertPointToScreen(point);
        for (INode c : getRoot().subTree(true))
            if (c.traceHit(ap)) test.add(c);

        final List<INode> ret = test.stream().sorted(Comparator.comparingDouble(INode::getZPosition)).collect(Collectors.toList());
        Collections.reverse(ret);
        return ret;
    }

    default boolean isDecendantOf(INode someParent) {
        return someParent != this && buildParentHierarchy(someParent).contains(someParent);
    }

    default boolean isRoot() {
        return this instanceof NodeGui;
    }

    default boolean mouseClicked(
        Point p,
        int button,
        boolean consumed
    ) {
        if (isHidden() || !isUserInteractionEnabled()) return false;
        final Point dp = p.subtract(getPosition());
        final List<INode> tmp = familyByZ();
        Collections.reverse(tmp);
        return foldLeft(consumed, tmp, (c1, w) -> ((w == this) ? ((Function<Boolean, Boolean>) c -> mouseClicked_Impl(p, button, c)).apply(c1) : ((BiFunction<INode, Boolean, Boolean>) (n, c2) -> n.mouseClicked(dp, button, c2)).apply(w, c1)) || c1);
    }

    default boolean mouseReleased(
        Point p,
        int button,
        boolean consumed
    ) {
        if (isHidden() || !isUserInteractionEnabled()) return false;
        final Point dp = p.subtract(getPosition());
        final List<INode> tmp = familyByZ();
        Collections.reverse(tmp);
        return foldLeft(consumed, tmp, (c1, w) -> ((w == this) ? ((Function<Boolean, Boolean>) c -> mouseReleased_Impl(p, button, c)).apply(c1) : ((BiFunction<INode, Boolean, Boolean>) (n, c2) -> n.mouseReleased(dp, button, c2)).apply(w, c1)) || c1);
    }

    default boolean mouseDragged(
        Point p,
        int button,
        long time,
        boolean consumed
    ) {
        if (isHidden() || !isUserInteractionEnabled()) return false;
        final Point dp = p.subtract(getPosition());
        final List<INode> tmp = familyByZ();
        Collections.reverse(tmp);
        return foldLeft(consumed, tmp, (c1, w) -> ((w == this) ? ((Function<Boolean, Boolean>) c -> mouseDragged_Impl(p, button, time, c)).apply(c1) : ((BiFunction<INode, Boolean, Boolean>) (n, c2) -> n.mouseDragged(dp, button, time, c2)).apply(w, c1)) || c1);
    }

    default boolean mouseScrolled(
        Point p,
        int dir,
        boolean consumed
    ) {
        if (isHidden() || !isUserInteractionEnabled()) return false;
        final Point dp = p.subtract(getPosition());
        final List<INode> tmp = familyByZ();
        Collections.reverse(tmp);
        return foldLeft(consumed, tmp, (c1, w) -> ((w == this) ? ((Function<Boolean, Boolean>) c -> mouseScrolled_Impl(p, dir, c)).apply(c1) : ((BiFunction<INode, Boolean, Boolean>) (n, c2) -> n.mouseScrolled(dp, dir, c2)).apply(w, c1)) || c1);
    }

    default boolean keyPressed(
        char ch,
        int keycode,
        boolean consumed
    ) {
        if (isHidden() || !isUserInteractionEnabled()) return false;
        final List<INode> tmp = familyByZ();
        Collections.reverse(tmp);
        return foldLeft(consumed, tmp, (c1, w) -> ((w == this) ? ((Function<Boolean, Boolean>) c -> keyPressed_Impl(ch, keycode, c)).apply(c1) : ((BiFunction<INode, Boolean, Boolean>) (n, c2) -> n.keyPressed(ch, keycode, c2)).apply(w, c1)) || c1);
    }

    /**
     * @deprecated Use delegation
     */
    @Deprecated
    default void startMessageChain(String message) {
        if (!isRoot()) getParent().receiveMessage(message);
    }

    /**
     * @deprecated Use delegation
     */
    @Deprecated
    default void receiveMessage(String message) {
        receiveMessage_Impl(message);
        if (!isRoot()) getParent().receiveMessage(message);
    }

    default void removeFromParent() {
        getParent().setChildren(getParent().getChildren().stream().filter(p -> p != this).collect(Collectors.toList()));
        setParent(null);
    }

    default boolean traceHit(Point absPoint) {
        final Rect f = frame();
        final Rect af = new Rect(getParent().convertPointToScreen(f.origin), f.size);
        return af.contains(absPoint);
    }

    default void translateTo() {
        GL11.glTranslated(getPosition().x, getPosition().y, 0);
    } // zPosition-(if (parent == null) 0 else parent.zPosition))}
    default void translateFrom() {
        GL11.glTranslated(-getPosition().x, -getPosition().y, 0);
    } // -(zPosition-(if (parent == null) 0 else parent.zPosition)))}

    default Minecraft mcInst() {
        return Minecraft.getMinecraft();
    }
    default TextureManager renderEngine() {
        return mcInst().renderEngine;
    }
    default FontRenderer fontRenderer() {
        return mcInst().fontRenderer;
    }

    default Point convertPointFromScreen(Point p) {
        return convertPointFrom(p, getRoot()).subtract(getRoot().getPosition());
    }

    default Rect convertRectTo(Rect r, INode to) {
        return new Rect(convertPointTo(r.origin, to), r.size);
    }
    default Rect convertRectFrom(Rect r, INode from) {
        return new Rect(convertPointFrom(r.origin, from), r.size);
    }
    default Rect convertRectToScreen(Rect r) {
        return new Rect(convertPointToScreen(r.origin), r.size);
    }
    default Rect convertRectFromScreen(Rect r) {
        return new Rect(convertPointFromScreen(r.origin), r.size);
    }

    default boolean rayTest(Point point) {
        final List<INode> s = hitTest(point);
        return  !s.isEmpty() && s.get(0) == this;
    }

    default List<INode> subTree() {
        return subTree(false);
    }

    default List<INode> subTree(boolean activeOnly) {

        final List<INode> s = new ArrayList<>();
        if (!activeOnly || (!isHidden() && isUserInteractionEnabled())) gather(s, getChildren(), activeOnly);
        return s;
    }

    default void gather(List<INode> s, List<INode> children, boolean activeOnly) {
        final List<INode> ac = (activeOnly) ? children.stream().filter(c -> !c.isHidden() && c.isUserInteractionEnabled()).collect(Collectors.toList()) : children;
        s.addAll(ac);
        for (INode c : ac) gather(s, c.getChildren(), activeOnly);
    }

    default void pushZTo(double z) {
        pushZBy(z - getZPosition());
    }

    default void pushZBy(double z) {
        List<INode> tmp = subTree();
        tmp.add(this);
        for (INode c : tmp)
            c.setZPosition(c.getZPosition() + z);
    }

    default void addChild(INode w) {
        w.setParent(this);
        getChildren().add(w);
        w.onAddedToParent_Impl();
    }

    default void rootDrawBack(Point mouse, float rframe) {
        if (!isHidden()) {
            translateTo();
            final Point dp = mouse.subtract(getPosition());
            for (INode n : familyByZ()) {
                if (n == this) drawBack_Impl(mouse, rframe);
                else n.drawBack(dp, rframe);
            }
            translateFrom();
        }
    }

    default void rootDrawFront(Point mouse, float rframe) {
        if (!isHidden()) {
            final Point dp = mouse.subtract(getPosition());
            for (INode n : familyByZ()) {
                if (n == this) drawFront_Impl(mouse, rframe);
                else n.drawFront(dp, rframe);
            }
        }
    }

    default void translateToScreen() {
        final Point s = getParent().convertPointToScreen(Point.zeroPoint);
        GL11.glTranslated(-s.x, -s.y, 0);
    }
    default void translateFromScreen() {
        final Point s = getParent().convertPointToScreen(Point.zeroPoint);
        GL11.glTranslated(s.x, s.y, 0);
    }

    /**
     * Called when this node is added to another as a child. Should be used as
     * the main override point for initialization.
     */
    default void onAddedToParent_Impl() {};

    /**
     * Called when the mouse button is clicked.
     *
     * @param p
     *   The current position of the mouse, relative to the parent.
     * @param button
     *   The button that was clicked. 0 is left button, 1 is right.
     * @param consumed
     *   If another node has consumed this event.
     * @return
     *   If this node has consumed this event.
     */
    default boolean mouseClicked_Impl(Point p, int button, boolean consumed) {
        return false;
    }

    /**
     * Called when the mouse button is released.
     * @param p
     *   The current position of the mouse, relative to the parent.
     * @param button
     *   The button that was released. 0 is left button, 1 is right.
     * @param consumed
     *   If another node has consumed this event.
     * @return
     *   If this node has consumed this event.
     */
    default boolean mouseReleased_Impl(Point p, int button, boolean consumed) {
        return false;
    }

    /**
     * Called constantly while the mouse is held down.
     * @param p
     *   The current position of the mouse, relative to the parent.
     * @param button
     *   The button that is currently held down. 0 is left, 1 is right
     * @param time
     *   Amount of time the button has been held down for.
     * @param consumed
     *   If another node has consumed this event.
     * @return
     *   If this node has consumed this event.
     */
    default boolean mouseDragged_Impl(Point p, int button, long time, boolean consumed) {
        return false;
    }

    /**
     * Called when the mouse wheel is scrolled.
     * @param p
     *   The current position of the mouse, relative to the parent.
     * @param dir
     *   The direction of scroll. Negative for down, positive for up.
     * @param consumed
     *   If another node has consumed this event.
     * @return
     *   If this node has consumed this event.
     */
    default boolean mouseScrolled_Impl(Point p, int dir, boolean consumed) {
        return false;
    }

    /**
     * Called when a key is pressed on the keyboard.
     * @param c
     *   The charecter that was pressed.
     * @param keycode
     *   The keycode for the button that was pressed.
     * @param consumed
     *   If another node has consumed this event.
     * @return
     *   If this node has consumed this event.
     */
    default boolean keyPressed_Impl(char c, int keycode, boolean consumed) {
        return false;
    }

    /**
     * Called every frame before background draw call
     * @param mouse
     *   The current position of the mouse, relative to the parent.
     * @param rframe
     *   The partial frame until the next frame.
     */
    default void frameUpdate_Impl(Point mouse, float rframe) {}

    /**
     * Called every tick from the main game loop.
     */
    default void update_Impl() {}

    /**
     * Called to draw the background. All drawing is done relative to the parent,
     * as GL11 is translated to the parents position during this operation.
     * However, for the root node, drawing is relevant to itself.
     * @param mouse
     *   The current position of the mouse, relative to the parent.
     * @param rframe
     *   The partial frame until the next frame.
     */
    default void drawBack_Impl(Point mouse, float rframe) {}

    /**
     * Called to draw the foreground. All drawing is done relative to the parent,
     * as GL11 is translated to the parents position during this operation.
     * However, for the root node, drawing is relevant to itself.
     * @param mouse
     *   The current position of the mouse, relative to the parent.
     * @param rframe
     *   The partial frame until the next frame.
     */
    default void drawFront_Impl(Point mouse, float rframe) {}

    /**
     * Called when a subnode sends a message. This message is relayed to all
     * supernodes one by one and stops at the root node.
     *
     * @param message
     *   The message that was sent by a subnode using startMessageChain
     * @deprecated Use delegation
     */
    @Deprecated
    default void receiveMessage_Impl(String message) {}

    default void frameUpdate(Point mouse, float rframe) {
        frameUpdate_Impl(mouse, rframe);
        for (INode c : childrenByZ()) c.frameUpdate(mouse.subtract(getPosition()), rframe);
    }

    default void update() {
        update_Impl();
        for (INode c : childrenByZ()) c.update();
    }
}
