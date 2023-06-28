import { changeLight } from 'facepass-react-native-module';
import { TouchableOpacity, Text, View, StyleSheet } from 'react-native';
export default function TestLightPage() {
    return <View style={{ display: 'flex' }}>
        <View style={{ marginVertical: 10 }}></View>

        <TouchableOpacity style={styles.button} onPress={() => changeLight("white")}>
            <Text>
                White
            </Text>
        </TouchableOpacity>
        <View style={{ marginVertical: 10 }}></View>
        <TouchableOpacity style={styles.button} onPress={() => changeLight("green")}>
            <Text>
                Green
            </Text>
        </TouchableOpacity>
        <View style={{ marginVertical: 10 }}></View>
        <TouchableOpacity style={styles.button} onPress={() => changeLight("red")}>
            <Text>
                Red
            </Text>
        </TouchableOpacity>
        <View style={{ marginVertical: 10 }}></View>
        <TouchableOpacity style={styles.button} onPress={() => changeLight("yellow")}>
            <Text>
                Yellow
            </Text>
        </TouchableOpacity>
        <View style={{ marginVertical: 10 }}></View>
        <TouchableOpacity style={styles.button} onPress={() => changeLight("green_white")}>
            <Text>
                Green White
            </Text>
        </TouchableOpacity>
        <View style={{ marginVertical: 10 }}></View>
        <TouchableOpacity style={styles.button} onPress={() => changeLight("red_white")}>
            <Text>
                Red White
            </Text>
        </TouchableOpacity>
        <View style={{ marginVertical: 10 }}></View>
        <TouchableOpacity style={styles.button} onPress={() => changeLight("yellow_white")}>
            <Text>
                Yellow White
            </Text>
        </TouchableOpacity>
        <View style={{ marginVertical: 10 }}></View>
        <TouchableOpacity style={styles.button} onPress={() => changeLight("off")}>
            <Text>
                Off
            </Text>
        </TouchableOpacity>

    </View>
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