import { StatusBar } from 'expo-status-bar';
import {
  Image,
  TouchableOpacity,
  Dimensions,
  StyleSheet,
  View,
  PixelRatio,
  UIManager,
  Text,
  AppState,
  findNodeHandle,
  NativeEventEmitter,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useEffect, useRef, useState } from 'react';
import {
  FacePassViewManager,
  FacePass,
  enableQRScan,
  initData,
  enableTemperature,
  pauseListener,
} from 'facepass-react-native-module';
import { useIsFocused } from '@react-navigation/native';
function createFragment(viewId) {
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    UIManager.FacePassViewManager.Commands.create.toString(),
    [viewId]
  );
  // console.log("TEST");
}
const destroyFragment = (viewId) =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    UIManager.FacePassViewManager.Commands.remove.toString(),
    [viewId]
  );

export default function Home({ navigation }) {
  const windowwidth = Dimensions.get('window').width;
  const windowheight = Dimensions.get('window').height;
  const faceDetectionImage = useRef();
  const faceDetectionText = useRef();
  const faceDetectionTimeText = useRef();
  const faceDetectionView = useRef();
  const [image, setImage] = useState(null);
  const [name, setName] = useState(null);
  const [testimage, settestimage] = useState(null);
  const [appState, setAppState] = useState(AppState.currentState);
  const isFocused = useIsFocused();

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

  useEffect(() => {
    if (image) {
      faceDetectionView.current.setNativeProps({
        style: { backgroundColor: 'black' },
      });
      faceDetectionImage.current.setNativeProps({
        style: { opacity: 1 },
      });
      faceDetectionText.current.setNativeProps({
        style: { color: 'white' },
      });
      faceDetectionTimeText.current.setNativeProps({
        style: { color: 'white' },
      });
    } else {
      faceDetectionView.current.setNativeProps({
        style: { backgroundColor: 'transparent' },
      });
      faceDetectionImage.current.setNativeProps({
        style: { opacity: 0 },
      });
      faceDetectionText.current.setNativeProps({
        style: { color: 'transparent' },
      });
      faceDetectionTimeText.current.setNativeProps({
        style: { color: 'transparent' },
      });
    }
  }, [image]);

  useEffect(() => {
    enableQRScan(false);
    enableTemperature(true);

    // Add an event listener to receive the data
    const dataListener = eventEmitter.addListener(
      'FaceDetectedEvent',
      async (params) => {
        console.log('detected');
        if (image == null && name == null) {
          console.log('PARAMS', params);
          const facetoken = params.faceToken;
          const data = JSON.parse(await AsyncStorage.getItem(facetoken));
          setImage(params.image);
          setName(data.faceName);
        }
      }
    );
    const stopListener = eventEmitter.addListener(
      'FaceDetectedEndEvent',
      async () => {
        setImage(null);
        setName(null);
      }
    );

    const unknownFaceListener = eventEmitter.addListener(
      'UnknownFaceDetectedEvent',
      async (params) => {
        console.log(params);
        // const temp=JSON.parse(params.jsonString);
        // settestimage(temp.imd
      }
    );

    const qrDetectListener = eventEmitter.addListener(
      'QRDetectedEvent',
      async (params) => {
        console.log(params);
      }
    );

    return () => {
      // Clean up and remove event listeners
      dataListener.remove();
      stopListener.remove();
      unknownFaceListener.remove();
      qrDetectListener.remove();
    };
  });
  function getCurrentTime() {
    const currentTime = new Date();
    let hours = currentTime.getHours();
    const minutes = currentTime.getMinutes();
    const seconds = currentTime.getSeconds();
    let amPm = '';

    // Determine AM/PM and convert to 12-hour format
    if (hours >= 12) {
      amPm = 'PM';
      hours = hours === 12 ? 12 : hours - 12;
    } else {
      amPm = 'AM';
      hours = hours === 0 ? 12 : hours;
    }

    const formattedTime = `${String(hours).padStart(2, '0')}:${String(
      minutes
    ).padStart(2, '0')}:${String(seconds).padStart(2, '0')} ${amPm}`;
    return formattedTime;
  }

  return (
    <View>
      <View
        style={{
          zIndex: 1,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'flex-start',
        }}
      >
        <View style={{ flexGrow: 0, flexShrink: 1 }}>
          <TouchableOpacity
            style={[styles.button, { maxWidth: 100 }]}
            onPress={() => {
              navigation.navigate('FaceManagement');
            }}
          >
            <Text style={{ color: 'white' }}>Setting</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.button, { maxWidth: 100 }]}
            onPress={() => {
              pauseListener(true);
            }}
          >
            <Text style={{ color: 'white' }}>Pause</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.button, { maxWidth: 100 }]}
            onPress={() => {
              pauseListener(false);
            }}
          >
            <Text style={{ color: 'white' }}>Unpause</Text>
          </TouchableOpacity>
        </View>
        <View>
          {testimage ? (
            <Image
              style={{ opacity: 1, width: 70, height: 70 }}
              source={{
                uri: 'data:image/png;base64,' + testimage,
              }}
            />
          ) : (
            ''
          )}
        </View>
        <View
          ref={faceDetectionView}
          style={{
            backgroundColor: 'transparent',
            margin: 50,
            padding: 5,
            display: 'flex',
            flexDirection: 'row',
          }}
        >
          <Image
            ref={faceDetectionImage}
            style={{ opacity: 0, width: 70, height: 70 }}
            source={{
              uri: 'data:image/png;base64,' + image,
            }}
          />
          <View style={{ display: 'flex', flexDirection: 'column' }}>
            <Text ref={faceDetectionText} style={{ color: 'transparent' }}>
              {name}
            </Text>
            <Text ref={faceDetectionTimeText} style={{ color: 'transparent' }}>
              {getCurrentTime()}
            </Text>
          </View>
        </View>
      </View>
      <FacePassViewManager
        style={{
          // converts dpi to px, provide desired height
          height: PixelRatio.getPixelSizeForLayoutSize(windowheight - 80),
          // converts dpi to px, provide desired width
          width: PixelRatio.getPixelSizeForLayoutSize(windowwidth),
        }}
        ref={ref}
      />
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  button: {
    alignSelf: 'flex-end',
    backgroundColor: '#33b5e5',
    padding: 10,
    minWidth: 100,
  },
});
