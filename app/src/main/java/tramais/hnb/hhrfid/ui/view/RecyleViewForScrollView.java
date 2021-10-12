package tramais.hnb.hhrfid.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * Created by Lenovo on 2017/9/22.
 */

public class RecyleViewForScrollView extends SwipeRecyclerView {

    private ScrollView mParent;

    private float mDownY;

    public RecyleViewForScrollView(Context context) {
        super(context);
    }

    public RecyleViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyleViewForScrollView(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}