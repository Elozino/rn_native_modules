package com.rn_native_modules

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var notificationManager: NotificationManager
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "channelId-location",
                "Location Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        if (checkLocationPermission()) {
            val notification = NotificationCompat.Builder(this, "channelId-location")
                .setContentTitle("Tracking Location")
                .setContentText("Location: null")
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)

            locationClient.getLocationUpdates(10000L)
                .catch { e ->
                    e.printStackTrace()
                }
                .onEach { location ->
                    val lat = location.latitude.toString()
                    val long = location.longitude.toString()
                    val updatedNotification = notification.setContentText(
                        "location: ($lat, $long)"
                    )
                    sendLocationUpdate(location)
                    notificationManager.notify(1, updatedNotification.build())
                }
                .launchIn(serviceScope)

            startForeground(1, notification.build())
        } else {
            // If permissions are not granted, request them
            /*requestLocationPermission()*/
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun stop() {
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    /*    private fun requestLocationPermission() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }*/

    private fun sendLocationUpdate(location: Location) {
        val intent = Intent(LOCATION_UPDATE_EVENT)
        intent.putExtra("latitude", location.latitude)
        intent.putExtra("longitude", location.longitude)
        localBroadcastManager.sendBroadcast(intent)
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val LOCATION_UPDATE_EVENT = "LOCATION_UPDATE_EVENT"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
