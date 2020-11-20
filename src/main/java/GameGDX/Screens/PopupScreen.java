package GameGDX.Screens;

import GameGDX.UI;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class PopupScreen extends Screen {

    protected float scale = 1;

    public PopupScreen(){}
    public PopupScreen(String screenName)
    {
        super(screenName);
    }
    public PopupScreen(TextureRegion tr)
    {
        SetMainSize(tr.getRegionWidth(),tr.getRegionHeight());
        UI.NewImage(tr,main);
    }

    @Override
    protected void ShowAction() {
        main.setScale(0);
        Action ac = Actions.scaleTo(scale,scale,0.4f, Interpolation.bounceOut);
        main.addAction(Actions.sequence(ac, Actions.run(showDone)));
    }

    @Override
    protected void HideAction() {
        Action ac = Actions.scaleTo(0,0,0.2f, Interpolation.fade);
        main.addAction(Actions.sequence(ac, Actions.run(hideDone)));
    }
}
