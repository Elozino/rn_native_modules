package com.rn_native_modules

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import android.util.Log

class LocationModule(reactContext:ReactApplicationContext): ReactContextBaseJavaModule(reactContext) {
    override fun getName() = "LocationModule"
}