package mrtjp.core.gui;

import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;

public class InventorySlotNode extends TNode {

    public int slotIdx = -1;

    public Size size = new Size(16, 16);

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    @Override
    public void frameUpdate_Impl(Point mouse, float rframet) {
        final NodeGui root = getRoot();
        final TSlot3 slot = ((NodeContainer) root.inventorySlots).slots().get(slotIdx);

        if (hidden || buildParentHierarchy(root).stream().anyMatch(INode::isHidden)) {
            slot.xDisplayPosition = 9999;
            slot.yDisplayPosition = 9999;
        } else {
            final Point absPos = parent.convertPointTo(position, getRoot());
            slot.xDisplayPosition = absPos.x;
            slot.yDisplayPosition = absPos.y;
        }
    }
}
