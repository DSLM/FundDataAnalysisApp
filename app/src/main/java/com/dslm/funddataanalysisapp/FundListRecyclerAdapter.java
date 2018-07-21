package com.dslm.funddataanalysisapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FundListRecyclerAdapter extends RecyclerView.Adapter
{
    private List<SimpleFundData> fundDataList;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    
    public FundListRecyclerAdapter(List<SimpleFundData> fundDataList, Context context)
    {
        this.fundDataList = fundDataList;
        this.context = context;
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        CustomViewHolder customViewHolder = new CustomViewHolder(LayoutInflater.from(
                context).inflate(R.layout.fund_item_view, viewGroup,
                false));
        return customViewHolder;
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i)
    {
        SimpleFundData fundData = fundDataList.get(i);
        CustomViewHolder customViewHolder = (CustomViewHolder) viewHolder;
        customViewHolder.name.setText(fundData.getName());
        customViewHolder.code.setText(fundData.getCode());
        customViewHolder.date.setText(fundData.getDate());
        customViewHolder.netWorthTrend.setText(String.valueOf(String.format("%.2f", fundData.getNetWorthTrend())));
        customViewHolder.equityReturn.setText(String.valueOf(String.format("%.2f%%", fundData.getEquityReturn())));
    
        if( mOnItemClickListener!= null)
        {
            customViewHolder.itemView.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickListener.onClick(i);
                }
            });
            customViewHolder.itemView.setOnLongClickListener( new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    mOnItemClickListener.onLongClick(i);
                    return false;
                }
            });
        }
    }
    
    @Override
    public int getItemCount()
    {
        return fundDataList.size();
    }
    
    private class CustomViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView code;
        TextView date;
        TextView netWorthTrend;
        TextView equityReturn;
        
        public CustomViewHolder(View view)
        {
            super(view);
            name = (TextView) view.findViewById(R.id.text_name);
            code = (TextView) view.findViewById(R.id.text_code);
            date = (TextView) view.findViewById(R.id.text_date);
            netWorthTrend = (TextView) view.findViewById(R.id.text_net_worth_trend);
            equityReturn = (TextView) view.findViewById(R.id.text_equity_return);
        }
    }
    
    public void addData(SimpleFundData fundData)
    {
        //在list中添加数据，并通知条目加入一条
        fundDataList.add(fundDataList.size(), fundData);
        //添加动画
        notifyItemInserted(fundDataList.size() - 1);
    }
    
    public void refreshData()
    {
        OpenHelper openHelper = MainActivity.openHelper;
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
        fundDataList.clear();
        fundDataList.addAll(fundDAO.queryAll());
        sqLiteDatabase.close();
        notifyDataSetChanged();
    }
    
    public interface OnItemClickListener
    {
        void onClick( int position);
        void onLongClick( int position);
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.mOnItemClickListener = onItemClickListener;
    }
    
    public String getCode(int position)
    {
        return fundDataList.get(position).getCode();
    }
    
    
    public String getName(int position)
    {
        return fundDataList.get(position).getName();
    }
}
