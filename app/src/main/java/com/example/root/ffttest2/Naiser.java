package com.example.root.ffttest2;

import android.app.Activity;
import android.util.Log;

public class Naiser {
    public static double[] Naiser_corr(double[] signal, int Nu, int DIVIDE_FACTOR) {
        short[] PN_seq = new short[] {1, -1, -1, -1, -1, -1, 1, -1};
        int total_length = signal.length;
        int L = 8;
        int preamble_L = L * Nu;
        int multi_num = 0;
        int len_corr = (total_length - preamble_L)/DIVIDE_FACTOR + 2;
        double[] naiser_corr = new double[len_corr];
        int num = 0;
        for(int  i = 0; i < total_length - preamble_L -1; i = i+DIVIDE_FACTOR){
            double[] seg = Utils.segment(signal, i, i+preamble_L - 1);
            double Pd = 0;
            for(int k = 0; k < L - 1; ++k){
                multi_num ++;
                int bk = PN_seq[k]*PN_seq[k+1];
                double[] seg1 = Utils.segment(seg, k*Nu, (k+1)*Nu - 1);
                double[] seg2 = Utils.segment(seg, (k + 1)*Nu, (k+2)*Nu - 1);
                Pd += bk*Utils.sum_multiple(seg1, seg2);

            }
            double Rd = Utils.sum_multiple(seg, seg);
            naiser_corr[num] = Pd/Rd;
            num ++;
        }
        double[] max_info = Utils.max(naiser_corr);
        max_info[1] = max_info[1] *DIVIDE_FACTOR;
        Log.e("multiple times",Integer.toString(multi_num));
        return  max_info;
    }

    public static int Naiser_check_valid(double[] signal, int peak_index) {
        int win_size = 1200;
        double naiser_threshold = Constants.NaiserThresh; //adjustable
        int step_size = 8;
        if(peak_index - win_size < 0 || peak_index + win_size + 7860 > signal.length) return -2;
        double[] preamble_seg = Utils.segment(signal, peak_index - win_size, peak_index + win_size + 7860 -1);
        double[] max_info = Naiser_corr(preamble_seg, 960, step_size);
        Log.e("naiser","max info "+max_info[0]+","+naiser_threshold);
        if(max_info[0] < naiser_threshold) return -1;
        else{
            return (int)(peak_index - win_size + max_info[1]);
        }
    }

}
