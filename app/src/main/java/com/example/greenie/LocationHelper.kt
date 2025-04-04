package com.example.greenie

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.location.LocationListenerCompat

/**
 * Manages location updates using the Android Location Manager.
 *
 * @param context The application context.
 * @param onLocationChangedCB A callback function to be executed when a new location is available.
 *                           The callback receives a Location object as an argument.
 */
class LocationHelper(private val context: Context, private val onLocationChangedCB: (Location) -> Unit) {

    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    /**
     * Creates a new instance of the LocationListener.
     */
    private var listener: LocationListener = LocationListenerCompat { onLocationChangedCB }

    /**
     * Starts location updates using the specified provider, time interval, and minimum distance.
     *
     * @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION])
     * @param provider The location provider to use (e.g., LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).
     * @param interval The minimum time between location updates in milliseconds.
     * @param accuracy The minimum distance between location updates in meters.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startLocationUpdates(provider: String = LocationManager.GPS_PROVIDER, interval: Long = 1000, accuracy: Float = 1f) {
        locationManager.requestLocationUpdates(
            provider, // Provider (GPS or Network)
            interval, // Minimum time between updates in milliseconds (1 second)
            accuracy, // Minimum distance between updates in meters (1 meter)
            listener
        )
    }
    /**
     * Stops location updates.
     */
    fun stopLocationUpdates() {
        locationManager.removeUpdates(listener)
    }
}