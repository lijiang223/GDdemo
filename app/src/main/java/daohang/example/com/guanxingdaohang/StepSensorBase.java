package daohang.example.com.guanxingdaohang;

import android.content.Context;
import android.hardware.SensorManager;


/**
 * 计步传感器抽象类，子类分为加速度传感器、或计步传感器
 */
public abstract class StepSensorBase{
    private Context context;
    protected StepCallBack stepCallBack;
    protected SensorManager sensorManager;
    protected static int CURRENT_SETP = 0;
    protected boolean isAvailable = false;
    private static int STEPSENSORWS = 0;
    private static int STEPSENSOR = 1;

    public StepSensorBase(Context context, StepCallBack stepCallBack) {
        this.context = context;
        this.stepCallBack = stepCallBack;
    }

    public interface StepCallBack {
        /**
         * 计步回调
         */
        void Step(int stepNum);
    }

    /**
     * 开启计步
     */
    public boolean registerStep(float[] a) {
//        if (sensorManager != null) {
//            sensorManager.unregisterListener(this);
//            sensorManager = null;
//        }
//        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        sensorManager = SensorUtil.getInstance().getSensorManager(context);
        registerStepListener(a);
        return isAvailable;
    }

    /**
     * 注册计步监听器
     */
    protected abstract void registerStepListener(float[] a);

    /**
     * 注销计步监听器
     */
    public abstract void unregisterStep();
}
