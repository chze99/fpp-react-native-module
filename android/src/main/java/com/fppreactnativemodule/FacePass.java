package com.fppreactnativemodule;

import androidx.annotation.NonNull;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.Unit;

import java.util.AbstractMap.SimpleEntry;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableArray;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.CharsetUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.facebook.react.bridge.Arguments;

import android.database.sqlite.SQLiteDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Base64;

import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassConfig;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageRotation;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassModel;
import mcv.facepass.types.FacePassPose;
import mcv.facepass.types.FacePassRCAttribute;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassAgeGenderResult;
import mcv.facepass.types.FacePassRecognitionState;
import mcv.facepass.types.FacePassTrackOptions;

import com.fppreactnativemodule.camera.CameraManager;
import com.fppreactnativemodule.camera.CameraPreview;
import com.fppreactnativemodule.camera.CameraPreviewData;
import com.fppreactnativemodule.utils.FileUtil;

@ReactModule(name = FacePass.NAME)
public class FacePass extends ReactContextBaseJavaModule
    implements LifecycleEventListener, ActivityEventListener {
  public static final String NAME = "FacePass";
  private final ReactApplicationContext context;
  Activity activity = getCurrentActivity();

  public FacePass(ReactApplicationContext context) {
    super(context);
    context.addLifecycleEventListener(this);
    context.addActivityEventListener(this);
    this.context = context;

  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private static final String DEBUG_TAG = "FacePassDemo";

  /* 程序所需权限 ：相机 文件存储 网络访问 */
  private static final int PERMISSIONS_REQUEST = 1;
  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
  private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
  private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
  private String[] Permission = new String[] { PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE,
      PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE };

  /* SDK 实例对象 */
  FacePassHandler mFacePassHandler;

  /* 判断程序是否有所需权限 android22以上需要自申请权限 */
  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
          context.checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
          context.checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
          context.checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
          context.checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  /* 请求程序所需权限 */
  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      activity.requestPermissions(Permission, PERMISSIONS_REQUEST);
    }
  }

  // @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      boolean granted = true;
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED)
          granted = false;
      }
      if (!granted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
          if (!activity.shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_READ_STORAGE)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_WRITE_STORAGE)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_INTERNET)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_ACCESS_NETWORK_STATE)) {
            Toast.makeText(context.getApplicationContext(), "需要开启摄像头网络文件存储权限", Toast.LENGTH_SHORT).show();
          }
      } else {
        // initFacePassSDK();
      }
    }
  }

  private void adaptFrameLayout() {
    SettingVar.isButtonInvisible = false;
    SettingVar.iscameraNeedConfig = false;
  }

  private void initToast() {
    SettingVar.isButtonInvisible = false;
  }

  private static final int REQUEST_CODE_CHOOSE_PICK = 1;

  @Override
  public void onNewIntent(Intent intent) {
  }


  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    Log.v("onActivityResult", "Called");
    switch (requestCode) {
      // 从相册选取照片后读取地址
      case REQUEST_CODE_CHOOSE_PICK:
        if (resultCode == -1) {
          if (pickerSuccessCallback != null) {
            if (resultCode == Activity.RESULT_CANCELED) {
              pickerCancelCallback.invoke("IMAGE_SELECT_CANCELED_ERROR");
            } else if (resultCode == Activity.RESULT_OK) {
              String path = "";

              Uri uri = data.getData();
              String[] pojo = { MediaStore.Images.Media.DATA };
              CursorLoader cursorLoader = new CursorLoader(context, uri, pojo, null, null, null);
              Cursor cursor = cursorLoader.loadInBackground();
              if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(pojo[0]));
              }
              if (!TextUtils.isEmpty(path) && "file".equalsIgnoreCase(uri.getScheme())) {
                path = uri.getPath();
              }
              if (TextUtils.isEmpty(path)) {
                try {
                  path = FileUtil.getPath(activity.getApplicationContext(), uri);
                } catch (Exception e) {
                }
              }
              if (TextUtils.isEmpty(path)) {
                pickerCancelCallback.invoke("IMAGE_SELECT_FAIL_ERROR");
                return;
              }
              if (!TextUtils.isEmpty(path)) {
                try {
                  pickerSuccessCallback.invoke(path);
                } catch (Exception e) {
                  pickerCancelCallback.invoke(e.toString());
                }
              }
            }
          }
        }
        break;
    }
  };

  public void initData() {
    if (!hasPermission()) {
      requestPermission();
    } else {
      initFacePassSDK();
    }
      
      SharedPreferences temp = activity.getSharedPreferences(
        "fppreactnative", Context.MODE_PRIVATE);
      String parameter=temp.getString("parameters","");
      if (parameter != "") {
        try {
          JSONObject parameters = new JSONObject(parameter);

          Log.v("PARAMETERS", Boolean.toString(parameters.getBoolean("livenessEnabled")));
          initFaceHandler(parameters.getInt("rcAttributeAndOcclusionMode"),
              (float) parameters.getDouble("searchThreshold"),
              (float) parameters.getDouble("livenessThreshold"),
              parameters.getBoolean("livenessEnabled"),
              parameters.getBoolean("rgbIrLivenessEnabled"),
              (float) parameters.getDouble("poseThresholdRoll"),
              (float) parameters.getDouble("poseThresholdPitch"),
              (float) parameters.getDouble("poseThresholdYaw"),
              (float) parameters.getDouble("blurThreshold"),
              (float) parameters.getDouble("lowBrightnessThreshold"),
              (float) parameters.getDouble("highBrightnessThreshold"),
              (float) parameters.getDouble("brightnessSTDThreshold"),
              parameters.getInt("faceMinThreshold"),
              parameters.getInt("retryCount"),
              parameters.getBoolean("smileEnabled"),
              parameters.getBoolean("maxFaceEnabled"),
              (float) parameters.getDouble("FacePoseThresholdPitch"),
              (float) parameters.getDouble("FacePoseThresholdRoll"),
              (float) parameters.getDouble("FacePoseThresholdYaw"),
              (float) parameters.getDouble("FaceBlurThreshold"),
              (float) parameters.getDouble("FaceLowBrightnessThreshold"),
              (float) parameters.getDouble("FaceHighBrightnessThreshold"),
              (float) parameters.getDouble("FaceBrightnessSTDThreshold"),
              parameters.getInt("FaceFaceMinThreshold"),
              parameters.getInt("FaceRcAttributeAndOcclusionMode"));

          // Log.d("TAG", "impl: " + setting);
        } catch (JSONException e) {
          Log.d("JSONERROR", e.toString());
        }
      } else {
        Log.v("PARAMETERS", "No parameter");

        initFaceHandler(1, 69, 55, true, false,
            30, 30, 30, 0.8f, 30, 210, 60,
            100, 2, false, true, 35, 35, 35, 0.7f,
            70, 220, 60, 100, 2);

      }
    
          }

  private void initFacePassSDK() {
    FacePassHandler.initSDK(activity.getApplicationContext());
    // FacePassHandler.getAuth(authIP, apiKey, apiSecret,true);
    Log.d("FacePassDemo", FacePassHandler.getVersion());
  }

  private boolean ageGenderEnabledGlobal;

  private void initFaceHandler(int rcAttributeAndOcclusionMode, float searchThreshold, float livenessThreshold,
      boolean livenessEnabled, boolean rgbIrLivenessEnabled,
      float poseThresholdRoll, float poseThresholdPitch, float poseThresholdYaw, float blurThreshold,
      float lowBrightnessThreshold, float highBrightnessThreshold, float brightnessSTDThreshold,
      int faceMinThreshold, int retryCount, boolean smileEnabled, boolean maxFaceEnabled, float FacePoseThresholdPitch,
      float FacePoseThresholdRoll, float FacePoseThresholdYaw, float FaceBlurThreshold,
      float FaceLowBrightnessThreshold, float FaceHighBrightnessThreshold, float FaceBrightnessSTDThreshold,
      int FaceFaceMinThreshold, int FaceRcAttributeAndOcclusionMode) {

    new Thread() {
      @Override
      public void run() {
        while (true
        // &&
        // !activity.isFinishing()
        ) {
          while (FacePassHandler.isAvailable()) {
            Log.d(DEBUG_TAG, "start to build FacePassHandler");
            FacePassConfig config;
            try {
              /* 填入所需要的模型配置 */
              config = new FacePassConfig();
              config.poseBlurModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "attr.pose_blur.arm.190630.bin");

              config.livenessModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "liveness.CPU.rgb.G.bin");

              config.searchModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "feat2.arm.K.v1.0_1core.bin");

              config.detectModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "detector.arm.G.bin");
              config.detectRectModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "detector_rect.arm.G.bin");
              config.landmarkModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "pf.lmk.arm.E.bin");

              config.rcAttributeModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "attr.RC.arm.F.bin");
              config.occlusionFilterModel = FacePassModel.initModel(activity.getApplicationContext().getAssets(),
                  "attr.occlusion.arm.20201209.bin");
            

              /* 送识别阈值参数 */
              config.rcAttributeAndOcclusionMode = rcAttributeAndOcclusionMode;
              config.searchThreshold = searchThreshold;
              config.livenessThreshold = livenessThreshold;
              config.livenessEnabled = livenessEnabled;
              config.rgbIrLivenessEnabled = rgbIrLivenessEnabled;
              ageGenderEnabledGlobal = (config.ageGenderModel != null);

              config.poseThreshold = new FacePassPose(poseThresholdRoll, poseThresholdPitch, poseThresholdYaw);
              config.blurThreshold = blurThreshold;
              config.lowBrightnessThreshold = lowBrightnessThreshold;
              config.highBrightnessThreshold = highBrightnessThreshold;
              config.brightnessSTDThreshold = brightnessSTDThreshold;
              config.faceMinThreshold = faceMinThreshold;
              config.retryCount = 2;
              config.smileEnabled = false;
              config.maxFaceEnabled = true;

              config.fileRootPath = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
              /* 创建SDK实例 */
              mFacePassHandler = new FacePassHandler(config);

              Log.v("mFacePassHandler", config.toString());
              /* 入库阈值参数 */
              FacePassConfig addFaceConfig = mFacePassHandler.getAddFaceConfig();
              addFaceConfig.poseThreshold.pitch = FacePoseThresholdPitch;
              addFaceConfig.poseThreshold.roll = FacePoseThresholdRoll;
              addFaceConfig.poseThreshold.yaw = FacePoseThresholdYaw;
              addFaceConfig.blurThreshold = FaceBlurThreshold;
              addFaceConfig.lowBrightnessThreshold = FaceLowBrightnessThreshold;
              addFaceConfig.highBrightnessThreshold = FaceHighBrightnessThreshold;
              addFaceConfig.brightnessSTDThreshold = FaceBrightnessSTDThreshold;
              addFaceConfig.faceMinThreshold = FaceFaceMinThreshold;
              addFaceConfig.rcAttributeAndOcclusionMode = FaceRcAttributeAndOcclusionMode;
              mFacePassHandler.setAddFaceConfig(addFaceConfig);
              FacePassHandlerHolder.setMyObject(mFacePassHandler);
            } catch (FacePassException e) {
              e.printStackTrace();

              Log.d("FacePassException", e.toString());
              Log.d(DEBUG_TAG, "FacePassHandler is null");
              return;
            }
            return;
          }
          try {
            /* 如果SDK初始化未完成则需等待 */
            sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
  }

  private Callback pickerSuccessCallback;
  private Callback pickerCancelCallback;

  @ReactMethod
  public void select_image(Callback successCallback, Callback cancelCallback) {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      cancelCallback.invoke("ACTIVITY_NOT_EXIST_ERROR");
      return;
    }

    pickerSuccessCallback = successCallback;
    pickerCancelCallback = cancelCallback;

    Intent intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
    intentFromGallery.setType("image/*"); // 设置文件类型
    intentFromGallery.addCategory(Intent.CATEGORY_OPENABLE);
    Log.v("intentfrom", intentFromGallery.toString());
    try {
      currentActivity.startActivityForResult(intentFromGallery, REQUEST_CODE_CHOOSE_PICK);
    } catch (Exception e) {
      cancelCallback.invoke(e);
    }
  };

  @ReactMethod
  public void sendDataToReactNative(String base64, String name, Float livenessScore) {
    // Create a WritableMap to hold the data
    WritableMap params = Arguments.createMap();
    params.putString("image", base64);
    params.putString("name", name);
    params.putDouble("livenessScore", (double) livenessScore);
    // Get the React Native event emitter and send the data as an event
    getReactApplicationContext()
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit("FaceDetectedEvent", params);
  }

  @ReactMethod
  public void addListener(String eventName) {

  }

  @ReactMethod
  public void removeListeners(Integer count) {

  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String addFace(String imagePath, Callback success, Callback failure) {

    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return "";
    }
    if (TextUtils.isEmpty(imagePath)) {
      failure.invoke("INVALID_IMAGE_PATH_ERROR");
      return "";
    }

    File imageFile = new File(imagePath);
    if (!imageFile.exists()) {
      failure.invoke("IMAGE_NOT_EXIST_ERROR");
      return "";
    }

    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

    try {
      FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);
      if (result != null) {
        if (result.result == 0) {
          success.invoke("FACE_ADDED_SUCCESSFULLY");
          return new String(result.faceToken);
        } else if (result.result == 1) {
          failure.invoke("NO_FACE_DETECTED_IN_IMAGE_ERROR");
          return "";

        } else {
          failure.invoke("FACE_IMAGE_QUALITY_ERROR");
          return "";

        }
      }
    } catch (FacePassException e) {
      e.printStackTrace();
      failure.invoke(e.getMessage());
      return "";

    }
    return "";

  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getFace(String faceTokenEt, Callback success, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return "";
    }
    try {
      byte[] faceToken = faceTokenEt.getBytes();
      Bitmap bitmap = mFacePassHandler.getFaceImage(faceToken);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
      byte[] byteArray = byteArrayOutputStream.toByteArray();
      String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
      return encoded;
    } catch (Exception e) {
      e.printStackTrace();
      failure.invoke(e.getMessage());
      return "";
    }
  }

  @ReactMethod
  public void deleteFace(String faceTokenEt, String groupNameEt, Callback success, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }
    boolean b = false;
    try {
      byte[] faceToken = faceTokenEt.getBytes();
      b = mFacePassHandler.deleteFace(faceToken);
      if (b) {
        String groupName = groupNameEt;
        if (TextUtils.isEmpty(groupName)) {
          failure.invoke("NULL_GROUPNAME_ERROR");
          return;
        }
        byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
        List<String> faceTokenList = new ArrayList<>();
        if (faceTokens != null && faceTokens.length > 0) {
          for (int j = 0; j < faceTokens.length; j++) {
            if (faceTokens[j].length > 0) {
              faceTokenList.add(new String(faceTokens[j]));
            }
          }

        }
      }
    } catch (FacePassException e) {
      e.printStackTrace();
      failure.invoke(e.getMessage());
    }
    String result = b ? "success " : "failed";
    if (b) {
      success.invoke("FACE_DELETE_SUCCESS");
    } else {
      toast("Fail to delete face");
    }
    Log.d(DEBUG_TAG, "delete face  " + result);
  }

  @ReactMethod
  public void bindGroup(String faceTokenEt, String groupNameEt, Callback success, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }

    byte[] faceToken = faceTokenEt.getBytes();
    String groupName = groupNameEt;
    if (TextUtils.isEmpty(groupName)) {
      failure.invoke("GROUP_NAME_IS_NULL_ERROR");
      return;
    } else if (faceToken == null || faceToken.length == 0) {
      failure.invoke("FACETOKEN_IS_NULL_ERROR");
      return;
    }
    try {
      boolean b = mFacePassHandler.bindGroup(groupName, faceToken);
      String result = b ? "success " : "failed";
      if (b) {
        success.invoke("BIND_GROUP_SUCCESS");
      } else {
        failure.invoke("BIND_GROUP_FAILED");
      }
    } catch (Exception e) {
      e.printStackTrace();
      failure.invoke(e.getMessage());
    }

  }

  @ReactMethod
  public void getGroupInfo(String groupNameEt, Callback success, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    List<String> faceTokenList = new ArrayList<>();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
    }
    String groupName = groupNameEt;
    if (TextUtils.isEmpty(groupName)) {
      failure.invoke("GROUP_NAME_NOT_EXIST_ERROR");
    }
    try {
      byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
      if (faceTokens != null && faceTokens.length > 0) {
        for (int j = 0; j < faceTokens.length; j++) {
          if (faceTokens[j].length > 0) {
            faceTokenList.add(new String(faceTokens[j]));
          }
        }

      }
      WritableArray array = Arguments.fromList(faceTokenList);
      success.invoke(array);

    } catch (Exception e) {
      e.printStackTrace();
      toast("Error getting local group info");

    }

  }

  @ReactMethod
  public void unbindFace(String StringfaceToken, String groupNameEt, Callback returns, Callback success,
      Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }

    String groupName = groupNameEt;
    if (TextUtils.isEmpty(groupName)) {
      failure.invoke("GROUP_NAME_IS_NULL_ERROR");
      return;
    }
    try {
      byte[] faceToken = StringfaceToken.getBytes();
      boolean b = mFacePassHandler.unBindGroup(groupName, faceToken);
      String result = b ? "success " : "failed";
      if (b) {
        byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
        List<String> faceTokenList = new ArrayList<>();
        if (faceTokens != null && faceTokens.length > 0) {
          for (int j = 0; j < faceTokens.length; j++) {
            if (faceTokens[j].length > 0) {
              faceTokenList.add(new String(faceTokens[j]));
            }
          }

        }
        success.invoke("FACE_UNBIND_SUCCESS");
        WritableArray array = Arguments.fromList(faceTokenList);
        returns.invoke(array);
      } else {
        failure.invoke("FACE_UNBIND_FAILED");
      }
    } catch (Exception e) {
      e.printStackTrace();
      failure.invoke(e.toString());
    }

  }

  @ReactMethod
  public void getAllGroup(Callback success, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }
    try {
      String[] groups = mFacePassHandler.getLocalGroups();
      if (groups != null && groups.length > 0) {
        List<String> data = Arrays.asList(groups);
        WritableArray array = Arguments.fromList(data);
        success.invoke(array);
      } else {
        failure.invoke("EMPTY_LOCAL_GROUP_ERROR");
      }
    } catch (FacePassException e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void createGroup(String groupNameEt, Callback success, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }
    String groupName = groupNameEt;
    if (TextUtils.isEmpty(groupName)) {
      failure.invoke("EMPTY_GROUP_INPUT_ERROR");
      return;
    }
    boolean isSuccess = false;
    try {
      isSuccess = mFacePassHandler.createLocalGroup(groupName);
    } catch (FacePassException e) {
      e.printStackTrace();
    }
    if (isSuccess) {
      success.invoke("GROUP_CREATION_SUCCESS");
    } else {
      failure.invoke("GROUP_CREATION_FAILED");
    }

  }

  @ReactMethod
  public void deleteGroup(String groupNameEt, Callback successCallback, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();

    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }
    String groupName = groupNameEt;
    boolean isSuccess = false;
    try {
      isSuccess = mFacePassHandler.deleteLocalGroup(groupName);
    } catch (FacePassException e) {
      e.printStackTrace();
    }
    if (isSuccess) {
      try {
        String[] groups = mFacePassHandler.getLocalGroups();
        if (groups != null) {
          WritableArray array = Arguments.fromList(Arrays.asList(groups));
          successCallback.invoke(array);
        }
      } catch (FacePassException e) {
        e.printStackTrace();
      }
    } else {
      failure.invoke("GROUP_DELETION_FAILED");
    }
  }

  private void toast(String msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onHostResume() {

  }

  @Override
  public void onHostPause() {

  }

  @Override
  public void onHostDestroy() {

  }

}
