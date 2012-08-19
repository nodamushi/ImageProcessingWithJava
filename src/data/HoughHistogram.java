package data;

import static java.lang.Math.*;

import java.awt.Point;

/**
 * ハフ変換で生成されるグラフを表現するためのクラス。
 *
 */
public class HoughHistogram extends IntDatas{
    
    public static class HoughHistogramPoint{
        public float theta;
        public int r;
        public HoughHistogramPoint(float t,int r){
            theta = t;this.r = r;
        }
    }

    private float onetheta;
    private int rmax;
    private int thetaSize;
    /**
     * 
     * @param thetaSize 0～180度を何分割するか
     * @param rSize どの距離まで空間をとるか。
     */
    public HoughHistogram(int thetaSize,int rSize){
        super(thetaSize, rSize*2+1);
        onetheta = (float)PI/thetaSize;
        rmax = rSize;
        this.thetaSize = thetaSize;
    }
    
    @Override
    public int getData(int x,int y){
        if(isOut(x, y))return 0;
        return super.getData(x,y);
    }

    
    
    private Point getMapLocation(float theta,int r){
        //突貫工事で面倒くさかったので、thetaは0～2π
        if(theta < 0||theta > PI)throw new ArrayIndexOutOfBoundsException("theta =" +theta/PI +"pi");
        if(theta > PI){
            theta -=PI;
            r = -r;
        }
        if(abs(r)>rmax)throw new ArrayIndexOutOfBoundsException("r > "+rmax);
        int t = (int)(theta/onetheta);
        return new Point(t,r+rmax);
    }
    
    /**
     * IntDatasの座標(x,y)をグラフの座標に変換します
     * @param x
     * @param y
     * @return
     */
    public HoughHistogramPoint convert(int x,int y){
        return new HoughHistogramPoint(onetheta*x, y-rmax);
    }
    
    /**
     * rの絶対値の最大値。範囲は-rmax～rmaxになる。
     * @return
     */
    public int getRMax(){
        return rmax;
    }
    
    /**
     * 180度を何分割したかを返す
     * @return
     */
    public int getThetaSize(){
        return thetaSize;
    }
    
    /**
     * 
     * @param theta rad 0～2π
     * @param r 距離
     * @return
     */
    public int getData(float theta,int r){
        Point p = getMapLocation(theta, r);
        return super.getData(p.x,p.y);
    }
    
    /**
     * 
     * @param theta rad 0～2π
     * @param r 距離
     * @return
     */
    public void setData(float theta,int r,int value){
        Point p = getMapLocation(theta, r);
        super.setData(p.x, p.y, value);
    }
    
    /**
     * 対応するヒストグラムを1増やす。
     * @param theta
     * @param r
     */
    public void inc(float theta,int r){
        Point p = getMapLocation(theta, r);
        super.inc(p.x,  p.y);
    }
    
    
    
    
}
