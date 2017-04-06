package jaygoo.com.animation.processors;

import android.graphics.Bitmap;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;

public abstract class SuperProcessor {
    protected RenderScript mRenderScript;
	
    public abstract Bitmap process(Bitmap input) throws RSRuntimeException;

    public SuperProcessor(RenderScript renderscript) {
	this.mRenderScript = renderscript;
    }
	
    public RenderScript getScript() {
	return mRenderScript;
    }
}
