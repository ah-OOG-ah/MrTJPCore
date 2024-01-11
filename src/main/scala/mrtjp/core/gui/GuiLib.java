package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import cpw.mods.fml.client.FMLClientHandler;
import mrtjp.core.resource.ResourceLib;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

public class GuiLib {

    /** @param x
     *   x pos of grid
     * @param y
     *   y pos of grid
     * @param w
     *   width of grid
     * @param h
     *   height of grid
     * @param dx
     *   x spacing of slots (0 means touching like in inventories)
     * @param dy
     *   y spacing of slots (0 means touching like in inventories)
     * @return
     *   Sequence of x and
     */
    public static List<Pair<Integer, Integer>> createSlotGrid(
        int x,
        int y,
        int w,
        int h,
        int dx,
        int dy
    ) {
        return createGrid(x, y, w, h, dx + 18, dy + 18);
    }

    public static List<Pair<Integer, Integer>> createGrid(int x, int y, int w, int h, int dx, int dy) {
        List<Pair<Integer, Integer>> grid = new ArrayList<>();
        for (int iy = 0; iy < h; ++iy)
            for (int ix = 0; ix < w; ++ix)
                grid.add(new ImmutablePair<>(x + ix * dx, y + iy * dy));
        return grid;
    }

    public static void drawPlayerInvBackground(int x, int y) {
        for (Pair<Integer, Integer> p : createSlotGrid(x, y, 9, 3, 0, 0))
            drawSlotBackground(p.getLeft() - 1, p.getRight() - 1);
        for (Pair<Integer, Integer> p : createSlotGrid(x, y + 58, 9, 1, 0, 0))
            drawSlotBackground(p.getLeft() - 1, p.getRight() - 1);
    }

    public static void drawSlotBackground(int x, int y) {
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ResourceLib.guiSlot().bind();
        final Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + 18, 0, 0, 1);
        t.addVertexWithUV(x + 18, y + 18, 0, 1, 1);
        t.addVertexWithUV(x + 18, y, 0, 1, 0);
        t.addVertexWithUV(x, y, 0, 0, 0);
        t.draw();
    }

    public static void drawGuiBox(int x, int y, int width, int height, float zLevel) {
        drawGuiBox(x, y, width, height, zLevel, true, true, true, true);
    }

    public static void drawGuiBox(
        int x,
        int y,
        int width,
        int height,
        float zLevel,
        boolean top,
        boolean left,
        boolean bottom,
        boolean right
    ) {
        final int u = 1;
        final int v = 29;
        ResourceLib.guiExtras().bind();
        GuiDraw.gui.setZLevel(zLevel);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glPushMatrix();
        GL11.glTranslated(x + 2, y + 2, 0);
        GL11.glScaled(width - 4, height - 4, 0);
        GuiDraw.drawTexturedModalRect(0, 0, u + 19, v, 1, 1);
        glPopMatrix();
        if (top) {
            glPushMatrix();
            GL11.glTranslated(x + 3, y, 0);
            GL11.glScaled(width - 6, 1, 0);
            GuiDraw.drawTexturedModalRect(0, 0, u + 4, v, 1, 3);
            glPopMatrix();
        }
        if (bottom) {
            glPushMatrix();
            GL11.glTranslated(x + 3, y + height - 3, 0);
            GL11.glScaled(width - 6, 1, 0);
            GuiDraw.drawTexturedModalRect(0, 0, u + 14, v, 1, 3);
            glPopMatrix();
        }
        if (left) {
            glPushMatrix();
            GL11.glTranslated(x, y + 3, 0);
            GL11.glScaled(1, height - 6, 0);
            GuiDraw.drawTexturedModalRect(0, 0, u, v + 4, 3, 1);
            glPopMatrix();
        }
        if (right) {
            glPushMatrix();
            GL11.glTranslated(x + width - 3, y + 3, 0);
            GL11.glScaled(1, height - 6, 0);
            GuiDraw.drawTexturedModalRect(0, 0, u + 8, v, 3, 1);
            glPopMatrix();
        }

        if (top && left) GuiDraw.drawTexturedModalRect(x, y, u, v, 4, 4);
        if (top && right)
            GuiDraw.drawTexturedModalRect(x + width - 3, y, u + 5, v, 3, 3);
        if (bottom && left)
            GuiDraw.drawTexturedModalRect(x, y + height - 3, u + 11, v, 3, 3);
        if (bottom && right)
            GuiDraw.drawTexturedModalRect(
                x + width - 4,
                y + height - 4,
                u + 15,
                v,
                4,
                4
            );
    }

    public static void drawLine(double x, double y, double x2, double y2) {
        final int count = FMLClientHandler.instance().getClient().thePlayer.ticksExisted;
        final float red = (float) (0.7f + Math.sin(((count + x) / 10.0d)) * 0.15f + 0.15f);
        final float green = (float) (0.0f + Math.sin(((count + x + y) / 11.0d)) * 0.15f + 0.15f);
        final float blue = (float) (0.0f + Math.sin(((count + y) / 12.0d)) * 0.15f + 0.15f);
        drawLine(x, y, x2, y2, new Color(red, green * 0, blue * 0).getRGB());
    }

    public static void drawLine(double x, double y, double x2, double y2, int color) {

        final Tessellator t = Tessellator.instance;

        final Color c = new Color(color);
        final float red = c.getRed() / 255.0f;
        final float green = c.getGreen() / 255.0f;
        final float blue = c.getBlue() / 255.0f;
        final float alpha = c.getAlpha() / 255.0f;

        glPushMatrix();
        glLineWidth(3.0f);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(red, green, blue, alpha);

        t.startDrawing(3);
        t.addVertex(x, y, 0.0d);
        t.addVertex(x2, y2, 0.0d);
        t.draw();

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void drawVerticalTank(
        int x,
        int y,
        int u,
        int v,
        int w,
        int h,
        int prog
    ) {
        GuiDraw.drawTexturedModalRect(x, y + h - prog, u, v + h - prog, w, prog);
    }
}
