package GameGDX.AssetLoading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetPackage {
    public String url = "";
    public ArrayList<AssetNode> list = new ArrayList<>(); //all file
    private Map<String, AssetNode> map = new HashMap<>();
    public ArrayList<AssetNode> activeNode = new ArrayList<>(); //main files uncluded atlas
    public ArrayList<AssetNode> loadableNode = new ArrayList<>(); //files to loaded

    public AssetPackage(){};
    public void Install()
    {
        map.clear();
        activeNode.clear();
        loadableNode.clear();

        for(AssetNode n : list)
        {
            if (n.main){
                if (map.containsKey(n.name)) System.out.println("trùng tên:"+n.pack+":"+n.name+"/"+map.get(n.name).name);
                map.put(n.name,n);
            }
            if (n.kind!= AssetNode.Kind.None)
            {
                if (n.kind!= AssetNode.Kind.TextureAtlas)
                    activeNode.add(n);
                if (!Contain(n.atlas)) loadableNode.add(n);
            }
        }
    }
    public void Add(List<AssetNode> nodes)
    {
        list.addAll(nodes);
    }
    public boolean Contain(String name)
    {
        return map.containsKey(name);
    }
    public AssetNode Get(String name)
    {
        return map.get(name);
    }
}
