package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import codechicken.lib.render.FontUtils;
import mrtjp.core.color.Colors;
import mrtjp.core.item.ItemKeyStack;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Queue;

public class NodeItemList extends TNode {

    public int x;
    public int y;
    public int w;
    public int h;
    public Size size;

    private final int squareSize = 20;
    private final int rows;
    private final int columns;

    public NodeItemList(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.position = new Point(x, y);
        this.size = new Size(w, h);
        rows = size.height / squareSize;
        columns = size.width / squareSize;
    }

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    private int currentPage = 0;
    private int pagesNeeded = 0;

    private boolean waitingForList = true;
    private boolean downloadFinished = false;

    private ItemKeyStack selection;
    private ItemKeyStack hover;

    private int xLast = 0;
    private int yLast = 0;

    private String filter = "";

    public ItemKeyStack getSelected() {
        return selection;
    }

    private Queue<ItemKeyStack> displayList = new ArrayDeque<>();

    public NodeItemList setDisplayList(ArrayDeque<ItemKeyStack> list) {
        this.displayList = list;
        this.waitingForList = false;
        this.currentPage = 0;
        return this;
    }

    public NodeItemList setNewFilter(String filt) {
        filter = filt.toLowerCase();
        xLast = -1;
        yLast = -1;
        currentPage = 0;
        return this;
    }

    public void pageUp() {
        ++currentPage;
        if (currentPage > pagesNeeded) currentPage = pagesNeeded;
    }

    public void pageDown() {
        --currentPage;
        if (currentPage < 0) currentPage = 0;
    }

    public void resetDownloadStats() {
        waitingForList = true;
        downloadFinished = false;
    }

    private boolean stringMatch(String name, String filter) {
        for (String s : filter.split(" ")) if (!name.contains(s)) return false;
        return true;
    }

    public boolean filterAllows(ItemKeyStack stack) {

        return stringMatch(stack.key().getName().toLowerCase(), filter);
    }

    private int getSeachedCount() {
        int count = 0;
        for (ItemKeyStack stack : displayList)
            if (filterAllows(stack)) ++count;
        return count;
    }

    @Override
    public void drawBack_Impl(Point mouse, float frame) {
        drawGradientRect(
            x,
            y,
            x + size.width,
            y + size.height,
            0xff808080,
            0xff808080
        );
        pagesNeeded = (getSeachedCount() - 1) / (rows * columns);
        if (pagesNeeded < 0) pagesNeeded = 0;
        if (currentPage > pagesNeeded) currentPage = pagesNeeded;

        if (!downloadFinished) drawLoadingScreen();
        else drawAllItems(mouse.x, mouse.y);
    }

    @Override
    public void drawFront_Impl(Point mouse, float rframe) {
        if (hover != null)
            GuiDraw.drawMultilineTip(
                mouse.x + 12,
                mouse.y - 12,
                hover.makeStack()
                    .getTooltip(
                        mcInst().thePlayer,
                        mcInst().gameSettings.advancedItemTooltips
                    )
            );
        FontUtils.drawCenteredString(
            "Page: " + (currentPage + 1) + "/" + (pagesNeeded + 1),
            x + (size.width / 2),
            y + frame().height() + 6,
            Colors.BLACK.rgb
        );
    }

    @Override
    public boolean mouseClicked_Impl(Point p, int button, boolean consumed) {
        if (!consumed && frame().contains(p)) {
            xLast = p.x;
            yLast = p.y;
            return true;
        }
        return false;
    }

    private void drawLoadingScreen() {
        final int barSizeX = size.width / 2;
        final long time = System.currentTimeMillis() / ((waitingForList) ? 40 : 8);
        final int percent = (int) (time % barSizeX);

        if (!waitingForList && percent > barSizeX - 8) downloadFinished = true;
        final int xStart = x + size.width / 2 - barSizeX / 2;
        final int yStart = y + frame().height() / 3;

        FontUtils.drawCenteredString(
            "downloading data",
            (x + size.width) / 2,
            (y + frame().height()) / 3 + squareSize,
            0xff165571
        );
        final int xSize = percent;
        final int ySize = 9;

        Gui.drawRect(xStart, yStart, xStart + xSize, yStart + ySize, 0xff165571);
    }

    private void drawAllItems(int mx, int my) {
        hover = null;
        selection = null;
        final int xOffset = x - (squareSize - 2);
        final int yOffset = y + 2;
        int renderPointerX = 1;
        int renderPointerY = 0;
        int itemNumber = 0;

        glItemPre();

        b: {
            c: for (ItemKeyStack keystack : displayList)  { // c.breakable

                if (!filterAllows(keystack)) break c;

                ++itemNumber;
                if (itemNumber <= rows * columns * currentPage) break c;
                if (itemNumber > (rows * columns) * (currentPage + 1)) break b;

                final int localX = xOffset + renderPointerX * squareSize;
                final int localY = yOffset + renderPointerY * squareSize;
                if (
                    mx > localX && mx < localX + squareSize && my > localY && my < localY + squareSize
                ) hover = keystack;
                if (
                    xLast > localX && xLast < localX + squareSize && yLast > localY && yLast < localY + squareSize
                ) selection = keystack;

                if (selection != null && (selection == keystack)) {
                    Gui.drawRect(
                        localX - 2,
                        localY - 2,
                        localX + squareSize - 2,
                        localY + squareSize - 2,
                        0xff000000
                    );
                    Gui.drawRect(
                        localX - 1,
                        localY - 1,
                        localX + squareSize - 3,
                        localY + squareSize - 3,
                        0xffd2d2d2
                    );
                    Gui.drawRect(
                        localX,
                        localY,
                        localX + squareSize - 4,
                        localY + squareSize - 4,
                        0xff595959
                    );
                }

                inscribeItemStack(localX, localY, keystack.makeStack());
                ++renderPointerX;

                if (renderPointerX > columns) {
                    renderPointerX = 1;
                    ++renderPointerY;
                }
                if (renderPointerY > rows) break b;
            }
        }
        glItemPost();
    }

    private void glItemPre() {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setLightmapTextureCoords(
            OpenGlHelper.lightmapTexUnit,
            240 / 1.0f,
            240 / 1.0f
        );
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    private void glItemPost() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    protected RenderItem renderItem = new RenderItem();
    private void inscribeItemStack(int xPos, int yPos, ItemStack stack) {
        final FontRenderer font = Optional.of(stack.getItem().getFontRenderer(stack)).orElse(fontRenderer());

        renderItem.zLevel = 100.0f;
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        renderItem.renderItemAndEffectIntoGUI(font, renderEngine(), stack, xPos, yPos);
        renderItem.renderItemOverlayIntoGUI(
            font,
            renderEngine(),
            stack,
            xPos,
            yPos,
            ""
        );
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        renderItem.zLevel = 0.0f;

        String s;
        if (stack.stackSize == 1) s = "";
        else if (stack.stackSize < 1000) s = stack.stackSize + "";
        else if (stack.stackSize < 100000) s = stack.stackSize / 1000 + "K";
        else if (stack.stackSize < 1000000) s = "0M" + stack.stackSize / 100000;
        else s = stack.stackSize / 1000000 + "M";
        font.drawStringWithShadow(
            s,
            xPos + 19 - 2 - font.getStringWidth(s),
            yPos + 6 + 3,
            16777215
        );
    }
}
