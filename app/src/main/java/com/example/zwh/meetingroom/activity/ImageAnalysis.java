package com.example.zwh.meetingroom.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebHistoryItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.zwh.meetingroom.R;
import com.example.zwh.meetingroom.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * created at 2019/2/17 22:36 by wenhaoz
 */

/***
 * Step1：调用FaceEngine的active方法激活设备，一个设备安装后仅需激活一次，卸载重新安装后需要重新激活。
 * Step2：调用FaceEngine的init方法初始化SDK，初始化成功后才能进一步使用SDK的功能。
 * Step3：调用FaceEngine的detectFaces方法进行图像数据或预览数据的人脸检测，若检测成功，则可得到一个人脸列表。（初始化时combineMask需要ASF_FACE_DETECT）
 * Step4：调用FaceEngine的extractFaceFeature方法可对图像中指定的人脸进行特征提取。（初始化时combineMask需要ASF_FACE_RECOGNITION）
 * Step5：调用FaceEngine的compareFaceFeature方法可对传入的两个人脸特征进行比对，获取相似度。（初始化时combineMask需要ASF_FACE_RECOGNITION）
 * Step6：调用FaceEngine的process方法，传入不同的combineMask组合可对Age、Gender、Face3Dangle、Liveness进行检测，传入的combineMask的任一属性都需要在init时进行初始化。
 * Step7：调用FaceEngine的getAge、getGender、getFace3Dangle、getLiveness方法可获取年龄、性别、三维角度、活体检测结果，且每个结果在获取前都需要在process中进行处理。
 * Step8：调用FaceEngine的unIn0
 * .it方法销毁引擎。在init成功后如不unInit会导致内存泄漏。
 *
 */

public class ImageAnalysis extends AppCompatActivity {
    private static final String TAG = "ImageAnalysis";
    private Button start_camera;
    private FaceEngine faceEngine;
    /***
     *检查faceEngine能否初始化成功的标志
     */
    private int faceEngineCode = -1;
    /***
     *被处理的图片
     */
    private Bitmap bitmap;
    private byte[] bgr24;
    private int width;
    private int height;
    private List<FaceInfo> faceInfoList;
    private List<AgeInfo> ageInfoList;
    private List<GenderInfo> genderInfoList;
    private List<LivenessInfo> livenessInfoList;
    private List<Face3DAngle> face3DAngleList;
    private FaceFeature[] faceFeatures;
    private int[] extractFaceFeatureCodes;
    private Uri imageUri;
    private int REQUEST_CAMERA = 1;
    private ImageView pre_picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_imageanalysis);
        pre_picture = (ImageView) findViewById(R.id.pre_picture);
        start_camera = (Button) findViewById(R.id.start_camera);
        initEngine();
        startCamera();
    }


    /***
     *完成相机启动的一系列工作
     *@return void
     *@author wenhaoz
     *created at 2019/2/17 21:48
     */
    private void startCamera() {
        File file = new File(getExternalCacheDir(), "login.jpg");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.example.zwh.meetingroom.fileprovider", file);
        } else {
            imageUri = Uri.fromFile(file);
        }
        Intent openCamera = new Intent("android.media.action.IMAGE_CAPTURE");
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCamera, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                Log.d(TAG, "onActivityResult: " + resultCode);
                loadImage(getExternalCacheDir() + "/login.jpg");
                processImage(getExternalCacheDir() + "/login.jpg");
                break;
        }
    }

    private void loadImage(final String path) {
        Glide.with(this).load(path).into(pre_picture);
    }


    /***
     *初始化FaceEngine引擎
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:05
     */
    public void initEngine() {
        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_HIGHER_EXT, 16, 10, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
        if (faceEngineCode == ErrorInfo.MOK) {
            Toast.makeText(ImageAnalysis.this, "初始化引擎成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ImageAnalysis.this, "初始化引擎失败" + faceEngineCode, Toast.LENGTH_SHORT).show();
        }
    }

    /***
     *销毁FaceEngine引擎
     *@param
     *@return
     *@author wenhaoz
     *created at 2019/2/18 0:05
     */
    public void unInitEngine() {
        if (faceEngine != null) {
            faceEngineCode = faceEngine.unInit();
            faceEngine = null;
            Log.i(TAG, "unInitEngine: " + faceEngineCode);
        }
    }

    /***
     *图片处理以及主要的逻辑处理
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:14
     * @param path
     */
    public void processImage(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //处理原图
                ImageConvert();
                //进行人脸检测,创建人脸储存信息集合
                sovleFaceMessage();
                //三大信息
                getMoreInfo();
                //人脸特征数据
                getFaceFeature();
            }
        }).start();
    }

    /***
     *进行人脸检测,创建人脸储存信息集合
     *@return void
     *@author wenhaoz
     *created at 2019/2/24 16:17
     */
    public void sovleFaceMessage() {

        faceInfoList = new ArrayList<>();
        int code = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
        if (code == ErrorInfo.MOK) {
            Log.d(TAG, "sovleFaceMessage: 人脸信息获取成功");
        } else {
            Log.d(TAG, "sovleFaceMessage:人脸信息获取失败 ");
        }

    }


    /***
     *原图像的处理
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:24
     */
    private void ImageConvert() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inJustDecodeBounds = true;
