package com.rn_native_modules

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule

class LocationModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "LocationModule"

    @ReactMethod
    fun startLocationService() {
        val intent = Intent(reactApplicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }
        reactApplicationContext.startService(intent)
    }

    @ReactMethod
    fun stopLocationService() {
        val intent = Intent(reactApplicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        reactApplicationContext.stopService(intent)
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action == LocationService.LOCATION_UPDATE_EVENT) {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                sendEvent("onLocationUpdate", latitude, longitude)
            }
        }
    }


    private fun sendEvent(eventName: String, latitude: Double, longitude: Double) {
        val params = Arguments.createMap().apply {
            putDouble("latitude", latitude)
            putDouble("longitude", longitude)
        }
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

}
