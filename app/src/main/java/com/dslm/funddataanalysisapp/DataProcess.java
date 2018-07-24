package com.dslm.funddataanalysisapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

//数据下载，数据库以及excel储存类
public class DataProcess
{
    public static void saveData(Handler handler, String data)
    {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(
                                data.getBytes(
                                        Charset.forName("utf8"))),
                        Charset.forName("utf8")));
        String line;
    
        String name = null;
        String code = null;
        long newestDate = 0;
        double newestNetWorthTrend = 0;
        double newestEquityReturn = 0;
        ArrayList<Long> dates = new ArrayList<>();
        HashMap<Long, Double> netWorthTrend = new HashMap<Long, Double>();
        HashMap<Long, Double> equityReturn = new HashMap<Long, Double>();
        HashMap<Long, Double> acWorthTrend = new HashMap<Long, Double>();
        
        try
        {
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("var fS_name = "))
                {
                    name = line.substring(15, line.length() - 2);
                }
                if (line.startsWith("var fS_code = "))
                {
                    code = line.substring(15, line.length() - 2);
                }
                if (line.startsWith("var Data_netWorthTrend = "))
                {
                    JSONArray analysis_1 = new JSONArray(line.substring(25, line.length() - 1));
                    for(int i = 0; i < analysis_1.length(); i++)
                    {
                        JSONObject jo = analysis_1.optJSONObject(i);
                        dates.add(jo.getLong("x"));
                        netWorthTrend.put(dates.get(i), jo.getDouble("y"));
                        equityReturn.put(dates.get(i), jo.getDouble("equityReturn"));
                    }
                    newestDate = dates.get(analysis_1.length() - 1);
                    newestNetWorthTrend = netWorthTrend.get(newestDate);
                    newestEquityReturn = equityReturn.get(newestDate);
                }
                if (line.startsWith("var Data_ACWorthTrend = "))
                {
                    JSONArray analysis_2 = new JSONArray(line.substring(24, line.length() - 1));
                    for(int i = 0; i < analysis_2.length(); i++)
                    {
                        JSONArray ja = analysis_2.optJSONArray(i);
                        acWorthTrend.put(ja.optLong(0), ja.optDouble(1));
                    }
                }
            }
        }
        catch (IOException e)
        {
            Log.e("数据解析", "saveData: ", e);
        }
        catch (JSONException e)
        {
            Log.e("数据解析", "saveData: ", e);
        }
        
        OpenHelper openHelper = MainActivity.openHelper;
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        FundDAO fundDAO = new FundDAO(sqLiteDatabase);
        SimpleFundData fundData = new SimpleFundData();
        fundData.setCode(code);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        fundData.setDate(sdf.format(new Date(newestDate)));
        fundData.setNetWorthTrend(newestNetWorthTrend);
        fundData.setEquityReturn(newestEquityReturn);
    
        Message message = new Message();
        message.obj = fundData;
        if(fundDAO.queryOne(fundData).getName() != null)
        {
            fundDAO.update(fundData);
            sqLiteDatabase.close();
            
            message.what = HandlerWhatValue.sameData;
        }
        else
        {
            fundData.setName(name);
            fundDAO.insert(fundData);
            sqLiteDatabase.close();
    
            message.what = HandlerWhatValue.addedData;
        }
    
    
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row;
        HSSFCell cell;
        File historyDataFile = new File(MainActivity.context.getFilesDir() + "/" + code + ".xls");
        int start = 2;
        
        try
        {
            if (!historyDataFile.exists())
            {
                historyDataFile.createNewFile();
            }
            else
            {
                FileInputStream input = new FileInputStream(historyDataFile);
                wb = new HSSFWorkbook(input);
                input.close();
                sheet = wb.getSheetAt(0);
                start += (int)sheet.getRow(0).getCell(0).getNumericCellValue();
            }
        }
        catch (IOException e)
        {
            Log.e("创建/读取基金excel问题", "saveData: ", e);
        }
        
        //row = sheet.createRow(0);
        //for(int i = 0; i < 21; i++)
        //{
            //row.createCell(i, CellType.STRING).setCellValue(i);
        //}
        CellStyle cellStyle;
        DataFormat format;
        for(int i = start; i <= dates.size() + 1; i++)
        {
            String f = String.valueOf(i - 1);
            if(sheet.getRow(i) != null)
            {
                row = sheet.getRow(i - 1);
            }
            else
            {
                row = sheet.createRow(i - 1);
            }
    
            sdf = new SimpleDateFormat("yy-MM-dd");
            
            row.createCell(0, CellType.STRING).setCellValue(sdf.format(new Date(dates.get(i - 2))));
            
            
            row.createCell(1, CellType.NUMERIC).setCellValue(netWorthTrend.get(dates.get(i - 2)));
            row.createCell(2, CellType.NUMERIC).setCellValue(acWorthTrend.get(dates.get(i - 2)));
            row.createCell(3, CellType.NUMERIC).setCellValue(equityReturn.get(dates.get(i - 2)));
            
            row.createCell(4, CellType.FORMULA).setCellFormula(
                    String.format("(B%d-IF(ISNUMBER(B%s),B%s,0))*IF(ISNUMBER(O%s),O%s,0)-IF(ISNUMBER(G%s),G%s,0)",
                            i, f, f, f, f, f, f));
            
            row.createCell(7, CellType.FORMULA).setCellFormula(
                    String.format("IF(F%d=\"\",\"\",ROUND((F%d-G%d)/B%d,2))",
                            i, i, i, i));
            row.createCell(8, CellType.FORMULA).setCellFormula(
                    String.format("IF(ISERROR(F%d/H%d),\"\",F%d/H%d)",
                            i, i, i, i));
            
            row.createCell(11, CellType.FORMULA).setCellFormula(
                    String.format("IF(J%d=\"\",\"\",J%d*B%d-K%d)",
                            i, i, i, i));
            row.createCell(12, CellType.FORMULA).setCellFormula(
                    String.format("IF(ISERROR(L%d/J%d),\"\",L%d/J%d)",
                            i, i, i, i));
            
            row.createCell(13, CellType.FORMULA).setCellFormula(
                    String.format("T%d-U%d",
                            i,i));
            
            row.createCell(14, CellType.FORMULA).setCellFormula(
                    String.format("IF(ISNUMBER(O%s),O%s,0)+IF(ISNUMBER(H%d),H%d,0)-IF(ISNUMBER(J%d),J%d,0)",
                            f, f, i, i, i, i));
            row.createCell(15, CellType.FORMULA).setCellFormula(
                    String.format("TRUNC(O%d*B%d,2)",
                            i, i));
            row.createCell(16, CellType.FORMULA).setCellFormula(
                    String.format("P%d-N%d",
                            i, i));
            row.createCell(17, CellType.FORMULA).setCellFormula(
                    String.format("IF(ISERROR(Q%d/N%d),0,Q%d/N%d)",
                            i, i, i, i));
            
            row.createCell(18, CellType.FORMULA).setCellFormula(
                    String.format("IF(ISERROR(N%d/O%d),0,N%d/O%d)",
                            i, i, i, i));
            
            row.createCell(19, CellType.FORMULA).setCellFormula(
                    String.format("IF(ISNUMBER(T%s),T%s,0)+IF(F%d=\"\",0,F%d)",
                            f, f, i, i));
            row.createCell(20, CellType.FORMULA).setCellFormula(
                    String.format("IF(ISNUMBER(U%s),U%s,0)+IF(L%d=\"\",0,L%d)",
                            f, f, i, i));
        }
    
        sheet.createRow(0).createCell(0).setCellValue(dates.size());
    
        wb.getSheetAt(0).getRow(0).createCell(1).setCellValue(true);
        
        try
        {
            FileOutputStream output = new FileOutputStream(historyDataFile);
            wb.write(output);
            output.flush();
            output.close();
        }
        catch(Exception e)
        {
            Log.e("写入基金excel问题", "saveData: ", e);
        }
        
        handler.sendMessage(message);
    }
}
