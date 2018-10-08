package daohang.example.com.guanxingdaohang;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by RL on 2017/12/21.
 */
public class HeightD {
    private float height = 2.5f;
    private float firstHeight = 0;
    private float secondHeight = 0;
    private static HeightD heightD;

    private HeightD(){
    }
    public synchronized static HeightD getInstance(){
        if(heightD == null) {
            heightD = new HeightD();
        }
        return heightD;
    }
    public void setHeight(Floor f){
        if(f.getFloor()==0){
            firstHeight = f.getHeight();
        }else if(f.getFloor()==1){
            secondHeight = f.getHeight();
            height = Math.abs(secondHeight - firstHeight);
        }
    }

    public Floor getPFloor(float p){
        float h = (float) ((1013.25-p/100)*9);
        int f = (int) (Math.abs(h-firstHeight)/height);
        Floor floor = new Floor();
        floor.setFloor(f);
        floor.setHeight(h);
        return floor;
    }

    public float getPHeight(float p){
        return (float) ((1013.25-p/100)*9);
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
