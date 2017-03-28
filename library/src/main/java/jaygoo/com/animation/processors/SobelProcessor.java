package jaygoo.com.animation.processors;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import jaygoo.com.sobelbyrsdemo.ScriptC_sobel;


public class SobelProcessor extends SuperProcessor {

	int[] pixels;
	public SobelProcessor(RenderScript renderscript) {
		super(renderscript);
	}

	@Override
	public Bitmap process(Bitmap input) throws RSRuntimeException {

		long time = System.currentTimeMillis();
		Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
		ScriptC_sobel mSobelScript = new ScriptC_sobel(mRenderScript);
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

		Log.d("HandleTime", "Sobel(ms): "+ (System.currentTimeMillis() - time));

		mSobelScript.destroy();
		inputAllocation.destroy();
		outputAllocation.destroy();
		return output;
	}


	private Allocation createAllocation(Bitmap bitmap) throws RSRuntimeException{
		int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		Allocation allocation = Allocation.createSized(mRenderScript,
				Element.I32(mRenderScript), bitmap.getWidth() * bitmap.getHeight(), Allocation.USAGE_GRAPHICS_TEXTURE);
		//初始化数组
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
