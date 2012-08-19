package mains;

import static java.awt.image.BufferedImage.*;
import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

import data.BitData;
import data.IntDatas;
import data.Pixels;
import filter.DistanceTransform;

/**
 * 
 * 遺伝的アルゴリズムのテスト
 *
 */
public class Main6{
    
    //探索する図形
    static final int[][] polygon1 //三角形ポリゴン
    ={{220,140,200},{320,340,210}};
   
    //DDA変換
    private static Point[] lineToPoints(int x0,int y0,int x1,int y1){
       int w = x1-x0;
       int h = y1-y0;
       Point[] ret;
       int xto = (w>>31)|1;//w>0 -> 1   w < 0 -> -1
       int yto = (h>>31)|1;
       w *= xto;//正数化
       h *= yto;
       if(w>h){//x方向に長い場合
           ret = new Point[w];
           int hweight = 0;
           for(int x = x0,i=0,y=y0;x!=x1;x+=xto,i++){
               hweight +=h;
               if(hweight>w){
                   y+=yto;
                   hweight -=w;
               }
               ret[i] = new Point(x,y);
           }
       }else{
           ret = new Point[h];
           int wweight = 0;
           for(int y = y0,i=0,x=x0;y!=y1;y+=yto,i++){
               wweight +=w;
               if(wweight>h){
                   x+=xto;
                   wweight-=h;
               }
               ret[i] = new Point(x,y);
           } 
       }
       return ret;
    }
    
    //図形をアフィン変換する
    private static void transformPolygon(int mx,int my ,double zoom,double theta,int[][] polygon){
        //重心を求める
        double wx=0,wy=0;
        int n = polygon[0].length;
        for(int i=0;i<n;i++){
            wx += polygon[0][i];
            wy += polygon[1][i];
        }
        wx/=n;
        wy/=n;
        double sin = sin(theta);
        double cos = cos(theta);
        for(int i=0;i<n;i++){
            double x = (polygon[0][i]-wx)*zoom;
            double y = (polygon[1][i]-wy)*zoom;
            polygon[0][i] = (int)(cos*x-sin*y+ wx+mx);
            polygon[1][i] = (int)(sin*x+cos*y +wy+my);
        }
    }
    
  //図形をアフィン変換する
    private static void transformPolygon(double zoom,double theta,int[][] polygon){
        //重心を求める
        double wx=0,wy=0;
        int n = polygon[0].length;
        for(int i=0;i<n;i++){
            wx += polygon[0][i];
            wy += polygon[1][i];
        }
        wx/=n;
        wy/=n;
        double sin = sin(theta);
        double cos = cos(theta);
        for(int i=0;i<n;i++){
            double x = (polygon[0][i]-wx)*zoom;
            double y = (polygon[1][i]-wy)*zoom;
            polygon[0][i] = (int)(cos*x-sin*y);
            polygon[1][i] = (int)(sin*x+cos*y);
        }
    }
    
    //ディープコピーの作成
    private static int[][] copy(int[][] i){
        int[][] ret = new int[i.length][];
        for(int k=0;k<i.length;k++){
            ret[k] = i[k].clone();
        }
        return ret;
    }
    
    //遺伝子情報に従って図形をアフィン変換する
    //移動の原点はマップ（画像）の中心とする
    private static int[][] transformPolygon(BitData gene,int[][] originalPolygon
            ,int mapWidth,int mapHeight){
        int[][] ret = copy(originalPolygon);
        long xmov = gene.getValue(0, 8);
        long ymov = gene.getValue(8,8+8);
        long ro = gene.getValue(8+8,8+8+5);
        long zo = gene.getValue(8+8+5,8+8+5+5); 
        
        int xm =xmov > 127? (int)(-1L << 8 | xmov) : (int)xmov;
        int ym =ymov > 127? (int)(-1L << 8 | ymov) : (int)ymov;
        double theta = 2*PI*ro/32;
        double zoom = zo/16d+1;
        transformPolygon(xm+mapWidth/2, ym+mapHeight/2, zoom, theta, ret);
        return ret;
    }
    
    //各点の平均距離をスコアとする
    private static double score(int[][] polygon,IntDatas distanceMap){
        double score = 0;
        int s=0;
        for(int i=0;i<polygon[0].length;i++){
            int t = i+1;
            if(t==polygon[0].length)t = 0;
            Point[] pp = lineToPoints(polygon[0][i], polygon[1][i], polygon[0][t], polygon[1][t]);
            for(Point p:pp){
                int x = p.x,y = p.y;
                if(distanceMap.isOut(x, y))continue;
                score += distanceMap.getData(x, y);
                s++;
            }
        }
        if(s==0)return 0;
        return score/s;
    }
    
