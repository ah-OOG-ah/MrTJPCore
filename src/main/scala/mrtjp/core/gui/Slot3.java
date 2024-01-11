package mrtjp.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Slot3 extends TSlot3 {

    public Slot3(IInventory inv, int i, int x, int y) {
        super(inv, i, x, y);
    }

    @Override
    public int getSlotStackLimit() {
        return slotLimitCalculator.get();
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return canRemoveDelegate.get();
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return canPlaceDelegate.apply(stack);
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        slotChangeDelegate.apply();
        slotChangeDelegate2.apply();
    }
}
