package com.dslm.funddataanalysisapp;

public class SimpleFundData
{
    private String code;
    private String name;
    private String date;
    private double netWorthTrend;
    private double equityReturn;
    
    public SimpleFundData()
    {
        this(null, null, null, 0, 0);
    }
    
    public SimpleFundData(String code,String name,String date,double netWorthTrend,double equityReturn)
    {
        this.code = code;
        this.name = name;
        this.date = date;
        this.netWorthTrend = netWorthTrend;
        this.equityReturn = equityReturn;
    }
    
    public double getNetWorthTrend()
    {
        return netWorthTrend;
    }
    
    public double getEquityReturn()
    {
        return equityReturn;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public void setEquityReturn(double equityReturn)
    {
        this.equityReturn = equityReturn;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setNetWorthTrend(double netWorthTrend)
    {
        this.netWorthTrend = netWorthTrend;
    }
}
