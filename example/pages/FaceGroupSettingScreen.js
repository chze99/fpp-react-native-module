import { useEffect, useRef, useState } from 'react';
import {
    StyleSheet, Text, Image, View, TextInput, FlatList, TouchableOpacity, NativeModules
} from 'react-native';
import DefaultPreference from 'react-native-default-preference';
import { selectImage, addFace, setDefaultGroupName, deleteFace, getFace, bindGroup, getGroupInfo, unbindGroup, getAllGroup, deleteGroup, createGroup, unbindFace } from 'facepass-react-native-module';
import Toast from 'react-native-toast-message';

export default function FaceGroupSettingScreen({ navigation }) {
    const { FacePass } = NativeModules

    const [isLoading, setIsLoading] = useState(true);
    const [imagePath, setImagePath] = useState("");
    const [faceToken, setFaceToken] = useState("");
    const [groupName, setGroupName] = useState("");
    const [faceImage, setImage] = useState("");
    const [facegrouplist, setFacegrouplist] = useState("");
    const [groupList, setGroupList] = useState("");
    const [groupCreateName, setGroupCreateName] = useState("");
    const [faceName, setFaceName] = useState("");
    const [defaultGroupName, setsDefaultGroupName] = useState("");
    const image = useRef();
    useEffect(() => {
        setIsLoading(false)
    }, [])

    useEffect(() => {
        setIsLoading(true)
        setIsLoading(false)
        console.log("Refresh")
    }, [imagePath])

    async function selectimage() {
        try {
            const uri = await selectImage()
            setImagePath(uri)
        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Error select image',
                text2: e,
            });
        }
    }

    async function addface() {
        if (imagePath && faceName) {
            try {
                const success = await addFace(imagePath);
                setFaceToken(success);
                DefaultPreference.set(success, faceName);
                Toast.show({
                    type: 'success',
                    text1: 'Face added',
                });
            } catch (e) {
                Toast.show({
                    type: 'error',
                    text1: 'Error adding face',
                    text2: e,
                });
            }
        } else {
            Toast.show({
                type: 'error',
                text1: 'Please input face name',

            });
        }
    }

    async function deleteface() {
        try {
            await deleteFace(faceToken, groupName)
            await DefaultPreference.clear(faceToken);
            setFaceToken("")
            Toast.show({
                type: 'success',
                text1: 'Face deleted',
            });
        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Error delete face',
                text2: e,
            });
        }
    }

    async function obtainFaceImage() {
        try {
            const success = await getFace(faceToken)
            setImage(success)

        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Error obtain image',
                text2: e,
            });
        }
    }

    async function bindFaceGroup() {
        try {
            await bindGroup(faceToken, groupName)
            Toast.show({
                type: 'success',
                text1: 'Group binded',
            });
        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Error bind group',
                text2: e,
            });
        }
    }

    async function groupInfo() {
        try {
            const data = await getGroupInfo(groupName)
            setFacegrouplist(data)

        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Error get group info',
                text2: e,
            });
        }
    }

    async function unbindGroup(facetoken) {
        try {
            const data = await unbindFace(facetoken, groupName)
            setFacegrouplist(data)

        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Error unbind group',
                text2: e,
            });
        }
    }

    async function getallGroup() {
        try {
            const data = await getAllGroup()
            setGroupList(data);

        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Error get all group',
            });
        }
    }
    async function changeDefaultGroup() {
        if (defaultGroupName != '') {
            try {
                await setDefaultGroupName(defaultGroupName);
                DefaultPreference.set("group_name", defaultGroupName);
                Toast.show({
                    type: 'success',
                    text1: 'Default group changed to',
                    text2: defaultGroupName,
                }); 
            } catch (e) {
                Toast.show({
                    type: 'error',
                    text1: 'Default group change failed',
                    text2: e,
                });
            }
        } else {
            Toast.show({
                type: 'error',
                text1: 'Default group change failed',
                text2: 'Please input group name'
            });
        }

    }

    async function addGroup(name) {
        try {
            await createGroup(name)
            Toast.show({
                type: 'success',
                text1: 'Created group:',
                text2: name,
            });
        } catch (e) {
            Toast.show({
                type: 'error',
                text1: 'Group creation failed',
                text2: e,
            });
        }
    }
    async function deletegroup(name) {
        try {
            const data = await deleteGroup(name)
            setGroupList(data)
            Toast.show({
                type: 'success',
                text1: 'Success delete this group:',
                text2: name,
            });
        } catch (e) {
            console.log(e);
        }
    }
    return (
        isLoading ? "" :
            <View style={{ flexDirection: "column", display: "flex", marginTop: 20, marginLeft: 20 }}>
                <View style={{ flexDirection: "row", display: "flex", marginTop: 20 }}>
                    <View style={styles.textInputView}>
                        <TextInput
                            style={styles.border}
                            onChangeText={setImagePath}
                            value={imagePath}
                            placeholder="Image path"
                        />
                    </View>
                    <View style={styles.textInputView}>
                        <TextInput
                            style={styles.border}
                            onChangeText={setFaceName}
                            value={faceName}
                            placeholder="Face name"
                        />
                    </View>
                    <View style={{ flexDirection: "row", flex: 2 }}>
                        <TouchableOpacity style={styles.button} onPress={() => { selectimage() }}>
                            <Text style={{ color: "white" }}>Select image</Text>
                        </TouchableOpacity>
                        <View style={{ marginLeft: 20 }}></View>

                        <TouchableOpacity style={styles.button} onPress={() => { addface() }}>
                            <Text style={{ color: "white" }}>Add face</Text>
                        </TouchableOpacity>
                    </View>
                </View>
                <View style={{ flexDirection: "row", display: "flex", marginTop: 20 }}>
                    <View style={styles.textInputView}>

                        <TextInput
                            style={styles.border}

                            onChangeText={setFaceToken}
                            value={faceToken}
                            placeholder="Facetoken"
                        />
                    </View>
                    <View style={{ flexDirection: "row", flex: 2 }}>

                        <TouchableOpacity style={styles.button} onPress={() => { deleteface() }}>
                            <Text style={{ color: "white" }}>Delete</Text>
                        </TouchableOpacity>
                        <View style={{ marginLeft: 20 }}></View>

                        <TouchableOpacity style={styles.button} onPress={() => { obtainFaceImage() }}>
                            <Text style={{ color: "white" }}>Obtain image</Text>
                        </TouchableOpacity>
                    </View>
                </View>
                <View style={{ flexDirection: "row", display: "flex", marginTop: 20, marginBottom: 10 }}>
                    <View style={styles.textInputView}>
                        <TextInput
                            style={styles.border}
                            onChangeText={setGroupName}
                            value={groupName}
                            placeholder="Groupname"

                        />
                    </View>
                    <View style={{ flexDirection: "row", flex: 2 }}>

                        <TouchableOpacity style={styles.button} onPress={() => { bindFaceGroup() }}>
                            <Text style={{ color: "white" }}>Bind group</Text>
                        </TouchableOpacity>
                        <View style={{ marginLeft: 20 }}></View>
                        <TouchableOpacity style={styles.button} onPress={() => { groupInfo() }}>
                            <Text style={{ color: "white" }}>Group info</Text>
                        </TouchableOpacity>
                    </View>
                </View>
                <FlatList
                    data={facegrouplist}
                    renderItem={
                        ({ item }) =>
                            <View style={{ display: 'flex', flexDirection: 'row', marginRight: 10, marginVertical: 5 }}>
                                <Text style={{ flex: 2, alignSelf: 'center' }} >{item}</Text>
                                <TouchableOpacity style={styles.button} onPress={() => { unbindGroup(item) }}>
                                    <Text style={{ color: "white" }}>Unbind</Text>
                                </TouchableOpacity>
                            </View>
                    }

                />
                <View style={{ flexDirection: "row", display: "flex", marginTop: 20, marginBottom: 10 }}>
                    <View style={styles.textInputView}>
                        <TextInput
                            style={styles.border}
                            onChangeText={setsDefaultGroupName}
                            value={defaultGroupName}
                            placeholder="Default Groupname"

                        />
                    </View>
                    <View style={{ flexDirection: "row", flex: 2 }}>

                        <TouchableOpacity style={styles.button} onPress={() => { changeDefaultGroup() }}>
                            <Text style={{ color: "white" }}>Change default group</Text>
                        </TouchableOpacity>
                        <View style={{ marginLeft: 20 }}></View>

                    </View>
                </View>

                {faceImage != "" ?
                    <Image
                        style={{ width: 70, height: 70 }}
                        source={{
                            uri: 'data:image/png;base64,' + faceImage,
                        }}>
                    </Image>
                    : ""}



                <View style={{ flexDirection: "row", display: "flex", marginTop: 20 }}>
                    <View style={styles.textInputView}>
                        <TextInput
                            style={styles.border}
                            onChangeText={setGroupCreateName}
                            value={groupCreateName}
                            placeholder="Group "
                        />
                    </View>
                    <View style={{ flexDirection: "row", flex: 2 }}>
                        <TouchableOpacity style={styles.button} onPress={() => { addGroup(groupCreateName) }}>
                            <Text style={{ color: "white" }}>Add group</Text>
                        </TouchableOpacity>
                    </View>
                </View>
                <View style={{ flexDirection: "column", display: "flex", marginTop: 20, marginBottom: 10 }}>
                    <TouchableOpacity style={styles.button} onPress={() => { getallGroup() }}>
                        <Text style={{ color: "white" }}>Get all group</Text>
                    </TouchableOpacity>
                </View>

    

                <View style={{ flexDirection: "column", display: "flex", marginTop: 20, marginBottom: 10 }}>
                    <FlatList
                        data={groupList}
                        renderItem={
                            ({ item }) =>
                                <View style={{ display: 'flex', flexDirection: 'row', marginRight: 10, marginVertical: 5 }}>
                                    <Text style={{ flex: 2, alignSelf: 'center' }} >{item}</Text>
                                    <TouchableOpacity style={styles.button} onPress={() => { deletegroup(item) }}>
                                        <Text style={{ color: "white" }}>X</Text>
                                    </TouchableOpacity>
                                </View>
                        }
                    />
                </View>


            </View>

    )
}


const styles = StyleSheet.create({

    button: {
        alignItems: 'center',
        backgroundColor: '#33b5e5',
        padding: 10,
        minWidth: 100
    },
    border: {
        borderWidth: 1, paddingVertical: 4, paddingLeft: 2
    },
    textInputView: {
        flex: 1, marginRight: 8, justifyContent: 'center'
    }

});
