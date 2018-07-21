package com.dslm.funddataanalysisapp;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

//数据下载类(子线程)
public class OkhttpRequest
{
    public static final OkHttpClient client = new OkHttpClient();
    
    public static void getFundData(final Handler addFundHandler, final String code)
    {
        Request request = new Request.Builder().url("http://fund.eastmoney.com/pingzhongdata/" + code + ".js").build();
        client.newCall(request).enqueue(new Callback()
        {
            Handler handler = addFundHandler;
            
            @Override
            public void onFailure(Call call, IOException e)
            {
                Message message = new Message();
                message.obj = code;
                message.what = HandlerWhatValue.netWorkProblem;
                handler.sendMessage(message);
                Log.e("下载失败", "onFailure: ", e);
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String data = response.body().string();
                if (data == null || data.indexOf("基金或股票信息") == -1)
                {
                    handler.sendEmptyMessage(HandlerWhatValue.codeIsWrong);
                } else
                {
                    DataProcess.saveData(addFundHandler, String.valueOf(data));
                }
            }
        });
    }
    
    public static void refreshFundData(final Handler addFundHandler, final List<String> codeList)
    {
        final int[] i = {0};
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
    
        final Handler handler = new Handler(handlerThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case HandlerWhatValue.sameData:
                        if(msg.obj != null)
                            Toast.makeText(MainActivity.context, String.valueOf(((SimpleFundData)msg.obj).getCode()) + "号基金刷新成功", Toast.LENGTH_SHORT).show();
                        if(i[0] < codeList.size())
                        {
                            getFundData(this, codeList.get(i[0]));
                            i[0]++;
                        }
                        else
                        {
                            addFundHandler.sendEmptyMessage(HandlerWhatValue.refreshedData);
                        }
                        break;
                    case HandlerWhatValue.netWorkProblem:
                        addFundHandler.sendMessage(msg);
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };
        handler.sendEmptyMessage(HandlerWhatValue.sameData);
    }
}
