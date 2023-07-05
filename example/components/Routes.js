import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import FaceManagement from '../pages/FaceManagement';
import Home from '../pages/Home';
import Home2 from '../pages/Home2';

import AddFace from '../pages/AddFace';
import FaceGroupSettingScreen from '../pages/FaceGroupSettingScreen';
import FacialRecognitionSettingScreen from '../pages/FacialRecognitionSettingScreen';
import SettingScreen from '../pages/SettingScreen';
import TestLightPage from '../pages/TestLightPage';
import GroupListPage from '../pages/GroupListPage';
import GroupListFacePage from '../pages/GroupListFacePage';
import { createStackNavigator } from '@react-navigation/stack';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import { SafeAreaProvider } from 'react-native-safe-area-context';
export default function Routes() {
  // const Stack = createStackNavigator();
  const Stack = createNativeStackNavigator();

  return (
    <SafeAreaProvider style={{ backgroundColor: '#000' }}>
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen name="Home" component={Home} />

          <Stack.Screen name="FaceManagement" component={FaceManagement} />

          <Stack.Screen name="Home2" component={Home2} />

          <Stack.Screen name="GroupListPage" component={GroupListPage} />
          
          <Stack.Screen
            name="GroupListFacePage"
            component={GroupListFacePage}
          />
         
          <Stack.Screen name="TestLightPage" component={TestLightPage} />
          
          <Stack.Screen name="AddFace" component={AddFace} />
         
          <Stack.Screen
            name="FaceGroupSettingScreen"
            component={FaceGroupSettingScreen}
          />
         
          <Stack.Screen
            name="FacialRecognitionSettingScreen"
            component={FacialRecognitionSettingScreen}
          />
          
          <Stack.Screen name="SettingScreen" component={SettingScreen} />
       
        </Stack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
