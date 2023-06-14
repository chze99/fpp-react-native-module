# fpp-react-native-module

Face Recognition Module for React Native

## Installation

```sh
npm install fpp-react-native-module
```
-Required for react native to save data:
```sh
npm install react-native-default-preference
```

**Android**

- In android/settings.gradle
```sh
...
include ':fpp-react-native-module', ':app'
project(':fpp-react-native-module').projectDir = new File(rootProject.projectDir, '../node_modules/fpp-react-native-module/android')
```
- In android/app/build.gradle

```sh
...
dependencies{
    ...
    implementation project(":fpp-react-native-module")
}

android{
  ...
      packagingOptions {
        exclude("META-INF/DEPENDENCIES") 
      } 
}
```
- In android/app/src/main/AndroidManifest.xml
```sh
  <uses-feature
        android:name="android.hardware.camera"
        android:required="true" />
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
```
- In MainActivity.java

```sh
import com.fppreactnativemodule.FacePass;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.ReactInstanceManager;
```

- Inside MainActivity class of MainActivity.java

```sh
  FacePass facepass;
```

- Inside onCreate() of MainActivity.java
```sh
  ReactInstanceManager mReactInstanceManager = getReactNativeHost().getReactInstanceManager();
    if (null == mReactInstanceManager.getCurrentReactContext()) {
      mReactInstanceManager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
        public void onReactContextInitialized(ReactContext validContext) {
          ReactApplicationContext context = (ReactApplicationContext) validContext;
          facepass = new FacePass(context);
          facepass.initData();
        }
      });
    } else {
      ReactApplicationContext context = (ReactApplicationContext)mReactInstanceManager.getCurrentReactContext();
      facepass = new FacePass(context);
      facepass.initData();
    }
```

## Usage

**React Native**

- App.js/Index.js
```sh
import DefaultPreference from 'react-native-default-preference';

//Inside 
export default function App(){

  useEffect(()=>{
    async function setPreferenceName(){
      await DefaultPreference.setName("fppreactnative")
      }
      setPreferenceName()
  },[])
 
  }
```

- ViewManager.js(Can be any name)
```sh
import {requireNativeComponent} from 'react-native';

export const FacePassViewManager =
  requireNativeComponent('FacePassViewManager');
```

- MainScreen.js(Can be any name,The page you want to display the face recognition screen,can also be in App.js)
```sh
import { NativeModules,StyleSheet,View,PixelRatio,UIManager,findNodeHandle,BackHandler, NativeEventEmitter } from 'react-native';
import { useEffect,useRef } from 'react';
import { FacePassViewManager } from '../components/ViewManager'; //ViewManager(.js) need to change to your file name if different
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
  const { FacePass } = NativeModules

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    createFragment(viewId);
    //When click back button will remove fragment
    BackHandler.addEventListener("hardwareBackPress", handleBackButtonClick);

    return () => {
      BackHandler.removeEventListener("hardwareBackPress", handleBackButtonClick);
      dataListener.remove();
    };
  }, []);


  function handleBackButtonClick() {
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

- If you use @react-navigation/native, then on MainScreen.js
```sh
  useEffect(() => {
    navigation.setOptions({
      headerLeft: () => (
        <TouchableOpacity onPress={() => { handleBackButtonClick(); navigation.goBack() }}  >
          <Icon name="arrow-back-outline" size={28} color="#000" />
        </TouchableOpacity>
      ),
    });
  }, [navigation])
