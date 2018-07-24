package com.dslm.funddataanalysisapp.exceltable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dslm.funddataanalysisapp.R;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ExcelTableActivity extends AppCompatActivity
{
    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel_table);
        Intent intent_accept = getIntent();
        Bundle bundle = intent_accept.getExtras();
        code = bundle.getString("code");
        setTitle(code + " " + bundle.getString("name"));
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
    
        try
        {
    
            File historyDataFile = new File(this.getFilesDir() + "/" + code + ".xls");
            if (historyDataFile.exists())
            {
                // FIXME: 2018/7/23 读取速度慢
                FileInputStream input = new FileInputStream(historyDataFile);
                wb = new HSSFWorkbook(input);
                input.close();
                if(wb.getSheetAt(0).getRow(0).getCell(1) == null)
                {
                    wb.getSheetAt(0).getRow(0).createCell(1).setCellValue(true);
                }
                if(wb.getSheetAt(0).getRow(0).getCell(1).getBooleanCellValue())
                {
                    HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
                    wb.getSheetAt(0).getRow(0).createCell(1).setCellValue(false);
                    try
                    {
                        FileOutputStream output = new FileOutputStream(historyDataFile);
                        wb.write(output);
                        output.flush();
                        output.close();
                    }
                    catch(Exception e)
                    {
                        Log.e("写入基金excel问题", "ExcelTableActivity: ", e);
                    }
                }
                System.out.println(System.currentTimeMillis());
                sheet = wb.getSheetAt(0);
            }
        }
        catch (IOException e)
        {
            Log.e("读取基金excel问题", "ExcelTableActivity: ", e);
        }
        
        leftPart(sheet);
        rightPart(sheet);
        setScrollTogether();
    }
    
    public void leftPart(HSSFSheet sheet)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("list", MODE_PRIVATE);
        String leftOrder = sharedPreferences.getString("left_order", "A");
    
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.id_left_row_title);
        layout.addView(getSpeRow((LinearLayout) getLayoutInflater().inflate(R.layout.title_row_view, null), leftOrder));
    
        RowRecyclerAdapter leftRowRecyclerAdapter = new RowRecyclerAdapter(sheet, leftOrder, this);
        RecyclerView leftRecyclerView = (RecyclerView) findViewById(R.id.id_excel_left_list);
        leftRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leftRecyclerView.setAdapter(leftRowRecyclerAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.divider_line_vertical));
        leftRecyclerView.addItemDecoration(divider);
    }
    
    public void rightPart(HSSFSheet sheet)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("list", MODE_PRIVATE);
        String rightOrder = sharedPreferences.getString("right_order", "BCDEFGHIJKLMNOPQRSTU");
    
        DataHorizontalScrollView layout = (DataHorizontalScrollView)findViewById(R.id.id_right_row_title);
        layout.addView(getSpeRow((LinearLayout) getLayoutInflater().inflate(R.layout.title_row_view, null), rightOrder));
    
        RowRecyclerAdapter rightRowRecyclerAdapter = new RowRecyclerAdapter(sheet, rightOrder, this);
        RecyclerView rightRecyclerView = (RecyclerView) findViewById(R.id.id_excel_right_list);
        rightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rightRecyclerView.setAdapter(rightRowRecyclerAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.divider_line_vertical));
        rightRecyclerView.addItemDecoration(divider);
    }
    
    public static LinearLayout getSpeRow(LinearLayout layout, String order)
    {
        int [] idList = {
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
        for(int i = 0; i < order.length(); i++)
        {
            View view = layout.findViewById(idList[order.charAt(i) - 'A']);
            view.setVisibility(View.VISIBLE);
            layout.removeView(view);
            layout.addView(view, i);
        }
        return layout;
    }
    
    public void setScrollTogether()
    {
        DataHorizontalScrollView titleScroll = findViewById(R.id.id_right_row_title);
        DataHorizontalScrollView dataScroll = findViewById(R.id.rows_right);
        
        titleScroll.setScrollView(dataScroll);
        dataScroll.setScrollView(titleScroll);
    
        final RecyclerView leftRecyclerView = (RecyclerView) findViewById(R.id.id_excel_left_list);
        final RecyclerView rightRecyclerView = (RecyclerView) findViewById(R.id.id_excel_right_list);
        
        leftRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE)
                    rightRecyclerView.scrollBy(0, dy);
            }
        });
        rightRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE)
                    leftRecyclerView.scrollBy(0, dy);
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_to_top:
                RecyclerView leftRecyclerView = (RecyclerView) findViewById(R.id.id_excel_left_list);
                RecyclerView rightRecyclerView = (RecyclerView) findViewById(R.id.id_excel_right_list);
    
                leftRecyclerView.scrollToPosition(0);
                rightRecyclerView.scrollToPosition(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_excel_table, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
}
