package tramais.hnb.hhrfid.util;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.request.target.ImageViewTarget;

public class TransformationUtils extends ImageViewTarget<Bitmap> {
    private final ImageView target;

    public TransformationUtils(ImageView imageView) {
        super(imageView);
        this.target = imageView;
    }

    /* access modifiers changed from: protected */
    public void setResource(Bitmap bitmap) {
        if (bitmap != null) {
            ((ImageView) this.view).setImageBitmap(bitmap);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            double width2 = (double) this.target.getWidth();
            Double.isNaN(width2);
            double d = (double) width;
            Double.isNaN(d);
            ViewGroup.LayoutParams layoutParams = this.target.getLayoutParams();
            layoutParams.height = (int) (((float) height) * (((float) (width2 * 0.1d)) / ((float) (d * 0.1d))));
            this.target.setLayoutParams(layoutParams);
        }
    }
}
