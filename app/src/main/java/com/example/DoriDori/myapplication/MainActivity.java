package com.example.DoriDori.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorManager mySensorManager; // 센서 매니저
    private SensorEventListener gyroListener; // 센서 리스너
    private Sensor myGyroscope; // 센서

    private double roll;  // x
    private double pitch; // y
    private double yaw;   // z

    /* 단위 시간을 구하기 위한 변수 */
    private double timestamp = 0.0;
    private double dt;
    /*count 함수 5분 고정 (5 * 60 = 300초) */
    private double count = 0.0;
    /* 회전각을 구하기 위한 변수 */
    private double rad_to_dgr = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    TextView x;
    TextView y;
    TextView z;
    TextView a;
    TextView b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x = (TextView)findViewById(R.id.x);
        y = (TextView)findViewById(R.id.y);
        z = (TextView)findViewById(R.id.z);
        a = (TextView)findViewById(R.id.시간);
        b = (TextView)findViewById(R.id.countup5m);
    final Vibrator vibrator;
    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);


        // 센서 매니저 생성
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 자이로스코프 센서를 사용하겠다고 등록
        myGyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 센서 이벤트 리스너
        gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) { // 센서 값이 변화할 때
                double gyroX = sensorEvent.values[0];
                double gyroY = sensorEvent.values[1];
                double gyroZ = sensorEvent.values[2];

                /* 단위시간 계산 */
                dt = (sensorEvent.timestamp - timestamp)*NS2S; //0.1초
                timestamp = sensorEvent.timestamp;

                /* 시간이 변화했으면 */
                if(dt-timestamp*NS2S != 0){
                    pitch = pitch + gyroY*dt;
                    roll = roll + gyroX*dt;
                    yaw = yaw + gyroZ*dt;
                //    count = timestamp*NS2S;

                    x.setText("[roll]:"+String.format("%.1f",roll*rad_to_dgr));
                    y.setText("[Pitch]:"+String.format("%.1f",pitch*rad_to_dgr));
                    z.setText("[yaw]:"+String.format("%.1f",yaw*rad_to_dgr));
                    a.setText("[단위시간]: "+String.format("%.1f",dt-timestamp*NS2S));
                   // b.setText("[5분카운트]: "+String.format("%.1f",count));
                }
                if((pitch*rad_to_dgr <= -20 || pitch*rad_to_dgr >= 20) || (roll*rad_to_dgr <= -20 || roll*rad_to_dgr >= 20) || (yaw*rad_to_dgr <= -20 || yaw*rad_to_dgr >= 20) ){
                    vibrator.vibrate(1000);
                    pitch =0;roll=0;yaw=0;
                        //countdown 5분이 되었을 때 -> 자이로센서 무시 => lock을 걸 예정(switch)

                }

              //  else
                  //  vibrator.cancel();


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    }

    protected void onResume() {
        super.onResume();
        mySensorManager.registerListener(gyroListener, myGyroscope,SensorManager.SENSOR_DELAY_UI);
    }
    protected void onPause() {
        super.onPause();
        mySensorManager.unregisterListener(gyroListener);
    }

    protected void onStop() {
        super.onStop();
    }
}
