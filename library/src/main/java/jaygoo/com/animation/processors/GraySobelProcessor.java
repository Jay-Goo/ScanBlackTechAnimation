package jaygoo.com.animation.processors;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import jaygoo.com.sobelbyrsdemo.ScriptC_graysobel;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/26
 * 描    述:
 * ================================================
 */
public class GraySobelProcessor extends SuperProcessor {

    public GraySobelProcessor(RenderScript renderscript) {
        super(renderscript);
    }

    @Override
    public Bitmap process(Bitmap input) throws RSRuntimeException {

        long time = System.currentTimeMillis();

        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
        ScriptC_graysobel mSobelScript = new ScriptC_graysobel(mRenderScript);
        Allocation inputAllocation = createAllocation(input);
        Allocation outputAllocation = Allocation.createTyped(mRenderScript, inputAllocation.getType());

        mSobelScript.set_gIn(inputAllocation);
        mSobelScript.set_gOut(outputAllocation);
        mSobelScript.set_mImageHeight(input.getHeight());
        mSobelScript.set_mImageWidth(input.getWidth());
        mSobelScript.set_gScript(mSobelScript);
        mSobelScript.bind_gOutPixels(outputAllocation);
        mSobelScript.invoke_compute();

        output = getBitmapFromAllocation(outputAllocation, output);

        Log.d("HandleTime", "GraySobel(ms): "+ (System.currentTimeMillis() - time));

        mSobelScript.destroy();
        inputAllocation.destroy();
        outputAllocation.destroy();
        return output;
    }


    private Allocation createAllocation(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        Allocation allocation = Allocation.createSized(mRenderScript,
                Element.I32(mRenderScript), bitmap.getWidth() * bitmap.getHeight(), Allocation.USAGE_SCRIPT);

        allocation.copy1DRangeFrom(0,bitmap.getWidth() * bitmap.getHeight(), pixels);
        return allocation;
    }

    private Bitmap getBitmapFromAllocation(Allocation allocation, Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        allocation.copyTo(pixels);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        return bitmap;
    }


}
