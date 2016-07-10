package com.webfactional.andrewk.akinventory;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private MainActivity mainActivity;

    private Location firstLocationFromGps = null;

    public Location getFirstLocationFromGps()
    {
        return firstLocationFromGps;
    }

    private Location locationFromGps;

    public Location getLocationFromGps()
    {
        return locationFromGps;
    }

    private Location firstLocationFromNetwork = null;

    public Location getFirstLocationFromNetwork()
    {
        return firstLocationFromNetwork;
    }

    private Location locationFromNetwork;

    public Location getLocationFromNetwork()
    {
        return locationFromNetwork;
    }

    private Location firstLocationFromPassive = null;

    public Location getFirstLocationFromPassive()
    {
        return firstLocationFromPassive;
    }

    private Location locationFromPassive;

    public Location getLocationFromPassive()
    {
        return locationFromPassive;
    }

    private Location firstLocationFromFused = null;

    public Location getFirstLocationFromFused()
    {
        return firstLocationFromFused;
    }

    private Location locationFromFused;

    public Location getLocationFromFused()
    {
        return locationFromFused;
    }

    public static final int GET_PERMISSIONS_REQUEST_CODE = 624;

    LocationManager locationManager;

    @Override
    public void onConnected(@Nullable Bundle connectionHint)
    {
        updateLocation();
    }

    public void updateLocation()
    {
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    mainActivity,
                    new String[]
                            {
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            },
                    GET_PERMISSIONS_REQUEST_CODE);

            return;
        }

        if (null == firstLocationFromGps)
        {
            locationFromGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            firstLocationFromGps = locationFromGps;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    locationFromGps = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        if (null == firstLocationFromNetwork)
        {
            locationFromNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            firstLocationFromNetwork = locationFromNetwork;

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    locationFromNetwork = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        if (null == firstLocationFromFused)
        {
            locationFromFused = LocationServices.FusedLocationApi.getLastLocation(mainActivity.getGoogleApiClientForLocationService());

            firstLocationFromFused = locationFromFused;

            LocationServices.FusedLocationApi.requestLocationUpdates(mainActivity.getGoogleApiClientForLocationService(), new LocationRequest(), new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location)
                {
                    locationFromFused = location;
                }
            });
        }

        if (null == firstLocationFromPassive)
        {
            locationFromPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            firstLocationFromPassive = locationFromPassive;

            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    locationFromPassive = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    public LocationService(MainActivity pMainActivity)
    {
        mainActivity = pMainActivity;

        locationManager = (LocationManager)mainActivity.getSystemService(Context.LOCATION_SERVICE);
    }
}
