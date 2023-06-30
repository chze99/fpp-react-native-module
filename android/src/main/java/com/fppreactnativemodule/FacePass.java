package com.fppreactnativemodule;

import androidx.annotation.NonNull;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.widget.ImageView;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.facebook.react.bridge.Arguments;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Base64;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

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
import com.q_zheng.QZhengGPIOManager;
import com.q_zheng.QZhengIFManager;
import com.q_zheng.QZGpio;
import com.example.yfaceapi.GPIOManager;
import com.fppreactnativemodule.utils.FileUtil;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.modules.core.PermissionAwareActivity;

import static com.fppreactnativemodule.utils.Helper.getSerialNumber;
import com.facebook.react.bridge.Promise;

@ReactModule(name = FacePass.NAME)
public class FacePass extends ReactContextBaseJavaModule
    implements LifecycleEventListener, ActivityEventListener, PermissionListener  {
  public static final String NAME = "FacePass";
  private final ReactApplicationContext context;
  Activity activity = getCurrentActivity();

  public FacePass(ReactApplicationContext context) {
    super(context);
    Activity activity = getCurrentActivity();
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

  private static final int PERMISSIONS_REQUEST = 1;
  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
  private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
  private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
  private String[] Permission = new String[] { PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE,
      PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE };

  FacePassHandler mFacePassHandler;
  QZhengIFManager qZhengManager;
  private GPIOManager gpioManager;
  Boolean appPaused = false;
  Boolean enableLight = true;
  QZhengGPIOManager QZhengGPIOInstance;
  private boolean ageGenderEnabledGlobal;
  private Callback pickerSuccessCallback;
  private Callback pickerCancelCallback;
  private Callback initSuccessCallback;
  private Callback initFailCallback;

  RequestQueue requestQueue;

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

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PermissionAwareActivity currentActivity = (PermissionAwareActivity) getCurrentActivity();
      currentActivity.requestPermissions(Permission, PERMISSIONS_REQUEST,this);
      Log.v("TESTING1","TEST");
    }
  }
  @Override
  public boolean onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
         Log.v("TESTING2","TEST");

    if (requestCode == PERMISSIONS_REQUEST) {
      boolean granted = true;
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED)
          granted = false;
      }
      if (!granted) {
              Log.v("TESTING3","TEST");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
          if (!activity.shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_READ_STORAGE)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_WRITE_STORAGE)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_INTERNET)
              || !activity.shouldShowRequestPermissionRationale(PERMISSION_ACCESS_NETWORK_STATE)) {
            Toast.makeText(context.getApplicationContext(), "需要开启摄像头网络文件存储权限", Toast.LENGTH_SHORT).show();
          }
      } else {
              Log.v("TESTING4","TEST");

        initFacePassSDK();
      }
    }
  return true;
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

  @ReactMethod
  private void changeLight(String light) {
    QZhengGPIOInstance = QZhengGPIOManager.getInstance(context);
    qZhengManager = new QZhengIFManager(context);
    gpioManager = GPIOManager.getInstance(context);

    // new device
    if (light.equals("white") && enableLight) {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
    } else if (light.equals("red")) {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
    } else if (light.equals("green")) {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
    } else if (light.equals("yellow_white")) {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
    } else if (light.equals("yellow")) {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
    } else if (light.equals("red_white")) {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
    } else if (light.equals("green_white")) {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
    } else {
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
      QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
    }
    // old device
    if (light.equals("white")) {
      gpioManager.pullUpWhiteLight();
      gpioManager.pullDownGreenLight();
      gpioManager.pullDownRedLight();
    } else if (light.equals("red")) {
      gpioManager.pullDownWhiteLight();
      gpioManager.pullDownGreenLight();
      gpioManager.pullUpRedLight();
    } else if (light.equals("green")) {
      gpioManager.pullDownWhiteLight();
      gpioManager.pullUpGreenLight();
      gpioManager.pullDownRedLight();
    } else {
      gpioManager.pullDownWhiteLight();
      gpioManager.pullDownGreenLight();
      gpioManager.pullDownRedLight();
    }
  }

  @ReactMethod
  public void cameraSetting(String setting) {
    if (!setting.isEmpty() && setting != null) {
      Log.v("Settings", setting);

      try {
        JSONObject settings = new JSONObject(setting);
        SettingVar.isSettingAvailable = settings.optBoolean("isSettingAvailable", true);
        SettingVar.isCross = settings.optBoolean("isCross", false);
        SettingVar.faceRotation = settings.optInt("faceRotation", 90);
        SettingVar.cameraPreviewRotation = settings.optInt("cameraPreviewRotation", 270);
        SettingVar.cameraFacingFront = settings.optBoolean("cameraFacingFront", false);
      } catch (JSONException e) {
        Log.d("JSONERROR", e.toString());
              SettingVar.isSettingAvailable = false;
      SettingVar.isCross = false;
      SettingVar.faceRotation = 270;
      SettingVar.cameraPreviewRotation = 90;
      SettingVar.cameraFacingFront = true;
      }
    } else {
      Log.v("Settings", "SETTING is null");

      SettingVar.isSettingAvailable = false;
      SettingVar.isCross = false;
      SettingVar.faceRotation = 270;
      SettingVar.cameraPreviewRotation = 90;
      SettingVar.cameraFacingFront = true;

    }
  }

  @ReactMethod
  public void setDefaultGroupName(String name) {
    if (name != null && !name.isEmpty()) {
      SettingVar.groupName = name;
    }
  }

  @ReactMethod
  public void initData(String parameter,Callback success,Callback fail) {
    if (!hasPermission()) {
      requestPermission();
    } else {
      initFacePassSDK();
    }
    // initFacePassSDK();
    Activity activity = getCurrentActivity();
    initSuccessCallback = success;
    initFailCallback = fail;
    if (!parameter.isEmpty() && parameter != "" && parameter != null) {
      try {
        JSONObject parameters = new JSONObject(parameter);

        SettingVar.searchThreshold = (float) parameters.optDouble("searchThreshold", 69);
        initFaceHandler(parameters.optInt("rcAttributeAndOcclusionMode", 1),
            (float) parameters.optDouble("searchThreshold", 69),
            (float) parameters.optDouble("livenessThreshold", 55),
            parameters.optBoolean("livenessEnabled", true), parameters.optBoolean("rgbIrLivenessEnabled", false),
            (float) parameters.optDouble("poseThresholdRoll", 35),
            (float) parameters.optDouble("poseThresholdPitch", 35),
            (float) parameters.optDouble("poseThresholdYaw", 35),
            (float) parameters.optDouble("blurThreshold", 0.8),
            (float) parameters.optDouble("lowBrightnessThreshold", 30),
            (float) parameters.optDouble("highBrightnessThreshold", 210),
            (float) parameters.optDouble("brightnessSTDThreshold", 80),
            parameters.optInt("faceMinThreshold", 100),
            parameters.optInt("retryCount", 2),
            parameters.optBoolean("smileEnabled", false),
            parameters.optBoolean("maxFaceEnabled", true),
            (float) parameters.optDouble("FacePoseThresholdPitch", 35),
            (float) parameters.optDouble("FacePoseThresholdRoll", 35),
            (float) parameters.optDouble("FacePoseThresholdYaw", 35),
            (float) parameters.optDouble("FaceBlurThreshold", 0.7),
            (float) parameters.optDouble("FaceLowBrightnessThreshold", 70),
            (float) parameters.optDouble("FaceHighBrightnessThreshold", 220),
            (float) parameters.optDouble("FaceBrightnessSTDThreshold", 60),
            parameters.optInt("FaceFaceMinThreshold", 100),
            parameters.optInt("FaceRcAttributeAndOcclusionMode", 2));

      } catch (JSONException e) {
        Log.d("JSONERROR", e.toString());
              initFaceHandler(1, 69, 55, true, false,
          30, 30, 30, 0.8f, 30, 210, 60,
          100, 2, false, true, 35, 35, 35, 0.7f,
          70, 220, 60, 100, 2);
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
    Activity activity = getCurrentActivity();
    FacePassHandler.initSDK(activity.getApplicationContext());
    Log.d("FacePassSDK", FacePassHandler.getVersion());
  }

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

        ) {
          while (FacePassHandler.isAvailable()) {
            Log.d(DEBUG_TAG, "start to build FacePassHandler");
            FacePassConfig config;
            Activity activity = getCurrentActivity();
            try {
              SettingVar.doneInitialize = false;
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
              mFacePassHandler = new FacePassHandler(config);

              Log.v("mFacePassHandler", config.toString());
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
              SettingVar.doneInitialize = true;
              initSuccessCallback.invoke("init success");
            } catch (FacePassException e) {
              e.printStackTrace();
              initFailCallback.invoke("init fail");
              Log.d("FacePassException", e.toString());
              Log.d(DEBUG_TAG, "FacePassHandler is null");
              return;
            }
            return;
          }
          try {
            sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
  }

  @ReactMethod
  public void selectImage(Callback successCallback, Callback cancelCallback) {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      cancelCallback.invoke("ACTIVITY_NOT_EXIST_ERROR");
      return;
    }

    pickerSuccessCallback = successCallback;
    pickerCancelCallback = cancelCallback;

    Intent intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
    intentFromGallery.setType("image/*");
    intentFromGallery.addCategory(Intent.CATEGORY_OPENABLE);
    Log.v("intentfrom", intentFromGallery.toString());
    try {
      currentActivity.startActivityForResult(intentFromGallery, REQUEST_CODE_CHOOSE_PICK);
    } catch (Exception e) {
      cancelCallback.invoke(e);
    }
  };

  @ReactMethod
  public void sendDataToReactNative(String faceToken) {
    WritableMap params = Arguments.createMap();
    params.putString("faceToken", faceToken);
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
  public void addFace(String imagePath, Callback success, Callback failure) {

    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }
    if (TextUtils.isEmpty(imagePath)) {
      failure.invoke("INVALID_IMAGE_PATH_ERROR");
      return;
    }
    Bitmap bitmap;
    try {
      bitmap = getBitmapByUrl(imagePath);
      Log.v("IMAGE path",imagePath.toString());

    } catch (Exception e) {
      Log.v("IMAGE fail",e.toString());
      File imageFile = new File(imagePath);
      if (!imageFile.exists()) {
        failure.invoke("IMAGE_NOT_EXIST_ERROR");
        return;
      } else {
        bitmap = BitmapFactory.decodeFile(imagePath);
      }
    }

    try {
      FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);
      if (result != null) {
        if (result.result == 0) {
          success.invoke(new String(result.faceToken));
        } else if (result.result == 1) {
          failure.invoke("NO_FACE_DETECTED_IN_IMAGE_ERROR");
          return;

        } else {
          failure.invoke("FACE_IMAGE_QUALITY_ERROR");
          return;

        }
      }
    } catch (FacePassException e) {
      e.printStackTrace();
      failure.invoke(e.getMessage());
      return;

    }

  }

  Bitmap getBitmapByUrl(String photoUrl) throws InterruptedException, ExecutionException, TimeoutException {
        Activity activity = getCurrentActivity();

    RequestFuture<Bitmap> future = RequestFuture.newFuture();
    ImageRequest request = new ImageRequest(
        photoUrl,
        future,
        1000,
        1000,
        ImageView.ScaleType.CENTER,
        null,
        future);
     requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
     requestQueue.add(request);
    return future.get(30, TimeUnit.SECONDS);
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public void getFace(String faceTokenEt, Callback success, Callback failure) {
    mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }
    try {
      byte[] faceToken = faceTokenEt.getBytes();
      Bitmap bitmap = mFacePassHandler.getFaceImage(faceToken);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
      byte[] byteArray = byteArrayOutputStream.toByteArray();
      String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
      success.invoke(encoded);
    } catch (Exception e) {
      e.printStackTrace();
      failure.invoke(e.getMessage());
      return;
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
        WritableArray array = Arguments.fromList(faceTokenList);
        success.invoke(array);

      }
    } catch (FacePassException e) {
      e.printStackTrace();
      failure.invoke(e.getMessage());
    }
    String result = b ? "success " : "failed";
    if (b) {
      toast("Face deleted");
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
  public void unbindFace(String StringfaceToken, String groupNameEt, Callback success,
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
        WritableArray array = Arguments.fromList(faceTokenList);
        success.invoke(array);
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
  public void checkGroupExist(String groupname,Callback success, Callback failure){
     mFacePassHandler = FacePassHandlerHolder.getMyObject();
    if (mFacePassHandler == null) {
      failure.invoke("FACEPASSHANDLER_NULL_ERROR");
      return;
    }
    Boolean isLocalGroupExist=false;
    try {
      String[] localGroups = mFacePassHandler.getLocalGroups();
      if (localGroups == null || localGroups.length == 0) {
        failure.invoke("EMPTY_LOCAL_GROUP_ERROR");
        return;
      }
      for (String group : localGroups) {
        if (groupname.equals(group)) {
          isLocalGroupExist=true;
          success.invoke("LOCALGROUP_FOUND");
        }
      }
      if (!isLocalGroupExist) {
        failure.invoke("LOCAL_GROUP_NOT_FOUND");
      }
    } catch (

    FacePassException e) {
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

  @ReactMethod
  public void controlDoor(String mode) {
    QZhengGPIOInstance = QZhengGPIOManager.getInstance(context);
    qZhengManager = new QZhengIFManager(context);
    gpioManager = GPIOManager.getInstance(context);
    QZGpio door = QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_DOOR);
    if (mode.equals("open")) {
      door.setValue(1);
    } else {
      door.setValue(0);
    }
  }

  @ReactMethod
  public void useIRCameraSupport(Boolean use) {
    SettingVar.useIRCameraSupport = use;
  }

  @ReactMethod
  public void restartDevice() {
    try {
      QZhengGPIOInstance = QZhengGPIOManager.getInstance(context);
      qZhengManager = new QZhengIFManager(context);
      gpioManager = GPIOManager.getInstance(context);
      qZhengManager.reboot(false, "", false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void enableTemperature(Boolean enable) {
    SettingVar.temperatureScan = enable;
  }

  @ReactMethod
  public void enableIRPreview(Boolean enable) {
    SettingVar.showIRPreview = enable;
  }

  @ReactMethod
  public void elevatorAccess(String elevatorGatewayIp, String uid, String premiseId) {
    Activity activity = getCurrentActivity();

    if (!elevatorGatewayIp.equals("")) {
      JSONObject object = new JSONObject();

      try {
        object.put("uid", uid);
        object.put("premiseId", premiseId);
        object.put("deviceId", getSerialNumber());
      } catch (JSONException e) {
        e.printStackTrace();
      }

      String url = "http://" + elevatorGatewayIp + "/v1/elevator/access/user";

      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              Log.d("elevatorAccess", "String Response : " + response.toString());
            }
          }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              Log.d("elevatorAccess", "Error Response : " + error.getMessage());
            }
          });
      requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
      requestQueue.add(jsonObjectRequest);
    }

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
