package com.example.gethub1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignFisher;

/**
 * Created by 刘武 on 2016/5/22.
 */
public class DealData implements PreProcessing {

    @Override
    public float[] filter(FilterPassType filterPassType, FilterCharacteristicsType FilterCharacteristicsType,
                          int filterOrder, double ripple, double fcf1, double fcf2, float[] data) {
        IirFilterDesignFisher iirFilterDesignFisher = null;
        if(FilterCharacteristicsType.toString().equals("chebyshev")){
            ripple=ripple;
        }
        else { ripple = 0.0;}
        if(filterPassType.toString().equals("lowpass")||filterPassType.toString().equals("highpass")){
            fcf2=0.0;
        }
        IirFilterCoefficients iirFilterCoefficients = iirFilterDesignFisher.design(filterPassType, FilterCharacteristicsType, filterOrder,ripple, fcf1,fcf2);
        float[] y = new float[data.length];
//下面通过得到的传感器数据进行滤波器的使用
//进行filtfilt 函数的编写，使用上面得到的参数。现在还没完全实现
        return y;
    }

    @Override
    public float Xcorr(float[] data) {
        return 0;
    }

    @Override
    public float[] movingAverage(float[] data, int movingSize) {
        float[] newData = new float[data.length];
        MovingAverage ma = new MovingAverage();
        ma.period = movingSize;
        for (int i = 0; i < data.length; i++) {
            ma.newNum(data[i]);
            newData[i] = ma.getAvg();
        }
        return newData;
    }

    @Override
    public float[][] ListToValue(ArrayList<String> data, int size, int chooseData) {
        String[] str = new String[chooseData];
        float[][] dataPro = new float[chooseData][size];
        for(int i =0 ;i < size ;i++){
            str = data.get(i).split(" ");
            for(int j=0;j<chooseData;j++){
                dataPro[j][i] = Float.parseFloat(str[j]);//链表数据进行数组转化
            }
        }
        return  dataPro;
    }

    public class MovingAverage {
        private final Queue<Double> window = new LinkedList<Double>();
        private int period;
        private float sum;

        public void newNum(double num) {
            sum += num;
            window.add(num);
            if (window.size() > period) {
                sum -= window.remove();
            }
        }

        public float getAvg() {
            if (window.isEmpty()) return 0; // technically the average is undefined
            return sum / window.size();
        }
    }
}
