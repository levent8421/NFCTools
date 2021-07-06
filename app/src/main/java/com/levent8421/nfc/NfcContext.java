package com.levent8421.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;
import android.provider.Settings;

public class NfcContext {
    private static final IntentFilter[] INTENT_FILTER_ARRAY;
    private static final String[][] TECH_LIST_ARRAY;

    static {
        final IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
            ndef.addCategory(Intent.CATEGORY_DEFAULT);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        final IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tech.addCategory(Intent.CATEGORY_DEFAULT);
        final IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tag.addCategory(Intent.CATEGORY_DEFAULT);
        INTENT_FILTER_ARRAY = new IntentFilter[]{ndef, tech, tag};
        TECH_LIST_ARRAY = new String[][]{
                new String[]{
                        NfcA.class.getName(),
                        NfcF.class.getName(),
                        NfcB.class.getName(),
                        NfcV.class.getName(),
                        Ndef.class.getName(),
                        TagTechnology.class.getName(),
                }
        };
    }

    private final Context context;
    private final NfcAdapter nfcAdapter;

    public NfcContext(Context context) {
        this.context = context;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public boolean isNfcSupport() {
        return nfcAdapter != null;
    }

    public boolean isNfcEnable() {
        if (!isNfcSupport()) {
            return false;
        }
        return nfcAdapter.isEnabled();
    }

    public void showNfcSetting() {
        if (!isNfcSupport()) {
            return;
        }
        final Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
        context.startActivity(intent);
    }

    public void enableForegroundDispatch(Activity activity) {
        if (!isNfcSupport()) {
            return;
        }
        final Intent intent = new Intent(context, activity.getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, 0);
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, INTENT_FILTER_ARRAY, TECH_LIST_ARRAY);
    }

    public void disableForegroundDispatch(Activity activity) {
        if (!isNfcSupport()) {
            return;
        }
        nfcAdapter.disableForegroundDispatch(activity);
    }
}
