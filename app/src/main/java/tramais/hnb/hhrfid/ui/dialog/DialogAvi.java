package tramais.hnb.hhrfid.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkfuns.logutils.LogUtils;
import com.wang.avi.AVLoadingIndicatorView;

import tramais.hnb.hhrfid.R;

public class DialogAvi extends Dialog {
    public TextView wait_desc;
    String desc;
    private final Context context;
    private AVLoadingIndicatorView mAvi;

    public DialogAvi(@NonNull Context context, String desc) {
        super(context, R.style.dialog_avi);
        this.context = context;
        this.desc = desc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_avi);
        mAvi = findViewById(R.id.avi);
        wait_desc = findViewById(R.id.wait_desc);
        setText(desc);

        // mAvi.smoothToShow();

    }

    public void setText(String desc) {
        if (!TextUtils.isEmpty(desc))
            wait_desc.setText(desc);
    }

    @Override
    public void setOnDismissListener(@Nullable final OnDismissListener listener) {
        super.setOnDismissListener(listener);
          mAvi.smoothToHide();
    }
}
