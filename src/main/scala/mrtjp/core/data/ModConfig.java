package mrtjp.core.data;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public abstract class ModConfig {
    public Configuration config = null;
    public String modID;

    public ModConfig(String modID) {
        this.modID = modID;
    }

    public class BaseCategory {

        public final ModConfig parent;
        public String key;
        public String comment;

        public BaseCategory(ModConfig parent, String key, String comment) {

            if (comment == null) comment = "";

            this.parent = parent;
            this.key = key;
            this.comment = comment;
            cat().setComment(comment);
        }

        public ConfigCategory cat() {
            return config.getCategory(key);
        }

        /**
         * Do not pass primitive arrays as the value! they're a pain to handle so I just didn't
         * also: Do not pass Collections as the value! they implode
         */
        public <T> T put(String key, T value) {
            return put(key, value, "", false);
        }

        /**
         * Do not pass primitive arrays as the value! they're a pain to handle so I just didn't
         * also: Do not pass Collections as the value! they implode
         */
        public <T> T put(String key, T value, boolean force) {
            return put(key, value, "", force);
        }

        /**
         * Do not pass primitive arrays as the value! they're a pain to handle so I just didn't
         * also: Do not pass Collections as the value! they implode
         */
        public <T> T put(String key, T value, String comment) {
            return put(key, value, comment, false);
        }

        /**
         * Do not pass primitive arrays as the value! they're a pain to handle so I just didn't
         * also: Do not pass Collections as the value! they implode
         */
        private Property.Type getType(Object value) {


            if (value.getClass().isArray()) {

                if (value instanceof Boolean[]) {
                    return Property.Type.BOOLEAN;
                } else if (value instanceof Integer[]) {
                    return Property.Type.INTEGER;
                } else if (value instanceof Double[]) {
                    return Property.Type.DOUBLE;
                } else {
                    // This is wrong, but this is what it did before
                    return Property.Type.STRING;
                }
            }

            if (value instanceof Boolean) {
                return Property.Type.BOOLEAN;
            } else if (value instanceof Integer) {
                return Property.Type.INTEGER;
            } else if (value instanceof Double) {
                return Property.Type.DOUBLE;
            }

            return Property.Type.STRING;
        }

        /**
         * Do not pass primitive arrays as the value! they're a pain to handle so I just didn't
         * also: Do not pass Collections as the value! they implode
         */
        public <T> T put(String key, T value, String comment, boolean force) {

            Property.Type propType = getType(value);
            Property prop;

            if (value.getClass().isArray()) {
                prop = new Property(key, Arrays.stream(((Object[]) value)).map(Object::toString).toArray(String[]::new), propType);
            } else {
                prop = new Property(key, value.toString(), propType);
            }

            prop.comment = comment;
            if (force || !cat().containsKey(key)) cat().put(key, prop);
            prop = cat().get(key);

            Object result;

            if (value.getClass().isArray()) {
                switch (propType) {
                    case BOOLEAN: result = prop.getBooleanList(); break;
                    case INTEGER: result = prop.getIntList(); break;
                    case DOUBLE: result = prop.getDoubleList(); break;
                    default: result = prop.getStringList(); break;
                }
            } else {
                switch (propType) {
                    case BOOLEAN: result = prop.getBoolean(); break;
                    case INTEGER: result = prop.getInt(); break;
                    case DOUBLE: result = prop.getDouble(); break;
                    default: result = prop.getString(); break;
                }
            }

            return (T) result;
        }

        public boolean containsKey(Object key) {
            return cat().containsKey(key.toString());
        }
    }

    public String getFileName() {
        return modID;
    }

    private boolean registered = false;

    public void loadConfig() {

        config = new Configuration(
            new File(Loader.instance().getConfigDir(), getFileName() + ".cfg")
        );
        initValues();
        if (config.hasChanged()) config.save();

        if (!registered) {
            FMLCommonHandler.instance().bus().register(this);
            registered = true;
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Objects.equals(event.modID, modID)) {
            initValues();
            config.save();
        }
    }

    protected abstract void initValues();
}
