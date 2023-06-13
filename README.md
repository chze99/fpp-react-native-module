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

- ViewManager.js(Can be any name)
```sh
import {requireNativeComponent} from 'react-native';

export const FacePassViewManager =
  requireNativeComponent('FacePassViewManager');
```

- MainScreen.js(Can be any name)
```sh
import {UIManager,findNodeHandle,BackHandler, NativeEventEmitter} from 'react-native';
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

- Other function
```sh
        const { FacePass } = NativeModules

        FacePass.select_image((uri) => {
            //do something, uri=imagepath
        }, (err) => {
            //Error message
        })

        const face =await FacePass.addFace(imagePath, 
            (success) => {
               //Success message
            }, (failure) => {
               //Error message
            }
        );
        if (temp != "") {
                DefaultPreference.set(face, "Face owner name");
        }

        FacePass.deleteFace(faceToken, groupName, (success) => {
                console.log(success);
            }, (failure) => {
                console.log(failure);
          })
        await DefaultPreference.clear(faceToken);

        const image = await FacePass.getFace(faceToken, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })

        FacePass.bindGroup(faceToken, groupName, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })

        FacePass.getGroupInfo(groupName, (data) => {
            //data=Array of list of group 
        }, (failure) => {
            //Error message
        }, 

         FacePass.unbindFace(facetoken, groupName, (data) => {
             //data=Array of list of group 
          }, (success) => {
            //success message
          }, (failure) => {
            //Error message
          })

          FacePass.getAllGroup((data) => {
             //data=Array of list of group 
        }, (failure) => {
            //Error message
        })

         FacePass.createGroup(name, (success) => {
            //success message
        }, (failure) => {
            //Error message
        })

          FacePass.deleteGroup(name, (data) => {
            //success message
        }, (failure) => {
            //Error message
        })


```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
