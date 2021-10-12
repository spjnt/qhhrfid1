package tramais.hnb.hhrfid.ui.popu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.interfaces.ChoicePhoto;


public class PopuChoicePicture extends PopupWindow implements View.OnClickListener {

    private View inflate;
    private TextView tvUnShare;
    private final Activity context;
    private TextView svGetPhoto;
    private TextView svTakePhoto;
    private TextView svCancle;
    private final ChoicePhoto photo;

    public PopuChoicePicture(Activity context, ChoicePhoto photo) {
        this.context = context;
        this.photo = photo;
        initView(context);
        setPopupWindow();

    }

    private void initView(Activity context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflate = inflater.inflate(R.layout.choice_pic, null);


        svGetPhoto = (TextView) inflate.findViewById(R.id.sv_get_photo);
        svTakePhoto = (TextView) inflate.findViewById(R.id.sv_take_photo);
        svCancle = (TextView) inflate.findViewById(R.id.sv_cancle);
        svGetPhoto.setOnClickListener(this);
        svTakePhoto.setOnClickListener(this);
        svCancle.setOnClickListener(this);
    }


    /**
     * 设置窗口的相关属性
     */
    @SuppressLint("InlinedApi")
    private void setPopupWindow() {
        this.setContentView(inflate);// 设置View
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.4f;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        // 设置SelectPicPopupWindow弹出窗体可点击
        context.getWindow().setAttributes(lp);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        this.setAnimationStyle(R.style.PopMenuAnimation);// 设置动画
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                dissMiss();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.sv_take_photo:
                photo.actionCamera();
                dissMiss();
                break;
            case R.id.sv_get_photo:
                photo.actionAlbum();
                dissMiss();
                break;
            case R.id.sv_cancle:
                dissMiss();
                break;
            default:

        }
    }

    private void dissMiss() {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 1f;
        context.getWindow().setAttributes(lp);
        dismiss();
    }

}
