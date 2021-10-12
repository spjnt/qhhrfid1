package tramais.hnb.hhrfid.ui.popu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.CommonAdapter;
import tramais.hnb.hhrfid.bean.LandDetail;
import tramais.hnb.hhrfid.lv.ViewHolder;



public class PopuDk extends PopupWindow implements View.OnClickListener {
    List<LandDetail> landDetails;
    private View conentView;
    private ListView listView;
    private View inflate;
    private TextView tv_confim, tv_title;

    private final View rootView;
    private final Activity context;
    private WindowManager.LayoutParams lp;
    private final String title;
    private TextView tvInsureSum, tvCheckSum, dkSum;

    public PopuDk(Activity context, View rootView, String title, List<LandDetail> landDetails) {
        this.landDetails = landDetails;

        this.rootView = rootView;
        this.context = context;
        this.title = title;
        initView(context, landDetails);

        setPopupWindow(context);

    }

    private void initView(Activity context, final List<LandDetail> landDetails) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflate = inflater.inflate(R.layout.popu_dk, null);
        listView = inflate.findViewById(R.id.lv_land_detail);

        dkSum = inflate.findViewById(R.id.dk_sum);
        tvInsureSum = inflate.findViewById(R.id.tv_insure_sum);
        tvCheckSum = inflate.findViewById(R.id.tv_check_sum);
        setLandDetailAdapter(landDetails);
        dkSum.setText(landDetails.get(0).getBlock_count());
        tvInsureSum.setText(landDetails.get(0).getSum_FSquare());
        tvCheckSum.setText(landDetails.get(0).getSum_FCheckSquare());
        tv_confim = inflate.findViewById(R.id.tv_confim);
        tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(title);
        tv_confim.setOnClickListener(this);

    }

    private void setLandDetailAdapter(List<LandDetail> landDetails) {
        if (landDetails == null || landDetails.size() == 0) return;
        listView.setAdapter(new CommonAdapter<LandDetail>(context, landDetails, R.layout.item_land_detail) {
            @Override
            public void convert(ViewHolder helper, LandDetail item) {
                TextView farmer_name = helper.getView(R.id.farmer_name);
                TextView farmer_dk = helper.getView(R.id.dk);
                TextView insure_dk = helper.getView(R.id.insure_dk);
                TextView check_dk = helper.getView(R.id.check_dk);
                farmer_name.setText(item.getFName());
                farmer_dk.setText(setNumber(item.getFLandNumber()));
                insure_dk.setText(item.getFSquare());
                check_dk.setText(item.getFCheckSquare());
            }
        });
    }

    private String setNumber(String number) {
        if (TextUtils.isEmpty(number) || number.length() <= 4) return number;
        else return "****" + number.substring(number.length() - 4, number.length());
    }

    /**
     * 设置窗口的相关属性
     */
    @SuppressLint("InlinedApi")
    private void setPopupWindow(Activity context) {
        this.setContentView(inflate);// 设置View
        lp = context.getWindow().getAttributes();
        lp.alpha = 0.4f;
        context.getWindow().setAttributes(lp);
        int height = context.getResources().getDisplayMetrics().heightPixels;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(height / 2);// 设置弹出窗口的高
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        this.setClippingEnabled(true);
        this.setAnimationStyle(R.style.PopMenuAnimation);// 设置动画
        this.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        inflate.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = inflate.findViewById(R.id.id_pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confim:
                dismiss();
                break;
        }
    }

    private void dissMiss() {
        lp.alpha = 1f;
        context.getWindow().setAttributes(lp);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        dissMiss();
    }
}
