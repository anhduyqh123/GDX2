package GameGDX.GUIData.IChild;

import com.badlogic.gdx.graphics.Color;

public enum IColor {
    WHITE(Color.WHITE),
    BROWN(Color.BROWN),
    YELLOW(Color.YELLOW),
    BLACK(Color.BLACK),
    GRAY(Color.GRAY),
    RED(Color.RED),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN);

    public Color value;
    IColor(Color value)
    {
        this.value = value;
    }
}
