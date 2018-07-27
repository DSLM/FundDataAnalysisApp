package com.dslm.funddataanalysisapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dslm.funddataanalysisapp.exceltable.ExcelTableActivity;

import java.util.Collections;
import java.util.List;

public class FundListRecyclerAdapter extends RecyclerView.Adapter
{
    private List<SimpleFundData> fundDataList;
    private Context context;
    
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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        SimpleFundData fundData = fundDataList.get(i);
        final CustomViewHolder customViewHolder = (CustomViewHolder) viewHolder;
        customViewHolder.name.setText(fundData.getName());
        customViewHolder.code.setText(fundData.getCode());
        customViewHolder.date.setText(fundData.getDate());
        customViewHolder.netWorthTrend.setText(String.valueOf(String.format("%.2f", fundData.getNetWorthTrend())));
        customViewHolder.equityReturn.setText(String.valueOf(String.format("%.2f%%", fundData.getEquityReturn())));
    
        if(fundData.getEquityReturn() < 0)
        {
            customViewHolder.netWorthTrend.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            customViewHolder.equityReturn.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
        }
        else if(fundData.getEquityReturn() > 0)
        {
            customViewHolder.netWorthTrend.setTextColor(Color.RED);
            customViewHolder.equityReturn.setTextColor(Color.RED);
        }
        else
        {
            customViewHolder.netWorthTrend.setTextColor(Color.BLACK);
            customViewHolder.equityReturn.setTextColor(Color.BLACK);
        }
    
        customViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!MainActivity.isDragging)
                {
                    Intent toExcelTable = new Intent();
                    toExcelTable.setClass(MainActivity.context, ExcelTableActivity.class);
                    toExcelTable.putExtra("code", customViewHolder.code.getText());
                    toExcelTable.putExtra("name", customViewHolder.name.getText());
                    MainActivity.context.startActivity(toExcelTable);
                }
            }
        });
        customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return false;
            }
        });
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
    
    public String getCode(int position)
    {
        return fundDataList.get(position).getCode();
    }
    
    
    public String getName(int position)
    {
        return fundDataList.get(position).getName();
    }
    
    public void exchange(int a, int b)
    {
        Collections.swap(fundDataList, a, b);
    }
}
