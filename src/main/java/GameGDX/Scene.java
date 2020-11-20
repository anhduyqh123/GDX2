package GameGDX;

import GameGDX.Screens.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Scene {
    public static float mWidth, mHeight,scaleX,scaleY,scale;
    public static int width,height;
    public static Group background,game,ui,ui2;
    public static Stage stage;

    public Scene(float gWidth,float gHeight)
    {
        mWidth = gWidth;
        mHeight = gHeight;
        Init();
        InitSize();
    }
    private void InitSize()
    {
        width = (int)stage.getViewport().getWorldWidth();
        height = (int)stage.getViewport().getWorldHeight();
        scaleX = width/ mWidth;
        scaleY = height/ mHeight;
        scale = Math.max(scaleX,scaleY);
    }
    private void Init()
    {
        background = new Group();
        game = new Group();
        game.addActor(new Group());
        game.addActor(new Group());
        ui = new Group();
        ui2 = new Group();

        stage = new Stage(new ExtendViewport(mWidth, mHeight));

        stage.addActor(background);
        stage.addActor(game);
        stage.addActor(ui);
        stage.addActor(ui2);

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) Screen.BackButtonEvent();
                return super.keyDown(event, keycode);
            }
        });
    }
    public static void Act()
    {
        stage.act(GDX.DeltaTime());
    }
    public static void Render()
    {
        stage.draw();
    }
    public static void Resize(int width,int height)
    {
        stage.getViewport().update(width,height);
    }
    public static Camera GetCamera()
    {
        return stage.getCamera();
    }
    public static void AddActorKeepPosition(Actor actor, Group group)
    {
        Vector2 pos1 = GetStagePosition(actor, Align.bottomLeft);
        Vector2 pos2 = group.stageToLocalCoordinates(pos1);
        actor.setPosition(pos2.x,pos2.y);
        group.addActor(actor);
    }
    //Position
    public static Vector2 GetStagePosition(Actor actor, int align)
    {
        Vector2 pos = new Vector2(actor.getX(align),actor.getY(align));
        actor.getParent().localToStageCoordinates(pos);
        return pos;
    }
}
