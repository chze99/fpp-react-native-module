import { StatusBar } from 'expo-status-bar';
import { StyleSheet, NativeModules } from 'react-native';
import DefaultPreference from 'react-native-default-preference';
import { useEffect, useState } from 'react';
import Routes from './components/Routes';
import 'react-native-gesture-handler';
import { initData, cameraSetting, setDefaultGroupName } from 'facepass-react-native-module';
import Toast from 'react-native-toast-message';

export default function App() {
  const { FacePass } = NativeModules
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    async function init() {
      await DefaultPreference.setName("fppreactnative")
      const setting = await DefaultPreference.get("parameters")
      const camera = await DefaultPreference.get("settings")
      const group = await DefaultPreference.get("group_name")
      // initData({"rcAttributeAndOcclusionMode":1,"searchThreshold":69,"livenessThreshold":55,"livenessEnabled":false,"rgbIrLivenessEnabled":false,"poseThresholdRoll":35,"poseThresholdPitch":35,"poseThresholdYaw":35,"blurThreshold":0.8,"lowBrightnessThreshold":30,"highBrightnessThreshold":210,"brightnessSTDThreshold":80,"faceMinThreshold":100,"retryCount":2,"smileEnabled":false,"maxFaceEnabled":true,"FacePoseThresholdPitch":35,"FacePoseThresholdRoll":35,"FacePoseThresholdYaw":35,"FaceBlurThreshold":0.7,"FaceLowBrightnessThreshold":70,"FaceHighBrightnessThreshold":220,"FaceBrightnessSTDThreshold":60,"FaceFaceMinThreshold":100,"FaceRcAttributeAndOcclusionMode":2})
      cameraSetting(JSON.parse(camera));
      console.log(setting)
      initData(JSON.parse(setting));
      setDefaultGroupName(group);
      // setDefaultGroupName("testi");
      //Then call function to initilize,all parameter need to be in JSON string format.
      // cameraSetting({
      //   "cameraFacingFront": false,
      //   "faceRotation": 90,
      //   "isSettingAvailable": true,
      //   "cameraPreviewRotation": 270,
      //   "isCross": true,
      // });

      setIsLoading(false)
    }
    init()
  }, [])


  if (!isLoading) {
    return (<>
      <Routes />
      <Toast />
    </>
    );
  } else {
    return "";
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
