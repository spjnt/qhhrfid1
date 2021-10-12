package tramais.hnb.hhrfid.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.CommonAdapter;
import tramais.hnb.hhrfid.bean.Region;
import tramais.hnb.hhrfid.interfaces.GetTwoString;
import tramais.hnb.hhrfid.lv.ViewHolder;

public class DialogChoiceRegion extends Dialog {
    List<Region.DataBean> data;
    GetTwoString getTwoString;
    List<String> towms = new ArrayList<>();
    List<String> volls = new ArrayList<>();
    private final Context context;
    private ListView mLvTown, mLvVoll;
    private String town = "";

    public DialogChoiceRegion(@NonNull Context context, List<Region.DataBean> data, GetTwoString getTwoString) {
        super(context, R.style.dialog);
        this.context = context;
        this.data = data;
        this.getTwoString = getTwoString;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice_region);
        mLvTown = findViewById(R.id.lv_town);
        mLvVoll = findViewById(R.id.lv_voll);

        towms.clear();
        for (Region.DataBean item : data) {
            String fTown = item.getFTown();

            if (!fTown.isEmpty() && !towms.contains(fTown)) towms.add(fTown);


        }
        if (towms != null && towms.size() > 0) {
            setTownAdapter(towms);
            setVollAdapter(towms.get(0));
            town = towms.get(0);
            mLvTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    town = towms.get(position);

                    setVollAdapter(towms.get(position));
                }
            });
            mLvVoll.setOnItemClickListener((parent, view, position, id) -> {
                getTwoString.getTwo(town, volls.get(position));
                dismiss();
            });
        } else {
            getTwoString.getTwo("数据异常", "数据异常");
            dismiss();
        }

    }

    private void setTownAdapter(List<String> towns) {
        if (towns.size() == 0) return;


        if (towns != null && towns.size() > 0)
            mLvTown.setAdapter(new CommonAdapter<String>(context, towns, R.layout.item_text) {
                @Override
                public void convert(ViewHolder helper, String item) {
                    TextView textView = helper.getView(R.id.tv);
                    textView.setText(item);

                }
            });
    }

    private void setVollAdapter(String town) {
        volls.clear();
        for (Region.DataBean item : data) {
            String fTown = item.getFTown();
            if (!fTown.isEmpty() && fTown.equals(town)) {
                String fVillage = item.getFVillage();
                if (!fVillage.isEmpty() && !volls.contains(fVillage)) volls.add(fVillage);
            }

        }

        if (volls != null && volls.size() > 0) {
            mLvVoll.setAdapter(new CommonAdapter<String>(context, volls, R.layout.item_text) {
                @Override
                public void convert(ViewHolder helper, String item) {
                    TextView textView = helper.getView(R.id.tv);
                    textView.setText(item);

                }
            });
        }
    }
}
