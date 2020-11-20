package GameGDX.GUIData.IChild;

import GameGDX.Scene;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class IChild {
    public String prefab = "";

    public ISize iSize = new ISize();
    public IPosition iPosition = new IPosition();
    protected boolean freeze = true;

    public Actor actor;
    protected GetValue<Actor> connect;

    public IChild(){}
    public IChild Clone()
    {
        return Clone(this.getClass());
    }
    protected <T> T Clone(Class<T> type)
    {
        Object[] clones = new Object[1];
        Reconnect(()->clones[0] = Ison.Clone(this,type));
        return (T)clones[0];
    }

    protected Actor NewActor()
    {
        return new Actor();
    }

    protected void InitActor()
    {
        if (actor==null) actor = NewActor();
        try {
            Group parent = (Group) connect.Get("");
            parent.addActor(actor);
        }catch (Exception e){}
    }
    public Vector2 GetSize()
    {
        return new Vector2(iSize.GetWidth(), iSize.GetHeight());
    }
    protected void BaseRefresh()
    {
        InitActor();
        Vector2 size = GetSize();
        actor.setSize(size.x,size.y);
        actor.setOrigin(iSize.origin.value);
        if (iSize.extendScreen) actor.setScale(Scene.scale);
        else actor.setScale(iSize.scale);

        actor.setPosition(iPosition.GetX(), iPosition.GetY(), iPosition.align.value);
    }

    public void Refresh()
    {
        BaseRefresh();
    }
    public void SetConnect(GetValue<Actor> connect)//this call when this created by json
    {
        this.connect = connect;
        iPosition.getTarget = connect;
    }
    public void Disconnect()
    {
        actor = null;
        connect = null;
        iSize.Disconnect();
        iPosition.getTarget = null;
    }
    public void Remove()
    {
        if (actor!=null) actor.remove();
        Disconnect();
    }
    public void Reconnect(Runnable runnable)
    {
        GetValue<Actor> con = connect;
        Remove();
        if (runnable!=null) runnable.run();
        SetConnect(con);
        Refresh();
    }

    protected Vector2 GetDefaultSize(String name)
    {
        return new Vector2();
    }
    public boolean IsFreeze()
    {
        return freeze;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IChild)) return false;
        IChild iChild = (IChild) o;
        return freeze == iChild.freeze &&
                iSize.equals(iChild.iSize) &&
                iPosition.equals(iChild.iPosition);
    }
}
