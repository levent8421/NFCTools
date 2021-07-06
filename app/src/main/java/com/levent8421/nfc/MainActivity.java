package com.levent8421.nfc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.levent8421.nfc.util.PlatformHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private NfcContext nfcContext;
    private PlatformHelper platformHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcContext = new NfcContext(this);
        platformHelper = new PlatformHelper(this);
        initView();
    }

    private void initView() {
        findViewById(R.id.btnCheckNfcAdapter).setOnClickListener(this);
        findViewById(R.id.btnCheckNfcEnable).setOnClickListener(this);
        findViewById(R.id.btnToTag).setOnClickListener(this);
        findViewById(R.id.btnHideNavStatusBar).setOnClickListener(this);
        findViewById(R.id.btnShowNavStatusBar).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCheckNfcAdapter:
                checkNfcAdapter();
                break;
            case R.id.btnCheckNfcEnable:
                checkNfcEnable();
                break;
            case R.id.btnToTag:
                startActivity(new Intent(this, TagActivity.class));
                break;
            case R.id.btnHideNavStatusBar:
                hideNavStatusBar();
                break;
            case R.id.btnShowNavStatusBar:
                showNavStatusBar();
                break;
        }
    }

    private void hideNavStatusBar() {
        final boolean successs = platformHelper.hideNavAndStatusBar();
        showToast("隐藏" + (successs ? "成功" : "失败"));
    }

    private void showNavStatusBar() {
        final boolean successs = platformHelper.showNavAndStatusBar();
        showToast("显示" + (successs ? "成功" : "失败"));
    }

    private void checkNfcEnable() {
        boolean nfcEnable = nfcContext.isNfcEnable();
        showToast("Enable:" + nfcEnable);
        if (!nfcEnable) {
            nfcContext.showNfcSetting();
        }
    }

    private void checkNfcAdapter() {
        showToast("Support:" + nfcContext.isNfcSupport());
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showToast("New Intent");
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
}