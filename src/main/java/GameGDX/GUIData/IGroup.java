package GameGDX.GUIData;

import GameGDX.GUIData.IChild.GetValue;
import GameGDX.GUIData.IChild.IChild;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.*;

public class IGroup extends IChild {
    protected Map<String, IChild> map = new HashMap<>();
    protected List<String> list = new ArrayList<>();

    public String sizeName = "";

    public IGroup()
    {
        freeze = false;
    }

    @Override
    protected Actor NewActor() {
        return new Group();
    }

    public void Move(String childName, int dir)
    {
        int index = list.indexOf(childName);
        int nIndex = index+dir;
        if (nIndex<0) nIndex = 0;
        if (nIndex>=list.size()) nIndex = list.size()-1;
        String nName = list.get(nIndex);
        list.set(nIndex,childName);
        list.set(index,nName);
        Reconnect(null);
    }
    public boolean Contain(String name)
    {
        return map.containsKey(name);
    }

    protected void AddChild(String name, IChild child)
    {
        if (map.containsKey(name)) return;
        map.put(name,child);
        list.add(name);
    }
    public void AddChildAndConnect(String name, IChild child)
    {
        AddChild(name,child);
        child.SetConnect(n->GetActor(n));
    }
    public void Remove(String name)
    {
        Actor child = GetActor(name);
        if (child!=null) child.remove();
        map.remove(name);
        list.remove(name);
    }
    public void Rename(String oldName,String newName)
    {
        IChild child = map.get(oldName);
        map.remove(oldName);
        map.put(newName,child);

        int index = list.indexOf(oldName);
        list.set(index,newName);
    }
    public IChild GetIChild(String name)
    {
        if (map.containsKey(name)) return map.get(name);
        for(IChild ic : map.values())
            if (ic instanceof IGroup)
            {
                IChild result = ((IGroup)ic).GetIChild(name);
                if (result!=null) return result;
            }
        return null;
    }
    public IGroup GetIGroup(String name)
    {
        return (IGroup) GetIChild(name);
    }

    public Actor GetActor(String name)
    {
        if (name.equals("")) return actor;
        return map.get(name).actor;
    }
    public <T> T GetActor(String name,Class<T> type){
        if (map.containsKey(name)) return (T)map.get(name).actor;
        for(IChild child : map.values())
            if (child instanceof IGroup)
            {
                IGroup iGroup = (IGroup)child;
                T actor = iGroup.GetActor(name,type);
                if (actor!=null) return actor;
            }
        return null;
    }

    @Override
    protected Vector2 GetDefaultSize(String name) {
        if (map.containsKey(name)) return GetIChild(name).GetSize();
        return null;
    }

    public void Refresh()
    {
        super.Refresh();
        for(String name : list)
            map.get(name).Refresh();
    }
    public Collection<String> GetChills()
    {
        return list;
    }
    @Override
    public void SetConnect(GetValue<Actor> connect) {
        iSize.getDefaultSize = name->GetDefaultSize(sizeName);
        super.SetConnect(connect);
        for(String name : list)
            map.get(name).SetConnect(n->GetActor(n));
    }

    @Override
    public void Disconnect() {
        super.Disconnect();
        for(IChild child : map.values())
            child.Disconnect();
    }
}
