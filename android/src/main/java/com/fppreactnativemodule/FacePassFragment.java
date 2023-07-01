package com.fppreactnativemodule;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.Base64;

import androidx.annotation.NonNull;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Callback;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactApplication;

import org.json.JSONException;

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
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
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
import com.example.yfaceapi.GPIOManager;
import android.util.Pair;

import com.fppreactnativemodule.camera.CameraManager;
import com.fppreactnativemodule.camera.CameraPreview;
import com.fppreactnativemodule.camera.CameraPreviewData;
import com.fppreactnativemodule.camera.ComplexFrameHelper;
import com.fppreactnativemodule.utils.FileUtil;
import static com.fppreactnativemodule.utils.Helper.getSerialNumber;

import com.q_zheng.QZhengGPIOManager;
import com.q_zheng.QZhengIFManager;
import com.q_zheng.QZGpio;

public class FacePassFragment extends Fragment implements CameraManager.CameraListener, View.OnClickListener {
  private Context context;
  Activity activity;

  private enum FacePassSDKMode {
    MODE_ONLINE,
    MODE_OFFLINE
  };

  private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;
  private static final String DEBUG_TAG = "FacePassDemo";
  public static String group_name = "fppreactnative";
  private static final int PERMISSIONS_REQUEST = 1;
  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
  private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
  private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
  private String[] Permission = new String[] { PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE,
      PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE };
  FacePassHandler mFacePassHandler;
  private CameraManager manager;
  private CameraManager mIRCameraManager;
  private CameraPreview cameraView;
  private CameraPreview mIRCameraView;
  private boolean isLocalGroupExist = false;
  private FaceView faceView;
  private ScrollView scrollView;
  private static boolean cameraFacingFront = true;
  private int cameraRotation;
  private static final int cameraWidth = 1280;
  private static final int cameraHeight = 720;
  private int heightPixels;
  private int widthPixels;
  int screenState = 0;
  private Toast mRecoToast;
  float faceTemperature = 0f;

  public class RecognizeData {
    public byte[] message;
    public FacePassTrackOptions[] trackOpt;

    public RecognizeData(byte[] message) {
      this.message = message;
      this.trackOpt = null;
    }

    public RecognizeData(byte[] message, FacePassTrackOptions[] opt) {
      this.message = message;
      this.trackOpt = opt;
    }
  }

  ArrayBlockingQueue<RecognizeData> mRecognizeDataQueue;
  ArrayBlockingQueue<CameraPreviewData> mFeedFrameQueue;
  ArrayBlockingQueue<CameraPreviewData> mFeedFrameIRQueue;

