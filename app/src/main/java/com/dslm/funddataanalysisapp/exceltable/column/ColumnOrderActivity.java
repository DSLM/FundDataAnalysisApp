package com.dslm.funddataanalysisapp.exceltable.column;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.dslm.funddataanalysisapp.R;

public class ColumnOrderActivity extends AppCompatActivity
{
    ColumnChoosePagerAdapter pagerAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_order);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("修改统计表的列排序");
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.id_column_choose_tab_layout);
    
        final ViewPager viewPager = (ViewPager)findViewById(R.id.id_column_choose_view_pager);
        pagerAdapter = new ColumnChoosePagerAdapter(
                new RecyclerView(this), new RecyclerView(this), this);
        viewPager.setAdapter(pagerAdapter);
        
        
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
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_check_button, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_check_button:
                pagerAdapter.saveInfo();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
