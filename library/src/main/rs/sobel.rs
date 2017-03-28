#pragma version(1)
#pragma rs java_package_name(jaygoo.com.sobelbyrsdemo)

#define MSG_TAG "SobelDetectingFromRenderScript"


rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

int *gOutPixels;
int count = 0;
int mImageWidth;
int mImageHeight;

//加粗边缘的左侧像素点
int strongPixels = 6;

//识别系数，系数越高轮廓越少，噪点越少
float coefficient = 0.435f;


static int argb(int alpha, int red, int green, int blue) {
    return (alpha << 24) | (red << 16) | (green << 8) | blue;
}

void setStrong(int i,int j){
    int x;
    for( x = 0; x< strongPixels; x++){
        if(i - x >= 0 && j-x >= 0){
            gOutPixels[(i - x ) * mImageWidth + (j-x)] = argb(255,33,150,243);
        }
    }
}

void root(const int *v_in, int *v_out) {
	if (count != 0) return;
 	count++;

    // 判断是否为边缘点的阈值
    int threshold = -9999999;

	for (int i = 1; i < mImageHeight; i++) {
		for (int j = 1; j < mImageWidth; j++) {
			int pix = v_in[i * mImageWidth + j];
			int Past_X = v_in[i * mImageWidth + (j - 1)];
			int Past_Y = v_in[(i - 1) * mImageWidth + j];
			
			int dev_X = (((Past_X >> 16) & 0xff - (pix >> 16) & 0xff) + ((Past_X >> 8) & 0xff - (pix >> 8) & 0xff) + (Past_X & 0xff - pix & 0xff)) / 3;
			int dev_Y = (((Past_Y >> 16) & 0xff - (pix >> 16) & 0xff) + ((Past_Y >> 8) & 0xff - (pix >> 8) & 0xff) + (Past_Y & 0xff - pix & 0xff)) / 3;
			
			gOutPixels[i * mImageWidth + j] = sqrt((float) dev_X * dev_X + (float) dev_Y * dev_Y);

			if (threshold < gOutPixels[j * mImageWidth + i]) {
                threshold = gOutPixels[j * mImageWidth + i];
            }
		}
	}

	rsDebug("threshold = ", threshold);
	rsDebug("color = ", argb(255,33,150,243));

	for (int i = 1; i < mImageHeight; i++) {
		for (int j = 1; j < mImageWidth; j++) {
			if (gOutPixels[i * mImageWidth + j] > threshold * coefficient) {
			//如果大于阙值，则保存灰度图该点的像素
				gOutPixels[i * mImageWidth + j] = argb(255,33,150,243);
				setStrong(i,j);

			} else{
				gOutPixels[i * mImageWidth + j] = 0 & 0xFF;
			}
		}
	}

}


void init() {
	rsDebug("Called init", rsUptimeMillis());
}

void compute() {
	rsForEach(gScript, gIn, gOut);
}