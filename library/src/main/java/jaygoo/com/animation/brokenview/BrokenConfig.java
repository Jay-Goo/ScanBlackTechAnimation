package jaygoo.com.animation.brokenview;

import android.graphics.Paint;
import android.graphics.Region;
import android.view.View;

public class BrokenConfig {

    //碎片数量
    int complexity = 24;

    //碎裂时间
    int breakDuration = 600;

    //下落时间
    int fallDuration = 900;

    //碎裂半径  dp
    int circleRiftsRadius = 86;

    Region region = null;

    View childView = null;

    Paint paint = null;

    public BrokenConfig setComplexityNumbers(int complexity){
        this.complexity = complexity;
        return this;
    }

    public BrokenConfig setBreakDuration(int breakDuration){
        this.breakDuration = breakDuration;
        return this;
    }

    public BrokenConfig setFallDuration(int fallDuration){
        this.fallDuration = fallDuration;
        return this;
    }

    public BrokenConfig setCircleRiftsRadius(int circleRiftsRadius){
        this.circleRiftsRadius = circleRiftsRadius;
        return this;
    }

}
