package com.example.zwh.meetingroom.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

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
 * Step8：调用FaceEngine的unInit方法销毁引擎。在init成功后如不unInit会导致内存泄漏。
 *
 */

public class ImageAnalysis {
    private Context mContext;
    private static final String TAG = "ImageAnalysis";
    /**
     * 请求权限的请求码
     */
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private FaceEngine faceEngine;
    private int activeCode;
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

    public ImageAnalysis(Context context) {
        this.mContext = context;
        faceEngine = new FaceEngine();
    }

    /***
     *初始化FaceEngine引擎
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:05
     */
    public void initEngine() {
        faceEngineCode = faceEngine.init(mContext, FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_ONLY, 16, 20, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
        if (faceEngineCode == ErrorInfo.MOK) {
            Toast.makeText(mContext, "初始化引擎成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "初始化引擎失败", Toast.LENGTH_SHORT).show();
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
        faceEngineCode = faceEngine.unInit();
        if (faceEngineCode == ErrorInfo.MOK) {
            Toast.makeText(mContext, "销毁引擎成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "销毁引擎失败", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     *图片处理以及主要的逻辑处理
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:14
     */
    public void processImage(final String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //处理原图
                ImageConvert(uri);
                //进行人脸检测,创建人脸储存信息集合
                faceInfoList = new ArrayList<FaceInfo>();
                int code = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
                if (code == ErrorInfo.MOK) {
                    Toast.makeText(mContext, "人脸信息获取成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "人脸信息获取失败", Toast.LENGTH_SHORT).show();
                }
                //三大信息
                getMoreInfo();
                //人脸特征数据
                getFaceFeature();
            }
        }).start();
        unInitEngine();
    }

    /***
     *得到人脸特征数据，为人脸对比做准备
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:53
     */
    private void getFaceFeature() {
        for (int i = 0; i < faceInfoList.size(); i++) {
            faceFeatures[i] = new FaceFeature();
            extractFaceFeatureCodes[i] = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(i), faceFeatures[i]);
            if (extractFaceFeatureCodes[i] != ErrorInfo.MOK) {
                Toast.makeText(mContext, "人脸特征信息提取失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "人脸特征信息提取成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /***
     *得到人脸的年龄，性别，是否为活体信息
     */
    private void getMoreInfo() {
        if (faceInfoList.size() > 0) {
            ageInfoList = new ArrayList<AgeInfo>();
            genderInfoList = new ArrayList<GenderInfo>();
            face3DAngleList = new ArrayList<Face3DAngle>();
            livenessInfoList = new ArrayList<LivenessInfo>();
            int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
            int ageCode = faceEngine.getAge(ageInfoList);
            int genderCode = faceEngine.getGender(genderInfoList);
            int livenessCode = faceEngine.getLiveness(livenessInfoList);
            if ((ageCode | genderCode | livenessCode | face3DAngleCode) != ErrorInfo.MOK) {
                Toast.makeText(mContext, "三大信息未获得", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < ageInfoList.size(); i++) {
                    Log.d(TAG, ageInfoList.get(i).toString() + "   " + genderInfoList.get(i) + "   " + face3DAngleList.get(i) + "   " + livenessInfoList.get(i));
                }
            }
        } else {
            Toast.makeText(mContext, "未检测到人脸", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     *原图像的处理
     *@return void
     *@author wenhaoz
     *created at 2019/2/18 0:24
     */
    private void ImageConvert(String uri) {
        Glide.with(mContext).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                bitmap = resource.copy(Bitmap.Config.ARGB_8888, true);
            }
        });

        /***
         *使用imageUtil工具类转换为方法中可识别的位图格式
         */
        bitmap = ImageUtil.alignBitmapForBgr24(bitmap);
        /***
         *得到图像的宽高
         */
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        /***
         *bitmap转为bgr数据
         */
        bgr24 = ImageUtil.bitmapToBgr(bitmap);
    }

}
