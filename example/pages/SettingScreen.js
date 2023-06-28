import { useEffect, useState } from 'react';
import {
    StyleSheet, Text, View, TouchableOpacity,
} from 'react-native';
import RadioButtons from "../components/RadioButtons";
import DefaultPreference from 'react-native-default-preference';
import { cameraSetting, useIRCameraSupport, enableIRPreview } from 'facepass-react-native-module';

export default function SettingScreen({ navigation }) {
    const [cameraFacingFront, setcameraFacingFront] = useState();
    const [faceRotation, setfaceRotation] = useState();
    const [isSettingAvailable, setisSettingAvailable] = useState();
    const [cameraPreviewRotation, setcameraPreviewRotation] = useState();
    const [isCross, setisCross] = useState();
    const [useIR, setUseIR] = useState();
    const [useIRPreview, setUseIRPreview] = useState();

    const [isLoading, setisLoading] = useState(true);
    useEffect(() => {
        init_data();

    }, [])

    async function init_data() {
        try {
            const temp = await DefaultPreference.get("settings")
            const settings = JSON.parse(temp);
            if (settings) {
                setcameraFacingFront(settings.cameraFacingFront);
                setfaceRotation(settings.faceRotation);
                setcameraPreviewRotation(settings.cameraPreviewRotation);
                setisCross(settings.isCross);
                setisSettingAvailable(settings.isSettingAvailable)
                setUseIR(true)
                setUseIRPreview(false)
            } else {
                setcameraFacingFront(true);
                setfaceRotation(90);
                setcameraPreviewRotation(270);
                setisCross(false);
                setisSettingAvailable(true)
                setUseIR(true)
                setUseIRPreview(false)

            }
        } catch (e) {
            console.log(e)
        } finally {
            setisLoading(false)
        }
    }

    const camera_direction = [
        {
            text: "Front",
        },
        {
            text: "Back",
        },
    ]

    const camera_rotation = [
        {
            text: "0",
        },
        {
            text: "90",
        },
        {
            text: "180",
        },
        {
            text: "270",
        },

    ];

    const face_rotation = [
        {
            text: "0",
        },
        {
            text: "90",
        },
        {
            text: "180",
        },
        {
            text: "270",
        },

    ];

    const screen_cross = [
        {
            text: "Yes",
        },
        {
            text: "No",
        },
    ]

    const use_ir = [
        {
            text: "Yes",
        },
        {
            text: "No",
        },
    ]

    const use_ir_preview = [
        {
            text: "Yes",
        },
        {
            text: "No",
        },
    ]
    const onCameraDirection = (value) => {
        if (value === "Front") {
            setcameraFacingFront(true)
        } else if (value === "Back") {
            setcameraFacingFront(false)
        }
        console.log("direction", value);
    };

    const onCameraRotation = (value) => {
        setcameraPreviewRotation(value)
        console.log("rotation", value);
    };

    const onFaceRotation = (value) => {
        setfaceRotation(value)
        console.log("face_rotation", value);
    };
    const onScreenCross = (value) => {
        if (value === "Yes") {
            setisCross(true)
        } else if (value === "No") {
            setisCross(false)
        }
        console.log("cross", value);
    };
    const onUseIr = (value) => {
        if (value === "Yes") {
            setUseIR(true)
        } else if (value === "No") {
            setUseIR(false)
        }
        console.log("IR", value);
    };
    const onUseIrPreview = (value) => {
        if (value === "Yes") {
            setUseIRPreview(true)
        } else if (value === "No") {
            setUseIRPreview(false)
        }
        console.log("IR", value);
    };

    function save() {
        setisSettingAvailable(true);
        const data = {
            cameraFacingFront: cameraFacingFront,
            faceRotation: faceRotation,
            isSettingAvailable: true,
            cameraPreviewRotation: cameraPreviewRotation,
            isCross: isCross
        }
        DefaultPreference.set('settings', JSON.stringify(data))
        cameraSetting(data);
        useIRCameraSupport(useIR);
        enableIRPreview(useIRPreview);
        navigation.navigate('Home')

    }

    function reset() {
        setisSettingAvailable(false);
        const data = {
            cameraFacingFront: true,
            faceRotation: 270,
            isSettingAvailable: false,
            cameraPreviewRotation: 90,
            isCross: false
        }
        DefaultPreference.set('settings', JSON.stringify(data))
        cameraSetting(data);
        useIRCameraSupport(true);
        enableIRPreview(false);
        navigation.navigate('Home')
    }


    return (
        isLoading ? "" :
            <View style={{ flexDirection: "column", display: "flex" }}>
                <Text>Camera direction</Text>
                {console.log("ABC", cameraFacingFront)}
                <RadioButtons values={camera_direction} onPress={onCameraDirection} defaults={cameraFacingFront ? 0 : 1} />
                <Text>Camera Rotation</Text>
                <RadioButtons values={camera_rotation} onPress={onCameraRotation} defaults={cameraPreviewRotation == 0 ? 0 : cameraPreviewRotation == 90 ? 1 : cameraPreviewRotation == 180 ? 2 : 3} />
                <Text>Face Rotation</Text>
                <RadioButtons values={face_rotation} onPress={onFaceRotation} defaults={faceRotation == 0 ? 0 : faceRotation == 90 ? 1 : faceRotation == 180 ? 2 : 3} />
                <Text>Screen cross</Text>
                <RadioButtons values={screen_cross} onPress={onScreenCross} defaults={isCross ? 0 : 1} />
                <Text>Use IR</Text>
                <RadioButtons values={use_ir} onPress={onUseIr} defaults={useIR ? 0 : 1} />
                <Text>Use IR Preview</Text>
                <RadioButtons values={use_ir_preview} onPress={onUseIrPreview} defaults={useIRPreview ? 0 : 1} />
                <View style={{ paddingHorizontal: 20, paddingTop: 50, justifyContent: "space-between", flexDirection: "row", display: "flex" }} >
                    <TouchableOpacity style={styles.button} onPress={() => { save() }}>
                        <Text>Save</Text>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.button} onPress={() => { reset() }}>
                        <Text>Reset</Text>
                    </TouchableOpacity>
                </View>
            </View>

    )
}


const styles = StyleSheet.create({

    button: {
        alignItems: 'center',
        backgroundColor: '#DDDDDD',
        padding: 10,
    },

});
