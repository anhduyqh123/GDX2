package GameGDX;

import GameGDX.AssetLoading.AssetNode;
import GameGDX.AssetLoading.AssetPackage;
import GameGDX.AssetLoading.GameData;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loader extends Actor {
    public static Loader instance;

    private static List<String> packLoaded; // loaded
    private static Map<String, AssetNode> mapAssets; //loaded node
    private static GameData gameData;
    public static AssetManager assets;
    public static IDownload iDownload;

    private Runnable doneLoading;
    private GDX.Runnable<Float> cbAssetsUpdate;

    public Loader(GameData gameData)
    {
        instance = this;
        packLoaded = new ArrayList<>();
        mapAssets = new HashMap<>();
        assets = new AssetManager();

        this.gameData = gameData;
        gameData.Install();
        Scene.background.addActor(this);
    }

    @Override
    public void act(float delta) {
        UpdateAssetLoading();
        super.act(delta);
    }

    private void UpdateAssetLoading()
    {
        if (doneLoading==null) return;
        if (assets.update()) {
            Runnable done = doneLoading;
            doneLoading = null;
            done.run();
        }
        if (cbAssetsUpdate!=null) cbAssetsUpdate.Run(assets.getProgress());
    }

    //LoadAsset
    private static void LoadAssets(List<AssetNode> list)
    {
        if (list==null) return;
        for (AssetNode as : list)
            LoadNode(as);
    }
    public static void LoadNode(AssetNode as)
    {
        if (assets.isLoaded(as.url)) return;
        switch (as.kind)
        {
            case TextureAtlas:
                assets.load(as.url, TextureAtlas.class);
                break;
            case Texture:
                TextureLoader.TextureParameter pepTexture = new TextureLoader.TextureParameter();
                pepTexture.minFilter = Texture.TextureFilter.Linear;
                pepTexture.magFilter = Texture.TextureFilter.Linear;
                assets.load(as.url, Texture.class);
                break;
            case Sound:
                GMusic.flat.Load(as.url, Sound.class);
                break;
            case Music:
                GMusic.flat.Load(as.url, Music.class);
                break;
            case BitmapFont:
                BitmapFontLoader.BitmapFontParameter pepBitmap = new BitmapFontLoader.BitmapFontParameter();
                pepBitmap.minFilter = Texture.TextureFilter.Linear;
                pepBitmap.magFilter = Texture.TextureFilter.Linear;
                assets.load(as.url, BitmapFont.class,pepBitmap);
                break;
            case Particle:
                ParticleEffectLoader.ParticleEffectParameter pepParticle=  new ParticleEffectLoader.ParticleEffectParameter();
                AssetPackage pack = GetAssetPackage(as.pack);
                if (pack.Contain("particle_atlas"))
                    pepParticle.atlasFile = pack.Get("particle_atlas").url;
                assets.load(as.url, ParticleEffect.class,pepParticle);
                break;

            default:
        }
    }
    private static void LoadPackage(String pack)
    {
        if (!gameData.Contains(pack)) return;
        if (packLoaded.contains(pack)) return;
        packLoaded.add(pack);
        GMusic.flat.LoadPackage(pack);
        for(AssetNode n : GetAssetPackage(pack).activeNode)
            mapAssets.put(n.name,n);
        LoadAssets(GetAssetPackage(pack).loadableNode);
    }
    public static void LoadPackages(Runnable done, String... packs){
        for(String pack : packs) LoadPackage(pack);
        assets.finishLoading();
        if(done!=null) done.run();
    }
    public static void LoadPackagesSync(GDX.Runnable<Float> cbProgress, Runnable onLoaded, String... packs)
    {
        for(String pack : packs) LoadPackage(pack);
        instance.doneLoading = onLoaded;
        instance.cbAssetsUpdate = cbProgress;
    }
    public static void LoadPackagesSyncH5(GDX.Runnable<Float> cbProgress, Runnable onLoaded, String... packs)
    {
        if (iDownload!=null)
            DownloadPackagesH5(cbProgress,()->LoadPackagesSync(cbProgress, onLoaded, packs),packs);
        else LoadPackagesSync(cbProgress, onLoaded, packs);
    }
    private static void DownloadPackagesH5(GDX.Runnable<Float> cbProgress, Runnable onCompleted, String... packs)
    {
        List<String> assets = new ArrayList<>();
        for(String pack : packs)
        {
            if (!gameData.Contains(pack)) continue;
            AssetPackage assetPackage = GetAssetPackage(pack);
            for(AssetNode n : assetPackage.list) assets.add(n.ToAssetFileUrl());
        }
        iDownload.Run(assets.toArray(new String[0]),cbProgress,onCompleted);
    }

    public static void UnloadPackage(String pack)
    {
        if (!gameData.Contains(pack)) return;
        RemovePackage(pack);
        for (AssetNode n : GetAssetPackage(pack).loadableNode)
            assets.unload(n.url);
    }
    public static void RemovePackage(String pack)
    {
        packLoaded.remove(pack);
    }
    //interface
    public interface IDownload{
        void Run(String[] assetFiles, GDX.Runnable<Float> onProgress, Runnable onCompleted);
    }

    //get value
    public static TextureRegion GetTexture(String name)
    {
        AssetNode node = GetNode(name);
        AssetPackage pack = GetAssetPackage(node.pack);
        if (pack.Contain(node.atlas))
        {
            AssetNode al = pack.Get(node.atlas);
            return assets.get(al.url, TextureAtlas.class).findRegion(name);
        }
        return new TextureRegion(Get(name, Texture.class));
    }

    public static BitmapFont GetFont(String name)
    {
        return Get(name, BitmapFont.class);
    }
    public static Sound GetSound(String name)
    {
        return Get(name, Sound.class);
    }
    public static Music GetMusic(String name)
    {
        return Get(name, Music.class);
    }
    public static ParticleEffect GetParticleEffect(String name)
    {
        return Get(name, ParticleEffect.class);
    }
    public static <T> T Get(String name,Class<T> type){
        AssetNode as = GetNode(name);
        return assets.get(as.url,type);
    }
    public static AssetNode GetNode(String name)
    {
        return mapAssets.get(name);
    }
    public static AssetPackage GetAssetPackage(String name)
    {
        return gameData.Get(name);
    }
}
