#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

extern "C" {
JNIEXPORT void JNICALL Java_com_example_bastien_opencv_test_GrabcutActivity_Grabcut(JNIEnv*, jobject, jlong addrRgba);

JNIEXPORT void JNICALL Java_com_example_bastien_opencv_test_GrabcutActivity_Grabcut(JNIEnv*, jobject, jlong addrRgba)
{
    cv::Mat& mRgb = *(cv::Mat*)addrRgba;
    cv::circle(mRgb, cv::Point(mRgb.cols/2, mRgb.rows/2), 50, cv::Scalar(255,0,0,255),3);
}
}
