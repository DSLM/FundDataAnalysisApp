package com.dslm.funddataanalysisapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.dslm.funddataanalysisapp.delete.DeleteActivity;
import com.dslm.funddataanalysisapp.exceltable.ExcelTableActivity;
import com.dslm.funddataanalysisapp.exceltable.column.ColumnOrderActivity;

import java.util.List;

import static com.dslm.funddataanalysisapp.MainActivity.openHelper;
import static com.dslm.funddataanalysisapp.MainActivity.context;

public class MainViewPagerAdapter extends PagerAdapter implements OnItemClickListener
{
    private List<View> viewList;
    public static RecyclerView recyclerView;
    public static FundListRecyclerAdapter recyclerAdapter;
    public static ItemTouchHelper itemTouchHelper;
    
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
    
            this.itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback()
            {
                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
                {
                    return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN,0);
                }
    
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1)
                {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = viewHolder1.getAdapterPosition();
                    SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
                    FundDAO fundDAO = new FundDAO(sqLiteDatabase);
                    
                    fundDAO.exchange(fromPosition, toPosition);
                    
                    sqLiteDatabase.close();
                    
                    recyclerAdapter.exchange(fromPosition, toPosition);
                    recyclerAdapter.notifyItemMoved(fromPosition, toPosition);
                    return false;
                }
    
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int i)
                {
        
                }
    
                @Override
                public boolean isLongPressDragEnabled()
                {
                    return MainActivity.isDragging;
                }
            });
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        if(viewList.get(position).findViewById(R.id.id_setting_listview) != null)
        {
            ListView settingListView = (ListView)viewList.get(position).findViewById(R.id.id_setting_listview);
            String [] list = {"删除基金", "修改统计表的列排序", "强制刷新"};
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
        //监听"设置"列表
        switch (position)
        {
            case 0:
                Intent toDelete = new Intent();
                toDelete.setClass(MainActivity.context, DeleteActivity.class);
                MainActivity.context.startActivity(toDelete);
                break;
            case 1:
                Intent toOrder = new Intent();
                toOrder.setClass(MainActivity.context, ColumnOrderActivity.class);
                MainActivity.context.startActivity(toOrder);
                break;
        }
    }
}
