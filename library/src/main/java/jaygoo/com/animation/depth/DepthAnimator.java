package jaygoo.com.animation.depth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;



import java.util.ArrayList;
import java.util.List;

import jaygoo.com.animation.depth.animations.DepthAnimation;
import jaygoo.com.animation.depth.animations.EnterAnimation;
import jaygoo.com.animation.depth.animations.EnterConfiguration;
import jaygoo.com.animation.depth.animations.ExitAnimation;
import jaygoo.com.animation.depth.animations.ExitConfiguration;
import jaygoo.com.animation.depth.animations.ReduceAnimation;
import jaygoo.com.animation.depth.animations.ReduceConfiguration;
import jaygoo.com.animation.depth.animations.RevertAnimation;
import jaygoo.com.animation.depth.animations.RevertConfiguration;
import jaygoo.com.animation.depth.animations.SuspendAnimation;
import jaygoo.com.animation.depth.animations.SuspendConfiguration;

/**
 * Created by florentchampigny on 03/03/2017.
 */

public class DepthAnimator {

    private Depth depth;
    private List<DepthAnimation> animations;
    private int currentIndex = -1;

    private List<DepthFragmentState> fragmentsState;
    private DepthRelativeLayoutContainer mDepthLayoutContainer;

    public DepthAnimator(Depth depth) {
        this.depth = depth;
        this.animations = new ArrayList<>();
        this.fragmentsState = new ArrayList<>();
    }

    public DepthAnimator reduce(Fragment fragment, ReduceConfiguration reduceConfiguration) {
        this.add(new ReduceAnimation().setReduceConfiguration(reduceConfiguration), fragment);
        return this;
    }

    public DepthAnimator reduce(Fragment fragment) {
        return reduce(fragment, null);
    }

    public DepthAnimator enter(Fragment fragment, EnterConfiguration enterConfiguration) {
        this.add(new EnterAnimation().setEnterConfiguration(enterConfiguration), fragment);
        return this;
    }

    public DepthAnimator enter(Fragment fragment) {
        return enter(fragment, null);
    }

    public DepthAnimator revert(Fragment fragment, RevertConfiguration revertConfiguration) {
        this.add(new RevertAnimation().setRevertConfiguration(revertConfiguration), fragment);
        return this;
    }

    public DepthAnimator revert(Fragment fragment) {
        return revert(fragment, null);
    }

    public DepthAnimator exit(Fragment fragment, ExitConfiguration exitConfiguration) {
        this.add(new ExitAnimation().setExitConfiguration(exitConfiguration), fragment);
        return this;
    }

    public DepthAnimator suspend(Fragment fragment){
        return suspend(fragment,null);
    }

    public DepthAnimator suspend(Fragment fragment, SuspendConfiguration suspendConfiguration){
        this.add(new SuspendAnimation().setSuspendConfiguration(suspendConfiguration),fragment);
        return this;
    }


    public DepthAnimator exit(Fragment fragment) {
        return exit(fragment, null);
    }

    private void add(DepthAnimation depthAnimation, Fragment fragment) {
        animations.add(depthAnimation);
        fragmentsState.add(new DepthFragmentState(fragment));
    }

    private DepthRelativeLayoutContainer findDepthLayoutContainer(Fragment fragment) {
        return findDepthLayoutContainer(fragment.getView(), 6);
    }

    private DepthRelativeLayoutContainer findDepthLayoutContainer(View view, int depth) {
        if (view instanceof DepthRelativeLayoutContainer) {
            return (DepthRelativeLayoutContainer) view;
        } else {
            if (view instanceof ViewGroup) {
                depth--;
                final ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); ++i){
                    final DepthRelativeLayoutContainer depthRelativeLayoutContainer = findDepthLayoutContainer(viewGroup.getChildAt(i), depth);
                    if (depthRelativeLayoutContainer != null) {
                        return depthRelativeLayoutContainer;
                    }
                }
            }
        }
        return null;
    }

    private void afterAnimationEnd(int index) {
        final Fragment fragmentAnimated = fragmentsState.get(index).getFragment();
        final DepthAnimation finishedAnimation = animations.get(index);
        depth.notifyListenersEnd(finishedAnimation, fragmentAnimated);
        this.currentIndex = index + 1;

        DepthLogger.log("afterAnimationEnd "+finishedAnimation.getClass().getCanonicalName());

        if(finishedAnimation instanceof ExitAnimation){
            if (fragmentAnimated != null) {
                final View fragmentView = fragmentAnimated.getView();
                if (fragmentView != null) {
                    fragmentView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            depth.removeFragment(fragmentAnimated);
                        }
                    }, 100);
                }
            }
        }

        if (currentIndex < animations.size()) {
            startAnimation(currentIndex);
        } else {
            DepthLogger.log("clear");

            for (DepthFragmentState depthFragmentState : fragmentsState) {
                depthFragmentState.clear();
            }
            fragmentsState.clear();
            for (DepthAnimation animation : animations) {
                animation.clear();
            }
            animations.clear();

            System.gc();

            depth.onAnimationFinished();
        }
    }

    public void release(){

    }
    void reloadFragmentsState() {
        DepthLogger.log("reloadFragmentsState");

        for (DepthFragmentState depthFragmentState : fragmentsState) {
            depthFragmentState.reloadReady();
        }
        startAnimation(currentIndex);
    }

    private void startAnimation(final int index) {
        this.currentIndex = index;
        DepthLogger.log("startAnimation "+currentIndex);

        final DepthFragmentState depthFragmentState = fragmentsState.get(index);

        final Fragment fragment = depthFragmentState.getFragment();
        if (fragment != null) {

            final boolean ready = depthFragmentState.isReady();
            if (ready) {
                DepthLogger.log("fragment ready");

                final DepthAnimation animation = animations.get(index);
                 mDepthLayoutContainer = findDepthLayoutContainer(fragment);
                animation.setDepthLayoutContainer(mDepthLayoutContainer);
                animation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        afterAnimationEnd(index);
                    }
                });

                animation.start();
            } else {
                DepthLogger.log("fragment added");

                depth.addFragment(depthFragmentState.getFragment());
            }
        } else {
            DepthLogger.log("fragment null");
        }
    }

    public void start() {
        //setup listeners
        if (!animations.isEmpty() && currentIndex == -1) {
            final int index = 0;
            startAnimation(index);
        }
    }
}
