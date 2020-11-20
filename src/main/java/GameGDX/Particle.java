package GameGDX;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class Particle extends Actor {
    public ParticleEffect pe;
    private boolean destroy,start;

    public Particle(){}
    public Particle(ParticleEffect pe, float x, float y) {
        this(pe,x,y,null);
    }
    public Particle(ParticleEffect pe, float x, float y, Group parent) {
        setPosition(x,y);
        if (parent!=null) parent.addActor(this);
        SetParticleEffect(pe);
    }
    public void SetParticleEffect(ParticleEffect pe)
    {
        this.pe = new ParticleEffect(pe);
    }
    public void Start()
    {
        Start(false);
    }
    public void Start(boolean destroy) {
        if (pe==null) return;
        pe.reset();
        pe.scaleEffect(this.getScaleX());
        this.destroy = destroy;
        start = true;
    }
    private void RefreshPosition()
    {
        pe.setPosition(getX(),getY());
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if (pe==null) return;
        if (!start) return;
        RefreshPosition();
        pe.update(delta);
        if (destroy && pe.isComplete()) remove();
    }

    @Override
    public boolean remove() {
        if (pe!=null) pe.dispose();
        return super.remove();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (pe==null) return;
        pe.draw(batch);
    }

    //extend
    public void SetSize(int index, float minX,float maxX,float minY,float maxY)
    {
        ParticleEmitter emit = pe.getEmitters().get(index);
        emit.getXScale().setHigh(maxX);
        emit.getXScale().setLow(minX);
        emit.getYScale().setHigh(maxY);
        emit.getYScale().setLow(minY);
    }
    public void SetSprite(int index, TextureRegion tr)
    {
        Array<Sprite> arr = new Array<>();
        arr.add(new Sprite(new TextureRegion(tr)));
        pe.getEmitters().get(index).setSprites(arr);
    }
    public ParticleEmitter GetEmitter(int index)
    {
        return pe.getEmitters().get(index);
    }

}