package com.dslm.funddataanalysisapp.exceltable.input;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import com.dslm.funddataanalysisapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDialogFragment extends DialogFragment implements View.OnClickListener
{
    private DateSelectedListener dateSelectedListener;
    private DatePicker datePicker;
    
    public DateDialogFragment()
    {
    
    }
    
    @Override
    public void onClick(View v)
    {
        dateSelectedListener.dateSelected(String.format("%02d-%02d-%02d",
                datePicker.getYear() % 100,
                datePicker.getMonth() + 1,
                datePicker.getDayOfMonth()));
        onDismiss(getDialog());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_time_picker, container, false);
        
        datePicker = view.findViewById(R.id.id_time_picker);
        datePicker.setMaxDate(System.currentTimeMillis());
    
        Button cancel = (Button)view.findViewById(R.id.date_cancel);
        Button check = (Button)view.findViewById(R.id.date_check);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDismiss(getDialog());
            }
        });
        check.setOnClickListener(this);
    
        setCancelable(false);
        
        return view;
    }
    
    public void setDateSelectedListener(DateSelectedListener dateSelectedListener)
    {
        this.dateSelectedListener = dateSelectedListener;
    }
}
