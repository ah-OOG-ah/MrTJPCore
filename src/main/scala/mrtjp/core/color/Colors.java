package mrtjp.core.color;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.ColourMultiplier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static mrtjp.core.color.Colors.Bruh.dyeDictionary;
import static mrtjp.core.color.Colors.Bruh.mixMap;

public enum Colors {

    WHITE("White", 0xffffff),
    ORANGE("Orange", 0xc06300),
    MAGENTA("Magenta", 0xb51ab5),
    LIGHT_BLUE("Light Blue", 0x6f84f1),
    YELLOW("Yellow", 0xbfbf00),
    LIME("Lime", 0x6bf100),
    PINK("Pink", 0xf14675),
    GREY("Grey", 0x535353),
    LIGHT_GREY("Light Grey", 0x939393),
    CYAN("Cyan", 0x008787),
    PURPLE("Purple", 0x5e00c0),
    BLUE("Blue", 0x1313c0),
    BROWN("Brown", 0x4f2700),
    GREEN("Green", 0x088700),
    RED("Red", 0xa20f06),
    BLACK("Black", 0x1f1f1f);

    public final String name;
    public final int rgb;

    public final int rgba;
    public final int argb;
    public final ColourRGBA c;

    public final float rF;
    public final float gF;
    public final float bF;

    public final int dyeID;
    public final int woolID;

    public final String oreDict;

    Colors(String name, int rgb) {

        this.name = name;
        this.rgb = rgb;

        rgba = rgba(0xff);
        argb = argb(0xff);
        c = new ColourRGBA(rgba);

        rF = (rgb >> 16 & 255) / 255.0f;
        gF = (rgb >> 8 & 255) / 255.0f;
        bF = (rgb & 255) / 255.0f;

        // These three used to be lazy - instead of doing that in Java, I'm just gonna make them eager
        dyeID = 15 - ordinal();
        woolID = ordinal();
        oreDict = dyeDictionary.get(dyeID);
    }

    public static Colors mcMix(Colors c1, Colors c2) {
        if (c1 == c2) return c1;
        return mixMap.getOrDefault(new HashSet<>(Arrays.asList(c1, c2)), null);
    }

    public static Colors fromWoolID(int id) {
        return apply(id);
    }
    public static Colors fromDyeID(int id) {
        return apply(15 - id);
    }
    public static Colors fromOreDict(String id) {
        if (dyeDictionary.contains(id))
            return fromDyeID(dyeDictionary.indexOf(id));
        return null;
    }
    public static Colors fromStack(ItemStack stack) {
        final int[] ids = OreDictionary.getOreIDs(stack);
        return Arrays.stream(ids)
            .mapToObj(id -> fromOreDict(OreDictionary.getOreName(id)))
            .filter(Objects::nonNull).findFirst()
            .orElse(null);
    }

    private static Colors apply(int id) {
        if (values().length > id) {
            return values()[id];
        }
        return null;
    }

    public int rgba(int a) {
        return rgb << 8 | a & 0xff;
    }

    public int argb(int a) {
        return  (a & 0xff) << 24 | rgb;
    }

    public ItemStack makeDyeStack() {
        return makeDyeStack(1);
    }

    public ItemStack makeDyeStack(int i) {
        return new ItemStack(Items.dye, i, dyeID);
    }

    @SideOnly(Side.CLIENT)
    public void setGL11Color(float alpha) {
        GL11.glColor4f(rF, gF, bF, alpha);
    }

    @SideOnly(Side.CLIENT)
    public ColourMultiplier getVertOp() {
        return ColourMultiplier.instance(rgba);
    }

    public Colors mcMix(Colors that) {
        return Colors.mcMix(this, that);
    }

    static class Bruh {
        protected final static List<String> dyeDictionary = new ArrayList<>();

        static  {
            dyeDictionary.add("dyeBlack");
            dyeDictionary.add("dyeRed");
            dyeDictionary.add("dyeGreen");
            dyeDictionary.add("dyeBrown");
            dyeDictionary.add("dyeBlue");
            dyeDictionary.add("dyePurple");
            dyeDictionary.add("dyeCyan");
            dyeDictionary.add("dyeLightGray");
            dyeDictionary.add("dyeGray");
            dyeDictionary.add("dyePink");
            dyeDictionary.add("dyeLime");
            dyeDictionary.add("dyeYellow");
            dyeDictionary.add("dyeLightBlue");
            dyeDictionary.add("dyeMagenta");
            dyeDictionary.add("dyeOrange");
            dyeDictionary.add("dyeWhite");
        }

        protected static final Map<Set<Colors>, Colors> mixMap = new HashMap<>();

        static  {
            // ???               -> WHITE
            mixMap.put(new HashSet<>(Arrays.asList(YELLOW, RED)), ORANGE);
            mixMap.put(new HashSet<>(Arrays.asList(PINK, PURPLE)), MAGENTA);
            mixMap.put(new HashSet<>(Arrays.asList(WHITE, BLUE)), LIGHT_BLUE);
            // ???               -> YELLOW
            mixMap.put(new HashSet<>(Arrays.asList(WHITE, GREEN)), LIME);
            mixMap.put(new HashSet<>(Arrays.asList(WHITE, RED)), PINK);
            mixMap.put(new HashSet<>(Arrays.asList(WHITE, BLACK)), GREY);
            mixMap.put(new HashSet<>(Arrays.asList(WHITE, GREY)), LIGHT_GREY);
            mixMap.put(new HashSet<>(Arrays.asList(BLUE, GREEN)), CYAN);
            mixMap.put(new HashSet<>(Arrays.asList(BLUE, RED)), PURPLE);
            // ???               -> BLUE
            mixMap.put(new HashSet<>(Arrays.asList(ORANGE, RED)), BROWN);
            mixMap.put(new HashSet<>(Arrays.asList(YELLOW, BLUE)), GREEN);
            // ???               -> RED
            // ???               -> BLACK
        }
    }
}
