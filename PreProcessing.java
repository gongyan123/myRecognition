package com.example.gethub1;

import java.util.ArrayList;

import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;

/**
 * Created by 刘武 on 2016/5/22.
 */
public interface PreProcessing {
    float[] filter(FilterPassType filterPassType,FilterCharacteristicsType FilterCharacteristicsType,
                    int filterOrder,double ripple,double fcf1,double fcf2,float[] data);
    // 滤波器（Lowpass, highpass, bandpass, bandstop）
    // 滤波器的类型（ Butterworth, Chebyshev (type 1), Bessel.）
    // filterOrder 滤波器阶数
    // ripple  仅在Chebyshev 出现
    // fcf1  fcf2 截止频率。当处于Lowpass, highpass时，只有 fcf2.
    // data 为原始数据数组
    float Xcorr(float[] data);
    //data 为原始数据数组，输出周期
    float[] movingAverage(float[] data,int movingSize);
    //data为原始数据数组，movingSize 为窗口大小
    float[][] ListToValue(ArrayList<String> data,int size,int chooseData);
    //data表示链表数据，size表示窗口尺寸大小，chooseData表示使用传感器的数据（比如只是用线性传感器X，陀螺仪Y，此时为2）
}
