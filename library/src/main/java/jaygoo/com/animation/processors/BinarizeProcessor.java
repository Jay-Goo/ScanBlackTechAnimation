package jaygoo.com.animation.processors;



import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;

import jaygoo.com.sobelbyrsdemo.ScriptC_binarize;


public class BinarizeProcessor extends SuperProcessor {

    public BinarizeProcessor(RenderScript renderscript) {
    	super(renderscript);
    }

    @Override
    public Bitmap process(Bitmap input) throws RSRuntimeException {
    	Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
    	Allocation inputAllocation = Allocation.createFromBitmap(mRenderScript, input);
    	Allocation outputAllocation = Allocation.createFromBitmap(mRenderScript, output);
    	ScriptC_binarize mBinarizeScript = new ScriptC_binarize(mRenderScript);
    		
    	mBinarizeScript.forEach_root(inputAllocation, outputAllocation);
    	outputAllocation.copyTo(output);
		mBinarizeScript.destroy();
    	return output;
    }


}
