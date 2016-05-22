package com.example.gethub1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    public final int segmentation = 70;//处理数据窗口大小
    public final int deleteNumber = 50;//删除数据尺寸
    public final int chooseData =1;//使用传感器信息的几个
    public float[][] sensorValue = new float[segmentation][chooseData];//将传感器数据链表转化为数组
    public TextView data_text;//传感器数据显示文本框
    public StringBuffer data_stringBuffer = new StringBuffer();//进行传感器数据加载
    public SensorManager sensorManager;
    public ArrayList<String> allData = new ArrayList<String>();//全部数据承载
    public float[] linear_acceleration = new float[3];//线型加速度
    public float[] gyroscope = new float[3];//陀螺仪传感器
    public Semaphore canPreprocessing = new Semaphore(0);//数据是否符合条件进行预处理
    public Semaphore canDelete = new Semaphore(1);//数据时候能进行删除或者进行长度的判断
    public DealData dealData = new DealData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data_text = (TextView) findViewById(R.id.text_data);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//传感器设备的获取
        new Thread(new Runnable() {//线程创建
            @Override
            public void run() {
                while (true){
                    try {
                        processData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void processData() throws InterruptedException {
        canPreprocessing.acquire(1);//接受信号量
        sensorValue = dealData.ListToValue(allData,segmentation,chooseData);//传感器数据转化为数组
        canDelete.acquire(1);
        for(int i=0;i<deleteNumber;i++){
            allData.remove(0);//进行数据的删除
        }
        canDelete.release(1);
        //下面举例进行一组传感器数据的处理
        float[] deal = sensorValue[0];//将第一行的数据传递给deal
        deal = dealData.movingAverage(deal,10);//进行移动平均的处理
        deal = dealData.filter(FilterPassType.lowpass, FilterCharacteristicsType.butterworth,10,0,0.34,0,deal);
        //上面使用移动平均和低通滤波器对数据进行处理。最终得到deal
        //数据，使用机器学习的方法或者信号处理的方法进行模型的匹配。
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_GAME);//线型传感器注册，50HZ
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);//陀螺仪传感器注册,50HZ
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_LINEAR_ACCELERATION:
                linear_acceleration[0] = event.values[0];
                linear_acceleration[1] = event.values[1];
                linear_acceleration[2] = event.values[2];
                data_stringBuffer.append(linear_acceleration[0]+" ");
                data_stringBuffer.append(linear_acceleration[1]+" ");
                data_stringBuffer.append(linear_acceleration[2]+" ");
                data_stringBuffer.append(gyroscope[0]+" ");
                data_stringBuffer.append(gyroscope[1]+" ");
                data_stringBuffer.append(gyroscope[2]);
                data_text.setText(data_stringBuffer.toString());//传感器数据显示
                allData.add(data_stringBuffer.toString());//将传感器数据加入到整个数据链表中
                try {
                    canDelete.acquire(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(allData.size()>segmentation){
                    canPreprocessing.release();//当数据满足窗口数据长度，则进行信号量的释放
                }
                canDelete.release(1);
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscope[0] = event.values[0];
                gyroscope[1] = event.values[1];
                gyroscope[2] = event.values[2];
                break;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
