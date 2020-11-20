package GameGDX.AssetLoading;

import GameGDX.GDX;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {
    private Map<String, AssetPackage> packs = new HashMap<>(); //all pack
    private boolean containAllFile;

    public GameData(){}
    public GameData(boolean h5){this.containAllFile = h5;}

    public boolean Contains(String pack)
    {
        return packs.containsKey(pack);
    }
    public AssetPackage Get(String pack)
    {
        return packs.get(pack);
    }
    public void Install()
    {
        for(AssetPackage assetPackage : packs.values())
            assetPackage.Install();
    }
    public void LoadPackage(String packName,String path)
    {
        NewPackage(packName,path);
        ReadDirectoryToBitmapFontAssets(packName,path+"Fonts/");
        ReadDirectoryToAtlasAssets(packName,path+"Atlas/"); //load atlas before texture
        ReadDirectoryTextureToAsset(packName,path+"Textures/");
        ReadDirectoryParticleAssets(packName,path+"Particles/");

        ReadFileToAsset(packName,true, AssetNode.Kind.Data, GDX.GetFile(path+"Data/"),"");
        ReadFileToAsset(packName,true, AssetNode.Kind.Sound, GDX.GetFile(path+"Sounds/"),"");
        ReadFileToAsset(packName,true, AssetNode.Kind.Music, GDX.GetFile(path+"Musics/"),"");
    }
    public void ReadDirectoryToAtlasAssets(String pack,String path)
    {
        FileHandle dir = GDX.GetFile(path);
        List<AssetNode> list = ReadFileToAsset(pack,true, AssetNode.Kind.TextureAtlas,dir,"atlas");
        if(containAllFile)
            ReadFileToAsset(pack,false, AssetNode.Kind.None,dir,"png");

        for(AssetNode n : list)
        {
            FileHandle file = GDX.GetFile(n.url);
            TextureAtlas.TextureAtlasData data = new TextureAtlas.TextureAtlasData(file,file.parent(),false);
            List<AssetNode> elements = new ArrayList<>();
            for(TextureAtlas.TextureAtlasData.Region r : data.getRegions())
                elements.add(new AssetNode(pack, AssetNode.Kind.Texture,n.name,r.name,true));
            packs.get(pack).Add(elements);
        }
    }

    public void ReadDirectoryToBitmapFontAssets(String pack,String path)
    {
        FileHandle dir = GDX.GetFile(path);
        ReadFileToAsset(pack,true, AssetNode.Kind.BitmapFont,dir,"fnt");
        if(containAllFile)
            ReadFileToAsset(pack,false, AssetNode.Kind.None,dir,"png");
    }
    public void ReadDirectoryParticleAssets(String pack,String path)
    {
        FileHandle dir = GDX.GetFile(path);
        ReadFileToAsset(pack,true, AssetNode.Kind.Particle,dir,"p");
        ReadFileToAsset(pack,true, AssetNode.Kind.None,dir,"atlas"); //particle_atlas.atlas
        if (containAllFile)
            ReadFileToAsset(pack,false, AssetNode.Kind.None,dir,"png");
    }
    public void ReadDirectoryTextureToAsset(String pack,String path)
    {
        AssetPackage assetPackage = packs.get(pack);
        FileHandle dir = GDX.GetFile(path);

        for(FileHandle f : dir.list())
            if (f.isDirectory()){
                if (assetPackage.Contain(f.name())) continue;
                ReadFileToAsset(pack,true, AssetNode.Kind.Texture,f,"");
            }
            else AddNode(assetPackage.list,pack,true, AssetNode.Kind.Texture,f,"");
    }

    public List<AssetNode> ReadFileToAsset(String pack, boolean mainFile, AssetNode.Kind kind, FileHandle dir, String extension){
        if (!packs.containsKey(pack)) NewPackage(pack,dir.path()+"/");
        List<AssetNode> list = ReadFileToList(pack,mainFile, kind, dir, extension);
        packs.get(pack).Add(list);
        return list;
    }
    public void NewPackage(String pack,String url)
    {
        if (packs.containsKey(pack)) return;
        AssetPackage assetPackage = new AssetPackage();
        assetPackage.url = url;
        packs.put(pack,assetPackage);
    }
    private static List<AssetNode> ReadFileToList(String pack, boolean main, AssetNode.Kind kind, FileHandle dir, String extension)
    {
        List<AssetNode> list = new ArrayList<>();
        if (dir==null) return list;
        for(FileHandle child : dir.list())
            if (child.isDirectory()) list.addAll(ReadFileToList(pack,main,kind,child,extension));
            else AddNode(list,pack,main,kind,child,extension);
        return list;
    }
    private static void AddNode(List<AssetNode> list, String pack, boolean main, AssetNode.Kind kind, FileHandle dir, String extension)
    {
        if (dir.extension().equals("DS_Store")) return;
        if(!extension.equals("") && !dir.extension().equals(extension)) return;
        list.add(new AssetNode(pack,kind,dir,main));
    }
}
