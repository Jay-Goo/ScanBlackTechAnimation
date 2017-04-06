package jaygoo.com.animation.processors;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/23
 * 描    述:
 * ================================================
 */
public class BlurProcessor extends SuperProcessor {

    public BlurProcessor(RenderScript renderscript) {
        super(renderscript);
    }

    @Override
    public Bitmap process(Bitmap bitmap) throws RSRuntimeException {
        if (bitmap == null)return null;

        long time = System.currentTimeMillis();

        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        Allocation allIn = Allocation.createFromBitmap(mRenderScript, bitmap);
        Allocation allOut = Allocation.createFromBitmap(mRenderScript, outBitmap);

        //高斯模糊级别: 0 < radius <= 25
        blurScript.setRadius(25.0f);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);

        Log.d("HandleTime", "Blur(ms): "+ (System.currentTimeMillis() - time));
        blurScript.destroy();
        return outBitmap;
    }


}
