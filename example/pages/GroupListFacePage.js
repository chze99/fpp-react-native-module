import {
  FlatList,
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Image,
} from 'react-native';
import { useEffect, useState } from 'react';
import { useRoute } from '@react-navigation/native';
import {
  getGroupInfo,
  getFace,
  deleteFace,
  unbindFace,
} from 'facepass-react-native-module';
import _ from 'lodash';
import AsyncStorage from '@react-native-async-storage/async-storage';
export default function GroupListFacePage({ navigation, props }) {
  const [facegrouplist, setFacelist] = useState('');
  const route = useRoute();
  const { name } = route.params;

  useEffect(() => {
    console.log(name);
    async function groupInfo() {
      try {
        const data = await getGroupInfo(name);
        console.log(data);
        const facedata = await Promise.all(
          _.map(data, async (datas) => {
            console.log('test', await AsyncStorage.getItem(datas));
            const dataList = JSON.parse(await AsyncStorage.getItem(datas));
            return {
              token: datas,
              name: dataList.faceName,
              image: dataList.fileName,
            };
          })
        );
        setFacelist(facedata);
        console.log(facedata);
      } catch (e) {
        // Toast.show({
        //     type: 'error',
        //     text1: 'Error get group info',
        //     text2: e,
        // });
        console.log('DATA', e);
      }
    }
    groupInfo();
  }, []);

  async function groupInfo() {
    try {
      const data = await getGroupInfo(name);
      console.log(data);
      const facedata = await Promise.all(
        _.map(data, async (datas) => {
          console.log('test', await AsyncStorage.getItem(datas));
          const dataList = JSON.parse(await AsyncStorage.getItem(datas));
          return {
            token: datas,
            name: dataList.faceName,
            image: dataList.fileName,
          };
        })
      );
      setFacelist(facedata);
      console.log(facedata);
    } catch (e) {
      // Toast.show({
      //     type: 'error',
      //     text1: 'Error get group info',
      //     text2: e,
      // });
      console.log('DATA', e);
    }
  }

  return (
    <View>
      {_.map(facegrouplist, (data) => {
        return (
          <View
            key={data.token}
            style={{
              display: 'flex',
              flexDirection: 'row',
              paddingTop: 5,
              marginHorizontal: 20,
              marginVertical: 10,
            }}
          >
            {data.image != '' ? (
              <Image
                style={{ width: 70, height: 70 }}
                source={{ uri: 'file:///' + data.image }}
              />
            ) : (
              ''
            )}

            <Text style={{ flex: 1 }}>{data.name}</Text>
            <Text style={{ flex: 1 }}>{data.token}</Text>
            <TouchableOpacity
              style={styles.button}
              onPress={async () => {
                try {
                  await deleteFace(data.token, 'testi');
                  await AsyncStorage.removeItem(data.token);
                  groupInfo(name);
                } catch (e) {
                  console.log(e);
                }
              }}
            >
              <Text>Unbind</Text>
            </TouchableOpacity>
          </View>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  button: {
    alignItems: 'center',
    backgroundColor: 'blue',
    padding: 10,
  },
});
