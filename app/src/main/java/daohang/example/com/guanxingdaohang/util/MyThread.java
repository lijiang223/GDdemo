package daohang.example.com.guanxingdaohang.util;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by RL on 2017/12/1.
 */
public class MyThread extends Thread{
    List<Integer> oList = null;
    int step = 0;
    private int mOrient;
    public MyThread(List<Integer> a,int s,int o){
        oList = a;
        step = s;
        mOrient = o;
    }

    @Override
    public void run() {
        for(int i=0;i<oList.size();i++){
            appendText(Environment.getExternalStorageDirectory()+"/output2.txt","第"+step+"步         "+oList.get(i)+"\n");
        }
        appendText(Environment.getExternalStorageDirectory()+"/output2.txt","第"+step+"步   最终角度"+mOrient+"\n");
    }

    public void appendText(String filepathString,String outputString) {
        FileWriter writer;
        try {
            File file=new File(filepathString);
            writer = new FileWriter(file,true);
            writer.write(outputString);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
