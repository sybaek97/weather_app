package com.crepass.weather.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.CancellationTokenSource;
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

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // 대략적인 위치나 정확한 위치를 요청할 수 있도록 LocationRequest 설정
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationTokenSource().getToken())
                    .addOnSuccessListener((android.app.Activity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                callback.onLocationReceived(location);
                            } else {
                                Toast.makeText(context, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "위치 정보 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
