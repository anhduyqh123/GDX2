package GameGDX.GUIData.IChild;

import com.badlogic.gdx.math.Vector2;

public class ISize {
    public float width,height,scale=1f;
    public IAlign origin = IAlign.bottomLeft;
    public boolean extendScreen;
    public GetValue<Vector2> getDefaultSize;

    public void Disconnect()
    {
        getDefaultSize = null;
    }
    private Vector2 GetDefault()
    {
        if (getDefaultSize==null) return null;
        return getDefaultSize.Get("");
    }
    public float GetWidth()
    {
        if (width>0) return width;
        Vector2 defaultSize = GetDefault();
        if (defaultSize!=null) return defaultSize.x;
        return width;
    }
    public float GetHeight()
    {
        if (height>0) return height;
        Vector2 defaultSize = GetDefault();
        if (defaultSize!=null) return defaultSize.y;
        return height;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ISize)) return false;

        ISize iSize = (ISize) object;

        if (Float.compare(iSize.width, width) != 0) return false;
        if (Float.compare(iSize.height, height) != 0) return false;
        if (Float.compare(iSize.scale, scale) != 0) return false;
        if (extendScreen != iSize.extendScreen) return false;
        return origin == iSize.origin;
    }
}
