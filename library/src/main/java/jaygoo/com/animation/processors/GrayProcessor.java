package jaygoo.com.animation.processors;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import jaygoo.com.animation.RecycleUtils;
import jaygoo.com.sobelbyrsdemo.ScriptC_grayscale;


public class GrayProcessor extends SuperProcessor {

	public GrayProcessor(RenderScript renderscript) {
		super(renderscript);
	}

	@Override
	public Bitmap process(Bitmap input) {
		if (input == null)return null;

		long time = System.currentTimeMillis();

		Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
		Allocation inputAllocation = Allocation.createFromBitmap(mRenderScript, input);
		Allocation outputAllocation = Allocation.createFromBitmap(mRenderScript, output);
		ScriptC_grayscale mGrayScaleScript = new ScriptC_grayscale(mRenderScript);
		mGrayScaleScript.forEach_root(inputAllocation, outputAllocation);
		outputAllocation.copyTo(output);

		Log.d("HandleTime", "Gray(ms): "+ (System.currentTimeMillis() - time));

		return output;
	}


}
