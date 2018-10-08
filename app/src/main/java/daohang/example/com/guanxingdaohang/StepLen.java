package daohang.example.com.guanxingdaohang;

/**
 * Created by lijiang on 2017/10/24.
 */
public class StepLen {
    public static double stepLen = 5;

    public double setStepLength(int count,float sX,float sY,float eX,float eY){
        double dx = Math.pow(sX - eX, 2);
        double dy = Math.pow(sY - eY, 2);
        stepLen = Math.sqrt(dx + dy)/count;
        return stepLen;
    }

    public void setMStepLength(){
        stepLen = 15;
    }
}
