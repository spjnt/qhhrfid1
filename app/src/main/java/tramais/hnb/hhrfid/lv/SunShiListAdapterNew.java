package tramais.hnb.hhrfid.lv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.bean.SunShiListBean;
import tramais.hnb.hhrfid.util.Utils;

public class SunShiListAdapterNew extends BaseAdapter {
    protected Context mContext;
    protected List<SunShiListBean> mDatas;

    protected LayoutInflater mInflater;


    public SunShiListAdapterNew(Context context, List<SunShiListBean> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
        this.mInflater = LayoutInflater.from(mContext);


    }

    @Override
    public int getCount() {
        return mDatas.size();
    }


    @Override
    public Object getItem(int position) {
        if (mDatas != null && position < mDatas.size()) {
            return mDatas.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void flushAdapter(int position, LinkedList<String> values) {
        SunShiListBean getAllLandListDTO = mDatas.get(position);
        getAllLandListDTO.setFInsureQty(values.get(0));
        getAllLandListDTO.setFRiskQty(values.get(2));
        getAllLandListDTO.setFLossQty(values.get(1));
        getAllLandListDTO.setFLossLevel(values.get(3));
        notifyDataSetChanged();
    }

    public void saveMore(String more) {
        for (SunShiListBean item : mDatas) {
            item.setFLossQty(Utils.formatDouble((Double.parseDouble(more) / 100) * Double.parseDouble(item.getFInsureQty())));
            item.setFRiskQty(Utils.formatDouble((Double.parseDouble(more) / 100) * Double.parseDouble(item.getFInsureQty())));
            item.setFLossLevel(more);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_sun_shi_list_new, null);
            holder = new ViewHolder();
            holder.one = view.findViewById(R.id.one);
            holder.two = view.findViewById(R.id.two);
            holder.three = view.findViewById(R.id.three);
            holder.four = view.findViewById(R.id.four);
            holder.five = view.findViewById(R.id.five);
            holder.six = view.findViewById(R.id.six);
            holder.seven = view.findViewById(R.id.seven);
            holder.eight = view.findViewById(R.id.eight);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        //输入框  复选框 设置key
        holder.four.setTag(position);
        holder.five.setTag(position);
        holder.six.setTag(position);
        holder.seven.setTag(position);

        SunShiListBean item = mDatas.get(position);
        if (item != null) {

            holder.one.setText(position + 1 + "");
            holder.two.setText(item.getFFarmerName());
            holder.three.setText(item.getFlandName());
            holder.four.setText(item.getFInsureQty() == null || item.getFInsureQty().equals("null") ? "0" : item.getFInsureQty());
            holder.five.setText(item.getFRiskQty() == null || item.getFRiskQty().equals("null") ? "0" : item.getFRiskQty());
            holder.six.setText(item.getFLossQty() == null || item.getFLossQty().equals("null") ? "0" : item.getFLossQty());
            holder.seven.setText(item.getFLossLevel() == null || item.getFLossLevel().equals("null") ? "0%" : item.getFLossLevel() + "%");
            holder.eight.setText("删除");

            holder.eight.setOnClickListener(v -> {
                        mDatas.remove(item);
                        notifyDataSetChanged();
                    }

            );
        }


        return view;

    }

    public List<SunShiListBean> getDatasFinal() {
        return mDatas;
    }

    public static class ViewHolder {


        TextView one;
        TextView two;
        TextView three;

        TextView four, five, six, seven;
        TextView eight;
    }


}
