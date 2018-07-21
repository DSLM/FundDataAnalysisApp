package com.dslm.funddataanalysisapp.exceltable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;


public class DataHorizontalScrollView extends HorizontalScrollView
{
    private View mView;
    
    public DataHorizontalScrollView(Context context) {
        super(context);
    }
    
    public DataHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mView!=null) {
            mView.scrollTo(l, t);
        }
    }
    
    public void setScrollView(View view) {
        mView = view;
    }
}
