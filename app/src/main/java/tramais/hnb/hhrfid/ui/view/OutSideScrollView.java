package tramais.hnb.hhrfid.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class OutSideScrollView extends ScrollView {
    public OutSideScrollView(Context context) {
        super(context);
    }

    public OutSideScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutSideScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
