package tramais.hnb.hhrfid.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.apkfuns.logutils.LogUtils;

import java.util.Collection;
import java.util.List;

import tramais.hnb.hhrfid.lv.ViewHolder;


/**
 * Created by Administrator on 2017/6/22.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {

    protected final int mItemLayoutId;
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;

    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }


    @Override
    public T getItem(int position) {
        if (mDatas != null && position < mDatas.size()) {
            return mDatas.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();

    }

    public abstract void convert(ViewHolder helper, T item);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }


    public CommonAdapter<T> loadMore(Collection<T> collection) {
        LogUtils.e("mDatas" + mDatas.size());
        mDatas.addAll(collection);
        notifyDataSetChanged();
        return this;
    }


}
