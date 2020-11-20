package GameGDX.AssetLoading;

import com.badlogic.gdx.files.FileHandle;

public class AssetNode {
    public enum Kind {
        None,
        TextureAtlas,
        Texture,
        Sound,
        Music,
        BitmapFont,
        Particle,
        Data
    }

    public Kind kind;
    public String name;
    public String url;
    public String extension;
    public String size;
    public String pack="";
    public String atlas="";
    public boolean main = true;

    public AssetNode(){}
    public AssetNode(String pack, Kind kind, String atlas, String name, boolean main)
    {
        this.pack = pack;
        this.kind = kind;
        this.name = name;
        this.atlas = atlas;
        this.extension = "none";
        this.url = "none";
        this.size = "0";
        this.main = main;
    }
    public AssetNode(String pack, Kind kind, FileHandle fileHandle, boolean main)
    {
        this.pack = pack;
        this.kind = kind;
        this.name = fileHandle.nameWithoutExtension();
        this.extension = fileHandle.extension();
        this.url = fileHandle.path();
        this.size = fileHandle.length()+"";
        this.main = main;
    }

    public String ToAssetFileUrl()
    {
        String type = "b";
        String mimeType = "application/unknown";
        if (extension.equals("jpg")||extension.equals("png"))
        {
            type = "i";
            mimeType = "image/"+extension;
        }
        if (extension.equals("fnt")||extension.equals("txt")||extension.equals("atlas")) type = "t";
        if (extension.equals("mp3")||extension.equals("wav")||extension.equals("ogg")) type = "a";

        return type+":"+url+":"+size+":"+mimeType;
    }
}
