package tramais.hnb.hhrfid.ui.popu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.interfaces.GetOneString;



public class PopuChoice extends PopupWindow implements View.OnClickListener {
    List<String> data;
    private View conentView;
    private ListView listView;
    private View inflate;
    private TextView tv_confim, tv_title;
    private final GetOneString getRoom;
    private final View rootView;
    private final Activity context;
    private WindowManager.LayoutParams lp;
    private final String title;

    public PopuChoice(Activity context, View rootView, String title, List<String> data, GetOneString getRoom) {
        this.data = data;
        this.getRoom = getRoom;
        this.rootView = rootView;
        this.context = context;
        this.title = title;
        initView(context, data);
        setPopupWindow(context);

    }

    private void initView(Activity context, final List<String> data) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflate = inflater.inflate(R.layout.popu_choice, null);
        listView = inflate.findViewById(R.id.list_room_choice);
        listView.setAdapter(new ArrayAdapter<String>(context, R.layout.item_room, data));
        tv_confim = inflate.findViewById(R.id.tv_confim);
        tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(title);
        tv_confim.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getRoom.getString(data.get(position));
                dismiss();
            }
        });
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
