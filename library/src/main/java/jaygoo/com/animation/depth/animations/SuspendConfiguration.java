package jaygoo.com.animation.depth.animations;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/23
 * 描    述:
 * ================================================
 */
public class SuspendConfiguration {

    private float translationY = 0;
    private long duration = 1600;
    public long getDuration() {
        return duration;
    }

    public SuspendConfiguration setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    float getTranslationY() {
        return translationY;
    }

    public SuspendConfiguration setTranslationY(float translationY) {
        this.translationY = translationY;
        return this;
    }
}
