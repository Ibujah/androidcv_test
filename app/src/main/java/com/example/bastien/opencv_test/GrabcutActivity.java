package com.example.bastien.opencv_test;

import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

public class GrabcutActivity extends AppCompatActivity {

    public static Mat s_img;

    private Bitmap m_imgbit;
    private ImageView m_imgview;
    private Mat m_img, m_mask, m_bgdModel, m_fgdModel;
    private Rect m_rect;
    private int m_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabcut);

        m_img = s_img.clone();
        m_mask = new Mat(s_img.rows(),s_img.cols(),CvType.CV_8U,new Scalar(Imgproc.GC_PR_BGD));
        m_rect = new Rect(0,0,s_img.cols(),s_img.rows());
        m_bgdModel = new Mat();
        m_fgdModel = new Mat();
        m_mode = Imgproc.GC_INIT_WITH_MASK;

        m_imgbit = Bitmap.createBitmap(m_img.cols(), m_img.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m_img, m_imgbit);

        m_imgview = (ImageView) findViewById(R.id.imageView);
        m_imgview.setImageBitmap(m_imgbit);
        m_imgview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
                    float offsetX = ((float)(v.getRight() - v.getLeft()) - (float)m_img.cols()/(float)m_img.rows()*(float)(v.getBottom() - v.getTop()))/(float)2.0;
                    Point ctr = new Point((event.getX() - offsetX)/(v.getBottom() - v.getTop())*m_img.rows(),
                                          (event.getY() - v.getTop())/(v.getBottom() - v.getTop())*m_img.rows());
                    if(((CheckBox) findViewById(R.id.checkIn)).isChecked()) {
                        Imgproc.circle(m_img, ctr,5,new Scalar(0, 255, 0), -1);
                        Imgproc.circle(m_mask,ctr,5,new Scalar(Imgproc.GC_FGD),-1);
                    }
                    else {
                        Imgproc.circle(m_img, ctr,5,new Scalar(255, 0, 0), -1);
                        Imgproc.circle(m_mask,ctr,5,new Scalar(Imgproc.GC_BGD),-1);
                    }
                    Utils.matToBitmap(m_img, m_imgbit);
                    m_imgview.setImageBitmap(m_imgbit);
                }
                return true;
            }
        });

        Button grabbut = (Button) findViewById(R.id.GrabcutButton);
        grabbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Imgproc.grabCut(s_img,m_mask,m_rect,m_bgdModel,m_fgdModel,1,m_mode);
                m_mode = Imgproc.GC_EVAL;
                s_img.copyTo(m_img);
                int size = (int) (m_mask.total() * m_mask.channels());
                byte[] temp = new byte[size];
                byte[] temp2 = new byte[size*3];
                m_mask.get(0, 0, temp);
                m_img.get(0, 0, temp2);
                for (int i = 0; i < size; i++) {
                    if((temp[i] & 1) != 0) {
                        temp2[i*3 + 1] = (byte)255;
                    }
                    else {
                        temp2[i*3 + 0] = (byte)255;
                    }
                }
                m_img.put(0,0,temp2);

                Utils.matToBitmap(m_img, m_imgbit);
                m_imgview.setImageBitmap(m_imgbit);
            }
        });
    }
}
