package jp.ac.gifu_u.info.onishi.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import jp.ac.gifu_u.info.onishi.myapplication.CameraView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private SensorManager sensorManager;
    private LocationManager locationManager;

    private TextView textView; // 表示用テキスト

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 画面にレイアウトを設定（括弧修正）
        setContentView(R.layout.activity_main);

        // TextViewの取得
        textView = findViewById(R.id.status_text);

        // センサ・位置情報マネージャの取得
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 位置情報のパーミッション確認と要求
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        // 明るさセンサ
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // 加速度センサ
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accSensor != null) {
            sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // GPS位置情報の取得（パーミッションチェック済み前提）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    // センサ値が変化したとき
    @Override
    public void onSensorChanged(SensorEvent event) {
        String msg = "";
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                msg = "明るさ: " + event.values[0] + " lux";
                break;
            case Sensor.TYPE_ACCELEROMETER:
                msg = "加速度: X=" + event.values[0] + " Y=" + event.values[1] + " Z=" + event.values[2];
                break;
        }
        textView.setText(msg);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 精度変更時の処理（今回は何もしない）
    }

    // 位置が更新されたとき
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Toast.makeText(this, "位置: 緯度=" + lat + " 経度=" + lon, Toast.LENGTH_SHORT).show();
    }

    private void showCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        CameraView camView = new CameraView(this);
        setContentView(camView);  // これにより既存のUIは消える点に注意
    }

    // その他の LocationListener メソッド
    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onProviderEnabled(@NonNull String provider) {}
    @Override public void onProviderDisabled(@NonNull String provider) {}
}
