import { StatusBar } from 'expo-status-bar';
import { Image, TouchableOpacity, Dimensions,  StyleSheet, View, PixelRatio, UIManager, Text, AppState, findNodeHandle,  NativeEventEmitter } from 'react-native';
import DefaultPreference from 'react-native-default-preference';
import { useEffect, useRef, useState } from 'react';
import { FacePassViewManager, FacePass } from 'facepass-react-native-module';
import { useIsFocused } from '@react-navigation/native';
function createFragment(viewId) {
    UIManager.dispatchViewManagerCommand(
        viewId,
        // we are calling the 'create' command
        UIManager.FacePassViewManager.Commands.create.toString(),
        [viewId],
    );
    // console.log("TEST");
}
const destroyFragment = viewId =>
    UIManager.dispatchViewManagerCommand(
        viewId,
        // we are calling the 'create' command
        UIManager.FacePassViewManager.Commands.remove.toString(),
        [viewId],
    );

export default function Home({ navigation }) {
    const windowwidth = Dimensions.get('window').width;
    const windowheight = Dimensions.get('window').height;
    const faceDetectionImage = useRef()
    const [image, setImage] = useState("");
    const [name, setName] = useState("");
    const [appState, setAppState] = useState(AppState.currentState);
    const isFocused = useIsFocused();





    useEffect(() => {
        async function setPreferenceName() {
            await DefaultPreference.setName("fppreactnative")
        }
        setPreferenceName()
    }, [])


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
    const dataListener = eventEmitter.addListener('FaceDetectedEvent', async (params) => {
        //Do something here,e.g.
        const image = params.image;
        const facetoken = params.name;
        //Name= image owner name,image=image in base64
        setImage(image);
        setName(await DefaultPreference.get(facetoken))
    });

    return (
        <View >
            <View style={{ zIndex: 1 }}>
                <TouchableOpacity style={styles.button} onPress={() => { navigation.navigate('FaceManagement') }}>
                    <Text style={{ color: "white" }}>Setting</Text>
                </TouchableOpacity>

                {/* <Image
        //     ref={faceDetectionImage}
        //     style={{ opacity: 1, width: 70, height: 70 }}
        //     source={{
        //       uri: 'data:image/png;base64,' + image,
        //     }}
        //   /> */}
                <View style={{ backgroundColor: "white" }}>
                    <Image
                        ref={faceDetectionImage}
                        style={{ opacity: 1, width: 70, height: 70 }}
                        source={{
                            uri: 'data:image/png;base64,' + image,
                        }}
                    />
                    <View style={{ display: 'flex', flexDirection: 'column' }}>
                        <Text >{name}</Text>
                    </View>
                </View>
            </View>
            <FacePassViewManager
                style={{

                    // converts dpi to px, provide desired height
                    height: PixelRatio.getPixelSizeForLayoutSize(
                        windowheight - 80
                    ),
                    // converts dpi to px, provide desired width
                    width: PixelRatio.getPixelSizeForLayoutSize(
                        windowwidth
                    ),
                }}

                ref={ref}
            />
            <StatusBar style="auto" />
        </View>
    );
}

const styles = StyleSheet.create({

    button: {
        alignItems: 'center',
        backgroundColor: '#33b5e5',
        padding: 10,
        minWidth: 100
    },
});
