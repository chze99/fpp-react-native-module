# facepass-react-native-module

Face Recognition Module for React Native

## Installation

```sh
npm install facepass-react-native-module
```

Require use of any package that allow react native to save and retrieve data, the data need to store as String of JSON.

DefaultPreference is the example of package in this documentation,you can freely choose other package to be used
**Android**



- In android/app/src/main/AndroidManifest.xml
```sh
  <uses-feature
        android:name="android.hardware.camera"
        android:required="true" />
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
```

## Usage

**React Native**


- App.js/Index.js
```sh
-For list of function name refer to document below
  import { FunctionName } from 'facepass-react-native-module';

-Example,
  import { cameraSetting, setDefaultGroupName, initData } from 'facepass-react-native-module';
  
  export default function App(){
    useEffect(()=>{
      async function init(){
        setting = //get ur setting from storage
        groupname= //get ur groupname from storage
        parameter = //get ur parameter from storage

        //this one is example using DefaultPreference package
        setting = await DefaultPreference.get("settings")

        //Then call function to initilize,all parameter need to be in JSON format.
        //The detail of json content need to be included for these function can refer to below
        cameraSetting(setting);
        initData(parameter)
        //This one is string format
        setDefaultGroupName(groupname);
        }
        init()
    },[])
 
  }
```

- MainScreen.js(Can be any name,The page you want to display the face recognition screen,can also be in App.js)
```sh
  import { StyleSheet,View,PixelRatio,UIManager,findNodeHandle,BackHandler, NativeEventEmitter } from 'react-native';
  import { useEffect,useRef } from 'react';
  import { FacePassViewManager,FacePass } from 'facepass-react-native-module';

```

- Outside function MainScreen of MainScreen.js
```sh
  const createFragment = viewId =>
    UIManager.dispatchViewManagerCommand(
      viewId,
      // we are calling the 'create' command
      UIManager.FacePassViewManager.Commands.create.toString(),
      [viewId],
    );
  const destroyFragment = viewId =>
    UIManager.dispatchViewManagerCommand(
      viewId,
    // we are calling the 'create' command
    UIManager.FacePassViewManager.Commands.remove.toString(),
    [viewId],
  );
```
- Inside function MainScreen of MainScreen.js
 ```sh
  const eventEmitter = new NativeEventEmitter(FacePass);
  const ref = useRef(null);

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    createFragment(viewId);
  }, []);

  //example of function to destroy fragment,require to be called
  when navigate to other screen.
  function destroyfragment() {
    const viewId = findNodeHandle(ref.current);
    destroyFragment(viewId);
    return false;
  }

  // Add an event listener to receive the data
  const dataListener = eventEmitter.addListener('FaceDetectedEvent', async (params) => {
    //Do something here,e.g.
    const image = params.image;
    const facetoken = params.name;
    //Name= image owner name,image=image in base64
  });

  return (
    <FacePassViewManager
      style={{
        // converts dpi to px, provide desired height
        height: PixelRatio.getPixelSizeForLayoutSize(
          yourheight,e.g. 500
        ),
        // converts dpi to px, provide desired width
        width: PixelRatio.getPixelSizeForLayoutSize(
          yourwidth,e.g. 500
        ),
      }}
      ref={ref}
      />
  );
```

*** Other function(FacePass related) ***


- Get a list of group name
```sh
  try{
    const data=await getAllGroup()
  }catch(error){
    //Error message
  }
```

- Create a new group with provided name
```sh
  try{
    const successMessage=await createGroup(name)
  }catch(error){
    //Error message
  }
```

- Delete the group with provided name
```sh
  try{
    const groupListArray =await deleteGroup(name)
  }catch(error){
    //Error message
  }
```

- Open file explorer and choose image
```sh
  try{
    const imagePath=await selectImage()
  }catch(error){
    //Error message
  }
```

- Add selected face into facial recognition
```sh
  try{
    const faceToken=await addFace(imagePath)
    //Here is to set the face owner name so that the app can know the face belong to who
    //"Face owner name" need to be replace with own function to get name,e.g an text field
    DefaultPreference.set(faceToken, "Face owner name");
  }catch(error){
    //Error message
  }
```

- Delete selected face
```sh
  //faceToken is generated from addFace function
  //groupName= Name of group with that Face added
  try{
    const successMessage=await deleteFace(faceToken, groupName)
    await DefaultPreference.clear(faceToken);
  }catch(error){
    //Error message
  }
```

- Get the face using faceToken, return an image in base64 format
```sh
  try{
    //base64 image action,e.g. setImage(base64Image) , setImage is the useState() variable.
    const base64Image =await getFace(faceToken)
  }catch(e){
    //Error message
  }
```

