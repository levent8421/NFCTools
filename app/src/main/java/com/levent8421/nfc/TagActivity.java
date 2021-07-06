package com.levent8421.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class TagActivity extends AppCompatActivity {
    public static final String TAG = "NFC";
    private NfcContext nfcContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        nfcContext = new NfcContext(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcContext.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcContext.enableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "new intent!!!" + intent.getAction());
        if (!NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            return;
        }
        final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        for (String tech : tag.getTechList()) {
            Log.i(TAG, "NFC TAG Tech=" + tech);
        }
        MifareClassic classic = MifareClassic.get(tag);
        try {
            classic.connect();
            int sectorCount = classic.getSectorCount();
            Log.i(TAG, "sectorCount=" + sectorCount);
            for (int sector = 0; sector < sectorCount; sector++) {
                int blockOffset = classic.sectorToBlock(sector);
                int nBlock = classic.getBlockCountInSector(sector);
                for (int block = blockOffset; block < blockOffset + nBlock; block++) {
                    byte[] bytes = classic.readBlock(block);
                    String data = new String(bytes);
                    String hex = toHex(bytes);
                    Log.i(TAG, String.format("Block[%s] Sector[%s] hex=[%s] data=[%s]", block, sector, hex, data));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                classic.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(toHex(b))
                    .append(" ");
        }
        return sb.toString();
    }

    private String toHex(byte b) {
        String hex = "00" + Integer.toHexString(b);
        return hex.substring(hex.length() - 2);
    }
}