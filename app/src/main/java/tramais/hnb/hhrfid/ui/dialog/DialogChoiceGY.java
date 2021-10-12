package tramais.hnb.hhrfid.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.CommonAdapter;
import tramais.hnb.hhrfid.bean.GYAreaBean;
import tramais.hnb.hhrfid.interfaces.GetGYArea;
import tramais.hnb.hhrfid.lv.ViewHolder;
import tramais.hnb.hhrfid.net.RequestUtil;
import tramais.hnb.hhrfid.util.GsonUtil;

public class DialogChoiceGY extends Dialog {
    GetGYArea getGYArea;
    private final Context context;
    private ListView mLvContent;
    private EditText etSearch;
    private List<GYAreaBean> allAreas;
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    allAreas = (List<GYAreaBean>) msg.obj;
                    setAdapter(allAreas);
                    break;
            }
        }
    };

    public DialogChoiceGY(@NonNull Context context, GetGYArea getGYArea) {
        super(context, R.style.dialog);
        this.context = context;
        this.getGYArea = getGYArea;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice_gy);
        mLvContent = findViewById(R.id.lv_content);
        etSearch = (EditText) findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                RequestUtil.getInstance(context).getGYArea("", s.toString(), (rtnCode, message, totalNums, datas) -> {
                    if (datas != null && datas.size() > 0) {
                        List<GYAreaBean> allBillDetals = GsonUtil.getInstant().parseCommonUseArr(datas, GYAreaBean.class);
                        if (allBillDetals != null && allBillDetals.size() > 0) {
                            Message msg = new Message();
                            msg.obj = allBillDetals;
                            msg.what = 0;
                            handler.sendMessage(msg);
                        } else {
                            Toast.makeText(context, "暂无数据展示", Toast.LENGTH_LONG).show();
                            mLvContent.setAdapter(null);
                        }
                    }


                });
            }
        });
        mLvContent.setOnItemClickListener((parent, view, position, id) -> {
            getGYArea.getString(allAreas.get(position));

            dismiss();
        });
    }


    private void setAdapter(List<GYAreaBean> areas) {
        if (areas.size() == 0) return;
        mLvContent.setAdapter(new CommonAdapter<GYAreaBean>(context, areas, R.layout.item_text) {
            @Override
            public void convert(ViewHolder helper, GYAreaBean item) {

                TextView textView = helper.getView(R.id.tv);
                textView.setText(item.getCountry());
            }
        });
    }
}
