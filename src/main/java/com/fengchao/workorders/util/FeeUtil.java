package com.fengchao.workorders.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeeUtil {

    public static String Fen2Yuan(String totalFee){

        String totalFeeYuan;
        int feeSize = totalFee.length();
        if (2 < feeSize){
            totalFeeYuan = totalFee.substring(0,feeSize-2)+"."+totalFee.substring(feeSize-2);
        } else {
            if (2 == feeSize) {
                totalFeeYuan = "0." + totalFee;
            }else if(1 == feeSize){
                totalFeeYuan = "0.0"+totalFee;
            }else{
                totalFeeYuan = "0.00";
            }
        }

        return totalFeeYuan;
    }

    public static String Yuan2Fen(String feeYuan){

        String feeFen;
        int feeSize = feeYuan.length();
        if (0 == feeSize){
            return "0";
        }
        feeYuan = feeYuan.trim();
        int fenIndex = feeYuan.lastIndexOf('.');
        if (0 > fenIndex){
            return feeYuan+"00";
        }
        if (fenIndex == feeSize-1){
            return feeYuan.substring(0,fenIndex)+"00";
        }
        if (fenIndex == feeSize-2){
            return feeYuan.substring(0,fenIndex)+feeYuan.substring(fenIndex+1)+"0";
        }
        if (fenIndex == feeSize -3){
            return feeYuan.substring(0,fenIndex)+feeYuan.substring(fenIndex+1);
        }
        log.error("金额格式错误 {}",feeYuan);
        return "0";
    }
}
