package GameGDX.GUIData.IChild;

import com.badlogic.gdx.utils.Align;

public enum IAlign
{
    center(Align.center),
    top(Align.top),
    bottom(Align.bottom),
    left(Align.left),
    right(Align.right),
    topLeft(Align.topLeft),
    topRight(Align.topRight),
    bottomLeft(Align.bottomLeft),
    bottomRight(Align.bottomRight);

    public int value;
    IAlign(int value)
    {
        this.value = value;
    }
}
