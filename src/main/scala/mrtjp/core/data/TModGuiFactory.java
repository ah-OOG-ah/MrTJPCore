package mrtjp.core.data;

import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;

import java.util.Set;

public interface TModGuiFactory extends IModGuiFactory {
    @Override
    default void initialize(Minecraft minecraftInstance) {}
    @Override
    default Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    @Override
    default RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
}
