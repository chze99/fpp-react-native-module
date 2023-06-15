# fpp-react-native-module

Face Recognition Module for React Native

## Installation

```sh
npm install fpp-react-native-module
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
import { NativeModules } from 'react-native';

export default function App(){
  const { FacePass } = NativeModules

  useEffect(()=>{
    async function init(){
      setting = //get ur setting from storage
      groupname= //get ur groupname from storage
      parameter = //get ur parameter from storage

      //this one is example using DefaultPreference package
      setting = await DefaultPreference.get("settings")

      //Then call function to initilize,all parameter need to be in JSON string format.
      FacePass.cameraSetting(setting);
      FacePass.setDefaultGroupName(groupname);
      FacePass.initData(parameter)
      }
      init()
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

- Initial FacePass
```sh
        const { FacePass } = NativeModules
```

- Get a list of group name
```sh
        FacePass.getAllGroup((data) => {
             //data=Array of list of group 
        }, (failure) => {
            //Error message
        })
```

- Create a new group with provided name
```sh
        FacePass.createGroup(name, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })
```

- Delete the group with provided name
```sh
        FacePass.deleteGroup(name, (data) => {
            //success message
        }, (failure) => {
            //Error message
        })
```

- Open file explorer and choose image
```sh
        FacePass.selectImage((uri) => {
            //do something, uri=imagepath
        }, (err) => {
            //Error message
        })
```

- Add selected face into facial recognition
```sh
        FacePass.addFace(imagePath, 
            (success) => {
              //Here is to set the face owner name so that the app can know the face belong to who
              //"Face owner name" need to be replace with own function to get name,e.g an text field
             
              DefaultPreference.set(faceToken, "Face owner name");
            }, (failure) => {
               //Error message
            }
        );
```

- Delete selected face
```sh
        //faceToken is generated from addFace function
        //groupName= Name of group with that Face added
        FacePass.deleteFace(faceToken, groupName, (success) => {
                console.log(success);
            }, (failure) => {
                console.log(failure);
          })
        //Delete face data from storage
        await DefaultPreference.clear(faceToken);
```

- Get the face using faceToken, return an image in base64 format
```sh
      
       FacePass.getFace(faceToken, (success) => {
            //success action,e.g. setImage(success) , setImage is the useState() variable.
        }, (failure) => {
            //Error message
        })
```

- Bind to face to a group using faceToken and groupName
```sh

        FacePass.bindGroup(faceToken, groupName, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })
```

- Get all facetoken from the provided groupname
```sh
        FacePass.getGroupInfo(groupName, (data) => {
            //data=Array of list of facetoken
        }, (failure) => {
            //Error message
        }, 
```

- Unbind the faceToken from provided group
```sh
        FacePass.unbindFace(facetoken, groupName, (data) => {
             //data=Array of list of facetoken 
          }, (success) => {
            //success message
          }, (failure) => {
            //Error message
          })
```

***Other function (Not relate to FacePass)***

- Change the default local group(Loaded for facial recognition)
```sh
    //function name can be change to anything
    //defaultGroupName need to get yourself
    //e.g. a text field
    //This default group is the group that will be
    load on main screen
    
    async function changeDefaultGroup(){
        DefaultPreference.set("group_name",defaultGroupName);
        FacePass.setDefaultGroupName(defaultGroupName);
    }
```

- Example of function to save facial recognition parameter,restart is required for it to take effect
```sh
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
  //Example of function to save camera setting
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
          FacePass.cameraSetting(JSON.stringify(data));

         }
```

## Known Issue
The face recognition may not working if the app is first time launch after installation/clear storage or cache.Restart of application is needed after the app granted permission.


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
