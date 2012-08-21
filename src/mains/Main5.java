package mains;

import static java.awt.image.BufferedImage.*;
import static java.lang.Math.*;
import io.pnm.PGMImageIO;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import data.HoughHistogram;
import data.HoughHistogram.HoughHistogramPoint;
import data.Pixels;
import filter.CanyFilter;
import filter.HoughTransform;

/**
 *Hough変換のテスト
 */
public class Main5{

    
    public static void main(String[] args)throws IOException{
        //画像の読み込み
        BufferedImage original = PGMImageIO.readPGM(new File("w3.pgm"));
        Pixels p = Pixels.convertToPixels(original);
        
        //Cany法で輪郭線の抽出
        CanyFilter canyf = new CanyFilter(3);
        Pixels cany = canyf.filter(p);
        
        //Cany法で得たマップをHough変換してグラフを作成
        HoughTransform hough = new HoughTransform(360);
        HoughHistogram map = hough.hough(cany);
        
        //値がlevel以上の点を抽出
        int level = 40;
        ArrayList<HoughHistogramPoint> points = HoughTransform.search(map, level);
        
        //抽出した点をまとめます。
        float thetaLevel = (float)PI/180 * 4;//±4度まで誤差を許可
        int rLevel = 15;//±15まで誤差を許可
        int sLevel=2;//2つ以上まとまらなかった場合は除外する。
        ArrayList<HoughHistogramPoint> reduce = HoughTransform.reduce(points, thetaLevel,rLevel,sLevel);
        
        System.out.printf(
                "抽出した点の数\t：%d\nまとめた後の点の数\t：%d\n",
                points.size(),reduce.size());
        
        //見やすく画像加工
        BufferedImage img = new BufferedImage(p.width, p.height, TYPE_INT_RGB);
        Graphics2D  g = img.createGraphics();
        g.drawImage(original,0,0,null);
        
        //抽出した線を薄い緑色で描きます。
        g.setColor(new Color(0x3333ff66,true));
        for(HoughHistogramPoint hp:points){
            drawLine(g, hp.theta, hp.r, p.width, p.height);
        }
        
        //まとめた線を赤い色で描きます
        g.setColor(new Color(0xccff0000,true));
        for(HoughHistogramPoint hp:reduce){
            drawLine(g, hp.theta, hp.r, p.width, p.height);
        }
        g.dispose();
        
        //画像を表示
        Mains.showImage(img);
        
//        Mains.saveImage(img, "F:\\Dropbox\\gazoukaisekiron\\reports\\no6\\ret.png");
        
    }
    
    
    
    //x*cos+y*sin = rの直線を引きます
    private static void drawLine(Graphics2D g,float theta,int r,int w,int h){
        double sint = sin(theta);
        double cost = cos(theta);
        if(abs(sint)<0.0001f){//0除算回避
            double x0 = r/cost;//y=0での座標
            double x1 = (r-h*sint)/cost;//y=hでの座標
            g.drawLine((int)x0,0,(int)x1,h);
        }else{
            double y0 = r/sint;//x=0での座標
            double y1 = (r-cost*w)/sint;//x=wでの座標
            g.drawLine(0, (int)y0, w, (int)y1);
        }
    }
    
    
}
