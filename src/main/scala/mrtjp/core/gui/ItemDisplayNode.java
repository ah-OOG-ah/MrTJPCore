package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import klaxon.klaxon.descala.Procedure;
import mrtjp.core.item.ItemKeyStack;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

public class ItemDisplayNode extends TNode {

    public ItemKeyStack stack;
    public Size size = Size.zeroSize;

    public boolean drawNumber = true;
    public boolean drawTooltip = true;

    public int backgroundColour = 0;
    public Procedure clickDelegate = () -> {};

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    @Override
    public void drawBack_Impl(Point mouse, float rframe) {
        GuiDraw.drawRect(
            position.x,
            position.y,
            size.width,
            size.height,
            backgroundColour
        );
        ItemDisplayNode.renderItem(
            position,
            size,
            zPosition,
            drawNumber,
            stack.makeStack()
        );
    }

    @Override
    public boolean mouseClicked_Impl(Point p, int button, boolean consumed) {
        if (!consumed && rayTest(p)) {
            clickDelegate.apply();
            return true;
        }
        return false;
    }

    @Override
    public void drawFront_Impl(Point mouse, float rframe) {
        if (drawTooltip && frame().contains(mouse) && rayTest(mouse))
            drawTooltip(mouse);
    }

    public void drawTooltip(Point mouse) {
        ClipNode.tempDisableScissoring();
        // draw tooltip with absolute coords to allow it to force-fit on screen
        translateToScreen();
        final Point m = parent.convertPointToScreen(mouse);

        final List<String> lines = stack.makeStack()
            .getTooltip(mcInst().thePlayer, mcInst().gameSettings.advancedItemTooltips);
        final List<String> l2 = new ArrayList<>(lines.size());
        l2.add(lines.get(0));
        l2.addAll(lines.stream().skip(1).map(s -> EnumChatFormatting.GRAY + s).collect(Collectors.toList()));//Seq(lines.head) ++ lines.tail.map(EnumChatFormatting.GRAY + _)
        GuiDraw.drawMultilineTip(m.x + 12, m.y - 12, l2);

        translateFromScreen();
        ClipNode.tempEnableScissoring();
    }

    public static final RenderItem renderItem = new RenderItem();

    public static void renderItem(
        Point position,
        Size size,
        double zPosition,
        boolean drawNumber,
        ItemStack stack
    ) {
        final FontRenderer font = Optional.of(stack.getItem().getFontRenderer(stack)).orElse(Minecraft.getMinecraft().fontRenderer);
        final TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;

        final boolean f = font.getUnicodeFlag();
        font.setUnicodeFlag(true);

        glItemPre();
        glPushMatrix();
        new Scale(size.width / 16.0, size.height / 16.0, 1)
            .at(new Vector3(position.x, position.y, 0))
            .glApply();

        renderItem.zLevel = (float) (zPosition + 10.0);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        renderItem.renderItemAndEffectIntoGUI(
            font,
            renderEngine,
            stack,
            position.x,
            position.y
        );
        renderItem.renderItemOverlayIntoGUI(
            font,
            renderEngine,
            stack,
            position.x,
            position.y,
            ""
        );
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);
        renderItem.zLevel = (float) zPosition;

        if (drawNumber) {
            final String s = (stack.stackSize == 1) ? ""
            : (stack.stackSize < 1000) ? stack.stackSize + ""
            : (stack.stackSize < 100000) ? stack.stackSize / 1000 + "K"
            : (stack.stackSize < 1000000) ? "0." + stack.stackSize / 100000 + "M"
            : stack.stackSize / 1000000 + "M";
            font.drawStringWithShadow(
                s,
                position.x + 19 - 2 - font.getStringWidth(s),
                position.y + 6 + 3,
                16777215
            );
        }

        glPopMatrix();
        glItemPost();

        font.setUnicodeFlag(f);
    }

    public static void glItemPre() {
        glPushMatrix();
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderHelper.enableGUIStandardItemLighting();
        glEnable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setLightmapTextureCoords(
            OpenGlHelper.lightmapTexUnit,
            240 / 1.0f,
            240 / 1.0f
        );
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);
    }

    public static void glItemPost() {
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }
}
