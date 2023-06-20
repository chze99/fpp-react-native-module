import _ from "lodash";
import { useState, useEffect } from "react";
import { TouchableOpacity,  View, Text, StyleSheet, Image, } from "react-native";
import DefaultPreference from 'react-native-default-preference';
import { getGroupInfo, getFace, deleteFace, createGroup } from "facepass-react-native-module";
export default function FaceMangement({ navigation }) {
    const [faceList, setFaceList] = useState([]);
    async function showFaceList() {

        try {
            try {
                await createGroup("testi")
            } catch (e) {
                console.log(e)
            }
            const data = await getGroupInfo("testi")
            const facedata = await Promise.all(_.map(data, async (datas) => {
                const name = await DefaultPreference.get(datas)
                const imageData = await getFace(datas)

                return { token: datas, name: name, image: imageData }

            }))
            console.log(facedata)
            setFaceList(facedata);
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        showFaceList()
        console.log("test")
    }, [])

    return (
        <View>
            {/* <TouchableOpacity style={styles.button} onPress={() => {
                FacePass.addFace("/storage/emulated/0/download/1686709182566_102002.JPG", (success) => {
                    FacePass.bindGroup(success, "testi", (e) => { console.log(e) }, (e) => { console.log(e) })
                },
                    (e) => console.log(e))
            }}>
                <Text style={{ color: "white" }}>Test</Text>
            </TouchableOpacity> */}
            {/* <TouchableOpacity style={styles.button} onPress={()=>{
        console.log("test")
          FacePass.createGroup("testi", (success) => {
            DefaultPreference.set("group_name","testi");        
            console.log(success)
        }, (e) => {
            console.log(e)
        })
        }}>
            <Text style={{ color: "white" }}>Test2</Text>
        </TouchableOpacity> */}
            <TouchableOpacity style={styles.button} onPress={() =>
                navigation.navigate("AddFace")
            }>
                <Text style={{ color: "white" }}>Add face</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.button} onPress={() =>
                navigation.navigate("FaceGroupSettingScreen")
            }>
                <Text style={{ color: "white" }}>FaceGroupSettingScreen</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.button} onPress={() =>
                navigation.navigate("FacialRecognitionSettingScreen")
            }>
                <Text style={{ color: "white" }}> FacialRecognitionSettingScreen</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.button} onPress={() =>
                navigation.navigate("SettingScreen")
            }>
                <Text style={{ color: "white" }}>SettingScreen</Text>
            </TouchableOpacity>
            {/* <TouchableOpacity style={styles.button} onPress={() =>
                showFaceList()
            }>
                <Text style={{ color: "white" }}>Show face list</Text>
            </TouchableOpacity> */}
            {_.map(faceList, (data) => {
                return (<View key={data.token} style={{ display: "flex", flexDirection: "row", paddingTop: 5 }}>
                    {data.image != "" ?
                        <Image
                            style={{ width: 70, height: 70 }}
                            source={{
                                uri: 'data:image/png;base64,' + data.image,
                            }}>
                        </Image>
                        : ""}

                    <Text style={{ flex: 1 }}>{data.name}</Text>
                    <Text style={{ flex: 1 }}>{data.token}</Text>
                    <TouchableOpacity style={styles.button} onPress={async () => {
                        try {
                            const FaceList = await deleteFace(data.token, "testi");
                            await DefaultPreference.clear(data.token);
                            setFaceList(FaceList);
                        } catch (e) {
                            console.log(e)
                        }
                    }}><Text>Delete</Text></TouchableOpacity>
                </View>)
            })}
            {/* {faceImage != "" ?
                <Image
                    style={{ width: 70, height: 70 }}
                    source={{
                        uri: 'data:image/png;base64,' + faceImage,
                    }}>
                </Image>
                : ""} */}
        </View >
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