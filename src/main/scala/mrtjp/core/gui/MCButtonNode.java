package mrtjp.core.gui;

/**
 * Default implementation of a button in mc with normal render and text overlay
 */
public class MCButtonNode extends TButtonMC implements IButtonText {

    private final TButtonText textDelegate = new TButtonText();

    @Override
    public void drawButton(boolean mouseover) {
        super.drawButton(mouseover);
        textDelegate.drawButton(mouseover, this);
    }

    @Override
    public void setText(String s) {
        textDelegate.text = s;
    }
}
