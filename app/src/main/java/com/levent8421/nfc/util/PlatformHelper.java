package com.levent8421.nfc.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 平台相关帮助类
 * ClassName: PlatformHelper
 * 平台（指由厂商定制的安卓系统和厂商硬件平台）相关操作的帮助类
 *
 * @author levent
 */
public class PlatformHelper {
    private static final String LOG_TAG = "PlatformHelper";

    /**
     * 导航栏操作器
     */
    interface NavBarOperator {
        /**
         * 隐藏导航栏和状态栏
         *
         * @return 是否隐藏成功
         */
        boolean hideNavAndStatusBar();

        /**
         * 显示导航栏和状态栏
         *
         * @return 是否显示成功
         */
        boolean showNavAndStatusBar();
    }

    /**
     * 旧版平板使用的状态栏导航栏操作方式
     */
    static class McNavBarOperator implements NavBarOperator {
        private static final String CMD_SHOW_STATUS_BAR = "settings put system systembar_hide 0";
        private static final String CMD_HIDE_STATUS_BAR = "settings put system systembar_hide 1";
        private static final int STATE_CODE_OK = 0;
        private static final Intent INTENT_REFRESH_NAV_BAR_STATUS = new Intent("com.tchip.changeBarHideStatus");
        private final Context context;

        private McNavBarOperator(Context context) {
            this.context = context;
        }

        @Override
        public boolean hideNavAndStatusBar() {
            final int res = execCmdInShell(CMD_HIDE_STATUS_BAR);
            context.sendBroadcast(INTENT_REFRESH_NAV_BAR_STATUS);
            return res == STATE_CODE_OK;
        }

        @Override
        public boolean showNavAndStatusBar() {
            final int res = execCmdInShell(CMD_SHOW_STATUS_BAR);
            context.sendBroadcast(INTENT_REFRESH_NAV_BAR_STATUS);
            return res == STATE_CODE_OK;
        }
    }

    /**
     * 新版平板使用的导航栏状态栏操作方式
     */
    static class LqsNavBarOperator implements NavBarOperator {
        private static final Intent INTENT_HIDE_NAV_BAR = new Intent("android.intent.action.hidebar");
        private static final Intent INTENT_SHOW_NAV_BAR = new Intent("android.intent.action.showbar");
        private static final String CMD_HIDE_STATUS_BAR = "setprop persist.sys.hidetopbar 0";
        private static final String CMD_SHOW_STATUS_BAR = "setprop persist.sys.hidetopbar 1";
        private static final int STATE_CODE_OK = 0;
        private final Context context;

        private LqsNavBarOperator(Context context) {
            this.context = context;
        }

        @Override
        public boolean hideNavAndStatusBar() {
            final int res = execCmdInShell(CMD_HIDE_STATUS_BAR);
            context.sendBroadcast(INTENT_HIDE_NAV_BAR);
            Intent intent = new Intent("MyReceiver_Action");
            intent.putExtra("cmd", "hide_top_bar");
            context.sendBroadcast(intent);
            return res == STATE_CODE_OK;
        }

        @Override
        public boolean showNavAndStatusBar() {
            final int res = execCmdInShell(CMD_SHOW_STATUS_BAR);
            context.sendBroadcast(INTENT_SHOW_NAV_BAR);
            Intent intent = new Intent("MyReceiver_Action");
            intent.putExtra("cmd", "show_top_bar");
            context.sendBroadcast(intent);
            return res == STATE_CODE_OK;
        }
    }

    public static int execCmdInShell(String cmd) {
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

    private final Context context;
    private final List<NavBarOperator> navBarOperators;

    public PlatformHelper(Context context) {
        this.context = context;
        this.navBarOperators = this.buildNavBarOperators();
    }

    private List<NavBarOperator> buildNavBarOperators() {
        final List<NavBarOperator> operators = new ArrayList<>();
        operators.add(new McNavBarOperator(context));
        operators.add(new LqsNavBarOperator(context));
        return operators;
    }

    /**
     * 隐藏状态栏和导航栏
     *
     * @return 是否隐藏成功
     */
    public boolean hideNavAndStatusBar() {
        boolean success = false;
        for (NavBarOperator operator : this.navBarOperators) {
            try {
                final boolean res = operator.hideNavAndStatusBar();
                if (res) {
                    success = true;
                }
            } catch (Exception e) {
                Log.i(LOG_TAG, "Error on hide Nav&Status bar with:" + operator.getClass().getName(), e);
            }
        }
        return success;
    }

    /**
     * 显示状态栏和导航栏
     *
     * @return 是否显示成功
     */
    public boolean showNavAndStatusBar() {
        boolean success = false;
        for (NavBarOperator operator : this.navBarOperators) {
            try {
                final boolean res = operator.showNavAndStatusBar();
                if (res) {
                    success = true;
                }
            } catch (Exception e) {
                Log.i(LOG_TAG, "Error on show Nav&Status bar with:" + operator.getClass().getName(), e);
            }
        }
        return success;
    }
}
