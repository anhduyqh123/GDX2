package GameGDX;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

public class Transition {

    public static void ShowPieces(TextureRegion tr, int column, int row, Group parent, Runnable done)
    {
        Group group = new Group();
        parent.addActor(group);

        float tileW = tr.getRegionWidth()/column;
        float tileH = tr.getRegionHeight()/row;
        TextureRegion[][] grid = tr.split((int)tileW,(int)tileH);

        int[] count = {row*column};
        for(int j=0;j<row;j++)
            for(int i=0;i<column;i++)
            {
                int y = row-1-j;
                int index = j*column+i;
                NewPiece(grid[y][i],i*tileW,y*tileH,0.6f,index*0.02f,()-> {
                    count[0]--;
                    if (count[0]<=0){
                        group.remove();
                        done.run();
                    }
                },group);
            }
    }
    private static void NewPiece(TextureRegion tr, float x, float y, float duration, float delay, Runnable done, Group parent)
    {
        tr.flip(false,true);
        Image img = UI.NewImage(tr,Scene.width/2,Scene.height/2, Align.center,parent);
        if (x<Scene.width/2) img.setX(0, Align.right);
        else img.setX(Scene.width, Align.left);
        img.setOrigin(Align.center);
        img.setScale(0);

        Action ac0 = Actions.scaleTo(1,1,duration, Interpolation.swingOut);
        Action ac1 = Actions.moveTo(x,y,duration, Interpolation.swingOut);
        Action ac01 = Actions.parallel(ac0,ac1);
        Action ac2 = Actions.run(done);
        img.addAction(Actions.sequence(Actions.delay(delay),ac01,ac2));
    }
}
