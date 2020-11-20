package GameGDX.GUIData;

import GameGDX.GUIData.IChild.GetValue;
import GameGDX.GUIData.IChild.IAlign;
import GameGDX.GUIData.IChild.IChild;
import GameGDX.GUIData.IChild.IColor;
import GameGDX.Language;
import GameGDX.Loader;
import GameGDX.UI;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ILabel extends IChild {

    public String font = UI.fontName;
    public String text = "";
    public IAlign alignment = IAlign.center;
    public IColor color = IColor.WHITE;
    public float fontScale = 1f;
    public boolean bestFix,wrap,multiLanguage;

    private String GetText()
    {
        String txt = text;
        if (multiLanguage) txt = Language.instance.GetContent(text);
        return txt;
    }
    @Override
    protected Actor NewActor() {
        return UI.NewLabel(Loader.GetFont(font),GetText(),color.value,fontScale);
    }

    @Override
    public void SetConnect(GetValue<Actor> connect) {
        super.SetConnect(connect);
        iSize.getDefaultSize = n ->{
            Label lb = (Label)actor;
            return new Vector2(lb.getPrefWidth(),lb.getPrefHeight());
        };
    }

    @Override
    public void Refresh() {
        InitActor();
        Label lb = (Label)actor;
        lb.setText(GetText());
        lb.setColor(color.value);
        lb.setFontScale(fontScale);
        lb.setAlignment(alignment.value);
        UI.SetFont(lb, Loader.GetFont(font));
        super.Refresh();

        lb.setWrap(wrap);
        if (bestFix) UI.BestFix(lb,GetText(),fontScale,wrap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ILabel)) return false;
        if (!super.equals(o)) return false;
        ILabel label = (ILabel) o;
        return Float.compare(label.fontScale, fontScale) == 0 &&
                bestFix == label.bestFix &&
                wrap == label.wrap &&
                multiLanguage == label.multiLanguage &&
                font.equals(label.font) &&
                text.equals(label.text) &&
                alignment == label.alignment &&
                color == label.color;
    }
}
