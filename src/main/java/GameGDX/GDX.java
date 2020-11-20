package GameGDX;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;


/**
 * Version 1.2 by BaDuy
 */

public class GDX {
    private static Preferences prefs;
    public GDX()
    {
        prefs = Gdx.app.getPreferences("Save");
    }
    //App
    public static void ClearPreferences()
    {
        prefs.clear();
    }
    public static boolean IsHTML()
    {
        return Gdx.app.getType()== Application.ApplicationType.WebGL;
    }
    public static boolean IsIOS()
    {
        return Gdx.app.getType()== Application.ApplicationType.iOS;
    }
    public static void Log(Object value)
    {
        Gdx.app.log("log",String.valueOf(value));
    }
    public static void Error(Object value)
    {
        Gdx.app.error("error",String.valueOf(value));
    }
    public static float DeltaTime()
    {
        return Gdx.graphics.getDeltaTime();
    }
    public static float GetFPS(){return Gdx.graphics.getFramesPerSecond();};
    public static void Quit()
    {
        Gdx.app.exit();
    }
    public static float GetWidth()
    {
        return Gdx.graphics.getWidth();
    }
    public static float GetHeight()
    {
        return Gdx.graphics.getHeight();
    }
    public static void Vibrate(int milli)
    {
        Gdx.input.vibrate(milli);
    }

    //Prefs
    public static long GetPrefLong(String key, long defaul)
    {
        return prefs.getLong(key,defaul);
    }
    public static void SetPrefLong(String key, long value)
    {
        prefs.putLong(key,value);
        prefs.flush();
    }
    public static int GetPrefInteger(String key, int defaul)
    {
        return prefs.getInteger(key,defaul);
    }
    public static void SetPrefInteger(String key, int value)
    {
        prefs.putInteger(key,value);
        prefs.flush();
    }
    public static String GetPrefString(String key, String defaul)
    {
        return prefs.getString(key,defaul);
    }
    public static void SetPrefString(String key, String value)
    {
        prefs.putString(key,value);
        prefs.flush();
    }
    public static boolean GetPrefBoolean(String key, boolean defaul)
    {
        return prefs.getBoolean(key,defaul);
    }
    public static void SetPrefBoolean(String key, boolean value)
    {
        prefs.putBoolean(key,value);
        prefs.flush();
    }
    //PostRunnable
    public static void PostRunnable(java.lang.Runnable runnable)
    {
        Gdx.app.postRunnable(runnable);
    }
    public static void DelayRunnable(java.lang.Runnable runnable, float delay)
    {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                runnable.run();
            }
        },delay);
    }
    //FileHandle
    public static void WriteToFile(String path,String data)
    {
        Gdx.files.local(path).writeString(data,false);
    }
    public static String ReadFromFile(String path)
    {
        return GetFile(path).readString();
    }
    public static FileHandle GetFile(String path)
    {
        return Gdx.files.internal(path);
    }
    //encode
    private static String endCode = "zen";
    private static byte[] endBytes = endCode.getBytes();
    public static void Encode(FileHandle file)
    {
        byte[] bytes = file.readBytes();
        byte[] extendBytes = new byte[endBytes.length];
        for(int i=0;i<extendBytes.length;i++)
            extendBytes[i] = bytes[i];
        String extendSt = new String(extendBytes);
        if (extendSt.equals(endCode)) return;
        file.writeBytes(endBytes,false);
        file.writeBytes(bytes,true);
    }
    //decode
    public static byte[] Decode_Bytes(FileHandle file)
    {
        byte[] bytes = file.readBytes();
        byte[] extendBytes = new byte[endBytes.length];
        for(int i=0;i<extendBytes.length;i++)
            extendBytes[i] = bytes[i];
        String extendSt = new String(extendBytes);
        if (!extendSt.equals(endCode)) return bytes;
        byte[] newBytes = new byte[bytes.length-endBytes.length];
        for(int i=endBytes.length;i<bytes.length;i++)
            newBytes[i-endBytes.length] = bytes[i];
        return newBytes;
    }

    //frame buffer
    public static Texture GetFrameBuffer(Actor actor)
    {
        Batch batch = actor.getStage().getBatch();
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Scene.width, Scene.height, false);

        fbo.begin();
        batch.begin();
        actor.draw(batch,1);
        batch.end();
        fbo.end();

        return fbo.getColorBufferTexture();
    }
    public static TextureRegion GetTextureRegion(Actor actor)
    {
        TextureRegion tr = new TextureRegion(GetFrameBuffer(actor));
        tr.setRegion((int)actor.getX(),(int)actor.getY(),(int)actor.getWidth(),(int)actor.getHeight());
        tr.flip(false,true);
        return tr;
    }

    //interface
    public interface Runnable<T>{
        void Run(T value);
    }
    public interface Func<T1,T2>
    {
        T1 Run(T2 name);
    }

}
