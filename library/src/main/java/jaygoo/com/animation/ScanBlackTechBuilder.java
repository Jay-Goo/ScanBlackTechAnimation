package jaygoo.com.animation;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import jaygoo.com.animation.depth.Depth;
import jaygoo.com.animation.depth.DepthProvider;
import jaygoo.com.animation.widgets.BlackTechFragment;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/23
 * 描    述:
 * ================================================
 */
public class ScanBlackTechBuilder {

    private static final int MAX_BITMAP_SIZE = 60 * 1024 * 1024;
    private int layoutId;
    private Depth depth;
    private Bitmap srcBitmap;
    private BlackTechFragment blackTechFragment;
    private AppCompatActivity mActivity;

    public ScanBlackTechBuilder build(AppCompatActivity activity, int layoutId, Bitmap btp){

        if (activity != null && layoutId != 0 && btp != null){
            mActivity = activity;
            depth = DepthProvider.getDepth(activity);
            this.layoutId = layoutId;
            if (getBitmapSize(btp) >= MAX_BITMAP_SIZE){
                throw new RuntimeException("out of memory ! please compress your image first !!");
            }else {
                srcBitmap = btp;
            }
        }else {
            throw new RuntimeException("one of them is null !!");
        }
        return this;

    }


    public void start(){

            if (mActivity != null) {
                blackTechFragment = new BlackTechFragment(srcBitmap);
                mActivity.getSupportFragmentManager().beginTransaction().
                        replace(layoutId, blackTechFragment).commit();
                depth.setFragmentContainer(layoutId);
            }

    }


    public void revert(){

        if (blackTechFragment != null){
            blackTechFragment.startRevertAnimation();
        }
    }

    public ScanBlackTechBuilder setOnAnimatorEndListener(AnimatorEndListener listener){
        if (listener != null && blackTechFragment != null) {
            blackTechFragment.setScanBlackTechAnimatorEndListener(listener);
        }
        return this;
    }

    private int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){     //API 19
            return bitmap.getAllocationByteCount();
        } else {
            return bitmap.getByteCount();
        }
    }

}
