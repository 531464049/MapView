package com.aaa.lib.map.imp;

import android.content.Context;

/**
 * 错误码
 */
public class FaultCode {

    public static String getErrMsg(Context context, String codeStr) {
        int errCode = getErrorCode(codeStr);
        return getErrorMessageByCode(context, errCode);
    }

    public static String getErrMsg(Context context, int code) {
        //000100000二进制 第几位是1就表示错误码是多少
        int errCode = getErrorCode(code);
        return getErrorMessageByCode(context, errCode);
    }

    private static String getErrorMessageByCode(Context context, int errCode) {
        String errType = "";
        String errDetail = "";
        switch (errCode) {
            case 1:
                errType = context.getResources().getString(R.string.Error1);
                errDetail = context.getResources().getString(R.string.Error_detail_1);
                break;
            case 2:
                errType = context.getResources().getString(R.string.Error2);
                errDetail = context.getResources().getString(R.string.Error_detail_2);
                break;
            case 3:
                errType = context.getResources().getString(R.string.Error3);
                errDetail = context.getResources().getString(R.string.Error_detail_3);
                break;
            case 4:
                errType = context.getResources().getString(R.string.Error4);
                errDetail = context.getResources().getString(R.string.Error_detail_4);
                break;
            case 5:
                errType = context.getResources().getString(R.string.Error5);
                errDetail = context.getResources().getString(R.string.Error_detail_5);
                break;
            case 6:
                errType = context.getResources().getString(R.string.Error6);
                errDetail = context.getResources().getString(R.string.Error_detail_6);
                break;
            case 7:
                errType = context.getResources().getString(R.string.Error7);
                errDetail = context.getResources().getString(R.string.Error_detail_7);
                break;
            case 8:
                errType = context.getResources().getString(R.string.Error8);
                errDetail = context.getResources().getString(R.string.Error_detail_8);
                break;
            case 9:
                errDetail = context.getResources().getString(R.string.Error_detail_9);
                errType = context.getResources().getString(R.string.Error9);
                break;
            case 10:
                break;
            case 11:
                errDetail = context.getResources().getString(R.string.Error_detail_11);
                errType = context.getResources().getString(R.string.Error11);
                break;
            case 12:
                errType = context.getResources().getString(R.string.Error12);
                errDetail = context.getResources().getString(R.string.Error_detail_12);
                break;
            case 13:
                errType = context.getResources().getString(R.string.Error13);
                errDetail = context.getResources().getString(R.string.Error_detail_13);
                break;
            case 14:
                errType = context.getResources().getString(R.string.Error14);
                errDetail = context.getResources().getString(R.string.Error_detail_14);
                break;
            case 15:
                break;
            case 16:
                errDetail = context.getResources().getString(R.string.Error_detail_16);
                errType = context.getResources().getString(R.string.Error16);
                break;
            case 17:
                errType = context.getResources().getString(R.string.Error17);
                errDetail = context.getResources().getString(R.string.Error_detail_17);
                break;
            case 18:
                errType = context.getResources().getString(R.string.Error18);
                errDetail = context.getResources().getString(R.string.Error_detail_18);
                break;
            case 19:
                errType = context.getResources().getString(R.string.Error19);
                errDetail = context.getResources().getString(R.string.Error_detail_19);
                break;
            case 20:
                errType = context.getResources().getString(R.string.Error20);
                errDetail = context.getResources().getString(R.string.Error_detail_20);
                break;
            case 21:
                errType = context.getResources().getString(R.string.ErrorS1);
                errDetail = context.getResources().getString(R.string.Error_detail_S1);
                break;
            case 22:
                errType = context.getResources().getString(R.string.ErrorS2);
                errDetail = context.getResources().getString(R.string.Error_detail_S2);
                break;
            case 23:
                errType = context.getResources().getString(R.string.ErrorS3);
                errDetail = context.getResources().getString(R.string.Error_detail_S3);
                break;
            case 24:
                errType = context.getResources().getString(R.string.ErrorS4);
                errDetail = context.getResources().getString(R.string.Error_detail_S4);
                break;
            case 25:
                errType = context.getResources().getString(R.string.ErrorS5);
                errDetail = context.getResources().getString(R.string.Error_detail_S5);
                break;
            case 26:
                errType = context.getResources().getString(R.string.ErrorS6);
                errDetail = context.getResources().getString(R.string.Error_detail_S6);
                break;
            case 27:
                errType = context.getResources().getString(R.string.ErrorS7);
                errDetail = context.getResources().getString(R.string.Error_detail_S7);
                break;
            default:
                break;
        }
        return errType + "===" + errDetail;
    }


    private static int getErrorCode(int errCode) {
        int code = 0;
        while (errCode >= 2) {
            errCode = errCode / 2;
            code += 1;
        }
        return code;
    }

    private static int getErrorCode(String errCode) {
        int code = 0;
        for (int i = 0; i < errCode.length(); i++) {
            if (errCode.charAt(i) == '1') {
                code = i;
                break;
            }
        }
        return code;
    }
}
