package GameGDX;

import com.badlogic.gdx.audio.Music;

import java.util.HashMap;
import java.util.Map;

public class GMusic {
    public static GMusic instance;
    private static boolean muteM,muteS,vibrate;
    private static String musicName;
    public static PlatMusic flat = new PlatMusic() {
        @Override
        public void LoadPackage(String pack) {
        }

        @Override
        public <T> void Load(String url, Class<T> type) {
            Loader.assets.load(url,type);
        }

        @Override
        public void PlaySound(String name) {
            Loader.GetSound(name).play();
        }

        @Override
        public void PlayMusic(String name) {
            Music music = Loader.GetMusic(name);
            music.setLooping(true);
            music.play();
        }

        @Override
        public void StopMusic(String name) {
            Loader.GetMusic(name).stop();
        }
    };

    private static Map<String, GDX.Runnable<Boolean>> cbSound = new HashMap<>();
    private static Map<String, GDX.Runnable<Boolean>> cbMusic = new HashMap<>();
    private static Map<String, GDX.Runnable<Boolean>> cbVibrate = new HashMap<>();
    public static GDX.Runnable<Integer> doVibrate;

    public GMusic(String mName) {
        instance = this;

        musicName = mName;
        //muteS = GDX.GetPrefBoolean("muteS",false);
        muteM = GDX.GetPrefBoolean("muteM",false);
        vibrate = GDX.GetPrefBoolean("vibrate",true);
        GDX.DelayRunnable(()->PlayMusic(muteM),GDX.IsHTML()?2:0);
    }
    public static void AddSoundCallback(String st, GDX.Runnable<Boolean> cb)
    {
        cbSound.put(st,cb);
        RefreshSound();
    }
    public static void AddMusicCallback(String st, GDX.Runnable<Boolean> cb)
    {
        cbMusic.put(st,cb);
        RefreshMusic();
    }
    public static void AddVibrateCallback(String st, GDX.Runnable<Boolean> cb)
    {
        cbVibrate.put(st,cb);
        RefreshVibrate();
    }
    public static void DoVibrate()
    {
        if (!vibrate) return;
        if (doVibrate!=null) doVibrate.Run(30);
    }
    public static void SwitchVibrate()
    {
        vibrate = !vibrate;
        GDX.SetPrefBoolean("vibrate",vibrate);
        if (vibrate && doVibrate!=null) doVibrate.Run(100);
        RefreshVibrate();
    }

    public static void PlaySound(String name)
    {
        if (muteS) return;
        flat.PlaySound(name);
    }
    public static void SwitchSound()
    {
        muteS=!muteS;
        //GDX.SetPrefBoolean("muteS",muteS);
        RefreshSound();
    }
    public static void SwitchMusic()
    {
        muteM = !muteM;
        GDX.SetPrefBoolean("muteM",muteM);
        PlayMusic(muteM);
        RefreshMusic();
    }
    private static void PlayMusic(boolean mute)
    {
        if (musicName==null) return;
        if (mute) flat.StopMusic(musicName);
        else flat.PlayMusic(musicName);
    }

    private static void RefreshSound()
    {
        for(GDX.Runnable cb : cbSound.values()) cb.Run(muteS);
    }
    private static void RefreshMusic()
    {
        for(GDX.Runnable cb : cbMusic.values()) cb.Run(muteM);
    }
    private static void RefreshVibrate()
    {
        for(GDX.Runnable cb : cbVibrate.values()) cb.Run(vibrate);
    }

    public static void OnPause()
    {
        if (instance==null || muteM) return;
        PlayMusic(true);
    }
    public static void OnResume()
    {
        if (instance==null || muteM) return;
        PlayMusic(false);
    }

    //interface
    public interface PlatMusic
    {
        void LoadPackage(String pack);
        <T> void Load(String url, Class<T> type);
        void PlaySound(String name);
        void PlayMusic(String name);
        void StopMusic(String name);
    }
}