```



- Other function(FacePass related)
```sh
        const { FacePass } = NativeModules

        //Get a list of group name
        FacePass.getAllGroup((data) => {
             //data=Array of list of group 
        }, (failure) => {
            //Error message
        })

        //Create a new group with provided name
        FacePass.createGroup(name, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })

        //Delete the group with provided name
        FacePass.deleteGroup(name, (data) => {
            //success message
        }, (failure) => {
            //Error message
        })

        //Open file explorer and choose image
        FacePass.select_image((uri) => {
            //do something, uri=imagepath
        }, (err) => {
            //Error message
        })

        //Add selected face into facial recognition
        const faceToken =await FacePass.addFace(imagePath, 
            (success) => {
               //Success message
            }, (failure) => {
               //Error message
            }
        );
        if (face != "") {
                //Here is to set the face owner name so that the app can know the face belong to who
                //"Face owner name" need to be replace with own function to get name,e.g an text field
                DefaultPreference.set(faceToken, "Face owner name");
        }


        //Delete selected face
        //faceToken is generated from addFace function
        //groupName= Name of group with that Face added
        FacePass.deleteFace(faceToken, groupName, (success) => {
                console.log(success);
            }, (failure) => {
                console.log(failure);
          })
        await DefaultPreference.clear(faceToken);

        //Get the face using faceToken, return an image in base64 format
        const image = await FacePass.getFace(faceToken, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })

        //Bind to face to a group using faceToken and groupName
        FacePass.bindGroup(faceToken, groupName, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })

        //Get all facetoken from the provided groupname
        FacePass.getGroupInfo(groupName, (data) => {
            //data=Array of list of facetoken
        }, (failure) => {
            //Error message
        }, 

        //Unbind the faceToken from provided group
        FacePass.unbindFace(facetoken, groupName, (data) => {
             //data=Array of list of facetoken 
          }, (success) => {
            //success message
          }, (failure) => {
            //Error message
          })

```

- Other function (Not relate to FacePass)
```sh
    //function name can be change to anything
    //defaultGroupName need to get yourself
    //e.g. a text field
    //This default group is the group that will be
    load on main screen
    
    async function changeDefaultGroup(){
        DefaultPreference.set("group_name",defaultGroupName);
    }
```
```sh
      //function to save facial recognition parameter
      //Restart is required for it to take effect
        async function save() {
        const data = {
            rcAttributeAndOcclusionMode: rcAttributeAndOcclusionMode //0-5,whole number,Default:1,
            searchThreshold: searchThreshold //0-100,Default:69,
            livenessThreshold: livenessThreshold //0-100,Default:55,
            livenessEnabled: livenessEnabled //true,false,Default:true,
            rgbIrLivenessEnabled: false //Currently only can false,
            poseThresholdRoll: poseThresholdRoll //0-90,Default:35,
            poseThresholdPitch: poseThresholdPitch //0-90,Default:35,
            poseThresholdYaw: poseThresholdYaw //0-90,Default:351,
            blurThreshold: blurThreshold //0-1,Default:0.8,
            lowBrightnessThreshold: lowBrightnessThreshold //0-255,Default:30,
            highBrightnessThreshold: highBrightnessThreshold //0-255,Default:210,
            brightnessSTDThreshold: brightnessSTDThreshold //0-255,Default:80,
            faceMinThreshold: faceMinThreshold //0-512,whole number,Default:100,
            retryCount: retryCount //1-unlimited,whole number,Default:2,
            smileEnabled: smileEnabled //true/false,Default:false,
            maxFaceEnabled: maxFaceEnabled //true/false,Default:true,
            FacePoseThresholdPitch: FacePoseThresholdPitch //0-90,Default:35,
            FacePoseThresholdRoll: FacePoseThresholdRoll //0-90,Default:35,
            FacePoseThresholdYaw: FacePoseThresholdYaw //0-90,Default:35,
            FaceBlurThreshold: FaceBlurThreshold //0-255,Default:0.7,
            FaceLowBrightnessThreshold: FaceLowBrightnessThreshold //0-255,Default:70,
            FaceHighBrightnessThreshold: FaceHighBrightnessThreshold //0-255,Default:220,
            FaceBrightnessSTDThreshold: FaceBrightnessSTDThreshold //0-255,Default:60,
            FaceFaceMinThreshold: FaceFaceMinThreshold //0-512,whole number,Default:100,
            FaceRcAttributeAndOcclusionMode: FaceRcAttributeAndOcclusionMode //whole number //0-5,Default:2,
        }
        DefaultPreference.set('parameters', JSON.stringify(data))
    }
```
```sh
  //function to save camera setting
        function save() {
          setisSettingAvailable(true);
          const data = {
              cameraFacingFront: cameraFacingFront //true,false,Default:false,
              faceRotation: faceRotation //0,90,180,270,Default:270,
              isSettingAvailable: true,//Leave it like this
              cameraPreviewRotation: cameraPreviewRotation //0,90,180,270,Default:90,
              isCross: isCross //true,false,Default:false,
          }
          DefaultPreference.set('settings', JSON.stringify(data))
         }
```

## Known Issue
The face recognition may not working if the app is first time launch without camera/file storage permission (Which mean the app ask for permission).Restart of application is needed after the app granted permission.


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
