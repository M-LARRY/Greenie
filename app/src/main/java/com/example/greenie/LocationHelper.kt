package com.example.greenie

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission

class LocationHelper(private val context: Context, private val onLocationChangedCB: (Location) -> Unit) {
    /**
     * Manages location updates using the Android Location Manager.
     *
     * @param context The application context.
     * @param onLocationChangedCB A callback function to be executed when a new location is available.
     *                           The callback receives a Location object as an argument.
     */
    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: LocationListenerCB
    /**
     * Initializes the LocationListener.
     */
    init {
        /**
         * Creates a new instance of the LocationListener.
         */
        listener = LocationListenerCB()
    }
    /**
     * Starts location updates using the specified provider, time interval, and minimum distance.
     *
     * @RequiresPermission(allOf = [Manifest.permission.ACACCESS_COARSE_LOCATION])
     * @param provider The location provider to use (e.g., LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).
     * @param interval The minimum time between location updates in milliseconds.
     * @param accuracy The minimum distance between location updates in meters.
     * @param locationListener The LocationListener to be called when a new location is available.
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
    /**
     * Inner class implementing the LocationListener interface.
     * This class handles location updates and calls the provided callback function.
     */
    inner class LocationListenerCB : android.location.LocationListener {
        /**
         * Called when a new location is available.
         *
         * @param location The new location object.
         */
        override fun onLocationChanged(location: Location) {
            /**
             * Executes the callback function with the new location data.
             */
            onLocationChangedCB(location)
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d("LocationHelper", "New Location: Latitude - $latitude, Longitude - $longitude")
            // Use the location data
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}