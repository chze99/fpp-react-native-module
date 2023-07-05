# facepass-react-native-module

Face Recognition Module for React Native

## Installation

```sh
npm install facepass-react-native-module
```

Require use of any package that allow react native to save and retrieve data, the data need to store as  JSON.

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
  <uses-permission android:name=" android.permission.SYSTEM_CAMERA
  " />
```

```sh
  <application>
    ....
   <service android:name="com.fppreactnativemodule.TemperatureService" android:enabled="true"/>   <!--   Add this   -->
  </application>

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
      async function init() {
        //DefaultPreference is the example package used to store data in this example,can choose whatever package you like
        const setting = await DefaultPreference.get("parameters")
        const camera = await DefaultPreference.get("settings")

        //Check and create a default group if no group is exist
        let group = "default";
        try {
          const success = await initData(JSON.parse(setting))
          try {
            const temp_group = await DefaultPreference.get("group_name")
            if (temp_group != null) {
              group = temp_group;
            }
            try {
              const check = await checkGroupExist(group)
            } catch (e) {
              try {
                const create = await createGroup(group)
               DefaultPreference.set("group_name",group)
              } catch (e) {
                  //Create group error
              }
            }
          } catch (e) {
            //Get group_name error
          }
        } catch (e) {
          //Init error
        }
        cameraSetting(JSON.parse(camera));
        setDefaultGroupName(group);

        setIsLoading(false)
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
  import { useIsFocused } from '@react-navigation/native';

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

        if (isFocused) {
            createFragment(viewId);
        } else {
            destroyFragment(viewId);
        }
    }, [isFocused]);



  // Add an event listener to receive the data
  useEffect(()=>{
      const dataListener = eventEmitter.addListener('FaceDetectedEvent', async (params) => {
        //Do something here,e.g.
        const faceToken = params.faceToken;
        //List of data:
          //faceToken //use to get image/as an id to recognize a person identidy
          //trackID //ID number of tracking ,for reference
          //searchScore //percentage it match the detected face
          //searchThreshold //percentage required for the application to say the face match the detected face
          //hairType //0-5,BALD, LITTLE_HAIR, SHORT_HAIR, LONG_HAIR, UNKNOWN, INVALID
          //beardType //0-4, NO_BEARD, MOUSTACHE, WHISKER, UNKNOWN, INVALID
          //hatType //0-4, NO_HAT, SAFETY_HELMET, OTHERS, UNKNOWN, INVALID
          //respiratorType //0-4,  COVER_MOUTH_NOSE, COVER_MOUTH, NO_RESPIRATOR, UNKNOWN, INVALID
          //glassesType //0-4,  NO_GLASSES, GLASSES_WITH_DARK_FRAME, GLASSES_OTHERS, UNKNOWN, INVALID
          //skinColorType //0-4, YELLOW, WHITE, BLACK, BROWN, INVALID
      });

    const stopListener = eventEmitter.addListener(
      'FaceDetectedEndEvent', async () => {
          //Do something after recognition such as remove the result view
      }
    )
    const unknownFaceListener= eventEmitter.addListener(
      'UnknownFaceDetectedEvent',
      async(params) =>{
        //Do something here,e.g.
        const image=params.image;
        //List of data:
          //image //image in base64 format
          //trackID //ID number of tracking ,for reference
          //searchScore //percentage it match the detected face ,  //may not available if no face in local group
          //searchThreshold //percentage required for the application to say the face match the detected face, //may not available if no face in local group
          //hairType //0-5,BALD, LITTLE_HAIR, SHORT_HAIR, LONG_HAIR, UNKNOWN, INVALID
          //beardType //0-4, NO_BEARD, MOUSTACHE, WHISKER, UNKNOWN, INVALID
          //hatType //0-4, NO_HAT, SAFETY_HELMET, OTHERS, UNKNOWN, INVALID
          //respiratorType //0-4,  COVER_MOUTH_NOSE, COVER_MOUTH, NO_RESPIRATOR, UNKNOWN, INVALID
          //glassesType //0-4,  NO_GLASSES, GLASSES_WITH_DARK_FRAME, GLASSES_OTHERS, UNKNOWN, INVALID
          //skinColorType //0-4, YELLOW, WHITE, BLACK, BROWN, INVALID
      }
    )

    return () => {
      // Clean up and remove event listeners
      dataListener.remove();
      stopListener.remove();
      unknownFaceListener.remove();
    };
    
  },[])
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
    
    //Image path can be wherether local path or an url image
    //Here is to set the face owner name so that the app can know the face belong to who
    //"Face owner name" need to be replace with own function to get name,e.g an text field
    //DefaultPreference is the example package used to store data in this example,can choose whatever package you like
    
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
    const successMessage=await deleteFace("faceToken", "groupName")
    //Then delete face from local storage if got
  }catch(error){
    //Error message
  }
