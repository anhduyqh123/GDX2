package GameGDX.Screens;

import GameGDX.Scene;
import GameGDX.UI;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

public class GameScreen extends Screen {
    public static Group bg,game,ui;

    public GameScreen()
    {
        bg = new Group();
        game = new Group();
        ui = new Group();
        main.addActor(bg);
        main.addActor(game);
        main.addActor(ui);
    }
    public GameScreen(String screenName)
    {
        super(screenName);
        game = GetActor("game", Group.class);
        ui = GetActor("ui", Group.class);
    }

    //default
    public void Show()
    {
        list.add(this);
        Scene.ui.addActor(this);
    }
    public void Hide()
    {
        hideDone.run();
        list.remove(this);
    }
    public void CloseAllScreen()
    {
        if (!list.contains(this)) return;
        while (!IsLatest()) GetLatest().Hide();
    }
    //lock part
    public void LockScreen(float unlockTime,Runnable unlock)
    {
        Image lock = UI.NewImage(new Color(1,1,1,0f),0,0, Align.bottomLeft, Scene.width, Scene.height,main);
        Run(()->{
            if (unlock!=null) unlock.run();
            lock.remove();
        },unlockTime);
    }
}