    //２点交差オペレータで新たな遺伝子を作成する。　rは各遺伝子が突然変異する確率
    private static void createNewGeneration(double r,BitData src1,BitData src2,BitData dst1,BitData dst2){
        int a = rand.nextInt(bitLength),b = rand.nextInt(bitLength);//0～bitLength-1の乱数を生成
        if(a == b){
            a= b!=5? 5:20;//テキトー
        }
        int s = min(a, b),e = max(a,b);
        long bit1 = src1.getValue();
        long bit2 = src2.getValue();
        
        dst1.setValue(s, e, bit2);
        dst2.setValue(s,e,bit1);
        
        //確率rにしたがって遺伝子を突然遠位させる。
        for(int i=0;i<bitLength;i++){
            double n = rand.nextDouble();
            if(n < r){
                dst1.negative(i);
            }
            n = rand.nextDouble();
            if(n < r){
                dst2.negative(i);
            }
        }
    }
    
    //遺伝子の長さ
    //回転は32方向の5bit,移動は-128px～127pxの8bit、拡大は1/16倍単位で2倍までの5bitで表現する。
    //順に0bit側から、x方向移動(8bit)、y方向移動(8bit)、回転(5bit)、拡大(5bit)の計26bitを用いて遺伝子を表現する
    static final int bitLength = 8+8+5+5;
//            +5;
    static final Random rand = new Random(System.currentTimeMillis());
    
    //面倒くさいのでソートはJavaAPIに任せませう
    private static class SortObject implements Comparable<SortObject>{
        BitData gene;
        double score;
        public int compareTo(SortObject o) {
            double k = o.score-score;//大きいものを前に、小さいものを後ろにする。
            return k > 0 ? 1:k < 0? -1:0;
        }
    }
    
    private static SortObject[] initData(IntDatas distancemap,int[][] polygonpoints,int populationSize){
        SortObject[] genes = new SortObject[populationSize];
        for(int i=0;i<populationSize;i++){//ランダムに第一世代を作成
            genes[i] = new SortObject();
            genes[i].gene = new BitData(bitLength,rand.nextLong());
            int[][] polygon = transformPolygon(genes[i].gene, polygonpoints,
                    distancemap.width, distancemap.height);
            genes[i].score = score(polygon, distancemap);
        }
        
        Arrays.sort(genes);
        return genes;
    }
    
    /**
     * 
     * @param distancemap
     * @param polygonpoints 元もなる図形
     * @param populationSize 個体数
     * @param r 突然変異の確立
     * 
     */
    private static void ga(SortObject[] genes,IntDatas distancemap,int[][] polygonpoints,double r){
        
        int populationSize = genes.length;
        
        int out = populationSize/3;
        //上位の親3分の１を残し、残りをすべて新しい子に置き換える。
        for(int p = 0;p<out;p++){
            //親の決定
            BitData m = genes[rand.nextInt(out)].gene,f = genes[rand.nextInt(out)].gene;
            int c1 = populationSize-1-p*2,c2 = populationSize-2-p*2;
            BitData bc1 = genes[c1].gene,bc2 = genes[c2].gene;
            createNewGeneration(r, m, f, bc1, bc2);
        }
        
        for(int i=out;i<populationSize;i++){//各子供のスコアを計算
            int[][] polygon = transformPolygon(genes[i].gene, polygonpoints,
                    distancemap.width, distancemap.height);
            genes[i].score = score(polygon, distancemap);
        }
        Arrays.sort(genes);
        

        
        
        
    }
    
    private static void drawPolygon(Graphics2D g,SortObject s,int w,int h,int[][] pol){
        int[][] draw = transformPolygon(s.gene, pol, w,h);
        Polygon polygon = new Polygon(draw[0], draw[1], draw[0].length);
        g.drawPolygon(polygon);
    }
    
    
    
