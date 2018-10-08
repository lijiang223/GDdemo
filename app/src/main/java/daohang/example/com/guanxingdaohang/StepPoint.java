package daohang.example.com.guanxingdaohang;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import daohang.example.com.guanxingdaohang.util.Coordinate;
import daohang.example.com.guanxingdaohang.util.MyThread;


/**
 * Created by lijiang on 2017/10/24.
 */
public class StepPoint implements StepSensorBase.StepCallBack, OrientSensor.OrientCallBack,OrientSensor.TOrientCallBack,HeightSensor.HeightCallBack{

    private StepSensorBase mStepSensor;
    private OrientSensor mOrientSensor;
    private HeightSensor heightSensor;
    private IOrientListener orientListener;
    private IPointListener pointListener;
    protected IStepListener iStepListener;
    private IheightListener iheightListener;
    protected int mOrient = 0;
    protected int mTOrient = 0;
    protected float x = 0;
    protected float y = 0;
    private float cx = 0;
    private float cy = 0;
    private Coordinate coordinate;
    private Context context;
    private int type = 0;
    private long oldTime = 0;

    private int HEIGHTLISTEN = 1;
    private int TYPE = 0;
    private float currentP = 0;
    float[] Q;

    public StepPoint(Context context, int type){
        this.context = context;
        if(type == 0){
            mStepSensor = new StepSensorWSAcceleration(context, this);
        }else if(type == 1){
            mStepSensor = new StepSensorAcceleration(context,this);
        }
        this.type = type;
        setDefaultTPoint();
        mOrientSensor = new OrientSensor(context, this,this);
        heightSensor = new HeightSensor(context, this);
    }

    /**
     * 初始化起点和坐标监听
     * @param x
     * @param y
     * @param i
     * @return
     */
    public boolean init(float x, float y, IPointListener i){
        if(coordinate == null){
            return false;
        }
        mStepSensor.CURRENT_SETP = 0;
        Point point = coordinate.coordinateTra(x, y);
        this.x = point.x;
        this.y = point.y;
        pointListener = i;
        return true;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean init(float x, float y){
        if(coordinate == null){
            return false;
        }
        mStepSensor.CURRENT_SETP = 0;
        Point point = coordinate.coordinateTra(x, y);
        this.x = point.x;
        this.y = point.y;
        return true;
    }

    /**
     * 设置步长和坐标系转换关系
     * @param count
     * @param sX
     * @param sY
     * @param eX
     * @param eY
     */
    public void setTPoint(int count,float sX,float sY,float eX,float eY){
        StepLen stepLen = new StepLen();
        stepLen.setStepLength(count+1,sX,sY,eX,eY);
        coordinate = new Coordinate(sX, sY, eX, eY, cx, cy);
    }

    /**
     * 设置步长
     * @param count
     * @param sX
     * @param sY
     * @param eX
     * @param eY
     */
    public void setStepLength(int count,float sX,float sY,float eX,float eY){
        if(count<=0){
            return;
        }
        StepLen stepLen = new StepLen();
        stepLen.setStepLength(count,sX,sY,eX,eY);
    }

    /**
     * 设置默认坐标系
     */
    public void setDefaultTPoint(){
        coordinate = new Coordinate();
    }

    public void setDefault(){
        coordinate = new Coordinate();
        StepLen stepLen = new StepLen();
        stepLen.setMStepLength();
        cx = 0;
        cy = 0;
    }

    /**
     * 设置电梯第一二点
     * @param f   楼层
     */
    public void setTHeight(int f){
        HeightD heightD = HeightD.getInstance();
        float height = heightD.getPHeight(currentP);
        Floor floor = new Floor();
        floor.setFloor(f);
        floor.setHeight(height);
        heightD.setHeight(floor);
    }


    /**
     * 获取数据
     * @param a 加速度数据(步数)
     * @param n  角速度
     * @param Q  四元数
     * @return 是否成功
     */
    public boolean setDate(float[] a,float[] n,float[] Q,float q){
        if(a.length != 3||n.length != 3||Q.length!=4){
            return false;
        }
        //外设
        a[0] = (float) (a[0]*9.8);
        a[1] = (float) (a[1]*9.8);
        a[2] = (float) (a[2]*9.8);
        this.Q = Q;
        mStepSensor.registerStep(a);
        mOrientSensor.registerOrient(a,n,Q);
        heightSensor.registerOrient(q);
        return true;
    }

    /**
     * 获取数据
     * @param a 加速度数据(步数)
     * @param c 磁场数据
     * @param g 手机传入重力
     * @return 是否成功
     */
    public boolean setPhoneDate(float[] a,float[] c,float[] g){
        if(a.length != 3||c.length != 3){
            return false;
        }
        if(System.currentTimeMillis() - oldTime>100){
            mStepSensor.registerStep(a);
            oldTime = System.currentTimeMillis();
        }
        return true;
    }

    /**
     * 设置方向监听
     * @param orientListener
     */
    public void setOrientListener(IOrientListener orientListener){
        this.orientListener = orientListener;
    }

    /**
     * 设置步数监听
     * @param
     */
    public void setStepListener(IStepListener stepListener){
        this.iStepListener = stepListener;
    }

    /**
     * 设置坐标监听
     * @param
     */
    public void setPointListener(IPointListener pointListener){
        this.pointListener = pointListener;
    }

    /**
     * 设置步数监听
     * @param
     */
    public void setIheightListener(IheightListener stepListener){
        this.iheightListener = stepListener;
    }

    @Override
    public void TOrient(int torient) {
        mTOrient = torient;
        if(orientListener!=null){
            orientListener.orient((int) mTOrient);
        }
    }

    @Override
    public void Height(Floor f) {
        if(iheightListener!=null&&TYPE==HEIGHTLISTEN){
            iheightListener.height(f.getFloor(),f.getHeight());
        }
    }

    /**
     * 控制 1高度回掉，还是 0步数回掉
     */
    public void setHPFlag(int type){
        if(type == 0){
            TYPE = 0;
        }else{
            TYPE = 1;
        }
    }

    public interface IStepListener {
        /**
         * 回掉监听
         */
        void IStep(int count);
    }

    public interface IOrientListener {
        /**
         * 回掉监听
         */
        void orient(int Orient);
    }

    public interface IPointListener {
        /**
         * 回掉监听
         */
        void point(float x, float y);
    }
    public interface IheightListener {
        /**
         * 回掉监听
         */
        void height(int c, float h);
    }

    @Override
    public void Orient(float orient,float pitch,float roll) {
        mOrient = (int) orient;
    }

    @Override
    public void Step(int stepNum) {
        x += (float) (StepLen.stepLen * Math.sin(Math.toRadians(mTOrient)));
        y += -(float) (StepLen.stepLen * Math.cos(Math.toRadians(mTOrient)));
        cx += (float) (StepLen.stepLen * Math.sin(Math.toRadians(mTOrient)));
        cy += -(float) (StepLen.stepLen * Math.cos(Math.toRadians(mTOrient)));
        if(iStepListener!=null){
            iStepListener.IStep(stepNum);
        }
        if(orientListener!=null){
            orientListener.orient(mTOrient);
        }
        if(pointListener!=null){
            Point point = coordinate.coordinateTra(x, y);
            pointListener.point(point.x,point.y);
        }
    }

    public void resetQ(){
        if(Q!=null){
            mOrientSensor.defaultQ(Q);
        }
    }
}
