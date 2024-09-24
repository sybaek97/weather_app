package com.crepass.weather.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHandler {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationCallback callback;

    public LocationHandler(Context context, FusedLocationProviderClient fusedLocationClient, LocationCallback callback) {
        this.context = context;
        this.fusedLocationClient = fusedLocationClient;
        this.callback = callback;
    }

    // 위치 권한 요청
    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((android.app.Activity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, requestCode);
        } else {
            getCurrentLocation();
        }
    }

    // 현재 위치 가져오기
    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((android.app.Activity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            callback.onLocationReceived(location);
                        }
                    });
        } else {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 위치 콜백 인터페이스 정의
    public interface LocationCallback {
        void onLocationReceived(Location location);
    }
}
