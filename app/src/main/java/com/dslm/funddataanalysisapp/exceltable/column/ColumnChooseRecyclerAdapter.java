package com.dslm.funddataanalysisapp.exceltable.column;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.dslm.funddataanalysisapp.R;

import java.util.*;

import static android.content.Context.MODE_PRIVATE;

public class ColumnChooseRecyclerAdapter extends RecyclerView.Adapter
{
    private List<String> colList = new ArrayList<>();//独有
    private List<Boolean> checkList = new ArrayList<>();//独有
    private String side;
    private Context context;
    private String order;
    private String chooseOrder;
    private Map<Character, String> charToName = new HashMap<>();
    private Map<String, Character> nameToChar = new HashMap<>();
    private String [] colStringList = {
            "日期",
            "净值(元)",
            "累计净值",
            "日涨幅",
            "当日收益",
            "买入(元)",
            "买入手续费(元)",
            "买入份额",
            "买入成本单价",
            "卖出份额",
            "卖出手续费(元)",
            "卖出(元)",
            "卖出成本单价",
            "本金(元)",
            "持仓份额",
            "持仓市值(元)",
            "持仓收益(元)",
            "持仓收益率",
            "摊薄单价",
            "总投入(元)",
            "总赎回(元)"};
    
    public ColumnChooseRecyclerAdapter(String side, Context context)
    {
        this.side = side;
        this.context = context;
        for(int i = 0; i < colStringList.length; i++)
        {
            charToName.put((char)('A' + i), colStringList[i]);
            nameToChar.put(colStringList[i], (char)('A' + i));
        }
    
        SharedPreferences sharedPreferences = context.getSharedPreferences("list", MODE_PRIVATE);
        switch (side)
        {
            case "left":
                order = sharedPreferences.getString("left_order", "A");
                chooseOrder = sharedPreferences.getString("left_choose_order", "ABCDEFGHIJKLMNOPQRSTU");
                break;
            case "right":
                order = sharedPreferences.getString("right_order", "BCDEFGHIJKLMNOPQRSTU");
                chooseOrder = sharedPreferences.getString("right_choose_order", "ABCDEFGHIJKLMNOPQRSTU");
                break;
            default:
                order = chooseOrder = "";
        }
    
        for(int i = 0;  i < chooseOrder.length(); i++)
        {
            colList.add(charToName.get(chooseOrder.charAt(i)));
            checkList.add(order.indexOf(chooseOrder.charAt(i)) != -1);
        }
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        CustomViewHolder customViewHolder = new CustomViewHolder(LayoutInflater.from(
                context).inflate(R.layout.choice_of_column, viewGroup,
                false));
        return customViewHolder;
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i)
    {
        final CheckedTextView checkedTextView = ((CustomViewHolder) viewHolder).checkedTextView;
        checkedTextView.setText(colList.get(i));
        checkedTextView.setChecked(checkList.get(i));
        checkedTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkedTextView.setChecked(!checkedTextView.isChecked());
                checkList.set(colList.indexOf(checkedTextView.getText()), checkedTextView.isChecked());
            }
        });
    }
    
    @Override
    public int getItemCount()
    {
        return colList.size();
    }
    
    private class CustomViewHolder extends RecyclerView.ViewHolder
    {
        CheckedTextView checkedTextView;
        
        public CustomViewHolder(View view)
        {
            super(view);
            checkedTextView = (CheckedTextView)view;
        }
    }
    
    public void saveInfo()
    {
        order = "";
        chooseOrder = "";
        for(int i = 0;  i < colList.size(); i++)
        {
            chooseOrder += nameToChar.get(colList.get(i));
            order += checkList.get(i) ? nameToChar.get(colList.get(i)) : "";
        }
        SharedPreferences.Editor editor = context.getSharedPreferences("list", MODE_PRIVATE).edit();
    
        editor.putString(side + "_order", order);
        editor.putString(side + "_choose_order", chooseOrder);
        editor.commit();
        
    }
    
    public void exchange(int a, int b)
    {
        Collections.swap(colList, a, b);
        Collections.swap(checkList, a, b);
    }
    
}
