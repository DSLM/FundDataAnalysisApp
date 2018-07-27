package com.dslm.funddataanalysisapp.exceltable.column;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ColumnChoosePagerAdapter extends PagerAdapter
{
    private List<RecyclerView> RecyclerViewList;
    private List<ColumnChooseRecyclerAdapter> adapterList;
    private Context context;
    
    public ColumnChoosePagerAdapter(RecyclerView a, RecyclerView b, Context context)
    {
        this.context = context;
        RecyclerViewList = new ArrayList<>();
        adapterList = new ArrayList<>();
        RecyclerViewList.add(a);
        RecyclerViewList.add(b);
    }
    
    @Override
    public boolean isViewFromObject(View view, Object o)
    {
        return view == o;
    }
    
    @Override
    public int getCount()
    {
        return RecyclerViewList.size();
    }
    
    
    @Override
    public Object instantiateItem(ViewGroup container, final int position)
    {
        container.addView(RecyclerViewList.get(position));
        RecyclerViewList.get(position).setLayoutManager(new LinearLayoutManager(context));
        switch (position)
        {
            case 0:
                ColumnChooseRecyclerAdapter adapter1 = new ColumnChooseRecyclerAdapter("left", context);
                RecyclerViewList.get(position).setAdapter(adapter1);
                adapterList.add(adapter1);
                break;
            case 1:
                ColumnChooseRecyclerAdapter adapter2 =new ColumnChooseRecyclerAdapter("right", context);
                RecyclerViewList.get(position).setAdapter(adapter2);
                adapterList.add(adapter2);
                break;
        }
        
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback()
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
                adapterList.get(position).exchange(fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return false;
            }
        
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i)
            {
            
            }
        });
        itemTouchHelper.attachToRecyclerView(RecyclerViewList.get(position));
        return RecyclerViewList.get(position);
    }
    
    public void saveInfo()
    {
        for(ColumnChooseRecyclerAdapter adapter: adapterList)
        {
            adapter.saveInfo();
        }
    }
}
