package com.kingsley.floatbardemo;

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kingsley.floatbardemo.adapter.CityAdapter;
import com.kingsley.floatbardemo.bean.CityBean;
import com.kingsley.floatbardemo.db.DBManager;
import com.kingsley.floatbardemo.util.PermissionUtil;
import com.kingsley.floatbardemo.view.FloatBarItemDecoration;
import com.kingsley.floatbardemo.view.IndexBar;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import kingsley.rvlibrary.base.RVBaseAdapter;

import static com.kingsley.floatbardemo.R.id.indexBar;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE = 10000;

    private LinearLayoutManager mLayoutManager;
    private LinkedHashMap<Integer, String> mHeaderList;
    private ArrayList<CityBean> mCityList;
    private CityAdapter mAdapter;
    private PopupWindow mOperationInfoDialog;
    private View mLetterHintView;

    private IndexBar mIndexBar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchData();
        initView();
    }

    private void initView() {
        mIndexBar = (IndexBar) findViewById(indexBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.addItemDecoration(
                new FloatBarItemDecoration(this, mHeaderList));
        mAdapter = new CityAdapter(R.layout.item_layout, mCityList);
        mRecyclerView.setAdapter(mAdapter);
        mIndexBar.setNavigators(new ArrayList<>(mHeaderList.values()));
        mIndexBar.setOnTouchingLetterChangedListener(new IndexBar.OnTouchingLetterChangeListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                showLetterHintDialog(s);
                for (Integer position : mHeaderList.keySet()) {
                    if (mHeaderList.get(position).equals(s)) {
                        mLayoutManager.scrollToPositionWithOffset(position, 0);
                        return;
                    }
                }
            }

            @Override
            public void onTouchingStart(String s) {
                showLetterHintDialog(s);
            }

            @Override
            public void onTouchingEnd(String s) {
                hideLetterHintDialog();
            }
        });
        mAdapter.setOnItemClickListener(new RVBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Log.i("TAG", "onItemClick: " + mHeaderList.size());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private void showLetterHintDialog(String s) {
        if (mOperationInfoDialog == null) {
            mLetterHintView = getLayoutInflater().inflate(R.layout.dialog_letter_hint, null);
            mOperationInfoDialog = new PopupWindow(mLetterHintView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, false);
            mOperationInfoDialog.setOutsideTouchable(true);
        }
        ((TextView) mLetterHintView.findViewById(R.id.dialog_letter_hint_textview)).setText(s);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mOperationInfoDialog.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
            }
        });
    }

    private void hideLetterHintDialog() {
        mOperationInfoDialog.dismiss();
    }

    /**
     * fetch the data to display
     * need permission of Manifest.permission.READ_CONTACTS
     */
    private void fetchData() {
        if (mHeaderList == null) {
            mHeaderList = new LinkedHashMap<>();
        }
        fetchContactList();
    }

    protected void fetchContactList() {
        if (PermissionUtil.checkHasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE)) {
            mCityList = DBManager.getInstance().getAllCities();
            Log.i("TAG", "fetchCityList: " + mCityList);
        } else {
            mCityList = new ArrayList<>(0);
        }
        preOperation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchData();
                mAdapter.notifyDataSetChanged();
            } else {
                Snackbar.make(mRecyclerView, "获取权限失败", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void preOperation() {
        mHeaderList.clear();
        if (mCityList.size() == 0) {
            return;
        }
        addHeaderToList(0, mCityList.get(0).getInitial());
        for (int i = 1; i < mCityList.size(); i++) {
            if (!mCityList.get(i - 1).getInitial().equalsIgnoreCase(mCityList.get(i).getInitial())) {
                addHeaderToList(i, mCityList.get(i).getInitial());
            }
        }
    }

    private void addHeaderToList(int index, String header) {
        mHeaderList.put(index, header);
    }

}

