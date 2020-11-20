package GameGDX;

import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class IJson2 extends IJson {

    /** @param knownType May be null if the type is unknown.
     * @param elementType May be null if the type is unknown. */
    public void toJson (Object object, Class knownType, Class elementType, Writer writer) {
        setWriter(writer);
        try {
            Object dfObject = getDefaultObject(knownType);
            writeValue(object,dfObject, knownType, elementType);
        } finally {
            StreamUtils.closeQuietly(this.writer);
            this.writer = null;
        }
    }

    public void writeValue (Object value, Object defaultObject, Class knownType, Class elementType) {
        try {
            if (value == null) {
                writer.value(null);
                return;
            }

            if ((knownType != null && knownType.isPrimitive()) || knownType == String.class || knownType == Integer.class
                    || knownType == Boolean.class || knownType == Float.class || knownType == Long.class || knownType == Double.class
                    || knownType == Short.class || knownType == Byte.class || knownType == Character.class) {
                writer.value(value);
                return;
            }

            Class actualType = value.getClass();

            if (actualType.isPrimitive() || actualType == String.class || actualType == Integer.class || actualType == Boolean.class
                    || actualType == Float.class || actualType == Long.class || actualType == Double.class || actualType == Short.class
                    || actualType == Byte.class || actualType == Character.class) {
                writeObjectStart(actualType, null);
                writeValue("value", value);
                writeObjectEnd();
                return;
            }

            if (value instanceof Serializable) {
                writeObjectStart(actualType, knownType);
                ((Serializable)value).write(this);
                writeObjectEnd();
                return;
            }

            Serializer serializer = classToSerializer.get(actualType);
            if (serializer != null) {
                serializer.write(this, value, knownType);
                return;
            }

            // JSON array special cases.
            if (value instanceof Array) {
                if (knownType != null && actualType != knownType && actualType != Array.class)
                    throw new SerializationException("Serialization of an Array other than the known type is not supported.\n"
                            + "Known type: " + knownType + "\nActual type: " + actualType);
                writeArrayStart();
                Array array = (Array)value;
                for (int i = 0, n = array.size; i < n; i++)
                    writeValue(array.get(i), elementType, null);
                writeArrayEnd();
                return;
            }
            if (value instanceof Queue) {
                if (knownType != null && actualType != knownType && actualType != Queue.class)
                    throw new SerializationException("Serialization of a Queue other than the known type is not supported.\n"
                            + "Known type: " + knownType + "\nActual type: " + actualType);
                writeArrayStart();
                Queue queue = (Queue)value;
                for (int i = 0, n = queue.size; i < n; i++)
                    writeValue(queue.get(i), elementType, null);
                writeArrayEnd();
                return;
            }
            if (value instanceof Collection) {
                if (typeName != null && actualType != ArrayList.class && (knownType == null || knownType != actualType)) {
                    writeObjectStart(actualType, knownType);
                    writeArrayStart("items");
                    for (Object item : (Collection)value)
                        writeValue(item, elementType, null);
                    writeArrayEnd();
                    writeObjectEnd();
                } else {
                    writeArrayStart();
                    for (Object item : (Collection)value)
                        writeValue(item, elementType, null);
                    writeArrayEnd();
                }
                return;
            }
            if (actualType.isArray()) {
                if (elementType == null) elementType = actualType.getComponentType();
                int length = ArrayReflection.getLength(value);
                writeArrayStart();
                for (int i = 0; i < length; i++)
                    writeValue(ArrayReflection.get(value, i), elementType, null);
                writeArrayEnd();
                return;
            }

            // JSON object special cases.
            if (value instanceof ObjectMap) {
                if (knownType == null) knownType = ObjectMap.class;
                writeObjectStart(actualType, knownType);
                for (ObjectMap.Entry entry : ((ObjectMap<?, ?>)value).entries()) {
                    writer.name(convertToString(entry.key));
                    writeValue(entry.value, elementType, null);
                }
                writeObjectEnd();
                return;
            }
            if (value instanceof ObjectSet) {
                if (knownType == null) knownType = ObjectSet.class;
                writeObjectStart(actualType, knownType);
                writer.name("values");
                writeArrayStart();
                for (Object entry : (ObjectSet)value)
                    writeValue(entry, elementType, null);
                writeArrayEnd();
                writeObjectEnd();
                return;
            }
            if (value instanceof IntSet) {
                if (knownType == null) knownType = IntSet.class;
                writeObjectStart(actualType, knownType);
                writer.name("values");
                writeArrayStart();
                for (IntSet.IntSetIterator iter = ((IntSet)value).iterator(); iter.hasNext;)
                    writeValue(Integer.valueOf(iter.next()), Integer.class, null);
                writeArrayEnd();
                writeObjectEnd();
                return;
            }
            if (value instanceof ArrayMap) {
                if (knownType == null) knownType = ArrayMap.class;
                writeObjectStart(actualType, knownType);
                ArrayMap map = (ArrayMap)value;
                for (int i = 0, n = map.size; i < n; i++) {
                    writer.name(convertToString(map.keys[i]));
                    writeValue(map.values[i], elementType, null);
                }
                writeObjectEnd();
                return;
            }
            if (value instanceof Map) {
                if (knownType == null) knownType = HashMap.class;
                writeObjectStart(actualType, knownType);
                Map defaultMap = (Map<?, ?>)defaultObject;
                for (Map.Entry entry : ((Map<?, ?>)value).entrySet()) {
                    writer.name(convertToString(entry.getKey()));
                    writeValue(entry.getValue(),defaultMap.get(entry.getKey()), elementType, null);
                }
                writeObjectEnd();
                return;
            }

            // Enum special case.
            if (ClassReflection.isAssignableFrom(Enum.class, actualType)) {
                if (typeName != null && (knownType == null || knownType != actualType)) {
                    // Ensures that enums with specific implementations (abstract logic) serialize correctly.
                    if (actualType.getEnumConstants() == null) actualType = actualType.getSuperclass();

                    writeObjectStart(actualType, null);
                    writer.name("value");
                    writer.value(convertToString((Enum)value));
                    writeObjectEnd();
                } else {
                    writer.value(convertToString((Enum)value));
                }
                return;
            }

            writeObjectStart(actualType, knownType);
            if (defaultObject==null) defaultObject = getDefaultObject(value.getClass());
            writeFields(value,getDefaultValues(defaultObject));
            writeObjectEnd();
        } catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }

    /** Writes all fields of the specified object to the current JSON object. */
    public void writeFields (Object object,Object[] defaultValues) {
        Class type = object.getClass();

        OrderedMap<String, FieldMetadata> fields = getFields(type);
        int i = 0;
        for (FieldMetadata metadata : new OrderedMap.OrderedMapValues<>(fields)) {
            Field field = metadata.field;
            if (ignoreDeprecated && readDeprecated && field.isAnnotationPresent(Deprecated.class)) continue;
            try {
                Object value = field.get(object);
                Object defaultValue = null;
                if (defaultValues != null) {
                    defaultValue = defaultValues[i++];
                    if (value == null && defaultValue == null) continue;
                    if (value != null && defaultValue != null) {
                        if (value.equals(defaultValue)) continue;
                        if (value.getClass().isArray() && defaultValue.getClass().isArray()) {
                            equals1[0] = value;
                            equals2[0] = defaultValue;
                            if (Arrays.deepEquals(equals1, equals2)) continue;
                        }
                    }
                }

                if (debug) System.out.println("Writing field: " + field.getName() + " (" + type.getName() + ")");
                writer.name(field.getName());
                writeValue(value,defaultValue, field.getType(), metadata.elementType);
            } catch (ReflectionException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            } catch (SerializationException ex) {
                ex.addTrace(field + " (" + type.getName() + ")");
                throw ex;
            } catch (Exception runtimeEx) {
                SerializationException ex = new SerializationException(runtimeEx);
                ex.addTrace(field + " (" + type.getName() + ")");
                throw ex;
            }
        }
    }
    protected final ObjectMap<Class, Object> defaultObject = new ObjectMap();
    protected Object getDefaultObject(Class type)
    {
        if (!usePrototypes) return null;
        if (defaultObject.containsKey(type)) return defaultObject.get(type);
        Object object;
        try {
            object = newInstance(type);
            defaultObject.put(type, object);
        } catch (Exception ex) {
            defaultObject.put(type, null);
            return null;
        }
        return object;
    }
    protected Object[] getDefaultValues (Object object) {
        if (!usePrototypes) return null;
        Class type = object.getClass();
        if (classToDefaultValues.containsKey(type)) return classToDefaultValues.get(type);
        ObjectMap<String, FieldMetadata> fields = getFields(type);
        Object[] values = new Object[fields.size];
        classToDefaultValues.put(type, values);

        int i = 0;
        for (FieldMetadata metadata : fields.values()) {
            Field field = metadata.field;
            if (readDeprecated && ignoreDeprecated && field.isAnnotationPresent(Deprecated.class)) continue;
            try {
                values[i++] = field.get(object);
            } catch (ReflectionException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            } catch (SerializationException ex) {
                ex.addTrace(field + " (" + type.getName() + ")");
                throw ex;
            } catch (RuntimeException runtimeEx) {
                SerializationException ex = new SerializationException(runtimeEx);
                ex.addTrace(field + " (" + type.getName() + ")");
                throw ex;
            }
        }
        return values;
    }

    /** @param type May be null if the type is unknown.
     * @return May be null. */
    public <T> T fromJson (Class<T> type, String json) {
        return (T)readValue(type, newInstance(type), null, new JsonReader().parse(json));
    }
    private Class GetClass(String className)
    {
        if (className != null) {
            Class type = getClass(className);
            if (type == null) {
                try {
                    return ClassReflection.forName(className);
                } catch (ReflectionException ex) {
                    throw new SerializationException(ex);
                }
            }
        }
        return null;
    }
    public <T> T readValue (Class<T> type, Object object, Class elementType, JsonValue jsonData) {
        if (jsonData == null) return null;

        if (jsonData.isObject()) {
            String className = typeName == null ? null : jsonData.getString(typeName, null);
            if (className != null) {
                type = getClass(className);
                if (type == null) {
                    try {
                        type = (Class<T>) ClassReflection.forName(className);
                    } catch (ReflectionException ex) {
                        throw new SerializationException(ex);
                    }
                }
            }

            if (type == null) {
                if (defaultSerializer != null) return (T)defaultSerializer.read(this, jsonData, type);
                return (T)jsonData;
            }

            if (typeName != null && ClassReflection.isAssignableFrom(Collection.class, type)) {
                // JSON object wrapper to specify type.
                jsonData = jsonData.get("items");
                if (jsonData == null) throw new SerializationException(
                        "Unable to convert object to collection: " + jsonData + " (" + type.getName() + ")");
            } else {
                Serializer serializer = classToSerializer.get(type);
                if (serializer != null) return (T)serializer.read(this, jsonData, type);

                if (type == String.class || type == Integer.class || type == Boolean.class || type == Float.class
                        || type == Long.class || type == Double.class || type == Short.class || type == Byte.class
                        || type == Character.class || ClassReflection.isAssignableFrom(Enum.class, type)) {
                    return readValue("value", type, jsonData);
                }

                if (object instanceof Serializable) {
                    ((Serializable)object).read(this, jsonData);
                    return (T)object;
                }

                // JSON object special cases.
                if (object instanceof ObjectMap) {
                    ObjectMap result = (ObjectMap)object;
                    for (JsonValue child = jsonData.child; child != null; child = child.next)
                        result.put(child.name, readValue(elementType, null, child));
                    return (T)result;
                }
                if (object instanceof ObjectSet) {
                    ObjectSet result = (ObjectSet)object;
                    for (JsonValue child = jsonData.getChild("values"); child != null; child = child.next)
                        result.add(readValue(elementType, null, child));
                    return (T)result;
                }
                if (object instanceof IntSet) {
                    IntSet result = (IntSet)object;
                    for (JsonValue child = jsonData.getChild("values"); child != null; child = child.next)
                        result.add(child.asInt());
                    return (T)result;
                }
                if (object instanceof ArrayMap) {
                    ArrayMap result = (ArrayMap)object;
                    for (JsonValue child = jsonData.child; child != null; child = child.next)
                        result.put(child.name, readValue(elementType, null, child));
                    return (T)result;
                }
                if (object instanceof Map) {
                    Map result = (Map)object;
                    for (JsonValue child = jsonData.child; child != null; child = child.next) {
                        if (child.name.equals(typeName)) continue;
                        String childClass = typeName == null ? null : child.getString(typeName,null);
                        elementType = childClass==null?elementType:GetClass(childClass);
                        Object defaultObject = result.containsKey(child.name)?result.get(child.name):newInstance(elementType);

                        result.put(child.name, readValue(elementType,defaultObject, null, child));
                    }
                    return (T)result;
                }

                readFields(object, jsonData);
                return (T)object;
            }
        }

        if (type != null) {
            Serializer serializer = classToSerializer.get(type);
            if (serializer != null) return (T)serializer.read(this, jsonData, type);

            if (ClassReflection.isAssignableFrom(Serializable.class, type)) {
                // A Serializable may be read as an array, string, etc, even though it will be written as an object.
                ((Serializable)object).read(this, jsonData);
                return (T)object;
            }
        }

        if (jsonData.isArray()) {
            // JSON array special cases.
            if (type == null || type == Object.class) type = (Class<T>) Array.class;
            if (ClassReflection.isAssignableFrom(Array.class, type)) {
                Array result = type == Array.class ? new Array() : (Array)newInstance(type);
                for (JsonValue child = jsonData.child; child != null; child = child.next)
                    result.add(readValue(elementType, null, child));
                return (T)result;
            }
            if (ClassReflection.isAssignableFrom(Queue.class, type)) {
                Queue result = type == Queue.class ? new Queue() : (Queue)newInstance(type);
                for (JsonValue child = jsonData.child; child != null; child = child.next)
                    result.addLast(readValue(elementType, null, child));
                return (T)result;
            }
            if (ClassReflection.isAssignableFrom(Collection.class, type)) {
                Collection result = type.isInterface() ? new ArrayList() : (Collection)newInstance(type);
                for (JsonValue child = jsonData.child; child != null; child = child.next)
                    result.add(readValue(elementType, null, child));
                return (T)result;
            }
            if (type.isArray()) {
                Class componentType = type.getComponentType();
                if (elementType == null) elementType = componentType;
                Object result = ArrayReflection.newInstance(componentType, jsonData.size);
                int i = 0;
                for (JsonValue child = jsonData.child; child != null; child = child.next)
                    ArrayReflection.set(result, i++, readValue(elementType, null, child));
                return (T)result;
            }
            throw new SerializationException("Unable to convert value to required type: " + jsonData + " (" + type.getName() + ")");
        }

        if (jsonData.isNumber()) {
            try {
                if (type == null || type == float.class || type == Float.class) return (T)(Float)jsonData.asFloat();
                if (type == int.class || type == Integer.class) return (T)(Integer)jsonData.asInt();
                if (type == long.class || type == Long.class) return (T)(Long)jsonData.asLong();
                if (type == double.class || type == Double.class) return (T)(Double)jsonData.asDouble();
                if (type == String.class) return (T)jsonData.asString();
                if (type == short.class || type == Short.class) return (T)(Short)jsonData.asShort();
                if (type == byte.class || type == Byte.class) return (T)(Byte)jsonData.asByte();
            } catch (NumberFormatException ignored) {
            }
            jsonData = new JsonValue(jsonData.asString());
        }

        if (jsonData.isBoolean()) {
            try {
                if (type == null || type == boolean.class || type == Boolean.class) return (T)(Boolean)jsonData.asBoolean();
            } catch (NumberFormatException ignored) {
            }
            jsonData = new JsonValue(jsonData.asString());
        }

        if (jsonData.isString()) {
            String string = jsonData.asString();
            if (type == null || type == String.class) return (T)string;
            try {
                if (type == int.class || type == Integer.class) return (T)Integer.valueOf(string);
                if (type == float.class || type == Float.class) return (T)Float.valueOf(string);
                if (type == long.class || type == Long.class) return (T)Long.valueOf(string);
                if (type == double.class || type == Double.class) return (T)Double.valueOf(string);
                if (type == short.class || type == Short.class) return (T)Short.valueOf(string);
                if (type == byte.class || type == Byte.class) return (T)Byte.valueOf(string);
            } catch (NumberFormatException ignored) {
            }
            if (type == boolean.class || type == Boolean.class) return (T)Boolean.valueOf(string);
            if (type == char.class || type == Character.class) return (T)(Character)string.charAt(0);
            if (ClassReflection.isAssignableFrom(Enum.class, type)) {
                Enum[] constants = (Enum[])type.getEnumConstants();
                for (int i = 0, n = constants.length; i < n; i++) {
                    Enum e = constants[i];
                    if (string.equals(convertToString(e))) return (T)e;
                }
            }
            if (type == CharSequence.class) return (T)string;
            throw new SerializationException("Unable to convert value to required type: " + jsonData + " (" + type.getName() + ")");
        }

        return null;
    }

    public void readFields (Object object, JsonValue jsonMap) {
        Class type = object.getClass();
        ObjectMap<String, FieldMetadata> fields = getFields(type);
        for (JsonValue child = jsonMap.child; child != null; child = child.next) {
            FieldMetadata metadata = fields.get(child.name().replace(" ", "_"));
            if (metadata == null) {
                if (child.name.equals(typeName)) continue;
                if (ignoreUnknownFields) {
                    if (debug) System.out.println("Ignoring unknown field: " + child.name + " (" + type.getName() + ")");
                    continue;
                } else {
                    SerializationException ex = new SerializationException(
                            "Field not found: " + child.name + " (" + type.getName() + ")");
                    ex.addTrace(child.trace());
                    throw ex;
                }
            }
            Field field = metadata.field;
            try {
                field.set(object, readValue(field.getType(),field.get(object), metadata.elementType, child));
            } catch (ReflectionException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            } catch (SerializationException ex) {
                ex.addTrace(field.getName() + " (" + type.getName() + ")");
                throw ex;
            } catch (RuntimeException runtimeEx) {
                SerializationException ex = new SerializationException(runtimeEx);
                ex.addTrace(child.trace());
                ex.addTrace(field.getName() + " (" + type.getName() + ")");
                throw ex;
            }
        }
    }

    //clone
    public <T> T Clone(Object object,Class<T> type)
    {
        if (type.isPrimitive() || type == String.class || type == Integer.class || type == Boolean.class || type == Float.class
                || type == Long.class || type == Double.class || type == Short.class || type == Byte.class
                || type == Character.class || ClassReflection.isAssignableFrom(Enum.class, type)) {
            return (T)object;
        }

        T clone = (T)newInstance(type);
        copyFields(object,clone);
        return clone;
    }
    public void copyFields (Object from, Object to) {
        if (from instanceof Collection)
        {
            Collection toCollection = (Collection)to;
            toCollection.clear();
            for (Object item : (Collection)from)
                toCollection.add(Clone(item,item.getClass()));
            return;
        }
        if (from instanceof Map)
        {
            Map toMap = (Map<?, ?>)to;
            toMap.clear();
            for (Map.Entry entry : ((Map<?, ?>)from).entrySet()) {
                Object value = entry.getValue();
                toMap.put(entry.getKey(),Clone(value,value.getClass()));
            }
            return;
        }
        ObjectMap<String, FieldMetadata> fields = new ObjectMap<>(getFields(from.getClass()));

        for (FieldMetadata fieldMetadata : fields.values())
        {
            Field field = fieldMetadata.field;
            setFields(from,to,field);
        }
    }
    private void setFields(Object from, Object to, Field field)
    {
        try {
            Class type = field.getType();
            if (type.isPrimitive() || type == String.class || type == Integer.class || type == Boolean.class || type == Float.class
                    || type == Long.class || type == Double.class || type == Short.class || type == Byte.class
                    || type == Character.class || ClassReflection.isAssignableFrom(Enum.class, type)) {
                field.set(to, field.get(from));
                return;
            }
            copyFields(field.get(from),field.get(to));
        }catch (Exception e){}
    }
}
