package GameGDX.GUIData.IChild;

import GameGDX.GJson;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.io.IOException;

public class Ison extends GJson {

    public static String ToJson(Object object)
    {
        Ison json = new Ison();
        return json.ToJson(object,object.getClass());
    }
    public static <T> T FromJson (String data)
    {
        Ison json = new Ison();
        try {
            return (T)json.FromJson(ToJsonValue(data));
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GetValue<IChild> getIChild;


    public <T> T FromJson (JsonValue jsonData) throws ReflectionException {
        String stType = jsonData.getString("class");
        Class type = ClassReflection.forName(stType);
        return FromJson(type,jsonData);
    }

    protected String ToJson(Object object,Object defaultObject)
    {
        try {
            WriteValue(object, defaultObject,true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            StreamUtils.closeQuietly(writer);
        }
        return writer.getWriter().toString();
    }


    @Override
    protected void WriteObject(Object value, Object defaultValue) throws IOException {
        try {
            String prefab = ((IChild)value).prefab;
            if (!prefab.equals(""))
                defaultValue = Clone(getIChild.Get(prefab));
        }catch (Exception e){}
        super.WriteObject(value,defaultValue);
    }

    @Override
    protected <T> T CreateObject(Class<T> type, Object object, JsonValue jsonData) throws ReflectionException {
        String prefab = jsonData.getString("prefab","");
        if (!prefab.equals("")) object = Clone(getIChild.Get(prefab));
        return super.CreateObject(type, object, jsonData);
    }
}