//        bitmap = BitmapFactory.decodeFile(getExternalCacheDir() + "/login.jpg", opt);
//
//        // 获取到这个图片的原始宽度和高度
//        int picWidth = opt.outWidth;
//        int picHeight = opt.outHeight;
//
//        // 获取屏的宽度和高度
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        int screenWidth = display.getWidth();
//        int screenHeight = display.getHeight();
//
//        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 4;
//
//        // 根据屏的大小和图片大小计算出缩放比例
//        if (picWidth > picHeight) {
//            if (picWidth > screenWidth) opt.inSampleSize = picWidth / screenWidth;
//        } else {
//            if (picHeight > screenHeight) opt.inSampleSize = picHeight / screenHeight;
//        }


        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(getExternalCacheDir() + "/login.jpg", opt);

        Bitmap mbitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        /***
         *使用imageUtil工具类转换为方法中可识别的位图格式
         */
        mbitmap = ImageUtil.alignBitmapForBgr24(mbitmap);
        /***
         *得到图像的宽高
         */
        width = mbitmap.getWidth();
        height = mbitmap.getHeight();
        /***
         *bitmap转为bgr数据
         */
        bgr24 = ImageUtil.bitmapToBgr(mbitmap);
    }

    /***
     *得到人脸特征数据，为人脸对比做准备
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:53
     */
    private void getFaceFeature() {
        faceFeatures=new FaceFeature[faceInfoList.size()];
        extractFaceFeatureCodes=new int[faceInfoList.size()];
        for (int i = 0; i < faceInfoList.size(); i++) {
            faceFeatures[i] = new FaceFeature();
            extractFaceFeatureCodes[i] = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(i), faceFeatures[i]);
            if (extractFaceFeatureCodes[i] != ErrorInfo.MOK) {
                Log.d(TAG, "getFaceFeature:人脸特征信息提取失败 ");
            } else {
                Log.d(TAG, "getFaceFeature:人脸特征信息提取成功 ");
            }
        }
    }

    /***
     *得到人脸的年龄，性别，是否为活体信息
     */
    private void getMoreInfo() {
        Log.d(TAG, "getMoreInfo: "+faceInfoList.size() );
        if (faceInfoList.size() > 0) {
            int faceProcessCode = faceEngine.process(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList,
                    FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
            if(faceProcessCode ==ErrorInfo.MOK){
                Log.d(TAG, "getMoreInfo:face处理成功 ");
            }else{
                Log.d(TAG, "getMoreInfo:face处理失败 "+faceProcessCode);
            }

            ageInfoList = new ArrayList<AgeInfo>();
            genderInfoList = new ArrayList<GenderInfo>();
            face3DAngleList = new ArrayList<Face3DAngle>();
            livenessInfoList = new ArrayList<LivenessInfo>();
            int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
            int ageCode = faceEngine.getAge(ageInfoList);
            int genderCode = faceEngine.getGender(genderInfoList);
            int livenessCode = faceEngine.getLiveness(livenessInfoList);
            if ((ageCode | genderCode | livenessCode | face3DAngleCode) != ErrorInfo.MOK) {
                Log.d(TAG, "getMoreInfo: 三大信息未获得"+face3DAngleCode+ageCode+genderCode+livenessCode);
            } else {
                for (int i = 0; i < ageInfoList.size(); i++) {
                    Log.d(TAG, ageInfoList.get(i).getAge() + "   " + genderInfoList.get(i).getGender() + "   " + face3DAngleList.get(i).getStatus()+" "+face3DAngleList.get(i).getRoll() + "   " + livenessInfoList.get(i).getLiveness());
                }
            }
        } else {
            Log.d(TAG, "getMoreInfo:未检测到人脸 ");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //刷新图片的显示
        Glide.with(this).clear(pre_picture);
    }


    @Override
    protected void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        unInitEngine();
        super.onDestroy();
    }


}
