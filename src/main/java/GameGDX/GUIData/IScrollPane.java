package GameGDX.GUIData;

import GameGDX.GUIData.IChild.IChild;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

public class IScrollPane extends IGroup {

    public float startX,startY;
    @Override
    protected Actor NewActor() {
        return new ScrollPane(null);
    }

    public void AddChildAndConnect(String childName, IChild child)
    {
        AddChild(childName,child);
        child.SetConnect(null);
    }

    @Override
    public void Refresh() {
        BaseRefresh();
        RefreshContent();
    }
    private void RefreshContent()
    {
        if (list.size()<=0) return;
        String name = list.get(0);
        IChild iChild = GetIChild(name);
        iChild.Refresh();

        ScrollPane scroll = (ScrollPane)actor;
        scroll.setActor(iChild.actor);
        scroll.scrollTo(startX,startY,0,0);
    }
}
