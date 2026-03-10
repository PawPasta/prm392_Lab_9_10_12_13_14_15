package com.prm392_sp26.se182138_lab13.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

public class LocationService {

    public static Location getLocation(Context context){

        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            return null;
        }

        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}
