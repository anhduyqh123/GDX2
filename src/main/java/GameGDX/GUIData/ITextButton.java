package GameGDX.GUIData;

import GameGDX.GUIData.IChild.GetValue;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ITextButton extends IGroup {

    public ITextButton()
    {
        freeze = true;
        IImage image = new IImage();
        image.texture = "button";
        image.isButton = true;

        ILabel label = new ILabel();

        label.text = "Name";
        label.bestFix = true;
        label.wrap = true;

        AddChild("image",image);
        AddChild("label",label);
    }
    @Override
    public void SetConnect(GetValue<Actor> connect) {
        super.SetConnect(connect);
        map.get("label").iSize.getDefaultSize = name->GetSize();
    }

    @Override
    public Vector2 GetSize() {
        return map.get("image").GetSize();
    }
}
