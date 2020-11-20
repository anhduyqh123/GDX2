package GameGDX.Screens;

import GameGDX.GUIData.GUIData;
import GameGDX.GUIData.IGroup;
import GameGDX.Scene;
import GameGDX.UI;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class Screen extends Group {
    public static List<Screen> list = new ArrayList<>();
    public static GUIData data = new GUIData();

    private IGroup iGroup;
    protected Image overlay;
    public Group main;
    public Runnable showDone,hideDone;

    public Runnable onHide,onShow,onBackHandle;

    public Screen(String screenName)
    {
        InitScreen();
        InitMain(screenName);
    }
    public Screen()
    {
        InitScreen();
        InitMain(Scene.width, Scene.height);
    }
    protected void SetMainSize(float width, float height)
    {
        main.setSize(width, height);
        main.setOrigin(Align.center);
        main.setPosition(Scene.width/2, Scene.height/2, Align.center);
    }
    private void InitMain(float width, float height)
    {
        main = new Group();
        SetMainSize(width, height);
        this.addActor(main);
    }
    private void InitMain(String screenName)
    {
        iGroup = GetObject(screenName);
        iGroup.SetConnect(name -> this);
        iGroup.Refresh();

        main = (Group) iGroup.actor;
        UI.SetBounds(main, Scene.width/2, Scene.height/2, Align.center, Scene.width, Scene.height);
        main.setOrigin(Align.center);
    }

    private void InitScreen()
    {
        overlay = UI.NewImage(new Color(0,0,0,0.5f),0,0, Align.bottomLeft, Scene.width, Scene.height,this);

        hideDone = this::remove;
        showDone = ()->{};
    }
    public Action Run(Runnable run, float delay)
    {
        Action ac1 = Actions.delay(delay);
        Action ac2 = Actions.run(run);
        Action ac12 = Actions.sequence(ac1,ac2);
        this.addAction(ac12);
        return ac12;
    }
    protected void ShowAction()
    {
        main.setColor(1,1,1,0);
        Action ac = Actions.fadeIn(0.4f, Interpolation.fade);
        main.addAction(Actions.sequence(ac, Actions.run(showDone)));
    }
    protected void HideAction()
    {
        Action ac = Actions.fadeOut(0.4f, Interpolation.fade);
        main.addAction(Actions.sequence(ac, Actions.run(hideDone)));
    }
    private void RunEvent(Runnable event)
    {
        if (event==null) return;
        event.run();
    }
    public void AddClick(String name, Runnable event)
    {
        Actor actor = GetActor(name);
        UI.AddClick(actor,event);
    }

    public Actor GetActor(String name)
    {
        return GetActor(name, Actor.class);
    }
    public <T> T GetActor(String name,Class<T> type)
    {
        return iGroup.GetActor(name,type);
    }
    public IGroup GetIChild(String name)
    {
        return (IGroup) iGroup.GetIChild(name);
    }

    public void Show()
    {
        RunEvent(onShow);
        list.add(this);
        Scene.ui.addActor(this);
        setTouchable(Touchable.enabled);

        main.clearActions();
        ShowAction();
    }
    public void Hide()
    {
        RunEvent(onHide);
        list.remove(this);

        setTouchable(Touchable.disabled);
        main.clearActions();
        HideAction();
    }
    public void OnBackButtonPressed()
    {
        RunEvent(onBackHandle);
    }
    public boolean IsLatest()
    {
        return this.equals(GetLatest());
    }

    public static void BackButtonEvent()
    {
        Screen screen = GetLatest();
        if (screen==null) return;
        screen.OnBackButtonPressed();
    }
    public static Screen GetLatest()
    {
        if (list.size()<=0) return null;
        return list.get(list.size()-1);
    }

    //Gui data
    public static IGroup GetObject(String name)
    {
        IGroup iGroup = (IGroup) data.Get(name);
        iGroup.Disconnect();
        return iGroup;
    }
}
