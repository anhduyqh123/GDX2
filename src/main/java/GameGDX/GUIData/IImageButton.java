package GameGDX.GUIData;

import GameGDX.GUIData.IChild.IAlign;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class IImageButton extends IGroup {

    public IImageButton()
    {
        freeze = true;

        IImage image = new IImage();
        image.texture = "button";
        image.isButton = true;

        IImage icon = new IImage();
        icon.iPosition.align = IAlign.center;
        icon.iPosition.ratioX = true;
        icon.iPosition.ratioY = true;
        icon.iPosition.x = 0.5f;
        icon.iPosition.y = 0.5f;

        AddChild("image",image);
        AddChild("icon",icon);
    }
    @Override
    public Vector2 GetSize() {
        return map.get("image").GetSize();
    }

    @Override
    public void Refresh() {
        super.Refresh();
        map.get("icon").actor.setTouchable(Touchable.disabled);
    }
}
