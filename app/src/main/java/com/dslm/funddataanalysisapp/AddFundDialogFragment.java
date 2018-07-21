package com.dslm.funddataanalysisapp;


import android.os.*;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.dslm.funddataanalysisapp.MainViewPagerAdapter.recyclerAdapter;

public class AddFundDialogFragment extends DialogFragment
{
    private EditText code;
    private Button cancel;
    private Button add;
    
    public AddFundDialogFragment()
    {
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_add_fund_dialog, container, false);
        
        initView(view);
        setCancelable(false);
        return view;
    }
    
    private void initView(View view)
    {
        code = (EditText)view.findViewById(R.id.id_fund_code_edit_text);
        cancel = (Button) view.findViewById(R.id.id_fund_code_cancel);
        add = (Button) view.findViewById(R.id.id_fund_code_add);
    
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDismiss(getDialog());
            }
        });
    
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(code.getText().toString().equals(""))
                    Toast.makeText(getActivity(), "基金代码不能为空", Toast.LENGTH_SHORT).show();
                else if(code.getText().toString().length() < 6)
                    Toast.makeText(getActivity(), "基金代码长度过短，应为6个数字", Toast.LENGTH_SHORT).show();
                else
                {
                    add.setEnabled(false);
                    Handler addFundHandler = new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg)
                        {
                            System.out.println(msg.what);
                            switch (msg.what)
                            {
                                case HandlerWhatValue.netWorkProblem:
                                    Toast.makeText(getActivity(), String.valueOf(((SimpleFundData)msg.obj).getCode()) + "号基金下载网络异常", Toast.LENGTH_SHORT).show();
                                    add.setEnabled(true);
                                    break;
                                case HandlerWhatValue.codeIsWrong:
                                    Toast.makeText(getActivity(), "基金代码不存在", Toast.LENGTH_SHORT).show();
                                    add.setEnabled(true);
                                    break;
                                case HandlerWhatValue.addedData:
                                    Toast.makeText(getActivity(), String.valueOf(((SimpleFundData)msg.obj).getCode()) + "号基金下载成功", Toast.LENGTH_SHORT).show();
                                    recyclerAdapter.addData((SimpleFundData) msg.obj);
                                    onDismiss(getDialog());
                                    break;
                                case HandlerWhatValue.sameData:
                                    Toast.makeText(getActivity(), "基金代码已存在", Toast.LENGTH_SHORT).show();
                                    add.setEnabled(true);
                                    break;
                                default:
                                    super.handleMessage(msg);
                                    break;
                            }
                        }
                    };
                    OkhttpRequest.getFundData(addFundHandler, code.getText().toString());
                }
            }
        });
    }
    
}
