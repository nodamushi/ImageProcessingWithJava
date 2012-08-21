package filter;

import static filter.convolution.Convolutions.*;
import static java.lang.Math.*;
import data.Pixels;
import filter.convolution.Convolution;
import filter.convolution.ConvolutionFilter.Direction;

public class CanyFilter{
    
    private Convolution
    gaus,//ガウスフィルタ
    difgaus,//一階微分ガウスフィルタ
    dif2gaus;//二階微分ガウスフィルタ
    
    private float mlevel=3;
    public CanyFilter(float sigma){
        gaus = createGaussianFilter(sigma);
        difgaus = createDifferentialGaussianFilter(sigma,gaus);
        dif2gaus = createSecondOrderDifferentialGaussianFilter(sigma,gaus);
    }
    
    /**
     * エッジと見なすための変化の閾値を設定します。
     * @param f
     */
    public void setMagnitudeLevel(float f){
        mlevel = f;
    }
    
    /**
     * エッジであれば1、そうでなければ0の値を画素に設定したPixelsを返します。
     * @param src
     * @return
     */
    public Pixels filter(Pixels src){
        return filter(src,new Pixels(src.width,src.height));
    }
    
    /**
     * エッジであれば1、そうでなければ0の値をdstの画素に設定します。
     * @param src 元データ
     * @param dst 書き込み先
     * @return dstと同じオブジェク
     *  @throws IllegalArgumentException dstのサイズがsrcより小さい場合
     */
    public Pixels filter(Pixels src,Pixels dst)throws IllegalArgumentException{
        int w = src.width,h=src.height;
        
        if(dst.width<w || dst.height<h){
            throw new IllegalArgumentException("dst size error!");
        }
        Pixels tmp = new Pixels(w,h);
        difgaus.convolution(src,tmp, Direction.X);
        
        Pixels
        lx = gaus.convolution(tmp, Direction.Y),
        lxy = dif2gaus.convolution(tmp, Direction.Y),
        ly = gaus.convolution(difgaus.convolution(src,tmp,Direction.Y), Direction.X),
        lxx = gaus.convolution(dif2gaus.convolution(src, tmp, Direction.X), Direction.Y),
        lyy = gaus.convolution(dif2gaus.convolution(src, tmp, Direction.Y), Direction.X),
        lvv = new Pixels(w,h),
        mag = tmp;//名前をわかりやすくして再利用
        int[] dir = new int[w*h];
        
        for(int x=0;x<w;x++)for(int y=0;y<h;y++){
            float
            Lx = lx.getPixel(x,y),
            Ly = ly.getPixel(x,y),
            Lxx = lxx.getPixel(x,y),
            Lyy = lyy.getPixel(x,y),
            Lxy = lxy.getPixel(x,y),
            Lx2 = Lx*Lx, Ly2 = Ly*Ly,
            ma = Lx2+Ly2,//変化量の大きさを表す
            Lvv = (Lx2*Lxx+2*Lx*Ly*Lxy+Ly2*Lyy);

            
            lvv.setPixel(x, y, Lvv);
            mag.setPixel(x, y, ma);
            
            //勾配方向を計算（45度刻みで離散化)
            dir[x+y*w] = getOrient(Lx, Ly);
        }
        
        for(int x=0;x<w;x++)for(int y=0;y<h;y++){
            float set = 0;
            float ma = mag.getPixel(x, y);
            //ある程度以上の変化量の時のみ処理する。
            if(ma > mlevel*mlevel){
                //勾配方向に0交差判定
                int i= dir[x+y*w];
                
                float 
                f = lvv.getPixel(x-dirx[i], y-diry[i]),
                f2=lvv.getPixel(x, y);
                if(f*f2<0) set=1;//0交差判定
            }
            dst.setPixel(x, y, set);
        }
      
        return dst;
    }

    //下のgetOrientで得られる領域の方向を表す
    private static final int[] 
            dirx = {1,1,0,-1,-1,-1,0,1},
            diry = {0,1,1,1,0,-1,-1,-1};
    
    /**
     * 座標(x,y)が以下のような領域のどの領域に入るのかを返します。<br>
     * 
     * ↑y　<br>
     * 　→x<br>
     * <table border ="1">
     * <tr><td>3</td><td>2</td><td>1</td></tr>
     * <tr><td>4</td><td>●</td><td>0</td></tr>
     * <tr><td>5</td><td>6</td><td>7</td></tr>
     * </table>
     * ●：原点
     * @param x 座標
     * @param y 座標
     * @return 0～7
     */
    public static int getOrient(double x,double y){
        if(abs(x) < 0.00001){//0除算と発散回避
            if(y > 0)return 2;
            return 6;
        }else{
            double tan = y/x;
            double atan = (atan(tan)/PI/2);//-0.25～0.25
            if(x < 0){
                atan = 0.5f+atan;//0.25～0.75
            }
            if(atan < 0)atan = 1+atan;//0.75～1
            atan += 0.25f/4f;//修正
            if(atan >= 1f)atan-=1f;
            
            return (int)(atan*8);
        }
    }
    
}
