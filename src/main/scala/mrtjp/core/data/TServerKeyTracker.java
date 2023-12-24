package mrtjp.core.data;

import net.minecraft.entity.player.EntityPlayer;

public abstract class TServerKeyTracker {
    public int id = -1;

    public boolean isKeyDown(EntityPlayer p) {
        return KeyTracking.isKeyDown(id, p);
    }

    public void register() {
        KeyTracking.registerTracker(this);
    }
}
