package filter;

import static java.lang.Math.*;

import java.util.ArrayList;

import data.HoughHistogram;
import data.HoughHistogram.HoughHistogramPoint;
import data.Pixels;

/**
 *ハフ変換を定義するクラス。 
 *
 */
public class HoughTransform{
    
    private int thetaSize;
    private float rad;
    private double[] cosmap,sinmap;
    
    /**
     * 
     * @param thetaSize ヒストグラムを作成する際に、角度について180度を何分割するか。
     */
    public HoughTransform(int thetaSize){
        if(thetaSize <=0)throw new IllegalArgumentException("thetaSize <= 0!");
        this.thetaSize  = thetaSize;
        rad = (float)(PI/thetaSize);
        //あらかじめcosとsinを計算
        cosmap = new double[thetaSize];
        sinmap = new double[thetaSize];
        for(int i=0;i<thetaSize;i++){
            float t = i*rad;
            cosmap[i] = cos(t);
            sinmap[i] = sin(t);
        }
    }
    
    /**
     * ハフ変換を行います。
     * @param edge エッジなら1,そうでないなら0のマップ
     * @return
     */
    public HoughHistogram hough(Pixels edge){
        int w = edge.width,h = edge.height;
        
        //r = x*cosθ+y*sinθの右辺は座標(x,y)を-θ回転した時のx座標の式なので、
        //rの範囲は-√(x^2+y^2)～√(x^2+y^2)
        //全体でのrの範囲はこれにx=width,y=heightを入れればいい
        int rmax = (int)(floor(hypot(w, h)));
        HoughHistogram map = new HoughHistogram(thetaSize, rmax);
        
        for(int x=0;x<w;x++)for(int y=0;y<h;y++){
            if(edge.getPixel(x, y)<1f)continue;//エッジじゃない
            
            //各角度について投票を行う
            for(int i=0;i<thetaSize;i++){
                float theta = i*rad;
                //r=x*cosθ+y*sinθの計算
                int r = (int)(x*cosmap[i]+y*sinmap[i]);
                //投票
                map.inc(theta, r);
            }
        }
        return map;
    }
    
    /**
     * ハフ変換で得たヒストグラムから、単純にlevel以上投票された点を抽出します
     * @param map
     * @param level
     * @return
     */
    public static ArrayList<HoughHistogramPoint> search(HoughHistogram map,int level){
        ArrayList<HoughHistogramPoint> points = new ArrayList<>();
        int w = map.width,h = map.height;
        for(int x = 0;x<w;x++)for(int y=0;y<h;y++){
            int v = map.getData(x, y);
            if(v > level){
                points.add(map.convert(x, y));
            }
        }
        return points;
    }
    
        
    /**
     * 得た点をいくつかの点にまとめます。
     * @param points 得た点群
     * @param thetaLevel まとめる範囲（角度 rad）
     * @param rlevel まとめる範囲（距離）
     * @param level まとめる個数がこの値以下なら無視する
     * @return
     */
    public static ArrayList<HoughHistogramPoint> reduce(ArrayList<HoughHistogramPoint> points,
            float thetaLevel,int rlevel,int level){
        ArrayList<HoughHistogramPoint> p = new ArrayList<>(),pc = new ArrayList<>(points);
        for(int i=0;i<pc.size();i++){
            HoughHistogramPoint hp = pc.get(i);
            float theta = hp.theta;
            int r = hp.r;
            int s = 1;
            for(int k = i+1;k<pc.size();k++){
                HoughHistogramPoint hp2 = pc.get(k);
                if(abs(hp2.theta -hp.theta)<thetaLevel && abs(hp.r-hp2.r)<rlevel){
                    pc.remove(k--);
                    theta += hp2.theta;
                    r+=hp2.r;
                    s++;
                }
            }
            if(s<level)continue;
            theta /=s;
            r /=s;
            p.add(new HoughHistogramPoint(theta, r));
        }
        
        return p;
    }
    
    
    
    /**
     * サーチする段階から、周辺のヒストグラムを見た方がいいんじゃないかと実装したけど、いい結果は得られなかった也。
     * @param map
     * @param level
     * @param thetaLevel
     * @param rlevel
     * @return
     */
    public static ArrayList<HoughHistogramPoint> search(HoughHistogram map,int level,
            float thetaLevel,int rlevel){
        ArrayList<HoughHistogramPoint> points = new ArrayList<>();
        
        int w = map.width,h = map.height;
        int tlevel = (int) (thetaLevel*map.getThetaSize()/PI);
        int maxv = 0;
        for(int x = 0;x<w;x++)for(int y=0;y<h;y++){
            int v = 0;
            
            for(int i = -tlevel;i<tlevel;i++)for(int k=-rlevel;k<rlevel;k++){
                v+=map.getData(x+i, y+k);
            }
            
            if(v > level){
                points.add(map.convert(x, y));
            }
            if(maxv<v)maxv=v;
        }
        System.out.println("max "+maxv);
        return points;
    }

}
