package com.rn_native_modules

import android.location.Location
import kotlinx.coroutines.flow.Flow


// This is an interface of how we access the location we are building
interface LocationClient {
    // Gets location updates
    fun getLocationUpdates(interval: Long): Flow<Location>

    // catch error should something go wrong
    class LocationException(message: String): Exception()
}
