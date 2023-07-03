import { Camera, CameraType } from 'expo-camera';
import { useEffect, useRef, useState } from 'react';
import {
  StyleSheet,
  View,
  TouchableOpacity,
  Text,
  TextInput,
  BackHandler,
} from 'react-native';
import * as MediaLibrary from 'expo-media-library';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { addFace, bindGroup } from 'facepass-react-native-module';
import Toast from 'react-native-toast-message';

export default function AddFace() {
  const cameraRef = useRef(null);
  const [faceName, setFaceName] = useState(null);
  const [groupName, setGroupName] = useState(null);

  const takePictureAsync = async () => {
    if (faceName != null && groupName != null) {
      if (cameraRef.current) {
        try {
          const { uri } = await cameraRef.current.takePictureAsync();
          savePicture(uri);
        } catch (error) {
          Toast.show({
            type: 'error',
            text1: 'Error taking picture',
            text2: error,
          });
        }
      }
    } else {
      Toast.show({
        type: 'error',
        text1: 'Face name or group name field is empty',
      });
    }
  };
  const savePicture = async (pictureUri) => {
    try {
      const asset = await MediaLibrary.createAssetAsync(pictureUri);
      const fileName = asset.uri.split('file://').pop();
      try {
        const token = await addFace(fileName);
        const successMessage = await bindGroup(token, groupName);
        const data = { faceName: faceName, fileName: fileName };
        AsyncStorage.setItem(token, JSON.stringify(data));
        Toast.show({
          type: 'success',
          text1: 'Face added successfully',
        });
      } catch (e) {
        Toast.show({
          type: 'error',
          text1: 'Add face error',
          text2: e,
        });
      }
    } catch (error) {
      console.error('Error saving picture:', error);
    }
  };

  return (
    <View style={styles.container}>
      <Camera ref={cameraRef} style={styles.camera} type={CameraType.front} />
      <View style={styles.buttonContainer}>
        <TextInput
          style={styles.border}
          onChangeText={setFaceName}
          value={faceName}
          placeholder="Face Name"
        />
        <TextInput
          style={styles.border}
          onChangeText={setGroupName}
          value={groupName}
          placeholder="Group Name"
        />
        <View style={{ paddingHorizontal: 4 }} />
        <TouchableOpacity style={styles.button} onPress={takePictureAsync}>
          <Text style={styles.text}>Take Picture</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  camera: {
    flex: 10,
  },
  buttonContainer: {
    flex: 1,
    flexDirection: 'row',
    backgroundColor: 'transparent',
    margin: 10,
  },
  button: {
    flex: 1,
    borderWidth: 1,
    alignSelf: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'black',
  },
  border: {
    flex: 1,
    borderWidth: 1,
    paddingVertical: 4,
    paddingHorizontal: 5,
  },
});
