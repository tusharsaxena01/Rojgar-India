package com.bit.bharatplus;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bit.bharatplus.utils.FirebaseUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference locationRef;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get a reference to the "location" node in the Firebase Realtime Database
        locationRef = FirebaseDatabase.getInstance().getReference("location");

        // Initialize the fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(3000); // 3 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create a location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Update the locationRef with the latest location
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    // Get the user ID (you need to provide this)
                    String userId = FirebaseUtil.getCurrentUserId();

                    // Update the location in the Firebase Realtime Database
                    locationRef.child(userId).setValue(location);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
        startLocationUpdates();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Tracking user location")
                .setSmallIcon(R.drawable.ic_notification);

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(getLocationRequest(), locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(3000); // 3 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
