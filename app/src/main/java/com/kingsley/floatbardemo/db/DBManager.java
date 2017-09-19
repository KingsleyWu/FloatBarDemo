package com.kingsley.floatbardemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.kingsley.floatbardemo.App;
import com.kingsley.floatbardemo.bean.CityBean;
import com.kingsley.floatbardemo.bean.CityEntry;
import com.kingsley.floatbardemo.task.TaskScheduler;
import com.kingsley.floatbardemo.util.FileUtil;
import com.kingsley.floatbardemo.util.PYUtils;
import com.kingsley.floatbardemo.util.SPUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DBManager {
    private static DBManager sDBManager;
    private static DBHelper sDBHelper;
    private static final String CITY_INITED = "CITY_INITED";

    private DBManager() {
    }

    public static DBManager getInstance() {
        if (sDBManager == null) {
            synchronized (DBHelper.class) {
                if (sDBManager == null) {
                    sDBManager = new DBManager();
                    sDBHelper = DBHelper.getInstance(App.getInstance());
                }
            }
        }
        return sDBManager;
    }

    public void copyCitysToDB() {
        boolean cityInited = SPUtil.get(CITY_INITED, false);
        if (cityInited) {
            return;
        }

        TaskScheduler.runOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                String citys = FileUtil.assetFile2String("cityList.txt", App.getInstance());
                CityEntry cityEntry = new Gson().fromJson(citys, CityEntry.class);
                Collections.sort(cityEntry.getCity_info(), new CityComparator());
                // Gets the data repository in write mode
                SQLiteDatabase db = sDBHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    ContentValues values;
                    for (CityEntry.CityInfoEntity cityInfoEntity : cityEntry.getCity_info()) {
                        // Create a new map of values, where column names are the keys

                        StringBuilder stringBuilder = new StringBuilder();
                        StringBuilder initials = new StringBuilder();
                            String pinyin = PYUtils.getAbbreviation(cityInfoEntity.getCity());
                            stringBuilder.append(pinyin);
                            initials.append(pinyin.charAt(0));
                        stringBuilder.append(initials);

                        values = new ContentValues();
                        values.put(CityDao.CITY_NAME, cityInfoEntity.getCity());
                        values.put(CityDao.PINYIN, stringBuilder.toString());
                        values.put(CityDao.CITY_ID, cityInfoEntity.getId().substring(2, cityInfoEntity.getId().length()));
                        db.insert(CityDao.TABLE_NAME, null, values);
                    }
                    db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交。
                    SPUtil.put(CITY_INITED, true);
                } catch (Exception e) {
                    e.getMessage();
                } finally {
                    db.endTransaction(); //处理完成

                }
            }
        });
    }

    /**
     * 读取所有城市
     *
     * @return
     */
    public ArrayList<CityBean> getAllCities() {
        String allCitySql = "select * from " + CityDao.TABLE_NAME;
        return getCitys(allCitySql, true);
    }

    /**
     * 通过名字或者拼音搜索
     *
     * @param keyword
     * @return
     */
    public ArrayList<CityBean> searchCity(final String keyword) {

        String searchSql = "select * from " + CityDao.TABLE_NAME + " where " + CityDao.CITY_NAME + " like \"%" + keyword + "%\" or " + CityDao.PINYIN + " like \"%" + keyword + "%\" or " + CityDao.CITY_ID + " like \"%" + keyword + "%\"";

        return getCitys(searchSql, false);
    }

    private ArrayList<CityBean> getCitys(String sql, boolean all) {
        SQLiteDatabase db = sDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<CityBean> result = new ArrayList<>();
        CityBean city;
        String lastInitial = "";
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(CityDao.CITY_NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(CityDao.PINYIN));
            String cityId = cursor.getString(cursor.getColumnIndex(CityDao.CITY_ID));
            city = new CityBean(name, pinyin, cityId);
            String currentInitial = pinyin.substring(0, 1);
            if (!lastInitial.equals(currentInitial) && all) {

                lastInitial = currentInitial;
            }
            city.setInitial(currentInitial);
            result.add(city);
        }
        cursor.close();
        db.close();
        return result;
    }


    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<CityEntry.CityInfoEntity> {
        @Override
        public int compare(CityEntry.CityInfoEntity lhs, CityEntry.CityInfoEntity rhs) {
            char a = PYUtils.getAbbreviation(lhs.getCity()).charAt(0);
            char b = PYUtils.getAbbreviation(rhs.getCity()).charAt(0);
            return a - b;
        }
    }

}
