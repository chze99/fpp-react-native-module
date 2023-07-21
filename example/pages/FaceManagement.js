/* eslint-disable react-native/no-inline-styles */
/* eslint-disable react/react-in-jsx-scope */
import _ from 'lodash';
import { useState, useEffect } from 'react';
import { TouchableOpacity, View, Text, StyleSheet, Image } from 'react-native';
import DefaultPreference from 'react-native-default-preference';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {
  getGroupInfo,
  getFace,
  deleteFace,
  createGroup,
  restartDevice,
  controlDoor,
  initData,
  checkDoneInitialize,
  hideNavigationBar,
  setExposureCompensation,
} from 'facepass-react-native-module';
export default function FaceMangement({ navigation }) {
  useEffect(() => {
    console.log('test');
  }, []);

  async function init() {
    const setting = await AsyncStorage.getItem('parameters');
    const success = await initData(JSON.parse(setting));
  }

  return (
    <View>
      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('AddFace')}
      >
        <Text style={{ color: 'white' }}>Add face</Text>
      </TouchableOpacity>
      <View style={{ paddingVertical: 10 }} />
      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('FaceGroupSettingScreen')}
      >
        <Text style={{ color: 'white' }}>Face Group Setting Screen</Text>
      </TouchableOpacity>
      <View style={{ paddingVertical: 10 }} />

      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('FacialRecognitionSettingScreen')}
      >
        <Text style={{ color: 'white' }}>
          {' '}
          Facial Recognition Setting Screen
        </Text>
      </TouchableOpacity>
      <View style={{ paddingVertical: 10 }} />

      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('SettingScreen')}
      >
        <Text style={{ color: 'white' }}>Setting Screen</Text>
      </TouchableOpacity>
      <View style={{ paddingVertical: 10 }} />

      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('GroupListPage')}
      >
        <Text style={{ color: 'white' }}>Group List Page</Text>
      </TouchableOpacity>
      <View style={{ paddingVertical: 10 }} />

      <View style={{ paddingVertical: 10 }} />
      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('TestLightPage')}
      >
        <Text style={{ color: 'white' }}>Test Light Page</Text>
      </TouchableOpacity>

      <View style={{ paddingVertical: 10 }} />
      <View
        style={{
          paddingVertical: 10,
          display: 'flex',
          flexDirection: 'row',
          justifyContent: 'center',
        }}
      >
        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={() => restartDevice()}
        >
          <Text style={{ color: 'white' }}>Restart device</Text>
        </TouchableOpacity>
        <View style={{ padding: 10 }} />

        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={() => controlDoor('open')}
        >
          <Text style={{ color: 'white' }}>Open door</Text>
        </TouchableOpacity>
        <View style={{ padding: 10 }} />

        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={() => controlDoor('close')}
        >
          <Text style={{ color: 'white' }}>Close door</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={() => navigation.navigate('Home')}
        >
          <Text style={{ color: 'white' }}>home</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={() => navigation.navigate('Home2')}
        >
          <Text style={{ color: 'white' }}>home 2</Text>
        </TouchableOpacity>

        <View style={{ padding: 10 }} />
      </View>
      <View
        style={{
          paddingVertical: 10,
          display: 'flex',
          flexDirection: 'row',
          justifyContent: 'center',
        }}
      >
        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={async () => hideNavigationBar(true)}
        >
          <Text style={{ color: 'white' }}>Off Navbar</Text>
        </TouchableOpacity>
        <View style={{ padding: 10 }} />

        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={async () => hideNavigationBar(false)}
        >
          <Text style={{ color: 'white' }}>On Navbar</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={{ backgroundColor: '#33b5e5', padding: 10 }}
          onPress={async () => setExposureCompensation(5)}
        >
          <Text style={{ color: 'white' }}>Change exposure</Text>
        </TouchableOpacity>
      </View>
      <View style={{ paddingVertical: 10 }} />
    </View>
  );
}
const styles = StyleSheet.create({
  button: {
    alignItems: 'center',
    alignSelf: 'center',
    backgroundColor: '#33b5e5',
    padding: 10,
    minWidth: 300,
    maxWidth: 300,
  },
});
