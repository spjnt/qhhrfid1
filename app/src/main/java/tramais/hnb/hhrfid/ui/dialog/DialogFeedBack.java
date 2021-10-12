package tramais.hnb.hhrfid.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import tramais.hnb.hhrfid.R;

public class DialogFeedBack extends Dialog {
    private final Context context;
    private ImageView mIvImg;


    public DialogFeedBack(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feedback);

        mIvImg = findViewById(R.id.iv_img);
        mIvImg.setOnClickListener(view -> {
            dismiss();
        });
    }
}
