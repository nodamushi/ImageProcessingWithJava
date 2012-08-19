package filter;

import data.Pixels;
import filter.convolution.Convolution;
import filter.convolution.ConvolutionFilter.Direction;
import static filter.convolution.Convolutions.*;
import static java.lang.Math.*;

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
            Lvv = (Lx2*Lxx+2*Lx*Ly*Lxy+Ly2*Lyy)/(Lxx*Lxx+Lyy*Lyy);
            
            lvv.setPixel(x, y, Lvv);
            mag.setPixel(x, y, ma);
            
            //勾配方向を計算（45度刻みで離散化)
            //方向の番号の振り方↓
            //321
            //4_0
            //567
            
            if(abs(Lx)<0.00001f){
                if(Ly>0)
                    dir[x+y*w]=2;
                else
                    dir[x+y*w]=6;
            }else{
                float tan = Ly/Lx;
                float atan = (float)(atan(tan)/PI/2);//-0.25～0.25
                if(Lx < 0){
                    atan = 0.5f-atan;//0.25～0.75
                }
                if(atan < 0)atan = 1+atan;//0.75～1
                atan += 1f/8f;
                if(atan >= 1f)atan-=1f;
                
                int d = (int)(atan*8);
                dir[x+y*w]=d;
            }
        }
        //画素の周辺のピクセルに対する処理を
        //ループで一発で記述するためによく使う手段
        final int[] dirx = {1,1,0,-1,-1,-1,0,1},diry = {0,1,1,1,0,-1,-1,-1};
        for(int x=0;x<w;x++)for(int y=0;y<h;y++){
            float set = 0;
            float ma = mag.getPixel(x, y);
            //ある程度以上の変化量の時のみ処理する。
            if(ma > mlevel){
                //勾配方向に0交差判定
                int i= dir[x+y*w];
                float 
                f = lvv.getPixel(x+dirx[i], y+diry[i]),
                f2=lvv.getPixel(x, y);
                if(f*f2<0) set=1;//0交差判定
            }
            dst.setPixel(x, y, set);
        }
        
        return dst;
    }

}
