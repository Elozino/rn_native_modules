import {StyleSheet, Text, View, NativeModules, Button} from 'react-native';
import React from 'react';

const {CalendarModule} = NativeModules;

const App = () => {
  return (
    <View style={styles.container}>
      <Text>App</Text>
      <Button
        title="Calendar Native Module"
        onPress={() => {
          CalendarModule.createCalendarEvent('testName', 'testLocation');
        }}
      />
    </View>
  );
};

export default App;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
