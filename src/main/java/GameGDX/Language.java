package GameGDX;

import com.badlogic.gdx.utils.JsonValue;

import java.util.*;

public class Language extends GJson.JsonObject {
    public static Language instance;

    private Map<String, Node> map = new LinkedHashMap<>();
    public List<String> codes = new ArrayList<>();
    private String currentCode;

    private Map<String,Runnable> cbChange = new HashMap<>();
    private Runnable cbFirst;

    public Language()
    {
        instance = this;
    }
    public void SetCode(String newCode)
    {
        if (!codes.contains(newCode)) return;
        currentCode = newCode;
        if (cbFirst!=null) cbFirst.run();
        for(Runnable run : cbChange.values()) run.run();
    }
    public String GetCurrentCode()
    {
        return currentCode;
    }
    public String GetContent(String key)
    {
        return map.get(key).GetContent(currentCode);
    }
    public void AddFirst(Runnable cbFirst)
    {
        this.cbFirst = cbFirst;
    }
    public void AddChangeCallback(String key,Runnable cb)
    {
        cbChange.put(key,cb);
        cb.run();
    }

    public Node AddKey(String key)
    {
        return AddKey(key,new Node(codes));
    }
    public Node AddKey(String key,Node node)
    {
        if (map.containsKey(key)) return map.get(key);
        map.put(key,node);
        return map.get(key);
    }
    public void RemoveKey(String key)
    {
        if (!map.containsKey(key)) return;
        map.remove(key);
    }
    public Set<String> GetKeys()
    {
        return map.keySet();
    }

    public void AddCode(String code)
    {
        if (codes.contains(code)) return;
        codes.add(code);
        for(Node n : map.values())
            n.Add(code);
    }
    public void RemoveCode(String code)
    {
        if (!codes.contains(code)) return;
        codes.remove(code);
        for(Node n : map.values())
            n.Remove(code);
    }

    @Override
    public void SetData(JsonValue js) {
        map = GJson.ToLinkedMap(js.get("map"),Node.class);
        codes = GJson.ToList(js.get("codes"),String.class);
        currentCode = codes.get(0);
    }

    @Override
    public JsonValue ToJson() {
        JsonValue jsonValue = new JsonValue(JsonValue.ValueType.object);
        jsonValue.addChild("map",GJson.MapToJson(map));
        jsonValue.addChild("codes",GJson.ListToJson(codes));
        return jsonValue;
    }

    public static class Node extends GJson.JsonObject
    {
        private Map<String,String> map = new LinkedHashMap<>();

        public Node(){}
        public Node(List<String> codes)
        {
            for(String code : codes)
                Add(code);
        }
        public String GetContent(String key)
        {
            return map.get(key);
        }
        public void Remove(String key)
        {
            if(!map.containsKey(key)) return;
            map.remove(key);
        }
        public void Add(String key)
        {
            if (map.containsKey(key)) return;
            map.put(key,"");
        }
        public void Put(String key,String content)
        {
            map.put(key,content);
        }

        @Override
        public void SetData(JsonValue js) {
            map = GJson.ToLinkedMap(js,String.class);
        }

        public JsonValue ToJson()
        {
            return GJson.MapToJson(map);
        }
    }
}
