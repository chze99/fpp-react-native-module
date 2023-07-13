/* eslint-disable react/react-in-jsx-scope */
import { useEffect, useState } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  ScrollView,
  Alert,
} from 'react-native';
import RadioButtons from '../components/RadioButtons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import RNRestart from 'react-native-restart';
import { initData } from 'facepass-react-native-module';

export default function FacialRecognitionSettingScreen({ navigation }) {
  //List of facial data
  const [rcAttributeAndOcclusionMode, setrcAttributeAndOcclusionMode] =
    useState(1);
  const [searchThreshold, setsearchThreshold] = useState(69.0);
  const [livenessThreshold, setlivenessThreshold] = useState(55.0);
  const [livenessEnabled, setlivenessEnabled] = useState(true);
  const [rgbIrLivenessEnabled, setrgbIrLivenessEnabled] = useState(false);
  const [poseThresholdRoll, setposeThresholdRoll] = useState(35.0);
  const [poseThresholdPitch, setposeThresholdPitch] = useState(35.0);
  const [poseThresholdYaw, setposeThresholdYaw] = useState(35.0);
  const [blurThreshold, setblurThreshold] = useState(0.8);
  const [lowBrightnessThreshold, setlowBrightnessThreshold] = useState(30.0);
  const [highBrightnessThreshold, sethighBrightnessThreshold] = useState(210.0);
  const [brightnessSTDThreshold, setbrightnessSTDThreshold] = useState(80.0);
  const [faceMinThreshold, setfaceMinThreshold] = useState(100);
  const [retryCount, setretryCount] = useState(2);
  const [smileEnabled, setsmileEnabled] = useState(false);
  const [FacePoseThresholdPitch, setFacePoseThresholdPitch] = useState(35.0);
  const [maxFaceEnabled, setmaxFaceEnabled] = useState(true);
  const [FacePoseThresholdRoll, setFacePoseThresholdRoll] = useState(35.0);
  const [FacePoseThresholdYaw, setFacePoseThresholdYaw] = useState(35.0);
  const [FaceBlurThreshold, setFaceBlurThreshold] = useState(0.7);
  const [FaceLowBrightnessThreshold, setFaceLowBrightnessThreshold] =
    useState(70.0);
  const [FaceHighBrightnessThreshold, setFaceHighBrightnessThreshold] =
    useState(220.0);
  const [FaceBrightnessSTDThreshold, setFaceBrightnessSTDThreshold] =
    useState(60.0);
  const [FaceFaceMinThreshold, setFaceFaceMinThreshold] = useState(100);
  const [FaceRcAttributeAndOcclusionMode, setFaceRcAttributeAndOcclusionMode] =
    useState(2);
  const [isLoading, setisLoading] = useState(true);
  const [warning, setWarning] = useState({
    rcAttributeAndOcclusionMode: false,
  });

  useEffect(() => {
    init_data();
  }, []);

  async function init_data() {
    try {
      const temp = await AsyncStorage.getItem('parameters');
      const parameters = JSON.parse(temp);
      console.log(parameters);
      if (parameters) {
        setrcAttributeAndOcclusionMode(parameters.rcAttributeAndOcclusionMode),
          setsearchThreshold(parameters.searchThreshold),
          setlivenessThreshold(parameters.livenessThreshold),
          setlivenessEnabled(parameters.livenessEnabled),
          setrgbIrLivenessEnabled(false),
          setposeThresholdRoll(parameters.poseThresholdRoll),
          setposeThresholdPitch(parameters.poseThresholdPitch),
          setposeThresholdYaw(parameters.poseThresholdYaw),
          setblurThreshold(parameters.blurThreshold),
          setlowBrightnessThreshold(parameters.lowBrightnessThreshold),
          sethighBrightnessThreshold(parameters.highBrightnessThreshold),
          setbrightnessSTDThreshold(parameters.brightnessSTDThreshold),
          setfaceMinThreshold(parameters.faceMinThreshold),
          setretryCount(parameters.retryCount),
          setsmileEnabled(parameters.smileEnabled),
          setmaxFaceEnabled(parameters.maxFaceEnabled),
          setFacePoseThresholdPitch(parameters.FacePoseThresholdPitch),
          setFacePoseThresholdRoll(parameters.FacePoseThresholdRoll),
          setFacePoseThresholdYaw(parameters.FacePoseThresholdYaw),
          setFaceBlurThreshold(parameters.FaceBlurThreshold),
          setFaceLowBrightnessThreshold(parameters.FaceLowBrightnessThreshold),
          setFaceHighBrightnessThreshold(
            parameters.FaceHighBrightnessThreshold
          ),
          setFaceBrightnessSTDThreshold(parameters.FaceBrightnessSTDThreshold),
          setFaceFaceMinThreshold(parameters.FaceFaceMinThreshold),
          setFaceRcAttributeAndOcclusionMode(
            parameters.FaceRcAttributeAndOcclusionMode
          );
      }
    } catch (e) {
      console.log(e);
    } finally {
      setisLoading(false);
    }
  }

  function warningHandler(original_data, setFunction, dataname, status) {
    setFunction({ ...original_data, [dataname]: status });
  }

  const liveness_enabled = [
    {
      text: 'Enable',
    },
    {
      text: 'Disable',
    },
  ];
  const RGB_IR_liveness_enabled = [
    {
      text: 'Enable',
    },
    {
      text: 'Disable',
    },
  ];
  const smile_enabled = [
    {
      text: 'Enable',
    },
    {
      text: 'Disable',
    },
  ];
  const maxface_enabled = [
    {
      text: 'Enable',
    },
    {
      text: 'Disable',
    },
  ];

  function onSmileEnable(value) {
    if (value === 'Enable') {
      setsmileEnabled(true);
    } else if (value === 'Disable') {
      setsmileEnabled(false);
    }
  }
  function onRGBIRLivenessEnable(value) {
    if (value === 'Enable') {
      setrgbIrLivenessEnabled(true);
    } else if (value === 'Disable') {
      setrgbIrLivenessEnabled(false);
    }
  }
  function onLivenessEnable(value) {
    if (value === 'Enable') {
      setlivenessEnabled(true);
    } else if (value === 'Disable') {
      setlivenessEnabled(false);
    }
  }
  function onMaxFaceEnable(value) {
    if (value === 'Enable') {
      setmaxFaceEnabled(true);
    } else if (value === 'Disable') {
      setmaxFaceEnabled(false);
    }
  }
  async function save() {
    const data = {
      searchThreshold: searchThreshold,
    };
    AsyncStorage.setItem('parameters', JSON.stringify(data));
    initData(data);
    Alert.alert(
      'Attention',
      'Please restart your application for the changes to take effect',
      [
        {
          text: 'Manual restart later',
          onPress: () => console.log('Manual restart pressed'),
        },
        {
          text: 'Restart Now',
          onPress: () => RNRestart.Restart(),
        },
      ]
    );
  }

  async function reset() {
    const data = {
      rcAttributeAndOcclusionMode: 1,
      searchThreshold: 70,
      livenessThreshold: 65,
      livenessEnabled: false,
      rgbIrLivenessEnabled: false,
      poseThresholdRoll: 35,
      poseThresholdPitch: 35,
      poseThresholdYaw: 35,
      blurThreshold: 0.8,
      lowBrightnessThreshold: 70,
      highBrightnessThreshold: 220,
      brightnessSTDThreshold: 80,
      faceMinThreshold: 100,
      retryCount: 2,
      smileEnabled: false,
      maxFaceEnabled: true,
      FacePoseThresholdPitch: 35,
      FacePoseThresholdRoll: 35,
      FacePoseThresholdYaw: 35,
      FaceBlurThreshold: 0.7,
      FaceLowBrightnessThreshold: 70,
      FaceHighBrightnessThreshold: 220,
      FaceBrightnessSTDThreshold: 60,
      FaceFaceMinThreshold: 100,
      FaceRcAttributeAndOcclusionMode: 2,
    };
    AsyncStorage.setItem('parameters', data);
    navigation.navigate('Home');
  }

  function validateDecimal(min, max, input) {
    try {
      const min_float = parseFloat(min);
      const max_float = parseFloat(max);
      const input_float = parseFloat(input);
      if (input_float >= min_float && input_float <= max_float) {
        return true;
      } else {
        return false;
      }
    } catch (e) {
      console.log(e);
      return false;
    }
  }

  function validateInt(min, max, input) {
    if (input == '') {
      return true;
    }

    try {
      const min_int = parseInt(min);
      const max_int = parseInt(max);
      const input_int = parseInt(input);

      if (input_int >= min_int && input_int <= max_int) {
        return true;
      } else {
        return false;
      }
    } catch (e) {
      console.log(e);
      return false;
    }
  }

  return isLoading ? (
    ''
  ) : (
    <ScrollView style={styles.scrollView}>
      <View style={{ flexDirection: 'column', display: 'flex', marginLeft: 5 }}>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>rcAttributeAndOcclusionMode</Text>
          <TextInput
            editable
            onChangeText={(text) => {
              setrcAttributeAndOcclusionMode(parseInt(text));
              if (validateInt(0, 5, text)) {
                warningHandler(
                  warning,
                  setWarning,
                  'rcAttributeAndOcclusionMode',
                  false
                );
              } else {
                warningHandler(
                  warning,
                  setWarning,
                  'rcAttributeAndOcclusionMode',
                  true
                );
              }
            }}
            value={
              rcAttributeAndOcclusionMode.toString() != 'NaN'
                ? rcAttributeAndOcclusionMode.toString()
                : ''
            }
            style={styles.textInputView}
            inputMode="numeric"
          />
          {warning.rcAttributeAndOcclusionMode ? (
            <Text style={styles.warningView}>Inavlid value,Value:0-5</Text>
          ) : (
            <Text style={styles.hintView}>Value:0-5</Text>
          )}
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>Search Threshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setsearchThreshold(parseFloat(text))}
            value={searchThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-100</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>Liveness Threshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setlivenessThreshold(parseFloat(text))}
            value={livenessThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-100</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>Liveness Enabled</Text>
          <RadioButtons
            values={liveness_enabled}
            onPress={onLivenessEnable}
            defaults={livenessEnabled ? 0 : 1}
          />
        </View>
        {/* <View style={styles.inputFieldView}>
                        <Text style={styles.titleTextView}>RGB IR Liveness Enabled</Text>
                        <RadioButtons values={RGB_IR_liveness_enabled} onPress={onRGBIRLivenessEnable} defaults={rgbIrLivenessEnabled ? 0 : 1} />
                    </View> */}
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>poseThresholdRoll</Text>
          <TextInput
            editable
            onChangeText={(text) => setposeThresholdRoll(parseFloat(text))}
            value={poseThresholdRoll.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-90</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>poseThresholdPitch</Text>
          <TextInput
            editable
            onChangeText={(text) => setposeThresholdPitch(parseFloat(text))}
            value={poseThresholdPitch.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-90</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>poseThresholdYaw</Text>
          <TextInput
            editable
            onChangeText={(text) => setposeThresholdYaw(parseFloat(text))}
            value={poseThresholdYaw.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-90</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>blurThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setblurThreshold(parseFloat(text))}
            value={blurThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-1, decimal</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>lowBrightnessThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setlowBrightnessThreshold(parseFloat(text))}
            value={lowBrightnessThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-255</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>highBrightnessThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) =>
              sethighBrightnessThreshold(parseFloat(text))
            }
            value={highBrightnessThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-255</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>brightnessSTDThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setbrightnessSTDThreshold(parseFloat(text))}
            value={brightnessSTDThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-255</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>faceMinThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setfaceMinThreshold(parseInt(text))}
            value={faceMinThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-512</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>retryCount</Text>
          <TextInput
            editable
            onChangeText={(text) => setretryCount(parseInt(text))}
            value={retryCount.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:1-unlimited</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>smileEnabled</Text>
          <RadioButtons
            values={smile_enabled}
            onPress={onSmileEnable}
            defaults={smileEnabled ? 0 : 1}
          />
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>Max Face Enabled</Text>
          <RadioButtons
            values={maxface_enabled}
            onPress={onMaxFaceEnable}
            defaults={maxFaceEnabled ? 0 : 1}
          />
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FacePoseThresholdPitch</Text>
          <TextInput
            editable
            onChangeText={(text) => setFacePoseThresholdPitch(parseFloat(text))}
            value={FacePoseThresholdPitch.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-90</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FacePoseThresholdRoll</Text>
          <TextInput
            editable
            onChangeText={(text) => setFacePoseThresholdRoll(parseFloat(text))}
            value={FacePoseThresholdRoll.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-90</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FacePoseThresholdYaw</Text>
          <TextInput
            editable
            onChangeText={(text) => setFacePoseThresholdYaw(parseFloat(text))}
            value={FacePoseThresholdYaw.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-90</Text>
        </View>

        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FaceBlurThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setFaceBlurThreshold(parseFloat(text))}
            value={FaceBlurThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-1,decimal</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FaceLowBrightnessThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) =>
              setFaceLowBrightnessThreshold(parseFloat(text))
            }
            value={FaceLowBrightnessThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-255</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FaceHighBrightnessThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) =>
              setFaceHighBrightnessThreshold(parseFloat(text))
            }
            value={FaceHighBrightnessThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-255</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FaceBrightnessSTDThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) =>
              setFaceBrightnessSTDThreshold(parseFloat(text))
            }
            value={FaceBrightnessSTDThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-255</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>FaceFaceMinThreshold</Text>
          <TextInput
            editable
            onChangeText={(text) => setFaceFaceMinThreshold(parseInt(text))}
            value={FaceFaceMinThreshold.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-512</Text>
        </View>
        <View style={styles.inputFieldView}>
          <Text style={styles.titleTextView}>
            FaceRcAttributeAndOcclusionMode
          </Text>
          <TextInput
            editable
            onChangeText={(text) =>
              setFaceRcAttributeAndOcclusionMode(parseInt(text))
            }
            value={FaceRcAttributeAndOcclusionMode.toString()}
            style={styles.textInputView}
            inputMode="numeric"
          />
          <Text style={styles.hintView}>Value:0-5</Text>
        </View>

        <View
          style={{
            paddingHorizontal: 20,
            paddingTop: 50,
            paddingBottom: 10,
            justifyContent: 'space-between',
            flexDirection: 'row',
            display: 'flex',
          }}
        >
          <TouchableOpacity
            style={styles.button}
            onPress={() => {
              save();
            }}
          >
            <Text>Save</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.button}
            onPress={() => {
              reset();
            }}
          >
            <Text>Reset</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  button: {
    alignItems: 'center',
    backgroundColor: '#33b5e5',
    padding: 10,
    minWidth: 100,
  },
  warningView: {
    flex: 2,
    alignSelf: 'center',
    color: 'red',
  },
  inputFieldView: {
    display: 'flex',
    flexDirection: 'row',
    marginBottom: 10,
  },
  titleTextView: {
    flex: 3,
    marginRight: 8,
    alignSelf: 'center',
  },
  textInputView: {
    flex: 1,
    marginRight: 8,
    justifyContent: 'center',
    borderWidth: 1,
    paddingLeft: 2,
  },
  hintView: {
    flex: 2,
    alignSelf: 'center',
  },
});
