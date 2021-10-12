package tramais.hnb.hhrfid.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.interfaces.GetOneString;
import tramais.hnb.hhrfid.ui.popu.EasyPopup;
import tramais.hnb.hhrfid.ui.popu.XGravity;
import tramais.hnb.hhrfid.ui.popu.YGravity;
import tramais.hnb.hhrfid.ui.view.TriangleDrawable;

public class PopuUtilsBig {
    Context context;

    public PopuUtilsBig(Context context) {
        this.context = context;
    }

    public void initChoicePop(View v, List<String> lists, GetOneString getString) {
        EasyPopup popup = EasyPopup.create();
        popup.setContentView(context, R.layout.animal_type_big);
        popup.setAnimationStyle(R.style.TopPopAnim);
        popup.setOnViewListener(new EasyPopup.OnViewListener() {

            @Override
            public void initViews(View view, EasyPopup basePopup) {
                View arrowView = view.findViewById(R.id.v_arrow_weibo);
                ListView lv = view.findViewById(R.id.lv_content);
                lv.setAdapter(new ArrayAdapter<String>(context, R.layout.item_text, lists));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (lists.get(position) instanceof String)
                            getString.getString(lists.get(position));
                        popup.dismiss();
                    }
                });
                arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.WHITE));
            }
        });
        popup.setBackgroundDimEnable(true);
        popup.setFocusAndOutsideEnable(true);
        popup.setDimValue(0.4f);
        popup.apply();
        int offsetY = (v.getHeight() - v.getHeight()) / 2;
        popup.showAtAnchorView(v, YGravity.BELOW, XGravity.CENTER, 0, offsetY);

    }
}
