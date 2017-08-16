package com.hlju.wangde.securityguards.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.hlju.wangde.securityguards.utils.PrefUtils;

import java.util.List;

public class LocationService extends Service {


    private LocationManager mLM;
    private MyListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mLM = (LocationManager) getSystemService(LOCATION_SERVICE);

        List<String> allProviders = mLM.getAllProviders();
        System.out.println(allProviders);


        mListener = new MyListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);//允许花费流量

        String bestProvider = mLM.getBestProvider(criteria, true);//获取当前最合适的位置提供者
        mLM.requestLocationUpdates(bestProvider, 0, 0, mListener);

    }

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class MyListener implements LocationListener {
        //位置发生变化
        @Override
        public void onLocationChanged(Location location) {
            String j = "j:" + location.getLongitude();//经度
            String w = "w:" + location.getLatitude();//纬度
            String accuracy = "accuracy:" + location.getAccuracy();//精确度
//            String altitude ="altitude"+location.getAltitude();//海拔

            String result = j + "\n" + w + "\n" + accuracy;
            String phone = PrefUtils.getString("safe_phone", "", getApplicationContext());
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(phone, null, result, null, null);

            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLM.removeUpdates(mListener);
        mListener = null;
    }
}
