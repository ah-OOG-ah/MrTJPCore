package mrtjp.core.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.core.inventory.InvWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import scala.Tuple2;
import scala.util.control.Breaks;

import java.util.List;
import java.util.function.Consumer;

public class NodeContainer extends Container {

    public Consumer<EntityPlayer> startWatchDelegate = (EntityPlayer p) -> {};
    public Consumer<EntityPlayer> stopWatchDelegate = (EntityPlayer p) -> {};
    public Consumer<Integer> slotChangeDelegate = (Integer slot) -> {};

    public List<TSlot3> slots() {
        return (List<TSlot3>) (List<?>) inventorySlots;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean canDragIntoSlot(Slot slot) {
        if (slot instanceof TSlot3) {
            return !((TSlot3) slot).phantomSlot;
        }
        return super.canDragIntoSlot(slot);
    }

    @Override
    public Slot addSlotToContainer(Slot slot) {
        if (!(slot instanceof TSlot3))
            throw new IllegalArgumentException("NodeContainers can only except slots of type Slot3");

        super.addSlotToContainer(slot);
        ((TSlot3) slot).slotChangeDelegate2 = () -> slotChangeDelegate.accept(slot.slotNumber);
        return slot;
    }

    @SideOnly(Side.CLIENT)
    public void addPlayerInv(int x, int y) {
        addPlayerInv(Minecraft.getMinecraft().thePlayer, x, y);
    }
    public void addPlayerInv(EntityPlayer player, int x, int y) {
        int next = 0;

        for (Pair<Integer, Integer> p : GuiLib.createSlotGrid(x, y + 58, 9, 1, 0, 0)) {

            addSlotToContainer(new Slot3(player.inventory, next++, p.getLeft(), p.getRight())); // hotbar
        }

        for (Pair<Integer, Integer> p : GuiLib.createSlotGrid(x, y, 9, 3, 0, 0)) {

            addSlotToContainer(new Slot3(player.inventory, next++, p.getLeft(), p.getRight())); // slots
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting c) {
        super.addCraftingToCrafters(c);
        if (c instanceof EntityPlayer) {
            if (!((EntityPlayer) c).worldObj.isRemote) {
                startWatchDelegate.accept((EntityPlayer) c);
            }
        }
    }

    @Override
    public void removeCraftingFromCrafters(ICrafting c) {
        super.removeCraftingFromCrafters(c);
        if (c instanceof EntityPlayer) {
            if (!((EntityPlayer) c).worldObj.isRemote) {
                stopWatchDelegate.accept((EntityPlayer) c);
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer p) {
        super.onContainerClosed(p);
        if (!p.worldObj.isRemote)
            stopWatchDelegate.accept(p);
    }

    @Override
    public ItemStack slotClick(
        int id,
        int mouse,
        int shift,
        EntityPlayer player
    ) {
        try { // Ignore exceptions raised from client-side only slots that wont be found here. To be removed.
            if (slots().size() > id && slots().get(id) != null) {
                final TSlot3 slot = slots().get(id);
                if (slot.phantomSlot)
                    return handleGhostClick(slot, mouse, shift, player);
            }
            return super.slotClick(id, mouse, shift, player);
        } catch (Exception ignored) {
            return null;
        }
    }

    private ItemStack handleGhostClick(
        TSlot3 slot,
        int mouse,
        int shift,
        EntityPlayer player
    ) {
        final ItemStack inSlot = slot.getStack();
        final ItemStack inCursor = player.inventory.getItemStack();

        if (inCursor != null && !slot.isItemValid(inCursor)) return inCursor;

        final boolean stackable = InvWrapper.areItemsStackable(inSlot, inCursor);
        if (stackable) {

            if (inSlot != null && inCursor == null) {

                slot.putStack(null);
            } else if (inSlot == null && inCursor != null) {

                final ItemStack newStack = inCursor.copy();
                newStack.stackSize = (mouse == 0) ? Math.min(inCursor.stackSize, slot.getSlotStackLimit()) : 1;
                slot.putStack(newStack);
            } else if (inSlot != null) {

                final int toAdd = (shift == 1) ? 10 : 1;
                if (mouse == 0)
                    inSlot.stackSize = Math.min(slot.getSlotStackLimit(), inSlot.stackSize + toAdd);
                else if (mouse == 1)
                    inSlot.stackSize = Math.max(0, inSlot.stackSize - toAdd);
                if (inSlot.stackSize > 0) slot.putStack(inSlot);
                else slot.putStack(null);
            }
        } else {

            final ItemStack newStack = inCursor.copy();
            newStack.stackSize = (mouse == 0) ? Math.min(inCursor.stackSize, slot.getSlotStackLimit()) : 1;
            slot.putStack(newStack);
        }

        return inCursor;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {

        ItemStack stack = null;
        if (slots().size() > i && slots().get(i) != null) {

            final TSlot3 slot = slots().get(i);
            if (slot != null && slot.getHasStack()) {
                stack = slot.getStack();
                final ItemStack manipStack = stack.copy();

                if (!doMerge(manipStack, i) || stack.stackSize == manipStack.stackSize)
                    return null;

                if (manipStack.stackSize <= 0) slot.putStack(null);
                else slot.putStack(manipStack);

                slot.onPickupFromSlot(player, stack);
           }
        }
        return stack;
    }

    public boolean doMerge(ItemStack stack, int from) {

        if (slots().size() - 36 <= from && from < slots().size())
            return tryMergeItemStack(stack, 0, slots().size() - 36, false);
        return tryMergeItemStack(stack, slots().size() - 36, slots().size(), false);
      }

    public boolean tryMergeItemStack(
        ItemStack stack,
        int start,
        int end,
        boolean reverse
    ) {
        boolean flag1 = false;
        int k = (reverse) ? end - 1 : start;

        TSlot3 slot = null;
        ItemStack inslot = null;
        if (stack.isStackable()) {

            while (stack.stackSize > 0 && (!reverse && k < end || reverse && k >= start)) {

                slot = slots().get(k);
                inslot = slot.getStack();
                if (!slot.phantomSlot && inslot != null && inslot.getItem() == stack.getItem() &&
                    (!stack.getHasSubtypes() || stack.getItemDamage() == inslot.getItemDamage()) &&
                    ItemStack.areItemStackTagsEqual(stack, inslot)) {

                        final int space = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize()) - inslot.stackSize;
                        if (space >= stack.stackSize) {
                            inslot.stackSize += stack.stackSize;
                            stack.stackSize = 0;
                            slot.onSlotChanged();
                            flag1 = true;
                        } else if (space > 0) {
                            stack.stackSize -= space;
                            inslot.stackSize += space;
                            slot.onSlotChanged();
                            flag1 = true;
                        }
                    }
                    if (reverse) --k; else ++k;
            }
        }

        if (stack.stackSize > 0) {
            k = (reverse) ? end - 1 : start;

            exit: {
                while (!reverse && k < end || reverse && k >= start) {
                    slot = slots().get(k);
                    inslot = slot.getStack();
                    if (!slot.phantomSlot && inslot == null && slot.isItemValid(stack)) {
                        final int space = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                        if (space >= stack.stackSize) {
                            slot.putStack(stack.copy());
                            slot.onSlotChanged();
                            stack.stackSize = 0;
                            flag1 = true;
                            break exit;
                        } else {
                            slot.putStack(stack.splitStack(space));
                            slot.onSlotChanged();
                            flag1 = true;
                        }
                    }
                    if (reverse) --k; else ++k;
                }
            }
        }

        return flag1;
    }

    // Hack to allow empty containers for use with guis without inventories
    @Override
    public void putStackInSlot(int slot, ItemStack stack) {
        if (inventorySlots.isEmpty() || inventorySlots.size() < slot) {
          // no-op
        } else {
            super.putStackInSlot(slot, stack);
        }
    }
}