  RecognizeThread mRecognizeThread;
  FeedFrameThread mFeedFrameThread;
  private FaceImageCache mImageCache;
  private Handler mAndroidHandler;
  Boolean appPaused = false;
  Boolean enableLight = true;
  private GPIOManager gpioManager;
  Boolean useIRCameraSupport = false;
  boolean temperatureScan = false;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    context = context;
    activity = getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    super.onCreateView(inflater, parent, savedInstanceState);
    return inflater.inflate(R.layout.activity_main, parent, false);
  }

  private Context mContext;

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

  }

  public Boolean timeOut = false;
  public Boolean showIRPreview = false;

  @Override
  public void onStart() {
    super.onStart();
    int counter = 0;
    mImageCache = new FaceImageCache();
    mRecognizeDataQueue = new ArrayBlockingQueue<RecognizeData>(5);
    mFeedFrameQueue = new ArrayBlockingQueue<CameraPreviewData>(1);
    mFeedFrameIRQueue = new ArrayBlockingQueue<CameraPreviewData>(1);
    useIRCameraSupport = SettingVar.useIRCameraSupport;
    showIRPreview = SettingVar.showIRPreview;
    initAndroidHandler();
    View view = getView();
    initView(view);
    QZhengGPIOInstance = QZhengGPIOManager.getInstance(context);
    qZhengManager = new QZhengIFManager(context);
    gpioManager = GPIOManager.getInstance(context);
    group_name = SettingVar.groupName;
    temperatureScan = SettingVar.temperatureScan;

    do {
      counter++;
      Log.v("doneInititialize", Boolean.toString(SettingVar.doneInitialize));
      Log.v("Counter", Integer.toString(counter));

      if (SettingVar.doneInitialize == true) {

        if (!hasPermission()) {
          requestPermission();
        }

        if (enableLight) {
          // changeLight("white");
        }
        if (temperatureScan) {
          TemperatureService.startService(activity.getApplicationContext());
        }

        mFacePassHandler = FacePassHandlerHolder.getMyObject();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();

        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();
      }

      if (counter >= 29000 && timeOut == false) {
        toast("Initializing time out,please restart your app");
        timeOut = true;
      }
    } while (SettingVar.doneInitialize == false && counter < 30000);
  }

  private void initView(View view) {
    int windowRotation = ((WindowManager) (activity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)))
        .getDefaultDisplay().getRotation() * 90;
    if (windowRotation == 0) {
      cameraRotation = FacePassImageRotation.DEG90;
    } else if (windowRotation == 90) {
      cameraRotation = FacePassImageRotation.DEG0;
    } else if (windowRotation == 270) {
      cameraRotation = FacePassImageRotation.DEG180;
    } else {
      cameraRotation = FacePassImageRotation.DEG270;
    }
    Log.i(DEBUG_TAG, "Rotation: cameraRation: " + cameraRotation);
    cameraFacingFront = true;

    if (SettingVar.isSettingAvailable) {
      cameraRotation = SettingVar.faceRotation;
      cameraFacingFront = SettingVar.cameraFacingFront;
    }

    Log.i(DEBUG_TAG, "Rotation: screenRotation: " + String.valueOf(windowRotation));
    Log.i(DEBUG_TAG, "Rotation: new cameraRation: " + cameraRotation);
    final int mCurrentOrientation = getResources().getConfiguration().orientation;
    if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
      screenState = 1;
    } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
      screenState = 0;
    }

    DisplayMetrics displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    heightPixels = displayMetrics.heightPixels;
    widthPixels = displayMetrics.widthPixels;
    scrollView = (ScrollView) getView().findViewById(R.id.scrollView);
    AssetManager mgr = activity.getApplicationContext().getAssets();
    Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
    faceView = (FaceView) getView().findViewById(R.id.fcview);
    SettingVar.cameraSettingOk = false;
    manager = new CameraManager();
    cameraView = (CameraPreview) getView().findViewById(R.id.preview);
    manager.setPreviewDisplay(cameraView);
    if (useIRCameraSupport) {
      mIRCameraManager = new CameraManager();
      if (showIRPreview) {
        mIRCameraView = (CameraPreview) getView().findViewById(R.id.previewIR);
      } else {
        mIRCameraView = (CameraPreview) getView().findViewById(R.id.previewIRHidden);
      }
      mIRCameraManager.setPreviewDisplay(mIRCameraView);
      mIRCameraManager.setListener(new CameraManager.CameraListener() {
        @Override
        public void onPictureTaken(CameraPreviewData cameraPreviewData) {

          ComplexFrameHelper.addIRFrame(cameraPreviewData);
        }
      });
    }
    manager.setListener(this);

  }

  @Override
  public void onPause() {
    Log.v("OnPause", "Pause");
    // changeLight("off");
    appPaused = true;
    super.onPause();

  }

  @Override
  public void onResume() {
    checkGroup();
    initToast();
    if (hasPermission()) {
      manager.open(activity.getWindowManager(), false, cameraWidth, cameraHeight);
      if (useIRCameraSupport) {
        mIRCameraManager.open(activity.getWindowManager(), true, cameraWidth, cameraHeight);
      }
    }
    adaptFrameLayout();
    super.onResume();
  }

  QZhengGPIOManager QZhengGPIOInstance;
  QZhengIFManager qZhengManager;

  // private void changeLight(String light) {

  // // new device
  // if (light.equals("white") && enableLight) {
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // } else if (light.equals("red")) {
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // } else if (light.equals("green")) {
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_HIGH);
  // } else {
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_R).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_B).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // QZhengGPIOInstance.getGPIO(QZhengGPIOManager.GPIO_ID_LED_G).setValue(QZhengGPIOManager.GPIO_VALUE_LOW);
  // }
  // // old device
  // if (light.equals("white")) {
  // gpioManager.pullUpWhiteLight();
  // gpioManager.pullDownGreenLight();
  // gpioManager.pullDownRedLight();
  // } else if (light.equals("red")) {
  // gpioManager.pullDownWhiteLight();
  // gpioManager.pullDownGreenLight();
  // gpioManager.pullUpRedLight();
  // } else if (light.equals("green")) {
  // gpioManager.pullDownWhiteLight();
  // gpioManager.pullUpGreenLight();
  // gpioManager.pullDownRedLight();
  // } else {
  // gpioManager.pullDownWhiteLight();
  // gpioManager.pullDownGreenLight();
  // gpioManager.pullDownRedLight();
  // }
  // }

  private void initAndroidHandler() {

    mAndroidHandler = new Handler();

  }

  private void checkGroup() {
    if (mFacePassHandler == null) {
      return;
    }
    try {
      String[] localGroups = mFacePassHandler.getLocalGroups();
      isLocalGroupExist = false;
      Log.v("LocalGroups", localGroups.toString());
      if (localGroups == null || localGroups.length == 0) {
        faceView.post(new Runnable() {
          @Override
          public void run() {
            toast("LocalGroup not found,Please create " + group_name + " Local Group");
          }
        });
        return;
      }
      for (String group : localGroups) {
        if (group_name.equals(group)) {
          isLocalGroupExist = true;
        }
      }
      Log.v("LocalGroupsExist", Boolean.toString(isLocalGroupExist));
      if (!isLocalGroupExist) {
        faceView.post(new Runnable() {

          @Override
          public void run() {
            toast("Please create " + group_name + " Local group");
          }
        });
      }
    } catch (

    FacePassException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onPictureTaken(CameraPreviewData cameraPreviewData) {
    if (useIRCameraSupport) {
      ComplexFrameHelper.addRgbFrame(cameraPreviewData);
    } else {
      mFeedFrameQueue.offer(cameraPreviewData);
    }
    // detectQRCode(cameraPreviewData);

  }

  // public void detectQRCode(CameraPreviewData cameraPreviewData) {

  //   InputImage image = InputImage.fromByteArray(cameraPreviewData.nv21Data,
  //       cameraPreviewData.width,
  //       cameraPreviewData.height,
  //       cameraPreviewData.rotation,
  //       InputImage.IMAGE_FORMAT_NV21);

  //   scanner.process(image)
  //       .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
  //         @Override
  //         public void onSuccess(List<Barcode> barcodes) {
  //           for (Barcode barcode : barcodes) {
  //             String uid = barcode.getRawValue();
  //             Log.d("QR counter", String.valueOf(recognitionIntervalCounter));
  //             Log.d("QR check in", uid);
  //             if (recognitionIntervalCounter <= 0) {
  //               if (loadedUsers.has(uid)) {
  //                 faceCheckIn(0, null, 100, 1, null, null, true, uid);
  //               } else {
  //                 toast("No access or invalid QR code.");
  //               }
  //               recognitionIntervalCounter = recognitionInterval;
  //             }
  //           }
  //         }
  //       });

  // }

  public void detectBrightness(CameraPreviewData cameraPreviewData) {
    long sumY = 0;
    for (int j = 0, yp = 0; j < cameraPreviewData.height; j++) {
      for (int i = 0; i < cameraPreviewData.width; i++, yp++) {
        int y = (0xff & ((int) cameraPreviewData.nv21Data[yp]));
        if (y < 0) {
          y = 0;
        }
        sumY += y;
      }
    }
    int brightness = (int) sumY / (cameraPreviewData.width * cameraPreviewData.height);

    Log.d("brightness", String.valueOf(brightness));
  }

  private class FeedFrameThread extends Thread {
    boolean isInterrupt;

    @Override
    public void run() {
      while (!isInterrupt) {

        Pair<CameraPreviewData, CameraPreviewData> framePair = null;
        CameraPreviewData cameraPreviewData = null;
        try {
          if (useIRCameraSupport) {
            framePair = ComplexFrameHelper.takeComplexFrame();
          } else {
            cameraPreviewData = mFeedFrameQueue.take();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
          continue;
        }
        if (mFacePassHandler == null) {
          continue;
        }
        long startTime = System.currentTimeMillis();
        FacePassImage image;
        FacePassImage imageIR;

        try {

          if (useIRCameraSupport) {

            image = new FacePassImage(framePair.first.nv21Data, framePair.first.width, framePair.first.height,
                cameraRotation, FacePassImageType.NV21);

            imageIR = new FacePassImage(framePair.second.nv21Data, framePair.second.width, framePair.second.height,
                cameraRotation, FacePassImageType.NV21);

          } else {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,
                cameraRotation, FacePassImageType.NV21);
            imageIR = null;
          }
        } catch (FacePassException e) {
          e.printStackTrace();
          Log.v("Error FacePassImage Data", e.toString());

          continue;
        }
        FacePassDetectionResult detectionResult = null;
        try {

          if (useIRCameraSupport) {
            detectionResult = mFacePassHandler.feedFrameRGBIR(image, imageIR);

          } else {
            detectionResult = mFacePassHandler.feedFrame(image);

          }
        } catch (FacePassException e) {
          e.printStackTrace();
        }

        if (detectionResult == null || detectionResult.faceList.length == 0) {
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              faceView.clear();
              faceView.invalidate();
            }
          });
        } else {
          final FacePassFace[] bufferFaceList = detectionResult.faceList;
          getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
              showFacePassFace(bufferFaceList);
            }
          });
        }

        if (SDK_MODE == FacePassSDKMode.MODE_OFFLINE) {
          if (detectionResult != null && detectionResult.message.length != 0) {
            Log.d(DEBUG_TAG, "mRecognizeDataQueue.offer");
            for (

                int i = 0; i < detectionResult.faceList.length; ++i) {
              Log.d(DEBUG_TAG, String.format(
                  "rc attribute faceList hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
                  detectionResult.faceList[i].rcAttr.hairType.ordinal(),
                  detectionResult.faceList[i].rcAttr.beardType.ordinal(),
                  detectionResult.faceList[i].rcAttr.hatType.ordinal(),
                  detectionResult.faceList[i].rcAttr.respiratorType.ordinal(),
                  detectionResult.faceList[i].rcAttr.glassesType.ordinal(),
                  detectionResult.faceList[i].rcAttr.skinColorType.ordinal()));
            }
            Log.d(DEBUG_TAG,
                "--------------------------------------------------------------------------------------------------------------------------------------------------");
            FacePassTrackOptions[] trackOpts = new FacePassTrackOptions[detectionResult.images.length];
            for (int i = 0; i < detectionResult.images.length; ++i) {
              if (detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.INVALID
                  && detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.NO_RESPIRATOR) {
                float searchThreshold = 80f;

                if (SettingVar.searchThreshold > 0) {

                  searchThreshold = SettingVar.searchThreshold;

                } else {
                  searchThreshold = 80f;
                }
                Log.v("searchThreshold", Float.toString(searchThreshold));
                float livenessThreshold = -1.0f;
                trackOpts[i] = new FacePassTrackOptions(detectionResult.images[i].trackId, searchThreshold,
                    livenessThreshold);
              }
              Log.d(DEBUG_TAG, String.format(
                  "rc attribute in FacePassImage, hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
                  detectionResult.images[i].rcAttr.hairType.ordinal(),
                  detectionResult.images[i].rcAttr.beardType.ordinal(),
                  detectionResult.images[i].rcAttr.hatType.ordinal(),
                  detectionResult.images[i].rcAttr.respiratorType.ordinal(),
                  detectionResult.images[i].rcAttr.glassesType.ordinal(),
                  detectionResult.images[i].rcAttr.skinColorType.ordinal()));
            }
            RecognizeData mRecData = new RecognizeData(detectionResult.message, trackOpts);
            mRecognizeDataQueue.offer(mRecData);
          }
        }
        if (temperatureScan) {
          faceTemperature = TemperatureService.getTemperature();
        }
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        for (int i = 0; i < detectionResult.faceList.length; ++i) {
          Log.i("DEBUG_TAG",
              "rect[" + i + "] = (" + detectionResult.faceList[i].rect.left + ", "
                  + detectionResult.faceList[i].rect.top + ", " + detectionResult.faceList[i].rect.right + ", "
                  + detectionResult.faceList[i].rect.bottom);
        }
        Log.i("]time", String.format("feedfream %d ms", runTime));
      }
    }

    @Override
    public void interrupt() {
      isInterrupt = true;
    }

  }

  int findidx(FacePassAgeGenderResult[] results, long trackId) {
    int result = -1;
    if (results == null) {
      return result;
    }
    for (int i = 0; i < results.length; ++i) {
      if (results[i].trackId == trackId) {
        return i;
      }
    }
    return result;
  }

  private class RecognizeThread extends Thread {

    boolean isInterrupt;

    @Override
    public void run() {
      while (!isInterrupt) {
        try {
          RecognizeData recognizeData = mRecognizeDataQueue.take();
          FacePassAgeGenderResult[] ageGenderResult = null;

          if (isLocalGroupExist) {
            Log.d(DEBUG_TAG, "RecognizeData >>>>");
            FacePassRecognitionResult[][] recognizeResultArray = mFacePassHandler.recognize(group_name,
                recognizeData.message, 1, recognizeData.trackOpt);
            if (recognizeResultArray != null && recognizeResultArray.length > 0) {
              for (FacePassRecognitionResult[] recognizeResult : recognizeResultArray) {
                if (recognizeResult != null && recognizeResult.length > 0) {
                  for (FacePassRecognitionResult result : recognizeResult) {
                    String faceToken = new String(result.faceToken);
                    if (FacePassRecognitionState.RECOGNITION_PASS == result.recognitionState) {
                      getFaceImageByFaceToken(result.trackId, faceToken, result);

                    }

                    int idx = findidx(ageGenderResult, result.trackId);
                    Log.d("RecognizeResult", String.format(
                        "recognize trackid: %d, searchScore: %f  searchThreshold: %f, hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
                        result.trackId,
                        result.detail.searchScore,
                        result.detail.searchThreshold,
                        result.detail.rcAttr.hairType.ordinal(),
                        result.detail.rcAttr.beardType.ordinal(),
                        result.detail.rcAttr.hatType.ordinal(),
                        result.detail.rcAttr.respiratorType.ordinal(),
                        result.detail.rcAttr.glassesType.ordinal(),
                        result.detail.rcAttr.skinColorType.ordinal()));
                  }
                }
              }
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (FacePassException e) {
          e.printStackTrace();
        }
      }
    }

    @Override
    public void interrupt() {
      isInterrupt = true;
    }
  }

  private boolean hasPermission() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return activity.checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
          activity.checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
          activity.checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
          activity.checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
          activity.checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      activity.requestPermissions(Permission, PERMISSIONS_REQUEST);
    }
  }

  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            Toast.makeText(activity.getApplicationContext(), "Please enable Camera,Network and File write permission",
                Toast.LENGTH_SHORT).show();
          }
      } else {
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

  @Override
  public void onDestroy() {
    mRecognizeThread.isInterrupt = true;
    mFeedFrameThread.isInterrupt = true;

    mRecognizeThread.interrupt();
    mFeedFrameThread.interrupt();
    if (manager != null) {
      manager.release();
    }
    if (useIRCameraSupport) {
      if (mIRCameraManager != null) {
        mIRCameraManager.release();
      }
    }
    if (mAndroidHandler != null) {
      mAndroidHandler.removeCallbacksAndMessages(null);
    }

    Log.v("OnDestroy", "Destroy");
    super.onDestroy();
  }

  private void showFacePassFace(FacePassFace[] detectResult) {
    faceView.clear();
    for (FacePassFace face : detectResult) {
      Log.d("facefacelist",
          "width " + (face.rect.right - face.rect.left) + " height " + (face.rect.bottom - face.rect.top));
      Log.d("facefacelist", "smile " + face.smile);
      boolean mirror = cameraFacingFront;
      StringBuilder faceIdString = new StringBuilder();
      faceIdString.append("ID = ").append(face.trackId);
      SpannableString faceViewString = new SpannableString(faceIdString);
      faceViewString.setSpan(new TypefaceSpan("fonts/kai"), 0, faceViewString.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      StringBuilder faceRollString = new StringBuilder();
      faceRollString.append("Rotation: ").append((int) face.pose.roll).append("°");
      StringBuilder facePitchString = new StringBuilder();
      facePitchString.append("UpDown: ").append((int) face.pose.pitch).append("°");
      StringBuilder faceYawString = new StringBuilder();
      faceYawString.append("LeftRight: ").append((int) face.pose.yaw).append("°");
      StringBuilder faceBlurString = new StringBuilder();
      faceBlurString.append("Blur: ").append(face.blur);
      StringBuilder smileString = new StringBuilder();
      smileString.append("Smile: ").append(String.format("%.6f", face.smile));
      Matrix mat = new Matrix();
      int w = cameraView.getMeasuredWidth();
      int h = cameraView.getMeasuredHeight();

      int cameraHeight = manager.getCameraheight();
      int cameraWidth = manager.getCameraWidth();

      float left = 0;
      float top = 0;
      float right = 0;
      float bottom = 0;
      switch (cameraRotation) {
        case 0:
          left = face.rect.left;
          top = face.rect.top;
          right = face.rect.right;
          bottom = face.rect.bottom;
          mat.setScale(mirror ? -1 : 1, 1);
          mat.postTranslate(mirror ? (float) cameraWidth : 0f, 0f);
          mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
          break;
        case 90:
          mat.setScale(mirror ? -1 : 1, 1);
          mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
          mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
          left = face.rect.top;
          top = cameraWidth - face.rect.right;
          right = face.rect.bottom;
          bottom = cameraWidth - face.rect.left;
          break;
        case 180:
          mat.setScale(1, mirror ? -1 : 1);
          mat.postTranslate(0f, mirror ? (float) cameraHeight : 0f);
          mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
          left = face.rect.right;
          top = face.rect.bottom;
          right = face.rect.left;
          bottom = face.rect.top;
          break;
        case 270:
          mat.setScale(mirror ? -1 : 1, 1);
          mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
          mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
          left = cameraHeight - face.rect.bottom;
          top = face.rect.left;
          right = cameraHeight - face.rect.top;
          bottom = face.rect.right;
      }
      RectF drect = new RectF();
      RectF srect = new RectF(left, top, right, bottom);

      mat.mapRect(drect, srect);
      faceView.addRect(drect);
      faceView.addId(faceIdString.toString());
      faceView.addRoll(faceRollString.toString());
      faceView.addPitch(facePitchString.toString());
      faceView.addYaw(faceYawString.toString());
      faceView.addBlur(faceBlurString.toString());
      faceView.addSmile(smileString.toString());
    }
    faceView.invalidate();
  }

  // public void showToast(CharSequence text, int duration, boolean isSuccess,
  // Bitmap bitmap) {
  // LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext()
  // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  // View toastView = inflater.inflate(R.layout.toast, null);
  // LinearLayout toastLLayout = (LinearLayout)
  // toastView.findViewById(R.id.toastll);
  // if (toastLLayout == null) {
  // return;
  // }
  // toastLLayout.getBackground().setAlpha(100);
  // ImageView imageView = (ImageView)
  // toastView.findViewById(R.id.toastImageView);
  // TextView idTextView = (TextView) toastView.findViewById(R.id.toastTextView);
  // TextView stateView = (TextView) toastView.findViewById(R.id.toastState);
  // SpannableString s;
  // if (isSuccess) {
  // s = new SpannableString("Verify Success");
  // imageView.setImageResource(R.drawable.success);
  // } else {
  // s = new SpannableString("Verify Failed");
  // imageView.setImageResource(R.drawable.success);
  // }
  // if (bitmap != null) {
  // imageView.setImageBitmap(bitmap);
  // }
  // stateView.setText(s);
  // idTextView.setText(text);

  // if (mRecoToast == null) {
  // mRecoToast = new Toast(activity.getApplicationContext());
  // mRecoToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
  // }
  // mRecoToast.setDuration(duration);
  // mRecoToast.setView(toastView);

  // mRecoToast.show();
  // }

  private static final int REQUEST_CODE_CHOOSE_PICK = 1;

  public void onClick(View v) {

  }

  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    Log.v("onActivityResult", "Called");
    switch (requestCode) {
      case REQUEST_CODE_CHOOSE_PICK:
        if (resultCode == -1) {
          if (pickerSuccessCallback != null) {
            if (resultCode == Activity.RESULT_CANCELED) {
              pickerCancelCallback.invoke("ImagePicker was cancelled");
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
                toast("Picture selection fail");
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

  private ReactInstanceManager mReactInstanceManager;
  public boolean detected=false;
  private void sendDataToReactNative(String faceToken) {
    ReactInstanceManager reactInstanceManager = ((ReactApplication) getActivity().getApplication()).getReactNativeHost()
        .getReactInstanceManager();
    ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
    FacePass nativeModule = reactContext.getNativeModule(FacePass.class);
    if (nativeModule != null && detected==false) {
      FacePass myNativeModule = (FacePass) nativeModule;
      myNativeModule.sendDataToReactNative(faceToken);
      detected=true;
    }
  }

    private void sendStopToReactNative() {
    ReactInstanceManager reactInstanceManager = ((ReactApplication) getActivity().getApplication()).getReactNativeHost()
        .getReactInstanceManager();
    ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
    FacePass nativeModule = reactContext.getNativeModule(FacePass.class);
    if (nativeModule != null) {
      FacePass myNativeModule = (FacePass) nativeModule;
      myNativeModule.sendStopToReactNative();
      detected=true;
    }
  }

  private void getFaceImageByFaceToken(final long trackId, String faceToken, final FacePassRecognitionResult result) {
    if (TextUtils.isEmpty(faceToken)) {
      return;
    }

    try {
      final Bitmap bitmap = mFacePassHandler.getFaceImage(faceToken.getBytes());
      mAndroidHandler.post(new Runnable() {
        @Override
        public void run() {
          Log.i(DEBUG_TAG, "getFaceImageByFaceToken cache is null");
          sendDataToReactNative(faceToken);
          Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              sendStopToReactNative();
            }
            }, 2000);
        }
      });
      if (bitmap != null) {
        return;
      }
    } catch (FacePassException e) {
      e.printStackTrace();
    }
  }

  private Callback pickerSuccessCallback;
  private Callback pickerCancelCallback;

  private void toast(String msg) {
    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
  }

  private static class FaceImageCache implements ImageLoader.ImageCache {

    private static final int CACHE_SIZE = 6 * 1024 * 1024;

    LruCache<String, Bitmap> mCache;

    public FaceImageCache() {
      mCache = new LruCache<String, Bitmap>(CACHE_SIZE) {

        @Override
        protected int sizeOf(String key, Bitmap value) {
          return value.getRowBytes() * value.getHeight();
        }
      };
    }

    @Override
    public Bitmap getBitmap(String url) {
      return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
      mCache.put(url, bitmap);
    }
  }

  private class FacePassRequest extends Request<String> {

    HttpEntity entity;

    FacePassDetectionResult mFacePassDetectionResult;
    private Response.Listener<String> mListener;

    public FacePassRequest(String url, FacePassDetectionResult detectionResult, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
      super(Method.POST, url, errorListener);
      mFacePassDetectionResult = detectionResult;
      mListener = listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
      String parsed;
      try {
        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
      } catch (UnsupportedEncodingException e) {
        parsed = new String(response.data);
      }
      return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
      mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
      return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
      MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

      for (FacePassImage passImage : mFacePassDetectionResult.images) {
        YuvImage img = new YuvImage(passImage.image, ImageFormat.NV21, passImage.width, passImage.height, null);
        Rect rect = new Rect(0, 0, passImage.width, passImage.height);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        img.compressToJpeg(rect, 95, os);
        byte[] tmp = os.toByteArray();
        ByteArrayBody bab = new ByteArrayBody(tmp, String.valueOf(passImage.trackId) + ".jpg");
        entityBuilder.addPart("image_" + String.valueOf(passImage.trackId), bab);
      }
      StringBody sbody = null;
      try {
        sbody = new StringBody("FACEPASS", ContentType.TEXT_PLAIN.withCharset(CharsetUtils.get("UTF-8")));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      entityBuilder.addPart("group_name", sbody);
      StringBody data = null;
      try {
        data = new StringBody(new String(mFacePassDetectionResult.message),
            ContentType.TEXT_PLAIN.withCharset(CharsetUtils.get("UTF-8")));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      entityBuilder.addPart("face_data", data);
      entity = entityBuilder.build();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try {
        entity.writeTo(bos);
      } catch (IOException e) {
        VolleyLog.e("IOException writing to ByteArrayOutputStream");
      }
      byte[] result = bos.toByteArray();
      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return result;
    }
  }

}