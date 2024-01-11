package mrtjp.core.gui;

import mrtjp.core.vec.Point;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

/**
 * Looking for the old TNode? Scala has wack trait stacking, so I had to pull the parts of TNode that used fields into
 * a class, and leave the rest in INode. To (effectively) preserve old behavior, simply make a TNode field and delegate
 * to that while implementing INode.
 */
public class TNode extends Gui implements INode {
    public INode parent;
    public List<INode> children = new ArrayList<>();

    public Point position = Point.zeroPoint;
    public double zPosition = 0;
    public boolean hidden = false;
    public boolean userInteractionEnabled = true;

    @Override
    public List<INode> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<INode> children) {
        this.children = children;
    }

    @Override
    public INode getParent() {
        return this.parent;
    }

    @Override
    public void setParent(INode parent) {
        this.parent = parent;
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public double getZPosition() {
        return this.zPosition;
    }

    @Override
    public void setZPosition(double zPosition) {
        this.zPosition = zPosition;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isUserInteractionEnabled() {
        return userInteractionEnabled;
    }
}
