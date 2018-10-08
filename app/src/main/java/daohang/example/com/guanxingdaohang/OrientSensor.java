package daohang.example.com.guanxingdaohang;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import daohang.example.com.guanxingdaohang.util.FileUtils;
import daohang.example.com.guanxingdaohang.util.SensorUtils;


/**
 * 方向传感器
 */

public class OrientSensor{
    private static final String TAG = "OrientSensor";
    private OrientCallBack orientCallBack;
    private TOrientCallBack torientCallBack;
    private Context context;
    String filepath = Environment.getExternalStorageDirectory()+"/output.txt";
    private static float q0 = 1, q1 = 0, q2 = 0, q3 = 0;
    private float yaw = 0;

    public OrientSensor(Context context, OrientCallBack orientCallBack,TOrientCallBack torient) {
        this.context = context;
        this.orientCallBack = orientCallBack;
        torientCallBack = torient;
    }

    public interface OrientCallBack {
        /**
         * 方向回调
         */
        void Orient(float orient,float pitch,float roll);
    }

    public interface TOrientCallBack {
        /**
         * 方向回调
         */
        void TOrient(int torient);
    }

    /**
     * 注册加速度传感器和地磁场传感器
     * @param a 加速度
     * @param n 角速度
     * @param Q 四元数
     * @return 是否支持方向功能
     */
    public Boolean registerOrient(float[] a,float[] n,float[] Q) {
        Boolean isAvailable = true;

        if(q0 ==1&&q1==0&&q2==0&&q3==0){
            if(Q[0]==0&&Q[1]==0&&Q[2]==0&&Q[3]==0){
                return false;
            }
            q0 = Q[0];
            q1 = Q[1];
            q2 = Q[2];
            q3 = Q[3];
            double toDegrees = Math.toDegrees(yaw);
            toDegrees = toDegrees+180;
            torientCallBack.TOrient((int) toDegrees);
        }else{
            jiaoyanQ(a, n);
        }
        return isAvailable;
    }

    /**
     * 初始化四元数
     * @param yaw
     * @param pitch
     * @param roll
     */
    public void updateQ(float yaw,float pitch,float roll){
        q0 = (float) (Math.cos(roll/2)* Math.cos(pitch/2)* Math.cos(yaw/2)+Math.sin(roll/2)*Math.sin(pitch/2)*Math.sin(yaw/2));
        q1 = (float) (Math.sin(roll/2)* Math.cos(pitch/2)* Math.cos(yaw/2)-Math.cos(roll/2)*Math.sin(pitch/2)*Math.sin(yaw/2));
        q2 = (float) (Math.cos(roll/2)*Math.sin(pitch/2)*Math.cos(yaw/2)+Math.sin(roll/2)* Math.cos(pitch/2)*Math.sin(yaw/2));
        q3 = (float) (Math.cos(roll/2)* Math.cos(pitch/2)*Math.sin(yaw/2) - Math.sin(roll/2)*Math.sin(pitch/2)* Math.cos(yaw/2));
    }

    public void defaultQ(float[] Q){
//        q0 = 1;
//        q1 = 0;
//        q2 = 0;
//        q3 = 0;
        q0 = Q[0];
        q1 = Q[1];
        q2 = Q[2];
        q3 = Q[3];
        FileUtils.appendText(filepath,"q0 =="+q0+"..q1 =="+q1+"..q2 == "+q2+"..q3=="+q3);
    }

    private float vx=0,vy=0,vz=0,ex=0,ey=0,ez=0,exInt = 0, eyInt = 0, ezInt = 0;
    private static float Ki = 0.0005f;
    private static float Kp = 0.2f;
    private float norm = 0;

    private void jiaoyanQ(float[] a, float[] n) {
        a[0] = (float) (a[0]*9.8);
        a[1] = (float) (a[1]*9.8);
        a[2] = (float) (a[2]*9.8);
        norm = (float) Math.sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]);
        if(norm == 0){
            return;
        }
        a[0] = a[0]/norm;
        a[1] = a[1]/norm;
        a[2] = a[2]/norm;
        vx = 2*(q1*q3-q0*q2);
        vy = 2*(q0*q1+q2*q3);
        vz = q0*q0-q1*q1-q2*q2+q3*q3;
        ex = (a[1]*vz - a[2]*vy);
        ey = (a[2]*vx - a[0]*vz);
        ez = (a[0]*vy - a[1]*vx);
        exInt = exInt + ex*Ki;
        eyInt = eyInt + ey*Ki;
        ezInt = ezInt + ez*Ki;

        n[0] = (float) (Math.toRadians(n[0]) + Kp*ex + exInt);
        n[1] = (float) (Math.toRadians(n[1]) + Kp*ey + eyInt);
        n[2] = (float) (Math.toRadians(n[2]) + Kp*ez + ezInt);
        q0 = q0 + (-q1*n[0] - q2*n[1] - q3*n[2])*0.05f;
        q1 = q1 + (q0*n[0] + q2*n[2] - q3*n[1])*0.05f;
        q2 = q2 + (q0*n[1] - q1*n[2] + q3*n[0])*0.05f;
        q3 = q3 + (q0*n[2] + q1*n[1] - q2*n[0])*0.05f;
        norm = (float) Math.sqrt(q0*q0+q1*q1+q2*q2+q3*q3);
        if(norm == 0){
            return;
        }
        q0 = q0/norm;
        q1 = q1/norm;
        q2 = q2/norm;
        q3 = q3/norm;
        float yaw = (float) (getYaw(q0, q1, q2, q3));
        double toDegrees = Math.toDegrees(yaw);
        FileUtils.appendText(filepath,"toDegrees == "+toDegrees+"\n");
        toDegrees = toDegrees+180;
        torientCallBack.TOrient((int) toDegrees);
    }

    public float getYaw(float q0,float q1,float q2,float q3){
        float f = 2*(q0*q3+q1*q2);
        float m = 2*(q3*q3+q2*q2)-1;
        yaw = (float) Math.atan2(f,m);
        return yaw;
    }
}
