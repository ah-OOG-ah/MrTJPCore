package mrtjp.core.gui;

import klaxon.klaxon.descala.Procedure;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TSlot3 extends Slot {

    public Procedure slotChangeDelegate = () -> {};
    public Supplier<Boolean> canRemoveDelegate = () -> true;
    public Function<ItemStack, Boolean> canPlaceDelegate = (ItemStack stack) -> inventory.isItemValidForSlot(getSlotIndex(), stack);
    public Supplier<Integer> slotLimitCalculator = inventory::getInventoryStackLimit;

    public boolean phantomSlot = false;

    public Procedure slotChangeDelegate2 = () -> {}; // used for container change delegate, do not set yourself!

    public TSlot3(IInventory inv, int i, int x, int y) {
        super(inv, i, x, y);
    }

    // additional methods required for this trait to work are located in class Slot3
}
