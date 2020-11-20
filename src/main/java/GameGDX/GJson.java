package GameGDX;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.io.IOException;
import java.io.StringWriter;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.*;

public class GJson {

    private static final Map<Class,Object> typeToDefaultObject = new HashMap<>();
    private static final Map<Class,Map<String, Field>> typeToFields = new HashMap<>();

    public static String ToJson(Object object)
    {
        GJson json = new GJson();
        return json.ToJson(object,object.getClass());
    }
    public static <T> T FromJson (Class type, String data)
    {
        GJson json = new GJson();
        return (T)json.FromJson(type,ToJsonValue(data));
    }


    //GJson
    protected JsonWriter writer;

    public GJson()
    {
        this(OutputType.minimal);
    }
    public GJson(OutputType outputType)
    {
        writer = new JsonWriter(new StringWriter());
        writer.setOutputType(outputType);
    }
    private <T> T FromJson (Object defaultObject, JsonValue jsonData)
    {
        try {
            return (T)ReadValue(defaultObject.getClass(),defaultObject,null,jsonData);
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected <T> T FromJson (Class type, JsonValue jsonData)
    {
        return FromJson(NewInstance(type),jsonData);
    }
    protected String ToJson(Object object,Class type)
    {
        return ToJson(object,GetDefaultObject(type));
    }
    protected String ToJson(Object object,Object defaultObject)
    {
        try {
            WriteValue(object, defaultObject);
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            StreamUtils.closeQuietly(writer);
        }
        return writer.getWriter().toString();
    }
    private void WriteValue(Object value,Object defaultValue) throws IOException {
        WriteValue(value, defaultValue,false);
    }
    protected void WriteValue(Object value,Object defaultValue,boolean writeType) throws IOException {
        if (value==null)
        {
            writer.value(null);
            return;
        }
        Class actualType = value.getClass();
        if (IsBaseType(actualType))
        {
            writer.value(value);
            return;
        }
        if (value instanceof Enum)
        {
            writer.value(value.toString());
            return;
        }
        if (value instanceof List)
        {
            writer.array();
            List<?> list = (List<?>)value;
            List<?> list0 = (List<?>)defaultValue;

            for(int i=0;i<list.size();i++)
            {
                Object defaultObject = null;
                Object childObject = list.get(i);
                if (list0!=null && list0.size()==list.size()) defaultObject = list0.get(i);
                else defaultObject = GetDefaultObject(childObject.getClass());
                WriteValue(childObject,defaultObject);
            }

            writer.pop();
            return;
        }
        if (value instanceof Map)
        {
            writer.object();
            Map<?, ?> map = (Map<?, ?>)value;
            Map<?, ?> map0 = (Map<?, ?>)defaultValue;

            for (Map.Entry entry : map.entrySet()){
                Object defaultChildObject = null;
                String key = entry.getKey().toString();
                Object childObject = map.get(key);
                if (map0!=null && map0.containsKey(key))
                    defaultChildObject = map0.get(key);
                else defaultChildObject = GetDefaultObject(childObject.getClass());
                writer.name(key);
                WriteValue(childObject,defaultChildObject,true);
            }
            writer.pop();
            return;
        }
        writer.object();
        if (writeType)
        {
            writer.name("class");
            writer.value(value.getClass().getName());
        }
        WriteObject(value,defaultValue);
        writer.pop();
    }
    protected void WriteObject(Object value,Object defaultValue) throws IOException {
        WriteFields(value,defaultValue);
    }
    private void WriteFields(Object value, Object defaultValue){
        Class type = value.getClass();
        for(Field f : GetFields(type).values())
        {
            try {
                Object object = f.get(value);
                Object object0 = null;
                if (defaultValue!=null) object0 = f.get(defaultValue);
                if (Equals(object,object0)) continue;
                writer.name(f.getName());
                WriteValue(object,object0);

            } catch (ReflectionException | IOException e) {
                e.printStackTrace();
            }
        }
    }
    private <T> T ReadValue(Class type, Object object, Class elementType, JsonValue jsonData) throws ReflectionException {
        if (jsonData==null) return null;
        if (IsBaseType(type)) return (T)ToBaseType(jsonData,type);
        if (ClassReflection.isAssignableFrom(Enum.class, type)) {
            String string = jsonData.asString();
            Enum[] constants = (Enum[])type.getEnumConstants();
            for (int i = 0, n = constants.length; i < n; i++) {
                Enum e = constants[i];
                if (string.equals(e.toString())) return (T)e;
            }
        }
        if (jsonData.isArray())
        {
            Collection result = (Collection) object;
            result.clear();
            for (JsonValue child = jsonData.child; child != null; child = child.next)
                result.add(ReadValue(elementType, NewInstance(elementType),null, child));
            return (T)result;
        }
        if (object instanceof Map)
        {
            Map result = new HashMap();
            Map map0 = (Map)object;
            for (JsonValue child = jsonData.child; child != null; child = child.next)
            {
                Object childObject = null;
                if (map0!=null && map0.containsKey(child.name)) childObject = map0.get(child.name);
                result.put(child.name,ReadValue(elementType, childObject,null, child));
            }
            return (T)result;
        }
        object = CreateObject(type,object,jsonData);
        ReadFields(object,jsonData);
        return (T)object;
    }
    protected <T> T CreateObject(Class<T> type, Object object, JsonValue jsonData) throws ReflectionException {
        String stType = jsonData.getString("class","");
        if (!stType.equals("")) type = ClassReflection.forName(stType);
        if (object==null) object = NewInstance(type);
        return (T) object;
    }
    private void ReadFields(Object object, JsonValue jsonMap) throws ReflectionException {
        Map<String, Field> map = GetFields(object.getClass());
        for (JsonValue child = jsonMap.child; child != null; child = child.next) {
            if (!map.containsKey(child.name)) continue;
            Field field = map.get(child.name);
            int index = (ClassReflection.isAssignableFrom(ObjectMap.class, field.getType())
                    || ClassReflection.isAssignableFrom(Map.class, field.getType())) ? 1 : 0;
            field.set(object,ReadValue(field.getType(),field.get(object),field.getElementType(index),child));
        }
    }

    private static boolean Equals(Object objectA,Object objectB)
    {
        if (objectA==null && objectB==null) return true;
        if (objectA!=null && objectB!=null) return objectA.equals(objectB);
        return false;
    }
    private static boolean IsBaseType(Class actualType)
    {
        return actualType.isPrimitive() || actualType == String.class || actualType == Integer.class || actualType == Boolean.class
                || actualType == Float.class || actualType == Long.class || actualType == Double.class || actualType == Short.class
                || actualType == Byte.class || actualType == Character.class;
    }
    private static <T> T ToBaseType(JsonValue js, Class<T> type)
    {
        String string = js.asString();
        if (type==int.class || type == Integer.class) return (T)Integer.valueOf(string);
        if (type==float.class || type == Float.class) return (T)Float.valueOf(string);
        if (type==long.class || type == Long.class) return (T)Long.valueOf(string);
        if (type==double.class || type == Double.class) return (T)Double.valueOf(string);
        if (type==short.class || type == Short.class) return (T)Short.valueOf(string);
        if (type==byte.class || type == Byte.class) return (T)Byte.valueOf(string);
        if (type == String.class) return (T)string;
        if (type==boolean.class || type == Boolean.class) return (T)Boolean.valueOf(string);
        if (type==char.class || type == Character.class) return (T)(Character)string.charAt(0);
        if (type == CharSequence.class) return (T)string;
        return null;
    }

    public static Map<String, Field> GetFields(Class type)
    {
        if (typeToFields.containsKey(type)) return typeToFields.get(type);

        Array<Class> classHierarchy = new Array();
        Class nextClass = type;
        while (nextClass != Object.class) {
            classHierarchy.add(nextClass);
            nextClass = nextClass.getSuperclass();
        }
        ArrayList<Field> allFields = new ArrayList();
        for (int i = classHierarchy.size - 1; i >= 0; i--)
            Collections.addAll(allFields, ClassReflection.getDeclaredFields(classHierarchy.get(i)));

        Map<String, Field> map = new HashMap<>();
        for(Field f : allFields)
        {
            if (!f.isAccessible()) {
                try {
                    f.setAccessible(true);
                } catch (AccessControlException ex) {
                    continue;
                }
            }
            if (f.isStatic()) continue;
            if (isInterface(type)) continue;

            map.put(f.getName(),f);
        }
        typeToFields.put(type,map);
        return map;
    }
    private static boolean isInterface(Class type)
    {
        if (type == Map.class || type == List.class) return false;
        return type.isInterface();
    }
    private static Object GetDefaultObject(Class type)
    {
        if (typeToDefaultObject.containsKey(type)) return typeToDefaultObject.get(type);
        Object object = NewInstance(type);
        typeToDefaultObject.put(type,object);
        return object;
    }
    public static <T> T Clone(Object object)
    {
        return (T)Clone(object,object.getClass());
    }
    public static  <T> T Clone(Object object,Class<T> type)
    {
        try {
            return (T)CloneValue(NewInstance(type),object);
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static  <T> T CloneValue(Object object,Object cloneObject) throws ReflectionException {
        Class actualType = cloneObject.getClass();
        if (IsBaseType(actualType)) return (T)cloneObject;
        if (cloneObject instanceof Enum) return (T)cloneObject;
        if (cloneObject instanceof List)
        {
            List list = new ArrayList();
            List list0 = (List)cloneObject;
            for(Object child : list0)
                list.add(CloneValue(NewInstance(child.getClass()),child));
            return (T)list;
        }
        if (cloneObject instanceof Map)
        {
            Map map = new HashMap();
            Map<?,?> map0 = (Map<?,?>) cloneObject;
            for (Map.Entry entry : map0.entrySet())
            {
                Object child = entry.getValue();
                Object c = CloneValue(NewInstance(child.getClass()),child);
                map.put(entry.getKey().toString(),c);
            }
            return (T)map;
        }
        CloneFields(object,cloneObject);
        return (T)object;
    }
    private static void CloneFields(Object object,Object cloneObject) throws ReflectionException {
        for(Field f : GetFields(cloneObject.getClass()).values())
        {
            Object fClone = f.get(cloneObject);
            Object fObject = f.get(object);
            if (Equals(fObject,fClone)) continue;
            if (fObject==null) fObject = NewInstance(f.getType());
            if (fClone!=null)  f.set(object,CloneValue(fObject,fClone));
            else f.set(object,null);
        }
    }
    public static Object NewInstance (Class type) {
        try {
            return ClassReflection.newInstance(type);
        } catch (Exception ex) {
            try {
                // Try a private constructor.
                Constructor constructor = ClassReflection.getDeclaredConstructor(type);
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (SecurityException ignored) {
            } catch (ReflectionException ignored) {
                if (ClassReflection.isAssignableFrom(Enum.class, type)) {
                    if (type.getEnumConstants() == null) type = type.getSuperclass();
                    return type.getEnumConstants()[0];
                }
                if (type.isArray())
                    throw new SerializationException("Encountered JSON object when expected array of type: " + type.getName(), ex);
                else if (ClassReflection.isMemberClass(type) && !ClassReflection.isStaticClass(type))
                    throw new SerializationException("Class cannot be created (non-static member class): " + type.getName(), ex);
                else
                    throw new SerializationException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
            } catch (Exception privateConstructorException) {
                ex = privateConstructorException;
            }
            throw new SerializationException("Error constructing instance of class: " + type.getName(), ex);
        }
    }

    //Json Value
    private static final JsonReader jsonReader = new JsonReader();

    public static String PrettyPrint(String data)
    {
        return ToJsonValue(data).prettyPrint(OutputType.minimal,0);
    }
    public static JsonValue ToJsonValue(String string)
    {
        return jsonReader.parse(string);
    }

    public static void Foreach(JsonValue json, GDX.Runnable<JsonValue> cb)
    {
        for (JsonValue child = json.child; child != null; child = child.next)
            cb.Run(child);
    }
    public static JsonValue MapToJson(Map<String,?> map)
    {
        JsonValue jsonValue = new JsonValue(JsonValue.ValueType.object);
        for(String key : map.keySet())
            jsonValue.addChild(key, ObjectToJson(map.get(key)));
        return jsonValue;
    }
    public static JsonValue ListToJson(List<?> list)
    {
        JsonValue jsonValue = new JsonValue(JsonValue.ValueType.array);
        for(Object ob : list)
            jsonValue.addChild(ObjectToJson(ob));
        return jsonValue;
    }
    private static JsonValue ObjectToJson(Object object)
    {
        if (object instanceof JsonObject) return ((JsonObject) object).ToJson();
        return new JsonValue(object.toString());
    }
    //To Object
    private static <T> T ToObject(JsonValue js, Class<T> type)
    {
        if (ClassReflection.isAssignableFrom(JsonObject.class,type))
        {
            JsonObject object = (JsonObject) GJson.NewInstance(type);
            object.SetData(js);
            return (T)object;
        }
        return ToBaseType(js,type);
    }
    public static <T> Map<String,T> ToMap(JsonValue js, Class<T> type)
    {
        Map<String,T> map = new HashMap<>();
        ToMap(js,map,type);
        return map;
    }
    public static <T> Map<String,T> ToLinkedMap(JsonValue js, Class<T> type)
    {
        Map<String,T> map = new LinkedHashMap<>();
        ToMap(js,map,type);
        return map;
    }
    private static <T> void ToMap(JsonValue js, Map<String,T> map, Class<T> type)
    {
        Foreach(js,child->map.put(child.name,ToObject(child,type)));
    }
    public static <T> List<T> ToList(JsonValue js, Class<T> type)
    {
        List<T> list = new ArrayList<>();
        Foreach(js,child->list.add(ToObject(child,type)));
        return list;
    }
    public static abstract class JsonObject
    {
        public abstract void SetData(JsonValue js);
        public abstract JsonValue ToJson();
    }
}
