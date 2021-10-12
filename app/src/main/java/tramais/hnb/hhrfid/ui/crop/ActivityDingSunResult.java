package tramais.hnb.hhrfid.ui.crop;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.table.MapTableData;

import tramais.hnb.hhrfid.R;
import tramais.hnb.hhrfid.base.BaseActivity;
import tramais.hnb.hhrfid.constant.Constants;
import tramais.hnb.hhrfid.util.JsonHelper;

public class ActivityDingSunResult extends BaseActivity {
    private SmartTable mSmartTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ActivityDingSunResultBinding inflate = ActivityDingSunResultBinding.inflate(LayoutInflater.from(this));
        setContentView(R.layout.activity_ding_sun_result);

    }

    @Override
    protected void initView() {
        setTitleText("地块详情");
        mSmartTable = findViewById(R.id.smart_table);
    }

    @Override
    protected void initData() {
        String json = getIntent().getStringExtra(Constants.json);
        mSmartTable.getConfig().setShowXSequence(false);
        mSmartTable.getConfig().setShowYSequence(false);
        mSmartTable.getConfig().setShowTableTitle(false);
        MapTableData tableData = MapTableData.create("抽样", JsonHelper.jsonToMapList(json));
        mSmartTable.setTableData(tableData);
    }

    @Override
    protected void initListner() {

    }


}
