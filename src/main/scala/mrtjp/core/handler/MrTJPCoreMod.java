package mrtjp.core.handler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = MrTJPCoreMod.modName,
    useMetadata = true,
    guiFactory = "mrtjp.core.handler.GuiConfigFactory",
    acceptedMinecraftVersions = "[1.7.10]",
    name = "MrTJPCore",
    version = MrTJPCoreMod.version
)
public class MrTJPCoreMod {

    public static final MrTJPCoreMod instance = new MrTJPCoreMod();
    public static final String modName = "MrTJPCoreMod";
    public static final String version = "GRADLETOKEN_VERSION";
    public static final Logger log = LogManager.getFormatterLogger("MrTJPCoreMod");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MrTJPConfig.loadConfig();
        MrTJPCoreProxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MrTJPCoreProxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MrTJPCoreProxy.postInit();
    }
}
