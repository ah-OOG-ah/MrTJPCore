package mrtjp.core.gui;

import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;

public class SideSelectNode extends TNode {

    public int x;
    public int y;
    public int w;
    public int h;
    public Size size;

    public SideSelectNode(int x, int y, int w, int h) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.position = new Point(x, y);
        this.size = new Size(w, h);

        addChild(buildButton(0, 0, "u", 1));
        addChild(buildButton((w / 5) * 2, 0, "n", 2));
        addChild(buildButton(0, (h / 5) * 2, "w", 4));
        addChild(buildButton((w / 5) * 4, (h / 5) * 1 * 2, "e", 5));
        addChild(buildButton((w / 5) * 2, (h / 5) * 2 * 2, "s", 3));
        addChild(buildButton((w / 5) * 4, (h / 5) * 2 * 2, "d", 0));
    }

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    public int sides = 0;
    public boolean exclusiveSides = false;

    private final ButtonNode[] buttons = new ButtonNode[6];

    private MCButtonNode buildButton(int x, int y, String text, int side) {
        final MCButtonNode b = new MCButtonNode();
        b.position = new Point(x, y);
        b.size = this.size.divide(3);
        b.setText(text);
        b.clickDelegate = () -> onSidePresed(side);
        buttons[side] = b;
        return b;
    }

    public void onSidePresed(int side) {
        final int old = sides;
        sides ^= 1 << side;
        if (exclusiveSides) sides &= 1 << side;
        if (old != sides) onSideChanged(side);

        for (int s = 0; s < 6; ++s)
            buttons[s].mouseoverLock = (sides & 1 << s) != 0;
    }

    public void onSideChanged(int oldside) {}
}
