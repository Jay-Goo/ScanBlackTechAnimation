package jaygoo.com.animation;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import jaygoo.com.animation.widgets.BlackTechFragment;

import static jaygoo.com.animation.widgets.BlackTechFragment.REDUCE_ANIMATION_TIME;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/23
 * 描    述:
 * ================================================
 */
public class ScanBlackTechBuilder {

    private static final long MIN_DURATION_TIME = REDUCE_ANIMATION_TIME + 2 * 1000 + 100;
    private int layoutId;
    private long startTime;
    private BlackTechFragment blackTechFragment;
    private WeakReference<AppCompatActivity>  mActivity;
    private AnimatorEndListener mAnimatorEndListener;

    public ScanBlackTechBuilder build(AppCompatActivity activity, int layoutId, String picPath){

        if (activity != null && layoutId != 0 && picPath != null){
            mActivity = new WeakReference<>(activity);
            this.layoutId = layoutId;

            blackTechFragment =  BlackTechFragment.newInstance(picPath);

        }else {
            throw new RuntimeException("one of them is null !!");
        }
        return this;

    }


    public void start(){
        startTime = System.currentTimeMillis();
        if (mActivity.get() != null && blackTechFragment != null) {

            mActivity.get().getSupportFragmentManager().beginTransaction().
                    add(layoutId, blackTechFragment).commitAllowingStateLoss();

        }

    }

    public void release(){
        if (mActivity != null && blackTechFragment != null) {

            removeCallBacks();
            mActivity.get().getSupportFragmentManager().beginTransaction().
                    remove(blackTechFragment).commitAllowingStateLoss();

        }
        mActivity = null;
    }

    public void revert(){

        long durationTime = System.currentTimeMillis() - startTime;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    blackTechFragment.startRevertAnimation();
                }catch (RuntimeException e){
                    e.printStackTrace();
                    if (mAnimatorEndListener != null){
                        mAnimatorEndListener.onAnimatorEnd();
                    }
                }
            }
            //至少时间保证扫描两遍
        },(MIN_DURATION_TIME - durationTime > 0 ? MIN_DURATION_TIME - durationTime : 0 ));

    }

    public ScanBlackTechBuilder setOnAnimatorEndListener(AnimatorEndListener listener){
        if (listener != null && blackTechFragment != null) {
            blackTechFragment.setScanBlackTechAnimatorEndListener(listener);
            mAnimatorEndListener = listener;
        }
        return this;
    }

    private void removeCallBacks(){
        mAnimatorEndListener = null;
        if (blackTechFragment != null){
            blackTechFragment.setScanBlackTechAnimatorEndListener(null);
        }
    }


}
