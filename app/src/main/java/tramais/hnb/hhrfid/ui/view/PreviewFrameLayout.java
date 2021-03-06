/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package tramais.hnb.hhrfid.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.apkfuns.logutils.LogUtils;

/**
 * A layout which handles the preview aspect ratio.
 */
public class PreviewFrameLayout extends RelativeLayout {
    /**
     * A callback to be invoked when the preview frame's size changes.
     */
    private Context context = null;
    private double mAspectRatio = 4.0 / 3.0;

    public PreviewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public void setAspectRatio(double ratio) {
        if (ratio <= 0.0)
            throw new IllegalArgumentException();
        if (mAspectRatio != ratio) {
            mAspectRatio = ratio;
            requestLayout();
        }
    }

    public void showBorder(boolean enabled) {
        setActivated(enabled);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int previewWidth = MeasureSpec.getSize(widthSpec);
        int previewHeight = MeasureSpec.getSize(heightSpec);

        if (previewWidth < previewHeight) {
            int tmp = previewWidth;
            previewWidth = previewHeight;
            previewHeight = tmp;
        }

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(previewHeight,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    previewWidth, MeasureSpec.EXACTLY));


        } else {

            super.onMeasure(MeasureSpec.makeMeasureSpec(previewWidth,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    previewHeight, MeasureSpec.EXACTLY));
        }
    }

    public interface OnSizeChangedListener {
        void onSizeChanged();
    }
}
