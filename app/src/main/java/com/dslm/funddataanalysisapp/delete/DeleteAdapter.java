package com.dslm.funddataanalysisapp.delete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

import com.dslm.funddataanalysisapp.R;

public class DeleteAdapter extends BaseAdapter
{
    private Context context;
    private List<String> fundList;
    private List<Boolean> delList;
    
    public DeleteAdapter(Context context, List<String> fundList)
    {
        this.context = context;
        this.fundList = fundList;
        delList = new ArrayList<>();
        for(String s: fundList)
        {
            delList.add(false);
        }
    }
    
    @Override
    public int getCount()
    {
        return fundList.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        return fundList.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.delete_check_text_view, null);
        }
        
        final CheckedTextView checkedTextView = (CheckedTextView)convertView.findViewById(R.id.checked_text_view);
        checkedTextView.setText(fundList.get(position));
        
        checkedTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkedTextView.setChecked(!checkedTextView.isChecked());
                delList.set(position, checkedTextView.isChecked());
            }
        });
        
        return checkedTextView;
    }
    
    public List<String> getChoices()
    {
        List<String> choices = new ArrayList<>();
        for(int i = 0; i < delList.size(); i++)
        {
            if(delList.get(i))
            {
                choices.add(fundList.get(i).substring(0, 6));
            }
        }
        return choices;
    }
}
