/*
package tramais.hnb.hhrfid.ui.crop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import java.util.ArrayList;
import java.util.List;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.BaseActivity;
import tramais.hnb.hhrfid.base.CommonAdapter;
import tramais.hnb.hhrfid.bean.CropChaKanBean;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.lv.ViewHolder;
import tramais.hnb.hhrfid.net.RequestUtil;
import tramais.hnb.hhrfid.util.GsonUtil;
import tramais.hnb.hhrfid.util.Utils;

public class CropCheck extends BaseActivity {
    int pageIndex = 1;
    int total = 0;
    private Button btnSearch;
    private ListView rv;
    private EditText etSearch;
    private String module_name;
    private MaterialRefreshLayout mRefreshLayout;
    */
/**
     * 在上拉刷新的时候，判断，是否处于上拉刷新，如果是的话，就禁止在一次刷新，保障在数据加载完成之前
     * 避免重复和多次加载
     *//*

    private boolean isLoadMore = true;
    private final List<CropChaKanBean> cropChaKanBeans_ = new ArrayList<>();
    private CommonAdapter<CropChaKanBean> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_check);
        initView();
    }

    @Override
    protected void initView() {
        mRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        btnSearch = (Button) findViewById(R.id.btn_search);
        rv = (ListView) findViewById(R.id.rv);
        etSearch = (EditText) findViewById(R.id.et_search);

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            module_name = intent.getStringExtra(Constants.MODULE_NAME);
            setTitleText(module_name);
        }
        getData(Utils.getEdit(etSearch), pageIndex);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cropChaKanBeans_.clear();
                getData(s.toString(), 1);
            }
        });

        initRefresh();
    }

    @Override
    protected void initListner() {
        btnSearch.setOnClickListener(v -> {
            Utils.goToNextUI(CropCheckPassActivity.class);
        });
        rv.setOnItemClickListener((parent, view, position, id) -> {
            CropChaKanBean bean = cropChaKanBeans_.get(position);
            Intent intent = new Intent(this, CropBaoAnActivity.class);
            intent.putExtra("bean", bean.getFNumber());
            startActivity(intent);
        });

    }

    private void getData(String input, int pageIndex) {
        RequestUtil.getInstance(this).GetLandChaKanList(input, 20, pageIndex, "", ((rtnCode, message, totalNums, datas) -> {
            total = totalNums;
            if (datas == null || datas.size() == 0) return;

            List<CropChaKanBean> cropChaKanBeans = GsonUtil.getInstant().parseCommonUseArr(datas, CropChaKanBean.class);
            cropChaKanBeans_.addAll(cropChaKanBeans);

            if (cropChaKanBeans_ != null && cropChaKanBeans_.size() > 0 && !isLoadMore)
                rv.post(new Runnable() {
                    @Override
                    public void run() {
                        rv.smoothScrollToPosition(cropChaKanBeans_.size() - 1);
                    }
                });
            else
                rv.setAdapter(null);

            setAdapter(cropChaKanBeans_);
        }));
    }

    private void setAdapter(List<CropChaKanBean> cropChaKanBeans) {

        if (cropChaKanBeans == null || cropChaKanBeans.size() == 0) return;
        rv.setAdapter(adapter = new CommonAdapter<CropChaKanBean>(this, cropChaKanBeans, R.layout.item_fenpei) {


            @Override
            public void convert(ViewHolder helper, CropChaKanBean item) {
                TextView tvFirst = (TextView) helper.getView(R.id.tv_first);

                TextView tvSecond = (TextView) helper.getView(R.id.tv_second);
                TextView tvThird = (TextView) helper.getView(R.id.tv_third);
                ImageView call = (ImageView) helper.getView(R.id.call);
                TextView tvFourth = (TextView) helper.getView(R.id.tv_fourth);
                Button btnToCheck = (Button) helper.getView(R.id.btn_to_check);
                call.setOnClickListener(v -> {
                    callPhone(item.getFMobile());
                });
                tvFirst.setText("报案号:" + item.getFNumber());
                tvSecond.setText("报案人:" + item.getFFarmerName());
                tvFourth.setText("地址:" + item.getFRiskAddress());
                tvThird.setText("电话:" + item.getFMobile());
                if (module_name.equals("任务查勘")) {
                    btnToCheck.setText("去查勘");
                } else if (module_name.equals("现场抽样")) {
                    btnToCheck.setText("去抽样");
                } else if (module_name.equals("理赔定损")) {
                    btnToCheck.setText("去定损");
                }

                btnToCheck.setOnClickListener(v -> {
                    Intent intent = new Intent();

                    if (intent != null) {
                        if (module_name.equals("任务查勘")) {
                            intent.setClass(mContext, CropCheckPassActivity.class);

                        } else if (module_name.equals("现场抽样")) {
                            intent.setClass(mContext, CropSmipleActivity.class);

                        } else if (module_name.equals("理赔定损")) {
                            intent.setClass(mContext, CropClaimAndLossDeter.class);
                        }

                        intent.putExtra("crop", item);
                        mContext.startActivity(intent);
                    }


                });


            }
        });
    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    private void initRefresh() {
        mRefreshLayout.setLoadMore(isLoadMore);

        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mRefreshLayout.setEnabled(false);
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);

                if (total < 20) {
                    showStr("暂无更多数据");
                    mRefreshLayout.finishRefreshLoadMore();
                } else {
                    isLoadMore = false;
                    getData(Utils.getEdit(etSearch), pageIndex++);

                    mRefreshLayout.finishRefreshLoadMore();
                }
                // 结束上拉刷新...

            }
        });
    }

}
*/
