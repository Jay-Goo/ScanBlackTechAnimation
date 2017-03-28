package jaygoo.com.animation.widgets;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jaygoo.com.animation.AnimatorEndListener;
import jaygoo.com.animation.R;
import jaygoo.com.animation.brokenview.BrokenCallback;
import jaygoo.com.animation.brokenview.BrokenConfig;
import jaygoo.com.animation.brokenview.BrokenView;
import jaygoo.com.animation.brokenview.DensityUtils;
import jaygoo.com.animation.depth.Depth;
import jaygoo.com.animation.depth.DepthProvider;
import jaygoo.com.animation.depth.animations.ReduceConfiguration;
import jaygoo.com.animation.depth.animations.RevertConfiguration;
import jaygoo.com.animation.exceptions.ProcessorException;
import jaygoo.com.animation.processors.ProcessorInterface;


@SuppressLint("ValidFragment")
public class BlackTechFragment extends Fragment {

    private static final int SOBEL_COMPLETE = 0x000010;
    private static final int SOBEL_FAIL = 0x000011;
    private static final int BLUR_COMPLETE = 0x000012;

    //进场动画时间
    private static final int REDUCE_ANIMATION_TIME = 3000;
    private static final int REVERT_ANIMATION_TIME = 2000;

    private boolean isSobelTimeOut;
    private Depth depth;
    private View rootView;
    private Bitmap srcBitmap;
    private Bitmap sobelBitmap;
    private Bitmap blurBitmap;
    private Bitmap grayBitmap;
    private Bitmap graySobelBitmap;
    private ImageView srcImageView;
    private ScanImageView maskImageView;
    private ProcessorInterface processor;
    private AnimatorEndListener mScanBlackTechAnimatorEndListener;

    private Handler animHandler = new Handler();

    private Handler detectHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SOBEL_COMPLETE:
                    maskImageView.setImageBitmap(graySobelBitmap);
                    detectHandler.sendEmptyMessageDelayed(BLUR_COMPLETE,500);
                    break;

                case BLUR_COMPLETE:
                    maskImageView.beginDraw(sobelBitmap,800);
                    srcImageView.setImageBitmap(blurBitmap);
                    break;

                case SOBEL_FAIL:

                    Log.d("HandleTime", "handleMessage: fail");
                    break;

            }
        }
    };

    @SuppressLint("ValidFragment")
    public BlackTechFragment (Bitmap bitmap) {

        srcBitmap = bitmap;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_black_tech, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;
        maskImageView = (ScanImageView)view.findViewById(R.id.maskImageView);
        srcImageView = (ImageView)view.findViewById(R.id.srcImageView);
        srcImageView.setImageBitmap(srcBitmap);

        startAnimation();

        processImage();
        startBeginningAnimation();


    }

    //进场动画
    private void startBeginningAnimation(){

        depth = DepthProvider.getDepth(getContext());
        depth.onFragmentReady(this);
        depth
                .animate()
                .reduce(this, new ReduceConfiguration().setScale(0.5f).setDuration(REDUCE_ANIMATION_TIME))
                .start();

    }

    //退场动画
    public void startRevertAnimation(){
        depth
                .animate()
                .revert(this,new RevertConfiguration().setDuration(REVERT_ANIMATION_TIME))
                .start();

        //退场结束后碎裂当前View
        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BrokenView brokenView = BrokenView.add2Window(getActivity());
                Point point = new Point(DensityUtils.getDeviceWith(getActivity())/2,
                        DensityUtils.getDeviceHeight(getActivity()) / 2 - DensityUtils.dip2px(80,getActivity()));
                brokenView.start(brokenView.createAnimator(rootView, point, new BrokenConfig()));
                brokenView.setCallback(new BrokenCallback() {
                    @Override
                    public void onFallingEnd(View v) {
                        super.onFallingEnd(v);
                        onRelease();
                        if (mScanBlackTechAnimatorEndListener != null){
                            mScanBlackTechAnimatorEndListener.onAnimatorEnd();
                        }

                    }
                });

            }
        },REVERT_ANIMATION_TIME);

        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                maskImageView.stopScan();
            }
        },500);

    }

    public void setScanBlackTechAnimatorEndListener(AnimatorEndListener listener){
        mScanBlackTechAnimatorEndListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    //图片轮廓提取、高斯模糊进程
    private void processImage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    long time = System.currentTimeMillis();

                    processor = ProcessorInterface.getProcessor(getActivity());
                    grayBitmap = processor.process(ProcessorInterface.ProcessorType.GRAY, srcBitmap);
                    sobelBitmap = processor.process(ProcessorInterface.ProcessorType.SOBEL,grayBitmap);
                    blurBitmap = processor.process(ProcessorInterface.ProcessorType.BLUR, srcBitmap);
                    graySobelBitmap = processor.process(ProcessorInterface.ProcessorType.GRAYSOBEL, sobelBitmap);
                    Log.d("HandleTime", "total(ms): "+ (System.currentTimeMillis() - time));

                    if (sobelBitmap != null && blurBitmap != null && graySobelBitmap != null){

                        //如果转场动画完成后，识别依然没完成，那么等识别完成后再执行
                        if (isSobelTimeOut){
                            detectHandler.sendEmptyMessage(SOBEL_COMPLETE);
                            Log.d("HandleTime", "run: 他超时了，我在吊用");
                        }
                    }else {
                        detectHandler.sendEmptyMessage(SOBEL_FAIL);
                    }

                } catch (ProcessorException e) {
                    e.printStackTrace();
                    detectHandler.sendEmptyMessage(SOBEL_FAIL);
                }

            }
        }).start();


    }

    private void startAnimation() {

        //转场动画完成后，判断识别是否完成
        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sobelBitmap != null  && graySobelBitmap != null && blurBitmap != null){
                    detectHandler.sendEmptyMessage(SOBEL_COMPLETE);
                    Log.d("HandleTime", "run:识别正常");
                }else {
                    isSobelTimeOut = true;
                }

            }
        },REDUCE_ANIMATION_TIME - 1000);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        depth.removeFragment(this);
        depth = null;
        rootView = null;
        srcImageView = null;
        maskImageView = null;
    }


    public void onRelease(){
        try {
            processor.release();
            srcBitmap.recycle();
            sobelBitmap.recycle();
            blurBitmap.recycle();
            grayBitmap.recycle();
            graySobelBitmap.recycle();
            System.gc();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }
}
