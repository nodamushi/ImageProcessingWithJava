package filter.convolution;

import data.Pixels;

/**
 * 一次元の畳み込み。<br>
 * フィルターの中心はwidth/2です。
 */
public class Convolution implements ConvolutionFilter{
    
    
    private float[] map;
    private float mapsum;
    private int width;
    boolean isNormalization=false;
   
    /**
     * 一次元の畳み込みを定義します。
     * @param mapdata 畳み込みのフィルターのマップ。長さが1以上
     * @throws IllegalArgumentException マップの長さが0である時
     */
    public Convolution(float... mapdata)throws IllegalArgumentException{
      map = mapdata;
      width = map.length;
      s = -width/2;
      e = s+width;
      for(float f:mapdata){
          mapsum+=f;
      }
      if(width == 0)throw new IllegalArgumentException("filter size is 0.");
    }
    
    //Convolutionsから取り出せるように
    float getMapValue(int p){
        return map[p-s];
    }
    
    /**
     * 畳み込みをしたときに、和をマップの総和で割るかどうか。デフォルトではfalse
     * @param t
     */
    public void setNormalization(boolean t){
        isNormalization = t;
    }
    
    /**
     * 畳み込みをしたときに、和をマップの総和で割るかどうか。デフォルトではfalse
     */
    public boolean isNormalization(){
        return isNormalization;
    }
    
    private int s,e;//畳み込みをする際の、ループの初期値と終了判定値
    @Override
    public Pixels convolution(Pixels src,Pixels dst,Direction d)throws IllegalArgumentException{
        int w = src.width,h = src.height;
        if(dst.width<w || dst.height<h){
            throw new IllegalArgumentException("dst size error!");
        }
        int xx,yy;
        switch (d) {
            case X:
                xx=1;
                yy=0;
                break;
            default:
                xx=0;
                yy=1;
                break;
        }
        for(int x = 0;x<w;x++)for(int y=0;y<h;y++){
            float sum=0;
            for(int k=s;k<e;k++){
                float p = src.getPixel(x+k*xx, y+k*yy);
                float m = map[k-s];
                sum += p*m;
            }
            if(isNormalization)sum/=mapsum;
            dst.setPixel(x, y, sum);
        }
        
        return dst;
    }
    
    @Override
    public Pixels convolution(Pixels src,Direction d){
        return convolution(src, new Pixels(src.width,src.height), d);
    }

}
