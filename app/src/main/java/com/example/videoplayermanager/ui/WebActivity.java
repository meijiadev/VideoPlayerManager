package com.example.videoplayermanager.ui;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseActivity;
import com.example.videoplayermanager.other.Logger;
import com.just.agentweb.AgentWeb;
import butterknife.BindView;
import butterknife.OnClick;

public class WebActivity extends BaseActivity {
    @BindView(R.id.container)
    LinearLayout mLinearLayout;
    @BindView(R.id.tvBaiDu)
    TextView tvBaiDu;
    @BindView(R.id.tvBilibili)
    TextView tvBili;
    @BindView(R.id.tvGitHub)
    TextView tvGitHUb;

    protected AgentWeb mAgentWeb;

    public static final String BAI_DU="https://www.baidu.com/?tn=80035161_1_dg";
    public static final String BI_LI_BI_LI="https://www.bilibili.com/";
    public static final String GIT_HUB="https://github.com/Justson/AgentWeb";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }

    @Override
    protected void initData() {
        long p = System.currentTimeMillis();
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mLinearLayout,new LinearLayout.LayoutParams(-1,-1) )//
                .useDefaultIndicator()//
                .createAgentWeb()//
                .ready()
                .go(null);
        mAgentWeb.getUrlLoader().loadUrl(BAI_DU);
        long n = System.currentTimeMillis();
        Logger.i("init used time:" + (n - p));
    }

    @OnClick({R.id.tvBaiDu,R.id.tvBilibili,R.id.tvGitHub})
    public void onClickView(View view){
        switch (view.getId()){
            case R.id.tvBaiDu:
                mAgentWeb.getUrlLoader().loadUrl(BAI_DU);
                break;
            case R.id.tvBilibili:
                mAgentWeb.getUrlLoader().loadUrl(BI_LI_BI_LI);
                break;
            case R.id.tvGitHub:
                mAgentWeb.getUrlLoader().loadUrl(GIT_HUB);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.clearWebCache();
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
