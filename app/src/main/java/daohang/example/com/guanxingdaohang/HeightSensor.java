package daohang.example.com.guanxingdaohang;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * 高度差
 */

public class HeightSensor {
    private static final String TAG = "HeightSensor";
    private HeightCallBack heightCallBack;
    private Context context;

    public HeightSensor(Context context, HeightCallBack heightCallBack) {
        this.context = context;
        this.heightCallBack = heightCallBack;
    }

    public interface HeightCallBack {
        /**
         * 高度回调
         */
        void Height(Floor f);
    }

    /**
     * 初始化数据
     *
     */
    public void registerOrient(float q) {
        Floor floor = HeightD.getInstance().getPFloor(q);
        heightCallBack.Height(floor);
    }
}
