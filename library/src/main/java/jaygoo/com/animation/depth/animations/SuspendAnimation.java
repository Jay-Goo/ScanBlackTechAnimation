package jaygoo.com.animation.depth.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;

import jaygoo.com.animation.depth.DepthRelativeLayout;
import jaygoo.com.animation.depth.TransitionHelper;


/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/23
 * 描    述:上升动画
 * ================================================
 */
public class SuspendAnimation extends DepthAnimation<SuspendAnimation>{

    private SuspendConfiguration mSuspendConfiguration = new SuspendConfiguration();

    public SuspendAnimation setSuspendConfiguration(SuspendConfiguration suspendConfiguration) {
        if (suspendConfiguration != null) {
            this.mSuspendConfiguration = suspendConfiguration;
        }
        return this;
    }

    @Override
    public void prepareAnimators(DepthRelativeLayout target, int index, int animationDelay) {
        final TimeInterpolator interpolator = TransitionHelper.interpolator;
        final float density = target.getResources().getDisplayMetrics().density;
        target.setPivotY(TransitionHelper.getDistanceToCenter(target));
        target.setPivotX(TransitionHelper.getDistanceToCenterX(target));
        target.setCameraDistance(10000 * density);

        final long totalDuration = mSuspendConfiguration.getDuration();

        final float finalTranslationY = (index * -1 * 8) * density + mSuspendConfiguration.getTranslationY() * density;

        { //translation Y
            final ObjectAnimator translationY = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, finalTranslationY);
            translationY.setDuration(totalDuration);
            translationY.setInterpolator(interpolator);
            translationY.setStartDelay(500);
            add(translationY);
        }

    }
}
