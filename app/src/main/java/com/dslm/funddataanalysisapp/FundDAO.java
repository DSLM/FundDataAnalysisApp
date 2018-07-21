package com.dslm.funddataanalysisapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//数据库操作类
public class FundDAO
{
    private SQLiteDatabase database;
    
    public FundDAO(SQLiteDatabase sqLiteDatabase)
    {
        this.database = sqLiteDatabase;
    }
    
    public boolean insert(SimpleFundData fundData)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fund_code", fundData.getCode());
        contentValues.put("name", fundData.getName());
        contentValues.put("newest_date", fundData.getDate());
        contentValues.put("net_worth_trend", fundData.getNetWorthTrend());
        contentValues.put("equity_return", fundData.getEquityReturn());
        long insertResult = database.insert("fund_list", null, contentValues);
        return insertResult != -1;
    }
    
    public boolean delete(String code)
    {
        int deleteResult = database.delete("fund_list", "fund_code=?", new String[] {code});
        return deleteResult != 0;
    }
    
    public boolean update(SimpleFundData fundData)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("newest_date", fundData.getDate());
        contentValues.put("net_worth_trend", fundData.getNetWorthTrend());
        contentValues.put("equity_return", fundData.getEquityReturn());
        int updateResult = database.update("fund_list", contentValues,
                "fund_code=?", new String[]{fundData.getCode() + ""});
        return updateResult != 0;
    }
    
    
    public SimpleFundData queryOne(SimpleFundData fundData)
    {
        Cursor cursor = database.query("fund_list", null,
                "fund_code=?", new String[]{fundData.getCode() + ""}, null,
                null, null, null);
        while (cursor.moveToNext())
        {
            fundData.setName(cursor.getString(2));
        }
        return fundData;
    }
    
    public List<SimpleFundData> queryAll()
    {
        List<SimpleFundData> fundDataList = new ArrayList<>();
            Cursor cursor = database.query("fund_list", null,
                    null, null, null,
                    null, null, null);
        while (cursor.moveToNext())
        {
            SimpleFundData fundData = new SimpleFundData();
            fundData.setCode(cursor.getString(1));
            fundData.setName(cursor.getString(2));
            fundData.setDate(cursor.getString(3));
            fundData.setNetWorthTrend(cursor.getDouble(4));
            fundData.setEquityReturn(cursor.getDouble(5));
            fundDataList.add(fundData);
        }
        return fundDataList;
    }
    
    public List<String> getCodeList()
    {
        List<String> codeList = new ArrayList<>();
        Cursor cursor = database.query("fund_list", null,
                null, null, null,
                null, null, null);
        while (cursor.moveToNext())
        {
            codeList.add(cursor.getString(1));
        }
        return codeList;
    }
    
    public List<String> getCodeAndNameList()
    {
        List<String> codeAndNameList = new ArrayList<>();
        Cursor cursor = database.query("fund_list", null,
                null, null, null,
                null, null, null);
        while (cursor.moveToNext())
        {
            codeAndNameList.add(cursor.getString(1) + " "
            +cursor.getString(2));
        }
        return codeAndNameList;
    }
}