- Bind to face to a group using faceToken and groupName
```sh
  try{
      const successMessage=await bindGroup(faceToken)
    }catch(e){
      //Error message
    }
```

- Get all facetoken from the provided groupname
```sh
  try{
    const faceTokenListArray=await getGroupInfo(groupName)
  }catch(e){
    //Error message
  }
```

- Unbind the faceToken from provided group
```sh
  try{
      const faceTokenListArray=await unbindFace(facetoken, groupName)
    }catch(e){
      //Error message
    }
```

***JSON format of Parameter for funtion***

- setDefaultGroupName()
```sh
    //function name can be change to anything
    //defaultGroupName need to get yourself
    //e.g. a text field
    //This default group is the group that will be
    load on min screen
    setDefaultGroupName("your_group_name")
```

- initData
```sh
  const json = {
    rcAttributeAndOcclusionMode: rcAttributeAndOcclusionMode, //0-5,whole number,Default:1
    searchThreshold: searchThreshold, //0-100,Default:69
    livenessThreshold: livenessThreshold, //0-100,Default:55
    livenessEnabled: livenessEnabled, //true,false,Default:true
    rgbIrLivenessEnabled: false, //Currently only can false
    poseThresholdRoll: poseThresholdRoll, //0-90,Default:35
    poseThresholdPitch: poseThresholdPitch, //0-90,Default:35
    poseThresholdYaw: poseThresholdYaw, //0-90,Default:35
    blurThreshold: blurThreshold, //0-1,Default:0.8
    lowBrightnessThreshold: lowBrightnessThreshold, //0-255,Default:30
    highBrightnessThreshold: highBrightnessThreshold, //0-255,Default:210
    brightnessSTDThreshold: brightnessSTDThreshold, //0-255,Default:80
    faceMinThreshold: faceMinThreshold, //0-512,whole number,Default:100
    retryCount: retryCount, //1-unlimited,whole number,Default:2
    smileEnabled: smileEnabled, //true/false,Default:false
    maxFaceEnabled: maxFaceEnabled, //true/false,Default:true
    FacePoseThresholdPitch: FacePoseThresholdPitch, //0-90,Default:35
    FacePoseThresholdRoll: FacePoseThresholdRoll, //0-90,Default:35
    FacePoseThresholdYaw: FacePoseThresholdYaw, //0-90,Default:35
    FaceBlurThreshold: FaceBlurThreshold, //0-255,Default:0.7
    FaceLowBrightnessThreshold: FaceLowBrightnessThreshold, //0-255,Default:70
    FaceHighBrightnessThreshold: FaceHighBrightnessThreshold, //0-255,Default:220
    FaceBrightnessSTDThreshold: FaceBrightnessSTDThreshold, //0-255,Default:60
    FaceFaceMinThreshold: FaceFaceMinThreshold, //0-512,whole number,Default:100
    FaceRcAttributeAndOcclusionMode: FaceRcAttributeAndOcclusionMode, //whole number //0-5,Default:2
  }
  initData(json)

  //Recommended value
  initData({
    "rcAttributeAndOcclusionMode":1,"searchThreshold":69,"livenessThreshold":55,"livenessEnabled":false,"rgbIrLivenessEnabled":false,"poseThresholdRoll":35,"poseThresholdPitch":35,"poseThresholdYaw":35,"blurThreshold":0.8,"lowBrightnessThreshold":30,"highBrightnessThreshold":210,"brightnessSTDThreshold":80,"faceMinThreshold":100,"retryCount":2,"smileEnabled":false,"maxFaceEnabled":true,"FacePoseThresholdPitch":35,"FacePoseThresholdRoll":35,"FacePoseThresholdYaw":35,"FaceBlurThreshold":0.7,"FaceLowBrightnessThreshold":70,"FaceHighBrightnessThreshold":220,"FaceBrightnessSTDThreshold":60,"FaceFaceMinThreshold":100,"FaceRcAttributeAndOcclusionMode":2
  })

```

- cameraSetting();
```sh
  const data = {
    cameraFacingFront: cameraFacingFront //true,false,Default:false,
    faceRotation: faceRotation //0,90,180,270,Default:90,
    isSettingAvailable: true,//Leave it like this
    cameraPreviewRotation: cameraPreviewRotation //0,90,180,270,Default:270,
    isCross: isCross //true,false,Default:false,
  }
  cameraSetting(data);

  //Recommended value     
  cameraSetting({
    "cameraFacingFront": false,
    "faceRotation": 90,
    "isSettingAvailable": true,
    "cameraPreviewRotation": 270,
    "isCross": true,
  });
        
```

## Known Issue
The face recognition may not working if the app is first time launch after installation/clear storage or cache.Restart of application is needed after the app granted permission.

If facial recognition setting is changed,restart is require for new facial recognition setting to take affect.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
