package daohang.example.com.guanxingdaohang.util;


import daohang.example.com.guanxingdaohang.Point;

/**
 * Created by lijiang on 2017/11/1.
 */
public class Coordinate {
    private double dO;

    public Coordinate(){
        dO = 0;
    }

    /**
     * 计算坐标系角度差
     * @param sX
     * @param sY
     * @param eX
     * @param eY
     * @param x
     * @param y
     */
    public Coordinate(float sX, float sY, float eX, float eY, float x, float y) {
        float dx = eX - sX;
        float dy = eY - sY;
        if(dy == 0){
            return;
        }
        double aO = Math.atan(dx / dy);
        double sO = Math.atan(x / y);
        dO = sO - aO;
    }

    public Point coordinateTra(float x, float y){
        Point point = new Point();
        point.x = (float) (x* Math.cos(dO)+y* Math.sin(dO));
        point.y = (float) (y* Math.cos(dO)-x* Math.sin(dO));
        return point;
    }
}
