package jaygoo.com.animation.brokenview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class BrokenView extends View {

    private ArrayList<BrokenAnimator> animMap;
    private LinkedList<BrokenAnimator> animList;
    private BrokenCallback callBack;
    private View mView;
    private boolean enable;

    public BrokenView(Context context) {
        super(context);
        init();
    }


    public BrokenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BrokenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // KITKAT(API 19) and earlier need to set it when use
        // PathMeasure.getSegment to display resulting path.
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        init();
    }

    private void init() {
        enable = true;
        animMap = new ArrayList<>();
        animList = new LinkedList<>();

        add2Window((Activity) getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ListIterator<BrokenAnimator> iterator = animList.listIterator(animList.size());
        while(iterator.hasPrevious()) {
            iterator.previous().draw(canvas);
        }
    }

//    public BrokenAnimator getAnimator(View view) {
//        BrokenAnimator bAnim = animMap.get(view);
//        if(bAnim != null && bAnim.getStage() != BrokenAnimator.STAGE_EARLYEND)
//            return bAnim;
//        else
//            return null;
//    }

    public BrokenAnimator createAnimator( View view, Point point, BrokenConfig config){

        Bitmap bitmap = Utils.convertViewToBitmap(view);
        if(bitmap == null)
            return null;

        mView = view;

        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect); //得到view相对于整个屏幕的坐标
        rect.offset(0, -Utils.dp2px(25)); //去掉状态栏高度

        BrokenAnimator bAnim = new BrokenAnimator(this,rect,bitmap,point,config);
        bAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                final BrokenAnimator bAnim = (BrokenAnimator)animation;
                // We can't set FallingDuration here because it
                // change the duration of STAGE_BREAKING.
                bAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        BrokenAnimator bA = (BrokenAnimator)animation;
                        bA.setInterpolator(new LinearInterpolator());
                        bA.setStage(BrokenAnimator.STAGE_FALLING);
                        bA.setFallingDuration();
                        onBrokenFalling();
                        bA.removeUpdateListener(this);
                    }
                });
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                BrokenAnimator bAnim = (BrokenAnimator)animation;

                animMap.remove(bAnim);
                animList.remove(bAnim);

                if(bAnim.getStage() == BrokenAnimator.STAGE_BREAKING) {
                    onBrokenCancelEnd();
               }
                else if(bAnim.getStage() == BrokenAnimator.STAGE_FALLING)
                   onBrokenFallingEnd();
            }
        });
        animList.addLast(bAnim);
        animMap.add(bAnim);
        return bAnim;
    }

    private void add2Window(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        rootView.addView(this, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Utils.screenWidth = dm.widthPixels;
        Utils.screenHeight = dm.heightPixels;

    }

    public void reset(){
        ListIterator<BrokenAnimator> iterator = animList.listIterator();
        while(iterator.hasNext()) {
            BrokenAnimator bAnim = iterator.next();
            bAnim.removeAllListeners();
            bAnim.cancel();
        }

        animList.clear();
        animMap.clear();
        invalidate();
    }

    public void release(Activity activity){
        ListIterator<BrokenAnimator> iterator = animList.listIterator();
        while(iterator.hasNext()) {
            BrokenAnimator bAnim = iterator.next();
            bAnim.removeAllUpdateListeners();
            bAnim.removeAllListeners();
            bAnim.cancel();
        }
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        for(int i = 0; i < rootView.getChildCount(); i++){
            if(rootView.getChildAt(i) instanceof BrokenView){
                rootView.removeViewAt(i);
            }
        }
        animList.clear();
        animMap.clear();
    }


    //auto start broken animation
    public void start(BrokenAnimator brokenAnimator){
        if (brokenAnimator != null){
            brokenAnimator.start();
            onBrokenStart();
        }


    }

    public void start(View view){
        Point point = new Point(Utils.screenWidth/2,
                Utils.screenHeight / 2);
        start(createAnimator(view,point,new BrokenConfig()));
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setCallback(BrokenCallback c){

        callBack = c;
    }

    protected void onBrokenCancel(){
        if(callBack != null)
            callBack.onCancel();
    }

    protected void onBrokenStart(){
        if(callBack != null)
            callBack.onStart();
    }

    protected void onBrokenCancelEnd(){
        if(callBack != null)
            callBack.onCancelEnd();
    }

    protected void onBrokenFallingEnd(){
        if(callBack != null )
            callBack.onFallingEnd();
    }

    protected void onBrokenRestart(){
        if(callBack != null)
            callBack.onRestart();
    }

    protected void onBrokenFalling(){

        // has memory leak, detail see BlackTechFragment.java #
        if (mView != null) {
            mView.setVisibility(View.INVISIBLE);
        }

        if(callBack != null){
            callBack.onFalling();
        }
    }


}
