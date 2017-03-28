package jaygoo.com.animation.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import static android.graphics.Region.Op.DIFFERENCE;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/22
 * 描    述:
 * ================================================
 */
public class ScanImageView extends android.support.v7.widget.AppCompatImageView {

    private int SPEED = 30;
    private Bitmap mMaskBitmap;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private int mSrcBmWidth;
    private int mSrcBmHeight;
    private float scale = 1;
    private int h = 0;

    // 1 scan up to down , -1 scan down to up, 0 stop scanning
    private int direction;
    private int reSizeTop;
    private boolean isDrawing;


    public ScanImageView(Context context) {
        super(context);
        init();
    }

    public ScanImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null != mMaskBitmap ) {
            isDrawing = false;

            if (direction == 1){// 自上而下扫描

                isDrawing = true;

                //自上而下，到底部收尾阶段
                if (h > canvas.getHeight()){
                    h = canvas.getHeight();
                    direction = -1;
                    canvas.drawBitmap(mMaskBitmap, 0, reSizeTop, mPaint);
                    invalidate();
                }else {

                    //自上而下，起步阶段
                    canvas.clipRect(0, 0, mSrcBmWidth, h);
                    canvas.drawBitmap(mMaskBitmap, 0, reSizeTop, mPaint);
                    h += SPEED;

                    //加上80ms，大约每次调用onDraw花费100ms
                    postInvalidateDelayed(80);
                }
            }else if (direction == -1){//自下而上扫描

                isDrawing = true;

                //自上而下，到底部收尾阶段
                if (h <=0){
                    h = SPEED;
                    direction = 1;
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    invalidate();
                }else {

                    //自下而上，起步阶段
                    canvas.clipRect(0, h, mSrcBmWidth, canvas.getHeight() - reSizeTop);
                    canvas.drawBitmap(mMaskBitmap, 0, reSizeTop, mPaint);
                    h -= SPEED;

                    //加上80ms，大约每次调用onDraw花费100ms
                    postInvalidateDelayed(80);
                }
            }

        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

    }

    public void stopScan(){
        direction = 0;
        isDrawing = false;
        setImageBitmap(mMaskBitmap);
    }

    public boolean isDrawing(){
        return  isDrawing;
    }

    public void beginDraw(final Bitmap mask ,final int duration) {

        if (mask != null) {

            postDelayed(new Runnable() {
                @Override
                public void run() {

                    mMaskBitmap = mask;
                    mSrcBmWidth = mask.getWidth();
                    mSrcBmHeight = mask.getHeight();

                    //fit center
                    float scale = 1.0f * mWidth / mSrcBmWidth;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale, mSrcBmWidth, mSrcBmHeight);
                    mMaskBitmap = Bitmap.createBitmap(mask, 0, 0, mSrcBmWidth, mSrcBmHeight, matrix, true);
                    mSrcBmWidth = (int) (scale * mSrcBmWidth);
                    mSrcBmHeight = (int) (scale * mSrcBmHeight);
                    reSizeTop = (mHeight - mSrcBmHeight)/2;

                    //根据时间计算扫描速度
                    if (mSrcBmHeight != 0) {
                        SPEED =  mSrcBmHeight / (duration / 100);
                    }
                    direction = 1;
                    invalidate();

                }
            },100);

        }

    }

}
