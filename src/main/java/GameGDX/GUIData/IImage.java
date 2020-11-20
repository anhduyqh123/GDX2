package GameGDX.GUIData;

import GameGDX.GDX;
import GameGDX.GUIData.IChild.GetValue;
import GameGDX.GUIData.IChild.IChild;
import GameGDX.Language;
import GameGDX.Loader;
import GameGDX.UI;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class IImage extends IChild {
    public String texture="";
    public float sizeScale = 1f;
    public boolean isButton,multiLanguage;
    public Touchable touchable = Touchable.enabled;

    @Override
    protected Actor NewActor() {
        return new Image();
    }

    @Override
    public void SetConnect(GetValue<Actor> connect) {
        super.SetConnect(connect);
        iSize.getDefaultSize = name->GetDefaultSize(GetTextureName());
    }
    private String GetTextureName()
    {
        String name = texture;
        if (multiLanguage) name+= Language.instance.GetCurrentCode();
        return name;
    }

    @Override
    protected Vector2 GetDefaultSize(String name) {
        Vector2 size = new Vector2();
        try {
            TextureRegion tr = Loader.GetTexture(name);
            size.set(tr.getRegionWidth(),tr.getRegionHeight());
        }catch (Exception e){};
        return size;
    }

    @Override
    public Vector2 GetSize() {
        Vector2 size = super.GetSize();
        return size.scl(sizeScale);
    }

    @Override
    public void Refresh() {
        super.Refresh();
        Image img = (Image)actor;
        img.setTouchable(touchable);

        actor.clearListeners();
        if (isButton) UI.EffectPressedButton(actor);

        try
        {
            TextureRegion tr = Loader.GetTexture(GetTextureName());
            img.setDrawable(new TextureRegionDrawable(tr));
        }
        catch (Exception e){
            GDX.PostRunnable(()->{
                TextureRegion tr = new TextureRegion(UI.NewTexture(100,100));
                img.setDrawable(new TextureRegionDrawable(tr));
            });
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof IImage)) return false;
        if (!super.equals(object)) return false;

        IImage iImage = (IImage) object;

        if (Float.compare(iImage.sizeScale, sizeScale) != 0) return false;
        if (isButton != iImage.isButton) return false;
        if (multiLanguage != iImage.multiLanguage) return false;
        if (!texture.equals(iImage.texture)) return false;
        return touchable == iImage.touchable;
    }
}
