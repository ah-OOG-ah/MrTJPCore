package mrtjp.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Looking for TInventory? Scala traits translate well to java, EXCEPT for a few things like inheriting non-static
 * fields, which TInventory did. Instead, implement ITJPInventory in your block/item/whatever and delegate all methods
 * to a field which extends SimpleInventory, if you don't want to implement everything yourself.
 *
 * Do you think this sucks, and is a terrible way to write code? Yeah, it makes more sense in Scala.
 * TODO: Make InstancedInvTile and InvItem so people have better options
 */
public interface ITJPInventory extends IInventory {

    void loadInv(NBTTagCompound tag);
    void loadInv(NBTTagCompound tag, String prefix);

    void saveInv(NBTTagCompound tag);
    void saveInv(NBTTagCompound tag, String prefix);

    void dropInvContents(World w, int x, int y, int z);
}
