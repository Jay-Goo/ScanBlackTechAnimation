package jaygoo.com.animation.depth;

import android.support.v4.app.Fragment;

import jaygoo.com.animation.depth.animations.DepthAnimation;


/**
 * Created by florentchampigny on 03/03/2017.
 */

public interface DepthListener {
    void onAnimationEnd(DepthAnimation depthAnimation, Fragment fragment);
}
