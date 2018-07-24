package com.dslm.funddataanalysisapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.dslm.funddataanalysisapp.delete.DeleteActivity;
import com.dslm.funddataanalysisapp.exceltable.ExcelTableActivity;

import java.util.List;

import static com.dslm.funddataanalysisapp.MainActivity.openHelper;
import static com.dslm.funddataanalysisapp.MainActivity.context;

public class MainViewPagerAdapter extends PagerAdapter implements OnItemClickListener
{
    private List<View> viewList;
    public static RecyclerView recyclerView;
    public static FundListRecyclerAdapter recyclerAdapter;
    
    public MainViewPagerAdapter(List<View> viewList)
    {
        this.viewList = viewList;
    }
    
    @Override
    public int getCount()
    {
        return viewList.size();
    }
    
    @Override
    public boolean isViewFromObject(View view, Object o)
    {
        return view == o;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        container.addView(viewList.get(position));
    
        if(viewList.get(position).findViewById(R.id.id_page_fund_list) != null)
        {
            SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
            FundDAO fundDAO = new FundDAO(sqLiteDatabase);
    
            recyclerAdapter = new FundListRecyclerAdapter(fundDAO.queryAll(), context);
            recyclerView = (RecyclerView) viewList.get(position).findViewById(R.id.id_page_fund_list).findViewById(R.id.id_recyler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(recyclerAdapter);
            
            sqLiteDatabase.close();
            
            recyclerAdapter.setOnItemClickListener(new FundListRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position)
                {
                    Intent toExcelTable = new Intent();
                    toExcelTable.setClass(MainActivity.context, ExcelTableActivity.class);
                    toExcelTable.putExtra("code", recyclerAdapter.getCode(position));
                    toExcelTable.putExtra("name", recyclerAdapter.getName(position));
                    MainActivity.context.startActivity(toExcelTable);
                }
                @Override
                public void onLongClick(int position)
                {
                }
            });
        }
        if(viewList.get(position).findViewById(R.id.id_setting_listview) != null)
        {
            ListView settingListView = (ListView)viewList.get(position).findViewById(R.id.id_setting_listview);
            String [] list = {"删除"};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    MainActivity.context,
                    android.R.layout.simple_expandable_list_item_1,
                    list);
            
            settingListView.setAdapter(arrayAdapter);
            
            settingListView.setOnItemClickListener(this);
        }
        
        return viewList.get(position);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //监听设置列表
        switch (position)
        {
            case 0:
                Intent toDelete = new Intent();
                toDelete.setClass(MainActivity.context, DeleteActivity.class);
                MainActivity.context.startActivity(toDelete);
                break;
        }
    }
    
    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        
    }
}
