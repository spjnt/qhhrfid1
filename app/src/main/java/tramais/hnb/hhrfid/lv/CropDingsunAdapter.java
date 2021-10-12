package tramais.hnb.hhrfid.lv;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.bean.DingSunDetailResult;
import tramais.hnb.hhrfid.bean.DingSunResultBean;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.interfaces.GetResultJsonArarry;
import tramais.hnb.hhrfid.net.RequestUtil;
import tramais.hnb.hhrfid.ui.crop.ActivityDingSunResult;
import tramais.hnb.hhrfid.util.GsonUtil;

public class CropDingsunAdapter extends BaseAdapter {
    public HashMap<Integer, String> contents = new HashMap<>();
    protected Context mContext;
    protected List<DingSunResultBean> mDatas;
    protected LayoutInflater mInflater;
    private int selectedEditTextPosition = -1;

    public CropDingsunAdapter(Context context, List<DingSunResultBean> mDatas) {
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

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_dingsun_info_list, null);
            holder = new ViewHolder();
            holder.one = view.findViewById(R.id.one);
            holder.two = view.findViewById(R.id.two);
            holder.three = view.findViewById(R.id.three);
            holder.four = view.findViewById(R.id.four);
            holder.five = view.findViewById(R.id.five);
            holder.five.addTextChangedListener(new MyTextChangedListener(holder, contents));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //输入框  复选框 设置key
        holder.five.setTag(position);
        DingSunResultBean item = mDatas.get(position);
        if (item != null) {
            holder.one.setText("删除");
            holder.two.setText(item.getFFarmerName());
            holder.three.setText(item.getFInsureQty() + "");
            holder.four.setText(item.getFRiskQty() + "");
            holder.five.setText(item.getFLossQty() + "");
            holder.one.setOnClickListener(v -> {
                        mDatas.remove(item);
                        notifyDataSetChanged();
                    }

            );
        }
        //输入的剂量
        if (!TextUtils.isEmpty(contents.get(position))) {//不为空的时候 赋值给对应的edittext
            holder.five.setText(contents.get(position));
            item.setFLossQty(contents.get(position));
        } else {
            holder.five.setText(item.getFLossQty());
            item.setFLossQty(item.getFLossQty());
        }

        //输入框选中
        holder.five.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                selectedEditTextPosition = position;
            }
            return false;
        });
        //输入框获取焦点
        holder.five.clearFocus();
        if (selectedEditTextPosition != -1 && selectedEditTextPosition == position) {
            //强制加上焦点
            holder.five.requestFocus();
            //设置光标显示到编辑框尾部
            holder.five.setSelection(holder.five.getText().length());
            //重置
            selectedEditTextPosition = -1;
        }
        //输入框强制获取焦点
        ViewHolder finalHolder = holder;
        holder.five.setOnFocusChangeListener((v, hasForce) -> {
            if (!hasForce) {
                item.setFLossQty(finalHolder.five.getText().toString());
            }
        });

        holder.two.setOnClickListener(v -> {
            RequestUtil.getInstance(mContext).GetDingSunlandDetailResult("BA2020081600002", item.getFFarmerNumber(), item.getFid(), new GetResultJsonArarry() {
                @Override
                public void getResult(int rtnCode, String message, int totalNums, JSONArray datas) {
                    List<DingSunDetailResult> dingSunDetailResults = GsonUtil.getInstant().parseCommonUseArr(datas, DingSunDetailResult.class);
                    String s = toJson(dingSunDetailResults);
                    Intent intent = new Intent(mContext, ActivityDingSunResult.class);
                    intent.putExtra(Constants.json, s);
                    mContext.startActivity(intent);
                }
            });
        });
        return view;

    }

    private String toJson(List<DingSunDetailResult> dingSunDetailResults) {
        if (dingSunDetailResults == null || dingSunDetailResults.size() == 0) return "";
        JSONArray array = new JSONArray();
        int index = 1;
        for (DingSunDetailResult item : dingSunDetailResults) {
            JSONObject object = new JSONObject(true);
            object.put("序号", index++);
            object.put("农户", item.getFFarmerName());
            object.put("地块名称", item.getFlandName());
            object.put("承保面积", item.getFInsureQty());
            object.put("受灾面积", item.getFRiskQty());
            object.put("损失面积", item.getFLossQty());
            object.put("损失程度", item.getFLossLevel());
            array.add(object);
        }
        return array.toJSONString();
    }

    public List<DingSunResultBean> getDatasFinal() {
        return mDatas;
    }

    public static class ViewHolder {


        TextView one;
        TextView two;
        TextView three;
        TextView four;
        EditText five;

    }

    public class MyTextChangedListener implements TextWatcher {

        public ViewHolder holder;
        public HashMap<Integer, String> contents;

        public MyTextChangedListener(ViewHolder holder, HashMap<Integer, String> contents) {
            this.holder = holder;
            this.contents = contents;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (holder != null && contents != null) {
                int position = (int) holder.five.getTag();
                contents.put(position, editable.toString());
            }
        }
    }

}
