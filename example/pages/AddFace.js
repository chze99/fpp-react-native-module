import { Camera, CameraType } from 'expo-camera';
import { useEffect, useRef, useState } from 'react';
import { StyleSheet, View, TouchableOpacity, Text, TextInput, BackHandler } from 'react-native';
import * as MediaLibrary from 'expo-media-library';
import DefaultPreference from 'react-native-default-preference';
import { addFace, bindGroup } from 'facepass-react-native-module';
export default function AddFace() {

    const cameraRef = useRef(null);
    const [faceName, setFaceName] = useState(null);

 

    const takePictureAsync = async () => {
        if (faceName != null) {
            if (cameraRef.current) {
                try {
                    const { uri } = await cameraRef.current.takePictureAsync();
                    savePicture(uri);


                } catch (error) {
                    console.error('Error taking picture:', error);
                }
            }
        } else {
            console.error('Error no face name');

        }
    }
    const savePicture = async (pictureUri) => {

        try {
            const asset = await MediaLibrary.createAssetAsync(pictureUri);
            const fileName = asset.uri.split('file://').pop();
            console.log('Picture saved:', fileName);
            try {
                const token = await addFace(fileName)
                const successMessage = await bindGroup(token, "testi"
                )
                DefaultPreference.set(token, faceName)
            } catch (e) {
                console.log(e)
            }


        } catch (error) {
            console.error('Error saving picture:', error);
        }
    };



    return (
        <View style={styles.container}>
            <Camera ref={cameraRef} style={styles.camera} type={CameraType.back}>
            </Camera>
            <View style={styles.buttonContainer}>
                <TextInput
                    style={styles.border}
                    onChangeText={setFaceName}
                    value={faceName}
                    placeholder="FaceName "
                />
                <View style={{ paddingHorizontal: 4 }}></View>
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
        borderWidth: 1, paddingVertical: 4, paddingHorizontal: 5
    },
})