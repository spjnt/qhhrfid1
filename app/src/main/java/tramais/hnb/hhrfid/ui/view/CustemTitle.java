package tramais.hnb.hhrfid.ui.view;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.ui.MainActivity;
import tramais.hnb.hhrfid.util.Utils;

public class CustemTitle extends LinearLayout {

    public ImageView mIvBack;
    public TextView mRootDelete;
    private TextView mRootTitle;
    private ImageView mSetting;
    private RelativeLayout mRlRoot;

    public CustemTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        initComp(view);
    }

    private void initComp(View view) {
        mIvBack = view.findViewById(R.id.iv_back);
        mRootTitle = view.findViewById(R.id.root_title);
        mSetting = view.findViewById(R.id.setting);
        mIvBack.setOnClickListener(view1 -> {
            ((Activity) getContext()).finish();
        });
        mRlRoot = view.findViewById(R.id.rl_root);
        mSetting.setOnClickListener(view1 -> {
            ((Activity) getContext()).finish();
            Utils.goToNextUI(MainActivity.class);
        });
        mRootDelete = view.findViewById(R.id.root_delete);
    }

    public ImageView getIvBack() {
        return mIvBack;
    }

    public void setTitleText(String title) {
        if (mRootTitle != null)
            mRootTitle.setText(title);
    }
    public void hideRootDelete(int i) {
        mRootDelete.setVisibility(i);
       // TextView textView = this.mRootDelete;
       /* if (textView != null) {

        }*/
    }
    public void showRootDelete() {
        if (mRootDelete != null)
            mRootDelete.setVisibility(VISIBLE);
    }

    public void setmRootDeleteText(String deleteText) {
        if (mRootDelete != null)
            mRootDelete.setVisibility(VISIBLE);
        mRootDelete.setText(deleteText);
    }

    public void setTitleText(int title) {
        if (mRootTitle != null)
            mRootTitle.setText(getResources().getText(title));
    }

    public void setTitleTextColor(int color) {
        if (mRootTitle != null)
            mRootTitle.setTextColor(getResources().getColor(color));
    }

    public void showRootSetting() {
        if (mSetting != null)
            mSetting.setVisibility(View.VISIBLE);
    }

    public void hideBackImage() {
        if (mIvBack != null)
            mIvBack.setVisibility(View.GONE);
    }

    public void hideAllTitle() {
        if (mRlRoot != null)
            mRlRoot.setVisibility(View.GONE);
    }


}
