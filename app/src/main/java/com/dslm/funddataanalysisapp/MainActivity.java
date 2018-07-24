package com.dslm.funddataanalysisapp;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.Toast;
import com.dslm.funddataanalysisapp.exceltable.input.InputActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static OpenHelper openHelper;
    public static Context context;
    public static Boolean isDragging;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        context = this;
        isDragging = false;
        viewPagerAndTabLayout();
        data();
    }
    
    public void viewPagerAndTabLayout()
    {
        //初始化viewpager
        final ViewPager viewPager = (ViewPager)findViewById(R.id.id_view_pager);
        View firstView = getLayoutInflater().inflate(R.layout.page_fund_list, null);
        View secondView = getLayoutInflater().inflate(R.layout.page_setting, null);
        
        List<View> viewList = new ArrayList<>();
        viewList.add(firstView);
        viewList.add(secondView);
    
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(viewList);
        viewPager.setAdapter(mainViewPagerAdapter);
    
        final TabLayout tabLayout = (TabLayout)findViewById(R.id.id_tab_layout);
    
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {
            
            }
        
            @Override
            public void onPageSelected(int i)
            {
                tabLayout.getTabAt(i).select();
            }
        
            @Override
            public void onPageScrollStateChanged(int i)
            {
            
            }
        });
    
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
            }
        
            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            
            }
        
            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            
            }
        });
    }
    
    public void data()
    {
        OpenHelper openHelper = new OpenHelper(this);
        MainActivity.openHelper = openHelper;
        SharedPreferences sharedPreferences = getSharedPreferences("list", MODE_PRIVATE);
        String leftOrder = sharedPreferences.getString("left_order", "A");
        String rightOrder = sharedPreferences.getString("right_order", "BCDEFGHIJKLMNOPQRSTU");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("left_order", leftOrder);
        editor.putString("right_order", rightOrder);
        System.out.println(leftOrder);
        System.out.println(rightOrder);
        editor.commit();
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.id_add_symbol:
                AddFundDialogFragment addFundDialogFragment = new AddFundDialogFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                addFundDialogFragment.show(getFragmentManager(),"登录");
                transaction.commit();
                return true;
            case R.id.id_refresh_symbol:
                item.setEnabled(false);
                SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
                FundDAO fundDAO = new FundDAO(sqLiteDatabase);
                List<String> stringList = fundDAO.getCodeList();
                sqLiteDatabase.close();
                Handler addFundHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg)
                    {
                        switch (msg.what)
                        {
                            case HandlerWhatValue.refreshedData:
                                item.setEnabled(true);
                                Toast.makeText(MainActivity.context, "刷新完成", Toast.LENGTH_SHORT).show();
                                MainViewPagerAdapter.recyclerAdapter.refreshData();
                                break;
                            case HandlerWhatValue.netWorkProblem:
                                Toast.makeText(MainActivity.context, String.valueOf(((SimpleFundData)msg.obj).getCode()) + "号基金刷新网络异常", Toast.LENGTH_SHORT).show();
                                MainViewPagerAdapter.recyclerAdapter.refreshData();
                                break;
                            default:
                                super.handleMessage(msg);
                                break;
                        }
                    }
                };
                OkhttpRequest.refreshFundData(addFundHandler, stringList);
                return true;
            case R.id.id_add_record_symbol:
                Intent toAddRecord = new Intent();
                toAddRecord.setClass(this, InputActivity.class);
                startActivity(toAddRecord);
                return true;
            case R.id.id_drag_symbol:
                isDragging = true;
                startActionMode(new ActionMode.Callback()
                {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu)
                    {
                        MenuInflater inflater = getMenuInflater();
                        inflater.inflate(R.menu.menu_check_button, menu);
                        mode.setTitle("基金排序");
                        return true;
                    }
    
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
                    {
                        return false;
                    }
    
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case android.R.id.home:
                                isDragging = false;
                                mode.finish();
                                return true;
                            case R.id.id_check_button:
                                isDragging = false;
                                mode.finish();
                                // TODO: 2018/7/24 此处刷新数据
                                return true;
                        }
                        return false;
                    }
    
                    @Override
                    public void onDestroyActionMode(ActionMode mode)
                    {
        
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
}
