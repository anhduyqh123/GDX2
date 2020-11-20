package GameGDX.GUIData;

import GameGDX.AssetLoading.AssetNode;
import GameGDX.AssetLoading.AssetPackage;
import GameGDX.GDX;
import GameGDX.GUIData.IChild.IChild;
import GameGDX.GUIData.IChild.Ison;
import GameGDX.Loader;
import com.badlogic.gdx.files.FileHandle;

import java.util.Set;

public class GUIData extends IGroup {
    private static GUIData instance;
    private AssetPackage assetPackage = Loader.GetAssetPackage("Objects");

    public GUIData()
    {
        instance = this;
        Ison.getIChild = this::Get;
    }
    public Set<String> GetKeys()
    {
        return map.keySet();
    }

    @Override
    public void Remove(String name) {
        map.remove(name);
        AssetNode node = assetPackage.Get(name);
        try {
            FileHandle file = new FileHandle(node.url);
            file.delete();
        }catch (Exception e){}
    }

    @Override
    public void Rename(String oldName, String newName) {
        IChild child = map.get(oldName);
        map.remove(oldName);
        map.put(newName,child);
    }

    @Override
    public void Move(String childName, int dir) {

    }

    @Override
    public void AddChildAndConnect(String childName, IChild child) {
        map.put(childName,child);
    }

    public void Refresh(String name)
    {
        map.get(name).Refresh();
    }
    public void Load()//load all
    {
        for(AssetNode n : assetPackage.list)
            Get(n.name);
    }
    public boolean Contains(String name)
    {
        return map.containsKey(name);
    }
    public IChild Get(String name)
    {
        if (map.containsKey(name)) return map.get(name);
        AssetNode n = assetPackage.Get(name);
        String data = GDX.ReadFromFile(n.url);
        IChild ic = Ison.FromJson(data);
        map.put(name,ic);
        return ic;
    }
    public void Save(String name,Runnable done)
    {
        if (!map.containsKey(name)) return;
        String path = assetPackage.url;
        IChild ic = map.get(name);
        ic.Reconnect(()->{
            String data = Ison.ToJson(ic);
            //data = Ison.PrettyPrint(data);
            String url = path+name+".ob";
            GDX.WriteToFile(url,data);
            done.run();
        });
    }

    public static IChild NewIChild(String prefab)
    {
        IChild iChild = instance.Get(prefab);
        IChild ob = Ison.Clone(iChild);
        ob.prefab = prefab;
        return ob;
    }
}
