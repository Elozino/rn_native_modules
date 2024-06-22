import {
  StyleSheet,
  Text,
  View,
  NativeModules,
  Button,
  NativeEventEmitter,
} from 'react-native';
import React, {useEffect} from 'react';

const {CalendarModule, LocationModule} = NativeModules;
const locationEventEmitter = new NativeEventEmitter(LocationModule);

const App = () => {
  useEffect(() => {
    const subscription = locationEventEmitter.addListener(
      'onLocationUpdate',
      good => {
        console.log(good);
      },
    );
    return () => subscription.remove();
  }, []);

  const startLocationService = () => {
    LocationModule.startLocationService();
  };

  const stopLocationService = () => {
    LocationModule.stopLocationService();
  };

  return (
    <View style={styles.container}>
      <Text>App</Text>
      <Button
        title="Calendar Native Module"
        onPress={() => {
          CalendarModule.createCalendarEvent('testName', 'testLocation');
        }}
      />
      <Button
        title="Location Start Native Module"
        onPress={startLocationService}
      />
      <Button
        title="Location Stop Native Module"
        onPress={stopLocationService}
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
