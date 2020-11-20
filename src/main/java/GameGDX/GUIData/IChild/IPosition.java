package GameGDX.GUIData.IChild;

import GameGDX.Scene;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class IPosition {

    public String targetX="";
    public IAlign alignX= IAlign.bottomLeft;
    public String targetY="";
    public IAlign alignY= IAlign.bottomLeft;
    public boolean ratioX,ratioY;//scale = true -> x->scale with screen
    public float x,y,delX,delY;
    public IAlign align= IAlign.bottomLeft;
    public GetValue<Actor> getTarget;

    public float GetX()
    {
        return GetX0()+delX;
    }
    public float GetY()
    {
        return GetY0()+delY;
    }
    private float GetX0()
    {
        if (!targetX.equals("")) return getTarget.Get(targetX).getX(alignX.value);
        if (ratioX){
            float width = getTarget.Get("").getWidth();
            if (width<=0) width = Scene.width;
            return x * width;
        }
        return x;
    }
    private float GetY0()
    {
        if (!targetY.equals("")) return getTarget.Get(targetY).getY(alignY.value);
        if (ratioY){
            float height = getTarget.Get("").getHeight();
            if (height<=0) height = Scene.height;
            return y * height;
        }
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IPosition)) return false;
        IPosition iPosition = (IPosition) o;
        return ratioX == iPosition.ratioX &&
                ratioY == iPosition.ratioY &&
                Float.compare(iPosition.x, x) == 0 &&
                Float.compare(iPosition.y, y) == 0 &&
                Float.compare(iPosition.delX, delX) == 0 &&
                Float.compare(iPosition.delY, delY) == 0 &&
                targetX.equals(iPosition.targetX) &&
                alignX == iPosition.alignX &&
                targetY.equals(iPosition.targetY) &&
                alignY == iPosition.alignY &&
                align == iPosition.align;
    }
}
