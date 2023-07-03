import {
  FlatList,
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';
import { getAllGroup } from 'facepass-react-native-module';
import { useEffect, useState } from 'react';
import { useRoute } from '@react-navigation/native';

export default function GroupListPage({ navigation }) {
  const [facegrouplist, setGroupList] = useState('');

  useEffect(() => {
    async function getallgroup() {
      try {
        const data = await getAllGroup();
        setGroupList(data);
      } catch (e) {
        Toast.show({
          type: 'error',
          text1: 'Error get all group',
          text2: e,
        });
      }
    }
    getallgroup();
  }, []);

  return (
    <View>
      <FlatList
        data={facegrouplist}
        renderItem={({ item }) => (
          <View
            style={{
              display: 'flex',
              flexDirection: 'row',
              marginHorizontal: 50,
              marginVertical: 10,
            }}
          >
            <Text style={{ flex: 2, alignSelf: 'center' }}>{item}</Text>
            <TouchableOpacity
              style={styles.button}
              onPress={() => {
                navigation.navigate('GroupListFacePage', { name: item });
              }}
            >
              <Text style={{ color: 'white' }}>View</Text>
            </TouchableOpacity>
          </View>
        )}
      />
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
