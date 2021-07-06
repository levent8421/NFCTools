package com.levent8421.nfc.util;

import android.content.Context;
import android.content.Intent;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Author:Created by zhurui
 * Time:6/29/21 3:32 PM
 * Description:This is NavigationBarUtils
 *
 * @author zhurui
 */
public class NavigationBarUtils {
    private static final String CMD_SHOW_STATUS_BAR = "settings put system systembar_hide 0";
    private static final String CMD_HIDE_STATUS_BAR = "settings put system systembar_hide 1";
    private static final Intent INTENT_TOGGLE_STATUS_BAR = new Intent("com.tchip.changeBarHideStatus");

    private static final Intent INTENT_HIDE_BAR = new Intent("android.intent.action.hidebar");
    private static final Intent INTENT_SHOW_BAR = new Intent("android.intent.action.showbar");
    private static final String INTENT_HIDE_STATUS_BAR = "setprop persist.sys.hidetopbar 0";
    private static final String INTENT_SHOW_STATUS_BAR = "setprop persist.sys.hidetopbar 1";
    private static final String INTENT_STATUS_BAR_DOWN = "setprop persist.sys.disexpandbar 0";
    private static final String INTENT_STATUS_BAR_NO_DOWN = "setprop persist.sys.disexpandbar 1";

    public static int hideNavigationMC(Context context) {
        final int res = execRootCmdSilent(CMD_HIDE_STATUS_BAR);
        context.sendBroadcast(INTENT_TOGGLE_STATUS_BAR);
        return res;
    }

    public static int showNavigationMC(Context context) {
        final int res = execRootCmdSilent(CMD_SHOW_STATUS_BAR);
        context.sendBroadcast(INTENT_TOGGLE_STATUS_BAR);
        return res;
    }

    public static int hideNavigationLQS(Context context) {
        final int res = execRootCmdSilent(INTENT_HIDE_STATUS_BAR);
        context.sendBroadcast(INTENT_HIDE_BAR);
        Intent intent = new Intent("MyReceiver_Action");
        intent.putExtra("cmd", "hide_top_bar");
        context.sendBroadcast(intent);
        return res;
    }

    public static int showNavigationLQS(Context context) {
        final int res = execRootCmdSilent(INTENT_SHOW_STATUS_BAR);
        context.sendBroadcast(INTENT_SHOW_BAR);
        Intent intent = new Intent("MyReceiver_Action");
        intent.putExtra("cmd", "show_top_bar");
        context.sendBroadcast(intent);
        return res;
    }

    private static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
