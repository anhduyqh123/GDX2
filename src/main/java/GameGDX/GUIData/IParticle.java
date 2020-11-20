package GameGDX.GUIData;

import GameGDX.GUIData.IChild.IChild;
import GameGDX.Loader;
import GameGDX.Particle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class IParticle extends IChild {

    public String parName = "";

    @Override
    protected Actor NewActor() {
        return new Particle();
    }

    @Override
    public void Refresh() {
        super.Refresh();
        try {
            Particle par = (Particle)actor;
            par.SetParticleEffect(Loader.GetParticleEffect(parName));
        }
        catch (Exception e){}
    }
    public void Start()
    {
        Particle par = (Particle)actor;
        par.Start(false);
    }
}
