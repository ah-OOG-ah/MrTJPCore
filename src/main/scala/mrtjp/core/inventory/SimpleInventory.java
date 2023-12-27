package mrtjp.core.inventory;

import mrtjp.core.world.WorldLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.Arrays;

/**
 * A basic implementation of an inventory, should work for most cases.
 * If you were looking for TInventory, the default methods have been merged with this class - if you don't want to
 * implement everything yourself, subclass this. If you wanted to attach an inventory to a block or item, have it
 * implement ITJPInventory, and if you're lazy you can delegate to a SimpleInventory field.
 */
public class SimpleInventory implements ITJPInventory {

    public final int size;
    public final String name;
    public final int stackLimit;

    private final ItemStack[] storage;


    public SimpleInventory(int size) {
        this(size, 64);
    }

    public SimpleInventory(int size, int lim) {
        this(size, "", lim);
    }

    public SimpleInventory(int size, String name) {
        this(size, name, 64);
    }

    public SimpleInventory(int size, String name, int stackLimit) {
        this.size = size;
        this.name = name;
        this.stackLimit = stackLimit;
        this.storage = new ItemStack[size];
    }

    @Override
    public void markDirty() {}

    @Override
    public int getSizeInventory() {
        return storage.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return storage[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = storage[slot];
        if (stack == null) return null;

        ItemStack out;
        if (stack.stackSize > count) {
            out = stack.splitStack(count);
        } else {
            out = stack;
            storage[slot] = null;
        }
        markDirty();
        return out;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = storage[slot];
        if (stack == null) return null;
        storage[slot] = null;
        markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack item) {
        storage[slot] = item;
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return name;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return stackLimit;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item) {
        return true;
    }

    /* Methods from ITJPInventory */

    public void loadInv(NBTTagCompound tag) { loadInv(tag, name); }
    public void loadInv(NBTTagCompound tag, String prefix) {
        NBTTagList tag1 = tag.getTagList(prefix + "items", 10);
        for (int i = 0; i < tag1.tagCount(); ++i) {
            NBTTagCompound tag2 = tag1.getCompoundTagAt(i);

            int index = tag2.getInteger("index");
            if (storage.length > index)
                storage[index] = ItemStack.loadItemStackFromNBT(tag2);
        }
    }

    public void saveInv(NBTTagCompound tag) { saveInv(tag, name); }
    public void saveInv(NBTTagCompound tag, String prefix) {
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < storage.length; ++i) {
            if (storage[i] != null && storage[i].stackSize > 0) {
                NBTTagCompound tag2 = new NBTTagCompound();
                tag2.setInteger("index", i);
                storage[i].writeToNBT(tag2);
                itemList.appendTag(tag2);
            }
        }

        tag.setTag(prefix + "items", itemList);
        tag.setInteger(prefix + "itemsCount", storage.length);
    }

    public void dropInvContents(World w, int x, int y, int z) {
        if (w.isRemote) return;
        for (ItemStack i : storage) if (i != null) WorldLib.dropItem(w, x, y, z, i);
        Arrays.fill(storage, null);
        markDirty();
    }
}
