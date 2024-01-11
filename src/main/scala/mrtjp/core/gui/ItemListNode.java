package mrtjp.core.gui;

import mrtjp.core.item.ItemKeyStack;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ItemListNode extends TNode {

    public List<ItemKeyStack> items = new ArrayList<>();
    public Size itemSize = new Size(16, 16);
    public int gridWidth = 3;

    public Function<ItemKeyStack, ItemDisplayNode> displayNodeFactory = (ItemKeyStack stack) -> new ItemDisplayNode();
    public Rect cullFrame = Rect.infiniteRect;

    private List<ItemDisplayNode> dispNodes = new ArrayList<>();

    @Override
    public Rect frame() {
        return new Rect(
            position,
            itemSize.multiply(
                Math.min(items.size(), gridWidth),
                items.size() / gridWidth + 1
            )
        );
    }

    public void reset() {
        final Iterator<ItemKeyStack> it = items.iterator();
        int x = 0;
        int y = 0;

        dispNodes.forEach(INode::removeFromParent);
        dispNodes.clear();

        while (it.hasNext()) {
            final ItemKeyStack i = it.next();
            final ItemDisplayNode d = displayNodeFactory.apply(i);
            if (d != null) {
                d.stack = i;
                d.size = itemSize;
                d.position = Point.apply(itemSize.multiply(x, y));
                addChild(d);

                final Rect df = convertRectToScreen(d.frame());
                if (cullFrame.intersects(df)) {
                    dispNodes.add(d);
                } else d.removeFromParent();

                ++x;
                if (x >= gridWidth) { x = 0; ++y; }
            }
        }
    }
}
