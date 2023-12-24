package mrtjp.core.data;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyTracking {

    private static int idPool = 0;
    private static final Map<Integer, Map<UUID, Boolean>> map = new HashMap<>();

    public static void updatePlayerKey(int id, EntityPlayer player, boolean state) {
        map.get(id).put(player.getGameProfile().getId(), state);
    }

    public static void registerTracker(TServerKeyTracker tracker) {
        tracker.id = idPool;
        idPool += 1;
        map.computeIfAbsent(
            tracker.id,
            k -> new HashMap<>() // default false
        );
    }

    public static boolean isKeyDown(int id, EntityPlayer player) {
        return map.get(id).computeIfAbsent(player.getGameProfile().getId(), k -> false);
    }
}
