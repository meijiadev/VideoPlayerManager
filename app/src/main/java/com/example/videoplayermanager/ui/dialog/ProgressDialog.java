package com.example.videoplayermanager.ui.dialog;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseDialog;
import com.example.videoplayermanager.base.MyDialogFragment;
import com.example.videoplayermanager.other.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.fragment.app.FragmentActivity;


/**
 * 进度弹窗
 */
public final class ProgressDialog {
    public static final class Builder extends MyDialogFragment.Builder<Builder>{
        public final TextView tvProgress;
        public final ProgressBar progressBar;
        private String title;
        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_progress);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setCancelable(false);
            tvProgress=findViewById(R.id.tv_progress);
            progressBar=findViewById(R.id.process_bar);
            progressBar.setMax(100);
            EventBus.getDefault().register(this);
        }

        public Builder setTitle(String title){
            this.title=title;
            return this;
        }
        @Override
        protected void dismiss() {
            super.dismiss();
            EventBus.getDefault().unregister(this);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void setProgress(MessageEvent messageEvent){
            switch (messageEvent.getType()){
                case updateProgress:
                    int progress= (int) messageEvent.getData();
                    tvProgress.setText(title+progress+"%");
                    progressBar.setProgress(progress);
                    break;
                case apkDownloadSucceed:
                case apkDownloadFailed:
                    dismiss();
                    break;
            }
        }
    }


}
