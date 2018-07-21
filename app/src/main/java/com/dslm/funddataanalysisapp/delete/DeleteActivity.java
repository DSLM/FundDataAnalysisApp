package com.dslm.funddataanalysisapp.delete;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import com.dslm.funddataanalysisapp.*;

import java.io.File;
import java.util.List;

public class DeleteActivity extends AppCompatActivity
{
    DeleteAdapter deleteAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        
        setTitle("删除基金");
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        ListView deleteListView = (ListView) findViewById(R.id.delete_list);
        OpenHelper openHelper = MainActivity.openHelper;
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
        deleteAdapter = new DeleteAdapter(this, fundDAO.getCodeAndNameList());
        sqLiteDatabase.close();
        deleteListView.setAdapter(deleteAdapter);
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
                try
                {
                item.setEnabled(false);
                List<String> choices = deleteAdapter.getChoices();
                OpenHelper openHelper = MainActivity.openHelper;
                SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
                FundDAO fundDAO = new FundDAO(sqLiteDatabase);
                for (String choice : choices)
                {
                    fundDAO.delete(choice);
                    try
                    {
                        File historyDataFile = new File(this.getFilesDir() + "/" + choice + ".xls");
                        if (historyDataFile.exists())
                            Log.d("删除结果", "onOptionsItemSelected() called with: delete = [" + historyDataFile.delete() + "]");
                    } catch (Exception e)
                    {
                        Log.e("删除基金", "onOptionsItemSelected: ", e);
                    }
                }
                sqLiteDatabase.close();
                MainViewPagerAdapter.recyclerAdapter.refreshData();}
                catch (Exception r)
                {System.out.println(r);}
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