```

- Get the face using faceToken, return an image in base64 format
```sh
  try{
    //base64 image action,e.g. setImage(base64Image) , setImage is the useState() variable.
    const base64Image =await getFace("faceToken")
  }catch(e){
    //Error message
  }
```

- Bind to face to a group using faceToken and groupName
```sh
  try{
      const successMessage=await bindGroup("faceToken","groupName")
    }catch(e){
      //Error message
    }
```

- Get all facetoken from the provided groupname
```sh
  try{
    const faceTokenListArray=await getGroupInfo("groupName")
  }catch(e){
    //Error message
  }
```

- Unbind the faceToken from provided group
```sh
  try{
      const faceTokenListArray=await unbindFace("facetoken", "groupName")
    }catch(e){
      //Error message
    }
```

- Check if group exist
```sh
  try{
     const check = await checkGroupExist("groupname")
  }catch(e){
    //Error message
  }
```

- Set the time to show face recogntion(Optional)
```sh
  setRecognitionDisplayTime(time)
  //time= integer,in milli-second
```

- Control the light
```sh
  changeLight("color")
  //List of color:"red","green","white","yellow","off"
  //"off" is to turn off light,the light need to be turn off on app exit else will remain turn on
```

- Control the door(Not test yet)
```sh
  controlDoor("command")
  //Command: "open","close"
```

- Use IR camera as support for face recognition
```sh
  useIRCameraSupport(boolean)
  //Command: true/false
```

- Restart the device 
```sh
  restartDevice()
```

- Enable IR camera preview
```sh
  enableIRPreview(boolean)
  //Command: true/false

```

- Enable temperature detection(Untested)
```sh
  enableTemperature(boolean)
  //Command: true/false
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

- List of success/error code
```sh
FACEPASSHANDLER_NULL_ERROR //The face pass handler is not initialized,suggest to restart the app
IMAGE_SELECT_CANCELED_ERROR //User cancel the image selection
IMAGE_SELECT_FAIL_ERROR //Something wrong on image selected
ACTIVITY_NOT_EXIST_ERROR //Cannot get the current activity
IMAGE_NOT_EXIST_ERROR //The path does not contain image
INVALID_IMAGE_PATH_ERROR //The passed string not a valid path
FACE_ADDED_SUCCESSFULLY //Successfully add the face for face recognition
NO_FACE_DETECTED_IN_IMAGE_ERROR //The provided image does not contain face
FACE_IMAGE_QUALITY_ERROR  //The face image does not meet the required quality
NULL_GROUPNAME_ERROR  // Group name is null
FACE_DELETE_SUCCESS // Success to delete face
GROUP_NAME_IS_NULL_ERROR // Null input for group name
FACETOKEN_IS_NULL_ERROR //Empty facetoken
BIND_GROUP_SUCCESS // Successfully bind group
BIND_GROUP_FAILED //  Fail to bind group,probably the provided group not exist
GROUP_NAME_NOT_EXIST_ERROR // group with the provided group name is not exist
GET_LOCAL_GROUP_INFO_ERROR // fail to get group info,probably the provided group not exist
FACE_UNBIND_SUCCESS // Successfully unbind the face from group
FACE_UNBIND_FAILED // Fail to unbind face from group
EMPTY_LOCAL_GROUP_ERROR //  No local group found in the application.
EMPTY_GROUP_INPUT_ERROR // No group anme provided
GROUP_CREATION_SUCCESS  //Success in group creation
GROUP_CREATION_FAILED //Fail to create group
GROUP_DELETION_SUCCESS //group delete success
```

## Known Issue

If facial recognition setting is changed,restart is require for new facial recognition setting to take affect.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)