    /**
     * 
     * @param args 第一引数が-iの時、テスト用画像を作成します。<br>
     * 遺伝的アルゴリズムの実行：srcfile <br>
     * テスト画像の作成：-i width height noise filename [-p vertexs x y [x y...]]
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException{

        if(args.length!=0 && args[0].equals("-i")){
            createMode(args);
            return;
        }
        
        BufferedImage img = ImageIO.read(new File(args[0]));
        Pixels p = Pixels.convertToPixels(img);
        int w = p.width,h = p.height;
        DistanceTransform dist = new DistanceTransform(50);
        IntDatas d = dist.transform(p);//距離画像
        
//        BufferedImage simg = d.toImage();
        
        int[][] pol = copy(polygon1);
        transformPolygon(0.66, 6*2*PI/32, pol);//そのままじゃつまらないので変換する
        
        SortObject[] genes = initData(d, pol, 100);
        
        BufferedImage simg = new BufferedImage(p.width, p.height, TYPE_INT_RGB);
        Graphics2D g = simg.createGraphics();
        g.drawImage(img,0,0,null);

        
        int loop = 500;
        for(int i=0;i<loop;i++){
            
            ga(genes,d, pol, 0.1);
            
            if((i+1)%25==0){
                int alpha = (i+1)*255/loop;
                int red = (i+1)*255/loop;
                int blue = (loop-i-1)*255/loop;
                Color c = new Color(alpha<<24|red << 16 | blue);
                g.setColor(c);
                drawPolygon(g, genes[0], w, h, pol);
            }
        }
        
//        g.setColor(new Color(0xffff0000,true));
//        int[][] draw = transformPolygon(ret[0], pol, p.width, p.height);
//        System.out.println(Arrays.toString(draw[0])+Arrays.toString(draw[1]));
//        Polygon polygon = new Polygon(draw[0], draw[1], draw[0].length);
//        g.drawPolygon(polygon);
        g.dispose();
        
        Mains.showImage(simg);
        
    }
    
    
    
    //------------------------------------------------------------
    //----------------Test用画像の作成----------------------------
    //------------------------------------------------------------
    static final int[][] polygon2 //四角形ポリゴン
    ={{100,190,150,40},{150,180,250,200}};
    static final int[][] polygon3 //四角形ポリゴンその二
    ={{50,100,150,60},{80,20,120,180}};
    
    
    
    /**
     * テストに用いる画像を作成します。
     * @param width 画像幅
     * @param height 画像高さ
     * @param polygons 描画する図形
     * @param noise 発生するノイズの割合(0～1）
     * @param filename 保存するファイルの名前。ファイル形式は自動でpng
     */
    public static void createTestingImage(int width,int height,Polygon[] polygons,double noise,
            String filename){
        BufferedImage img = new BufferedImage(width,height,TYPE_BYTE_GRAY);
        Graphics2D g  = img.createGraphics();
        
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.white);
        for(Polygon p:polygons){
            g.drawPolygon(p);
        }
        
        int noiseNumber = (int)(width*height*noise);
        Random r = new Random(System.currentTimeMillis());
        for(int i=0;i<noiseNumber;i++){
            int x =r.nextInt(width);
            int y =r.nextInt(height);
            g.drawRect(x, y, 0, 0);
        }
        g.dispose();
        
        try {
            ImageIO.write(img, "png", new File(filename+".png"));
            System.out.println(filename+".png output.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * テスト用画像を作る処理が思いの外長くなったので分離
     * @param args
     */
    public static void createMode(String[] args){
        int w,h;double noise;String filename;
        try{
            w = Integer.parseInt(args[1]);
            h = Integer.parseInt(args[2]);
            noise = Double.parseDouble(args[3]);
            filename = args[4];
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("-i ImageWidth ImageHeight Noise Filename");
            System.exit(1);
            return;//コンパイラがw,hとかが初期化されていないとエラーとはいたため
        }
        Polygon[] pls;
        
        if(args.length >5){
            ArrayList<Polygon> pol = new ArrayList<>();
            try{
                for(int i=5;i<args.length;i++){
                    if(args[i++]!="-p")break;
                    int s=Integer.parseInt(args[i++]);
                    int[] xpoint=new int[s],
                            ypoint=new int[s];
                    for(int k=i+s*2,t=0;i<k;t++){
                        int x = Integer.parseInt(args[i++]);
                        int y = Integer.parseInt(args[i++]);
                        xpoint[t] = x;
                        ypoint[t] = y;
                    }
                    pol.add(new Polygon(xpoint, ypoint, s));
                }
            }catch (Exception e) {
            // TODO: handle exception
            }
            pls = pol.toArray(new Polygon[pol.size()]);
        }else {
            pls = new Polygon[]
                    {new Polygon(polygon1[0],polygon1[1], 3),
                    new Polygon(polygon2[0],polygon2[1], 4),
                    new Polygon(polygon3[0],polygon3[1], 4)};
        }
        
        createTestingImage(w, h, pls, noise, filename);
    }

}
