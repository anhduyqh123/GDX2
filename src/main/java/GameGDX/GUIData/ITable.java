package GameGDX.GUIData;

import GameGDX.GUIData.IChild.IAlign;
import GameGDX.GUIData.IChild.IChild;
import GameGDX.GUIData.IChild.Ison;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ITable extends IGroup {

    public float childWidth,childHeight;
    public IAlign contentAlign = IAlign.center;
    public int column = 0;
    public float spaceX,spaceY;

    @Override
    protected Actor NewActor() {
        return new Table();
    }

    @Override
    public void Refresh() {
        super.Refresh();
        RefreshChildren();
    }
    private void RefreshChildren()
    {
        Table table = (Table)actor;
        table.clearChildren();

        int i=0;
        NewRow(table);
        for(String name : list)
        {
            table.add(GetActor(name));
            if (column==0) continue;
            i++;
            if (i%column==0) NewRow(table);
        }
        table.align(contentAlign.value);
    }
    private void NewRow(Table table)
    {
        Cell cell = table.row().spaceRight(spaceX).spaceTop(spaceY);
        if (childWidth*childHeight!=0) cell.width(childWidth).height(childHeight);
    }
    public void CloneChild(int amount)
    {
        if (list.size()<=0) return;
        String name = list.get(0);
        IChild child = GetIChild(name);
        list.clear();
        map.clear();

        for(int i=0;i<amount;i++)
        {
            child.Disconnect();
            IChild clone = Ison.Clone(child);
            AddChildAndConnect(name+i,clone);
        }
        Refresh();
    }
}
