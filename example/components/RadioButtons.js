import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';

export default function RadioButtons({ values, onPress, defaults }) {
  function RadioButton({ isChecked, text, onRadioButtonPress }) {
    const styles = StyleSheet.create({
      mainContainer: {
        height: 50,
        marginTop: 5,
        marginBottom: 5,
        marginLeft: 10,
        marginRight: 10,
        justifyContent: 'center',
        paddingLeft: 10,
        paddingRight: 10,
        // borderWidth: 0.5,
        // borderColor: "red",
        flexDirection: 'row',
        flexGrow: 0,
        flexShrink: 0,
        // minWidth:70,
        display: 'flex',
        alignItems: 'center',
      },
      radioButtonIcon: {
        backgroundColor: 'white',
        borderWidth: 3,
        borderColor: 'grey',
        height: 20,
        width: 20,
        borderRadius: 20 / 2,
        marginRight: 2,
        alignItems: 'center',
        justifyContent: 'center',
      },
      radioButtonIconInnerIcon: {
        height: 25,
        width: 25,
        backgroundColor: 'black',
        borderRadius: 25 / 2,
        borderWidth: 3,
        borderColor: 'grey',
      },
      radioButtonTextContainer: {
        height: 50,
        justifyContent: 'center',
        paddingLeft: 10,
      },
      radioButtonText: {
        fontSize: 16,
      },
    });
    const _renderCheckedView = () => {
      return isChecked ? (
        <View style={[styles.radioButtonIconInnerIcon]} />
      ) : null;
    };

    return (
      <TouchableOpacity
        style={styles.mainContainer}
        onPress={onRadioButtonPress}
      >
        <View style={[styles.radioButtonIcon]}>{_renderCheckedView()}</View>
        <View style={[styles.radioButtonTextContainer]}>
          <Text style={styles.radioButtonText}>{text}</Text>
        </View>
      </TouchableOpacity>
    );
  }

  const [currentSelectedItem, setCurrentSelectedItem] = useState(defaults);

  function _onPress(value, id) {
    onPress(value);
    setCurrentSelectedItem(id);
  }

  const _renderRadioButtons = () => {
    return (values || []).map((listItem, id) => {
      let isChecked = currentSelectedItem === id ? true : false;

      return (
        <RadioButton
          key={id}
          onRadioButtonPress={() => _onPress(listItem.text, id)}
          isChecked={isChecked}
          text={listItem.text}
        />
      );
    });
  };

  return (
    <View style={{ flexDirection: 'row', display: 'flex' }}>
      {_renderRadioButtons()}
    </View>
  );
}
