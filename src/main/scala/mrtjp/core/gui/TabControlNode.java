package mrtjp.core.gui;

import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;

public class TabControlNode extends TNode {

    public int x;
    public int y;

    public TabControlNode(int x, int y) {

        this.x = x;
        this.y = y;
        this.position = new Point(x, y);
    }

    @Override
    public Rect frame() {
        return new Rect(position, Size.zeroSize);
    }

    public TabNode active;

    public void onTabClicked(TabNode tab) {
        if (tab != active) {
            if (active != null) active.active = false;
            tab.active = true;
            active = tab;
        } else {
            tab.active = false;
            active = null;
        }
    }

    @Override
    public void frameUpdate_Impl(Point mouse, float rframe) {
        int dx = 0;
        for (INode w : children) {
            w.setPosition(new Point(w.getPosition().x, dx));
            dx += w.frame().height();
        }
    }
}
