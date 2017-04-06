package jaygoo.com.animation.widgets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jaygoo.com.animation.AnimatorEndListener;
import jaygoo.com.animation.R;
import jaygoo.com.animation.RecycleUtils;
import jaygoo.com.animation.brokenview.BrokenCallback;
import jaygoo.com.animation.brokenview.BrokenView;
import jaygoo.com.animation.depth.Depth;
import jaygoo.com.animation.depth.DepthProvider;
import jaygoo.com.animation.depth.animations.ReduceConfiguration;
import jaygoo.com.animation.depth.animations.RevertConfiguration;
import jaygoo.com.animation.exceptions.ProcessorException;
import jaygoo.com.animation.processors.ProcessorInterface;


public class BlackTechFragment extends Fragment {

    private static final int SOBEL_COMPLETE = 0x000010;
    private static final int SOBEL_FAIL = 0x000011;
    private static final int BLUR_COMPLETE = 0x000012;

    //进场动画时间
    public static final int REDUCE_ANIMATION_TIME = 3000;
    private static final int REVERT_ANIMATION_TIME = 2000;


    private boolean isSobelTimeOut;
    private Depth depth;
    private View rootView;
    private Bitmap srcBitmap;
    private Bitmap sobelBitmap;
    private Bitmap blurBitmap;
    private Bitmap graySobelBitmap;
    private ImageView srcImageView;
    private ScanImageView maskImageView;
    private BrokenView brokenView;
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
                    maskImageView.beginDraw(sobelBitmap);
                    srcImageView.setImageBitmap(blurBitmap);
                    break;

                case SOBEL_FAIL:

                    Log.d("HandleTime", "handleMessage: fail");
                    break;

            }
        }
    };


    private BrokenCallback mBrokenCallBack = new BrokenCallback() {

        @Override
        public void onFallingEnd() {
            super.onFallingEnd();
            if (mScanBlackTechAnimatorEndListener != null){
                mScanBlackTechAnimatorEndListener.onAnimatorEnd();
            }
        }

        @Override
        public void onFalling() {
            super.onFalling();

            // fix animation memory leak , but I don't know why ...
            // who can save my fucking life ?!!!!

            if (srcImageView != null) {
                srcImageView.setVisibility(View.GONE);
            }

            if (maskImageView != null) {
                maskImageView.setVisibility(View.GONE);
            }

        }

    };



    public static BlackTechFragment newInstance(String picPath) {
        Bundle bundle = new Bundle();
        bundle.putString("picPath",picPath);
        BlackTechFragment fragment = new BlackTechFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_black_tech, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view == null)return;

        rootView = view;
        maskImageView = (ScanImageView)view.findViewById(R.id.maskImageView);
        srcImageView = (ImageView)view.findViewById(R.id.srcImageView);
        srcImageView.post(new Runnable() {
            @Override
            public void run() {
                srcImageView.setImageBitmap(srcBitmap);
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getString("picPath",null) != null){
                srcBitmap = BitmapFactory.decodeFile(bundle.getString("picPath"));
        }
        if (srcBitmap != null) {
            processImage();
            startAnimation();
        }else {
            Log.i("HandleTime", "onViewCreated: srcBitmap is null!");
        }
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
    public void startRevertAnimation() throws RuntimeException{
        depth
                .animate()
                .revert(this,new RevertConfiguration().setDuration(REVERT_ANIMATION_TIME))
                .start();


        //退场结束后碎裂当前View
        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    brokenView = new BrokenView(getActivity());
                    brokenView.setCallback(mBrokenCallBack);
                    brokenView.start(rootView);

            }
        },REVERT_ANIMATION_TIME + 100);

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
                    Bitmap grayBitmap = processor.process(ProcessorInterface.ProcessorType.GRAY, srcBitmap);
                    sobelBitmap = processor.process(ProcessorInterface.ProcessorType.SOBEL
                            ,grayBitmap);
                    RecycleUtils.recycleBitmap(grayBitmap);
                    blurBitmap = processor.process(ProcessorInterface.ProcessorType.BLUR,
                            srcBitmap);
                    graySobelBitmap = processor.process(ProcessorInterface.ProcessorType.GRAYSOBEL,
                            sobelBitmap);
                    Log.d("HandleTime", "total(ms): "+ (System.currentTimeMillis() - time));

                    if (sobelBitmap != null && blurBitmap != null && graySobelBitmap != null){

                        //如果转场动画完成后，识别依然没完成，那么等识别完成后再执行
                        if (isSobelTimeOut){
                            detectHandler.sendEmptyMessage(SOBEL_COMPLETE);
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

        startBeginningAnimation();

        //转场动画完成后，判断识别是否完成
        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sobelBitmap != null  && graySobelBitmap != null && blurBitmap != null){
                    detectHandler.sendEmptyMessage(SOBEL_COMPLETE);
                }else {
                    isSobelTimeOut = true;
                }

            }
        },REDUCE_ANIMATION_TIME - 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onRelease();
    }



    private void onRelease(){
        try {
            rootView = null;

            depth = null;

            if (brokenView != null){
                brokenView.setCallback(null);
                brokenView.release(getActivity());
                brokenView = null;
            }

            if (maskImageView != null) {
                maskImageView.release();
                maskImageView = null;
            }

            processor.release();
            processor = null;

            RecycleUtils.recycleImageView(srcImageView);
            srcImageView = null;

            srcBitmap = null;
            RecycleUtils.recycleBitmap(sobelBitmap);
            sobelBitmap = null;
            RecycleUtils.recycleBitmap(blurBitmap);
            blurBitmap = null;
            RecycleUtils.recycleBitmap(graySobelBitmap);
            graySobelBitmap = null;


            if (animHandler != null){
                animHandler.removeCallbacksAndMessages(null);
                animHandler = null;
            }

            if (detectHandler != null){
                detectHandler.removeCallbacksAndMessages(null);
                detectHandler = null;
            }


            System.gc();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }
}
