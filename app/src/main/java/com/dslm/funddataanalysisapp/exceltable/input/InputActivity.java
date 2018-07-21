package com.dslm.funddataanalysisapp.exceltable.input;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.dslm.funddataanalysisapp.FundDAO;
import com.dslm.funddataanalysisapp.MainActivity;
import com.dslm.funddataanalysisapp.OpenHelper;
import com.dslm.funddataanalysisapp.R;
import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class InputActivity extends AppCompatActivity implements DateSelectedListener
{
    Spinner fundPicker;
    EditText datePicker;
    EditText editTextF;
    EditText editTextG;
    EditText editTextJ;
    EditText editTextK;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Intent intent_accept = getIntent();
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        fundPick();
        DatePick();
        editTextF = findViewById(R.id.input_column_F);
        editTextG = findViewById(R.id.input_column_G);
        editTextJ = findViewById(R.id.input_column_J);
        editTextK = findViewById(R.id.input_column_K);
    }
    
    public void fundPick()
    {
        fundPicker = (Spinner) findViewById(R.id.input_fund_picker);
        OpenHelper openHelper = MainActivity.openHelper;
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
        List<String> funds = fundDAO.getCodeAndNameList();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, funds);
        sqLiteDatabase.close();
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fundPicker.setAdapter(adapter);
    }
    
    public void DatePick()
    {
        datePicker = (EditText) findViewById(R.id.input_date_picker);
        datePicker.setInputType(InputType.TYPE_NULL);
        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.setDateSelectedListener(InputActivity.this);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                dateDialogFragment.show(getFragmentManager(), "日期选择");
                transaction.commit();
            }
        });
    }
    
    @Override
    public void dateSelected(String date)
    {
        datePicker.setText(date);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_check_button, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_check_button:
                item.setEnabled(false);
                
                // TODO: 2018/7/15 加入数据
                String code = String.valueOf(fundPicker.getSelectedItem()).substring(0, 6);
                String date = datePicker.getText().toString();
                HSSFWorkbook wb = new HSSFWorkbook();
                HSSFSheet sheet = wb.createSheet();
                HSSFRow row;
                File historyDataFile = new File(getFilesDir() + "/" + code + ".xls");
                try
                {
                    if (!historyDataFile.exists())
                    {
                        // TODO: 2018/7/16 警告基金不存在
                        finish();
                    } else
                    {
                        FileInputStream input = new FileInputStream(historyDataFile);
                        wb = new HSSFWorkbook(input);
                        input.close();
                        sheet = wb.getSheetAt(0);
                    }
                } catch (IOException e)
                {
                    Log.e("写入基金excel问题", "saveData: ", e);
                }
                for (int i = 2; i < (int) sheet.getRow(0).getCell(0).getNumericCellValue() + 2; i++)
                {
                    if (sheet.getRow(i).getCell(0).getStringCellValue().equals(date))
                    {
                        row = sheet.getRow(i);
                        DecimalFormat df = new DecimalFormat("#.00");
                        
                        System.out.println(i);
                        
                        if (editTextF.getText().toString().equals(""))
                        {
                            row.createCell(5).setCellValue("");
                        } else
                        {
                            row.createCell(5).setCellValue(
                                    Double.valueOf(
                                            df.format(
                                                    Double.valueOf(
                                                            editTextF.getText().toString()))));
                        }
                        if (editTextG.getText().toString().equals(""))
                        {
                            row.createCell(6).setCellValue("");
                        } else
                        {
                            row.createCell(6).setCellValue(
                                    Double.valueOf(
                                            df.format(
                                                    Double.valueOf(
                                                            editTextG.getText().toString()))));
                        }
                        if (editTextJ.getText().toString().equals(""))
                        {
                            row.createCell(9).setCellValue("");
                        } else
                        {
                            row.createCell(9).setCellValue(
                                    Double.valueOf(
                                            df.format(
                                                    Double.valueOf(
                                                            editTextJ.getText().toString()))));
                        }
                        if (editTextK.getText().toString().equals(""))
                        {
                            row.createCell(10).setCellValue("");
                        } else
                        {
                            row.createCell(10).setCellValue(
                                    Double.valueOf(
                                            df.format(
                                                    Double.valueOf(
                                                            editTextK.getText().toString()))));
                        }
                        break;
                    }
                }
                
                try
                {
                    FileOutputStream output = new FileOutputStream(historyDataFile);
                    wb.write(output);
                    output.flush();
                    output.close();
                } catch (Exception e)
                {
                    Log.e("写入基金excel问题", "saveData: ", e);
                }
                
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
