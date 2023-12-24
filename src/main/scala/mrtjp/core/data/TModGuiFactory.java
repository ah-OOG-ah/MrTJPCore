package mrtjp.core.data;

import cpw.mods.fml.client.IModGuiFactory;
// DO NOT REMOVE THESE IMPORTS
import cpw.mods.fml.client.IModGuiFactory.RuntimeOptionGuiHandler;
import cpw.mods.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
// Scala compiler is too dumb to compile this without the above imports
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
