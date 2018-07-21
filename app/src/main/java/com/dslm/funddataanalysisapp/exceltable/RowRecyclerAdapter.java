package com.dslm.funddataanalysisapp.exceltable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dslm.funddataanalysisapp.R;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;


/**
 * Created by Administrator on 2018/7/14.
 */

public class RowRecyclerAdapter extends RecyclerView.Adapter
{
    private HSSFSheet  sheet;
    private String order;
    private Context context;
    private final int [] idList = {
        R.id.data_A,
        R.id.data_B,
        R.id.data_C,
        R.id.data_D,
        R.id.data_E,
        R.id.data_F,
        R.id.data_G,
        R.id.data_H,
        R.id.data_I,
        R.id.data_J,
        R.id.data_K,
        R.id.data_L,
        R.id.data_M,
        R.id.data_N,
        R.id.data_O,
        R.id.data_P,
        R.id.data_Q,
        R.id.data_R,
        R.id.data_S,
        R.id.data_T,
        R.id.data_U};
    
    
    public RowRecyclerAdapter(HSSFSheet sheet, String order, Context context)
    {
        this.order = order;
        this.context = context;
        this.sheet = sheet;
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(
                context).inflate(R.layout.data_row_view, viewGroup,
                false);
        for(int j = 0; j < order.length(); j++)
        {
            View view = linearLayout.findViewById(idList[order.charAt(j) - 'A']);
            view.setVisibility(View.VISIBLE);
            linearLayout.removeView(view);
            linearLayout.addView(view, j);
        }
        CustomViewHolder customViewHolder = new CustomViewHolder(linearLayout);
        return customViewHolder;
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        HSSFRow row = sheet.getRow(getItemCount() - i);
        CustomViewHolder customViewHolder = (CustomViewHolder) viewHolder;
        for(int j = 0; j < order.length(); j++)
        {
            String data = "";
            try
            {
                if (row.getCell(order.charAt(j) - 'A') == null)
                {
                    data = "";
                } else
                {
                    switch (order.charAt(j) - 'A')
                    {
                        case 0:
                            data = String.valueOf(row.getCell(order.charAt(j) - 'A').getStringCellValue());
                            break;
                        case 1:
                        case 2:
                        case 18:
                            data = String.format("%.4f", row.getCell(order.charAt(j) - 'A').getNumericCellValue());
                            break;
                        case 8:
                        case 12:
                            if(row.getCell(order.charAt(j) - 'A').getNumericCellValue() == 0)
                            {
                                data = "";
                            }
                            else
                            {
                                data = String.format("%.4f", row.getCell(order.charAt(j) - 'A').getNumericCellValue());
                            }
                            break;
                        case 4:
                        case 5:
                        case 6:
                        case 9:
                        case 10:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 19:
                        case 20:
                            data = String.format("%.2f", row.getCell(order.charAt(j) - 'A').getNumericCellValue());
                            break;
                        case 7:
                        case 11:
                            if(row.getCell(order.charAt(j) - 'A').getNumericCellValue() == 0)
                            {
                                data = "";
                            }
                            else
                            {
                                data = String.format("%.2f", row.getCell(order.charAt(j) - 'A').getNumericCellValue());
                            }
                            break;
                        case 3:
                            data = String.format("%.4f%%", row.getCell(order.charAt(j) - 'A').getNumericCellValue());
                            break;
                        case 17:
                            data = String.format("%.2f%%", row.getCell(order.charAt(j) - 'A').getNumericCellValue()*100);
                            break;
                        default:
                            data = "";
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("这里", "onBindViewHolder: ", e);
            }
    
            customViewHolder.cells[j].setText(data);
        }
    }
    
    @Override
    public int getItemCount()
    {
        return (int)sheet.getRow(0).getCell(0).getNumericCellValue();
    }
    
    private  class CustomViewHolder extends RecyclerView.ViewHolder
    {
        TextView [] cells;
        
        public CustomViewHolder(View view)
        {
            super(view);
            cells = new TextView[order.length()];
            for(int i = 0; i < order.length(); i++)
            {
                cells[i] = (TextView) view.findViewById(idList[order.charAt(i) - 'A']);
            }
        }
    }
}
