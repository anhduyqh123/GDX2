package GameGDX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class UI {
    //InputListener
    public static void AddClick(Actor actor, Runnable rEvent)
    {
        actor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GMusic.PlaySound("pop");
                rEvent.run();
            }
        });
    }
    public static void AddClick_NoPop(Actor actor, Runnable rEvent)
    {
        actor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rEvent.run();
            }
        });
    }

    public static void EffectPressedButton(Actor actor)
    {
        actor.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer!=0) return;
                actor.setColor(Color.LIGHT_GRAY);
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer!=0) return;
                actor.setColor(Color.WHITE);
                super.exit(event, x, y, pointer, toActor);
            }

        });
    }

    //Texture
    public static Texture NewTexture(float width, float height)
    {
        return NewTexture(Color.WHITE,width,height);
    }
    public static Texture NewTexture(Color color, float width, float height)
    {
        Pixmap pixmap = new Pixmap((int)width, (int)height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, (int)width, (int)height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    //Actor
    public static void SetBounds(Actor actor, float x, float y, int align , float width, float height)
    {
        actor.setSize(width, height);
        actor.setPosition(x, y,align);
    }
    public static void SetBounds(Actor actor, float x, float y, int align , float width, float height, Group parent)
    {
        SetBounds(actor, x, y, align, width, height);
        if (parent!=null) parent.addActor(actor);
    }
    //image
    public static Image NewImage(NinePatch ninePatch, float x, float y, int align , float width, float height, Group parent)
    {
        Image img = new Image(ninePatch);
        SetBounds(img,x,y,align,width,height,parent);
        return img;
    }
    public static Image NewImage(TextureRegion texture)
    {
        Image img = new Image(texture);
        img.setSize(texture.getRegionWidth(),texture.getRegionHeight());
        return img;
    }
    public static Image NewImage(TextureRegion texture, float x, float y, int align , float width, float height, Group parent)
    {
        Image img = new Image(texture);
        SetBounds(img,x,y,align,width,height,parent);
        return img;
    }

    public static Image NewImage(TextureRegion texture, float x, float y, int align, Group parent)
    {
        return NewImage(texture,x,y, align ,texture.getRegionWidth(),texture.getRegionHeight(),parent);
    }
    public static Image NewImage(Color color, float x, float y, int align , float width, float height, Group parent)
    {
        Image img = new Image(NewTexture(width,height));
        img.setColor(color);
        SetBounds(img,x,y,align,width,height,parent);
        return img;
    }
    public static Image NewImage(Color color, Group parent)
    {
        return NewImage(color,0,0, Align.bottomLeft,parent.getWidth(),parent.getHeight(),parent);
    }
    public static Image NewImage(TextureRegion texture, Group parent)
    {
        return NewImage(texture,parent.getWidth()/2,parent.getHeight()/2, Align.center,texture.getRegionWidth(),texture.getRegionHeight(),parent);
    }
    public static Image NewImage(TextureRegion texture , float width, float height, Group parent)
    {
        return NewImage(texture,parent.getWidth()/2,parent.getHeight()/2, Align.center,width,height,parent);
    }

    //other
    public static void ReplaceTexture(Image img, TextureRegion tr)
    {
        img.setDrawable(new TextureRegionDrawable(tr));
        Resize(img,tr.getRegionWidth(),tr.getRegionHeight());
    }
    public static Image NewBackground(TextureRegion tr, Group parent)
    {
        Image img = UI.NewImage(tr,Scene.width/2,Scene.height/2, Align.center,parent);
        img.setOrigin(Align.center);
        img.setScale(Scene.scale);
        return img;

    }

    //Drawable
    public static Drawable NewDrawable(Texture texture)
    {
        return NewDrawable(new TextureRegion(texture));
    }
    public static Drawable NewDrawable(TextureRegion textureRegion)
    {
        return new TextureRegionDrawable(textureRegion);
    }
    //NewLabel
    private static float fontScale = 1f;
    public static String fontName = "font";

    public static void SetFont(Label label, BitmapFont font)
    {
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        label.setStyle(labelStyle);
    }
    public static void BestFix(Label label, String content, float maxScale, boolean wrap)
    {
        label.setFontScale(fontScale*maxScale);
        label.setText(content);
        label.setWrap(wrap);
        label.validate();
        if (label.getWidth()==0){
            GDX.Error("can't fix cause width=0");
            return;
        }
        float scale = label.getWidth()/label.getPrefWidth();
        if (wrap)
        {
            float area = label.getGlyphLayout().width*label.getGlyphLayout().height;
            float fixArea = label.getWidth()*label.getHeight();
            scale = (float) Math.sqrt(fixArea/area);
        }
        if (scale>1) scale = 1;
        label.setFontScale(scale*fontScale*maxScale);
    }
    public static Label NewLabel(String content, Color color, float scale)
    {
        return NewLabel(Loader.GetFont(fontName),content,color,scale);
    }
    public static Label NewLabel(String content, Color color)
    {
        return NewLabel(Loader.GetFont(fontName),content,color,1f);
    }
    public static Label NewLabel(String content)
    {
        return NewLabel(Loader.GetFont(fontName),content, Color.WHITE,1f);
    }
    public static Label NewLabel(BitmapFont font, String content, Color color, float scale)
    {
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(content, labelStyle);
        label.setColor(color);
        label.setText(content);
        label.setFontScale(scale*fontScale);
        label.setTouchable(Touchable.disabled);
        return label;
    }
    public static Label NewLabel(String content, int alignment, Color color , float scale, float x, float y, int align, float width, float height , Group parent)
    {
        return NewLabel(Loader.GetFont(fontName),content,alignment,color,scale,x,y,align,width,height,parent);
    }
    public static Label NewLabel(String content, float x, float y, int align, Group parent)
    {
        return NewLabel(content,1f,x,y,align,parent);
    }
    public static Label NewLabel(String content, float scale, float x, float y, int align, Group parent)
    {
        return NewLabel(content, Color.WHITE,scale,x,y,align,parent);
    }
    public static Label NewLabel(String content, int alignment, Color color , float scale, float x, float y, int align, Group parent)
    {
        Label lb = NewLabel(Loader.GetFont(fontName),content,color,scale,x,y,align,parent);
        lb.setAlignment(alignment);
        return lb;
    }
    public static Label NewLabel(BitmapFont font, String content, int alignment, Color color , float scale, float x, float y, int align, float width, float height , Group parent)
    {
        Label label = NewLabel(font, content, color,scale);
        SetBounds(label,x,y,align,width,height,parent);
        label.setAlignment(alignment);
        BestFix(label,content,scale,true);
        return label;
    }
    public static Label NewLabel(String content, Color color , float scale, float x, float y, int align, float width, float height , Group parent)
    {
        return NewLabel(content, Align.center, color, scale, x, y, align,width,height, parent);
    }
    public static Label NewLabel(String content, float x, float y, int align, float width, float height , Group parent)
    {
        return NewLabel(content, 1f, x, y, align,width,height, parent);
    }
    public static Label NewLabel(String content, float scale , float x, float y, int align, float width, float height , Group parent)
    {
        return NewLabel(content, Align.center, Color.WHITE, scale, x, y, align,width,height, parent);
    }
    public static Label NewLabel(String content, Color color , float scale, float x, float y, int align, Group parent)
    {
        return NewLabel(Loader.GetFont(fontName),content, color, scale, x, y, align, parent);
    }
    public static Label NewLabel(BitmapFont font, String content, Color color , float scale, float x, float y, int align, Group parent)
    {
        Label label = NewLabel(font, content, color,scale);
        SetBounds(label,x,y,align,label.getPrefWidth(),label.getPrefHeight(),parent);
        label.setAlignment(align);
        return label;
    }
    //button
    public static Image NewButton(TextureRegion texture)
    {
        Image bt = NewImage(texture);
        EffectPressedButton(bt);
        return bt;
    }
    public static Image NewButton(TextureRegion texture, Group parent)
    {
        return NewButton(texture,parent.getWidth()/2,parent.getHeight()/2, Align.center,texture.getRegionWidth(),texture.getRegionHeight(),parent);
    }
    public static Image NewButton(TextureRegion texture, float x, float y, int align , float width, float height, Group parent)
    {
        Image bt = NewImage(texture, x, y, align, width, height, parent);
        EffectPressedButton(bt);
        return bt;
    }
    public static Image NewButton(TextureRegion texture, float x, float y, int align , Group parent)
    {
        return  NewButton(texture, x, y, align , texture.getRegionWidth(), texture.getRegionHeight(),parent);
    }
    //buttonText
    public static boolean shadow;
    public static int offsetX,offsetY,offsetShadowX,offsetShadowY;
    private static TextureRegion GetDefaultButtonTexture()
    {
        return new TextureRegion(NewTexture(Color.BROWN,300,100));
    }
    public static Group NewTextButton(String content, Color cl, float scale, TextureRegion texture , float x, float y, int align , float width, float height, Group parent)
    {
        Group group = NewGroup(x, y, align, width, height, parent);
        Image button = NewImage(texture,width,height,group);
        EffectPressedButton(button);
        float mx = width/2+offsetX;
        float my = height/2+offsetY;
        if (shadow)
        NewLabel(content, Color.BLACK,scale,mx+offsetShadowX,my+offsetShadowY, Align.center,width-10,height-10,group);
        NewLabel(content, cl,scale,mx,my, Align.center,width-10,height-10,group);
        return group;

    }
    public static Group NewTextButton(String content, Color cl, float scale, float x, float y, int align , float width, float height, Group parent)
    {
        return NewTextButton(content,  cl,scale, GetDefaultButtonTexture(), x, y, align, width, height, parent);
    }
    public static Group NewTextButton(String content, Color cl, float scale, float x, float y, int align, Group parent)
    {
        TextureRegion texture = GetDefaultButtonTexture();
        return NewTextButton(content,  cl,scale,texture, x, y, align, texture.getRegionWidth(), texture.getRegionHeight(), parent);
    }
    public static Group NewTextButton(String content, Color cl, float scale, TextureRegion texture , float x, float y, int align , Group parent)
    {
        return NewTextButton(content,cl,scale, texture, x, y, align,texture.getRegionWidth(),texture.getRegionHeight(), parent);
    }
    //TextField
    public static TextField NewTextField(String content, float x, float y, int align, float width, float height, Group parent)
    {
        Drawable cursor = new TextureRegionDrawable(Loader.GetTexture("cursor"));
        Drawable selection = new TextureRegionDrawable(Loader.GetTexture("tfSelection"));
        Drawable background = new TextureRegionDrawable(Loader.GetTexture("tfbackground"));
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle(Loader.GetFont(fontName), Color.BLACK,cursor,selection,background);

        TextField textField = new TextField(content, textFieldStyle);
        textField.setSize(width, height);
        textField.setPosition(x,y,align);
        textField.setAlignment(Align.center);

        textField.addListener(new ClickListener(){
            boolean touch = false;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (touch) return;
                touch = true;
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        touch = false;
                        textField.setText(text);
                    }
                    @Override
                    public void canceled() {
                        touch = false;
                    }
                }, "ĐIỀN TÊN", textField.getText() + "", "");
            }
        });

        parent.addActor(textField);
        return textField;
    }

    public static Group NewIconButton(TextureRegion texture, TextureRegion icon, float x, float y, int align, Group parent)
    {
        return NewIconButton(texture, icon, x, y, align,texture.getRegionWidth(),texture.getRegionHeight(), parent);
    }
    public static Group NewIconButton(TextureRegion texture, TextureRegion icon, float x, float y, int align, float width, float height, Group parent)
    {
        Group group = NewGroup(x, y, align, width, height, parent);
        EffectPressedButton(UI.NewImage(texture,width,height,group));
        UI.NewImage(icon,group.getWidth()/2,group.getHeight()/2, Align.center,group).setTouchable(Touchable.disabled);
        return group;
    }

    public static void Resize(Actor actor, float width, float height)
    {
        float x = actor.getX(Align.center);
        float y = actor.getY(Align.center);
        actor.setSize(width, height);
        actor.setPosition(x,y, Align.center);
    }

    //NewGroup
    public static Group NewGroup(float width, float height){
        Group gr = new Group();
        gr.setSize(width, height);
        return gr;
    }
    public static Group NewGroup(float x, float y, int align, float width, float height, Group parent){
        Group gr = new Group();
        SetBounds(gr,x,y,align,width,height,parent);
        return gr;
    }
    //NewTable
    public static Table NewTable(float x, float y, int align, float width, float height, Group parent)
    {
        Table tb = new Table();
        SetBounds(tb,x,y,align,width,height,parent);
        return tb;
    }
    //NewScroll
    public static ScrollPane NewScrollPane(Actor content, float x, float y, int align, float width, float height, Group parent)
    {
        ScrollPane scroll = new ScrollPane(content);
        SetBounds(scroll,x,y,align,width,height,parent);
        scroll.scrollTo(0,0,0,0);
        return scroll;
    }
}
