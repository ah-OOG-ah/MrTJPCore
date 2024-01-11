package mrtjp.core.gui;

import codechicken.lib.gui.GuiDraw;
import org.lwjgl.opengl.GL11;

/**
 * Originally, this was a trait for buttons that render their foreground as text. It still kinda serves that purpose,
 * but instead it's used as a delegate field. Instead of Scala's stacking mixins, you manually call this one.
 * The more I go through this the more I'm convinced MrTJP made a vastly overcomplicated GUI system and was just drunk
 * on Scala. I can see the appeal but... something in me deeply rejects the idea of extending a class multiple time.
 * Also, Scala bytecode. 'Nuff said.
 */
public class TButtonText {

    public String text;
    public TButtonText setText(String t) {
        text = t;
        return this;
    }

    public void drawButton(boolean mouseover, ButtonNode parent) {
        GuiDraw.drawStringC(
            text,
            parent.position.x + parent.size.width / 2,
            parent.position.y + (parent.size.height - 8) / 2,
            (mouseover) ? 0xffffffa0 : 0xffe0e0e0
        );
        GL11.glColor4f(1, 1, 1, 1);
    }
}
