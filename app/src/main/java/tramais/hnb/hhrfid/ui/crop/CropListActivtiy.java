package tramais.hnb.hhrfid.ui.crop;

import android.os.Bundle;
import android.widget.Button;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.BaseActivity;
import tramais.hnb.hhrfid.util.Utils;

public class CropListActivtiy extends BaseActivity {

    private Button regist_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_list);
    }

    @Override
    protected void initView() {
        regist_new = findViewById(R.id.regist_new);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListner() {
        regist_new.setOnClickListener(v -> {
            Utils.goToNextUI(CropMakeDealActivity.class);
        });
    }
}
