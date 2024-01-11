package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import klaxon.klaxon.descala.Procedure;
import mrtjp.core.vec.Point;
import mrtjp.core.vec.Rect;
import mrtjp.core.vec.Size;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class SimpleTextboxNode extends TNode {

    public int x;
    public int y;
    public int w;
    public int h;
    public String tq;

    public SimpleTextboxNode() {
        this(0, 0, 0, 0, "");
    }

    public SimpleTextboxNode(int x, int y, int w, int h) {
        this(x, y, w, h, "");
    }

    public SimpleTextboxNode(int x, int y, int w, int h, String tq) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.tq = tq;

        this.position = new Point(x, y);
        this.text = tq;
    }

    public boolean enabled = true;
    public boolean focused = false;

    public String text;
    public String phantom = "";
    public String allowedcharacters = "";

    public Procedure textChangedDelegate = () -> {};
    public Procedure textReturnDelegate = () -> {};
    public Procedure focusChangeDelegate = () -> {};

    private int cursorCounter = 0;

    public Size size = new Size(w, h);

    @Override
    public Rect frame() {
        return new Rect(position, size);
    }

    public void setText(String t) {
        final String old = this.text;
        this.text = t;
        if (!Objects.equals(old, this.text)) textChangedDelegate.apply();
    }

    public void setFocused(boolean flag) {
        if (focused != flag) {
            focused = flag;
            if (focused) cursorCounter = 0;
            focusChangeDelegate.apply();
        }
    }

    public boolean canAddChar(char c) {
        if (allowedcharacters.isEmpty()) return ChatAllowedCharacters.isAllowedCharacter(c);
        return allowedcharacters.indexOf(c) >= 0;
    }

    @Override
    public void update_Impl() { ++cursorCounter; }

    @Override
    public boolean keyPressed_Impl(
        char c,
        int keycode,
        boolean consumed
    ) {
        if (enabled && focused && !consumed) {
            if (keycode == 1) { // esc

                setFocused(false);
                return true;
            }

            if (c == '\u0016') { // paste
                final String s = GuiScreen.getClipboardString();
                if (s == null || s.isEmpty()) return true;
                for (char ch : s.toCharArray())
                    if (!tryAddChar(ch)) return true;
            }

            if (keycode == Keyboard.KEY_RETURN) { // enter

                setFocused(false);
                textReturnDelegate.apply();
                return true;
            }

            if (keycode == Keyboard.KEY_BACK)
                return tryBackspace();
            else
                return tryAddChar(c);
        }
        return false;
    }

    private boolean tryAddChar(char c) {
        if (!canAddChar(c)) return false;
        final String ns = text + c;
        if (GuiDraw.getStringWidth(ns) > size.width - 8) return false;
        setText(ns);
        return true;
    }

    private boolean tryBackspace() {
        if (!text.isEmpty()) {
            setText(text.substring(0, text.length() - 1));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked_Impl(Point p, int button, boolean consumed) {
        if (!consumed && enabled && rayTest(p)) {
            setFocused(true);
            if (button == 1) setText("");
            return true;
        } else {
            setFocused(false);
            return false;
        }
    }

    @Override
    public void drawBack_Impl(Point mouse, float rframe) {
        GuiDraw.drawRect(
            position.x - 1,
            position.y - 1,
            size.width + 1,
            size.height + 1,
            0xffa0a0a0
        );
        GuiDraw.drawRect(
            position.x,
            position.y,
            size.width,
            size.height,
            0xff000000
        );

        if (text.isEmpty() && !phantom.isEmpty())
            GuiDraw.drawString(
                phantom,
                position.x + 4,
                position.y + size.height / 2 - 4,
                0x404040
            );

        final String drawText =
            text + ((enabled && focused && cursorCounter / 6 % 2 == 0) ? "_" : "");
        GuiDraw.drawString(
            drawText,
            position.x + 4,
            position.y + size.height / 2 - 4,
            (enabled) ? 0xe0e0e0 : 0x707070
        );
    }
}
