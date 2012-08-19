package data;

import static java.awt.image.BufferedImage.*;
import static java.lang.String.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * グレースケールの画素の集合を定義します
 */
public class Pixels{   
    private float[] pixels;
    private InterplationMethod im = InterplationMethod.NEAREST;
    public final int 
    /**画像の幅*/
    width,
    /**画像の高さ*/
    height;
    
    /**
     * dataを元データとした幅width,高さheightの画素の集合を定義します。
     * @param width 幅
     * @param height 高さ
     * @param data データ
     * @throws IllegalArgumentException データ長が幅×高さよりも小さい場合、もしくはdataがnull
     */
    public Pixels(int width,int height,float[] data)throws IllegalArgumentException{
        this.width = width;
        this.height = height;
        if(data==null )
            throw new IllegalArgumentException("data is null!");
        if(data.length < width*height)
            throw new IllegalArgumentException(format("data size[%d] != width[%d]*height[%d]",
                    data.length,width,height));
        pixels = new float[width*height];
        System.arraycopy(data, 0, pixels, 0, pixels.length);
    }
    
    /**
     * dataを元データとした幅width,高さheightの画素の集合を定義します。
     * @param width 幅
     * @param height 高さ
     * @param data データ
     * @throws IllegalArgumentException データ長が幅×高さよりも小さい場合、もしくはdataがnull
     */
    public Pixels(int width,int height,int[] data)throws IllegalArgumentException{
        this(width,height,convert(data));
    }
    /**
     * 画素の値が全て0の幅width,高さheightの画素の集合を定義します。
     * @param width 幅
     * @param height 高さ
     */
    public Pixels(int width,int height){
        this.width = width;
        this.height = height;
        pixels = new float[width*height];
    }
    
    /**
     * 全ての画素の値をvalue倍します。
     * @param value
     */
    public void multiple(float value){
        for (int i = 0; i < pixels.length; i++) {
             pixels[i] *=value;
        }
    }
    
    /**
     * 全ての画素の値にvalue加えます。
     * @param value
     */
    public void plus(float value){
        for (int i = 0; i < pixels.length; i++) {
             pixels[i] +=value;
        }
    }
    
    /**
     * 最小値が0になり、最大値が1になるように正規化します。<br>
     * 最小値と最大値が同じ時は、これらの値が0の時は0に、そうでないときは全て1になります。
     */
    public void normalize(){
        float max = pixels[0],min = max;
        for (int i = 0; i < pixels.length; i++) {
            if(max<pixels[i])max = pixels[i];
            else if(min > pixels[i])min = pixels[i];
        }
        float d = max-min;
        if(d==0f)
            for (int i = 0; i < pixels.length; i++)
                pixels[i] = max!=0?1f:0f;
        else
            for (int i = 0; i < pixels.length; i++)
                pixels[i] = (pixels[i]-min)/d;
    }
    
    /**
     * 範囲外の画素の値の補間方法を設定します。<br>
     * デフォルトではNEAREST。
     * @param i 補間方法　nullの時は何もしない
     */
    public void setInterpolationMethod(InterplationMethod i){
        if(i==null)return;
        im = i;
    }
    
    /**
     * 範囲外の画素の値の補間方法を返します。<br>
     * デフォルトではNEAREST。
     */
    public InterplationMethod getInterpolationMethod(){
        return im;
    }
    
    /**
     * 点(x,y)の画素の値を返します
     * @param x 座標
     * @param y 座標
     * @return 画素の値
     */
    public float getPixel(int x,int y){
        return im.getPixel(this, x, y);
    }
    
    /**
     * 点(x,y)の画素の値を設定します。範囲外の場合は何もしません。
     * @param x 座標
     * @param y 座標
     * @param value 設定する値
     */
    public void setPixel(int x,int y,float value){
        if(InterplationMethod.isOut(this, x, y))return;
        pixels[x+y*width] = value;
    }
    
    /**
     * このピクセルの各値が0～255の範囲で収まっている物と扱って、
     * グレースケールのBufferedImageを作成します。
     */
    public BufferedImage convertToImage(){
        BufferedImage img = new BufferedImage(width, height, TYPE_BYTE_GRAY);
        byte[] p = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < p.length; i++) {
            p[i] = (byte)pixels[i];
        }
        return img;
    }
    
    /**
     * BufferedImageからPixelsを生成します。
     * @param image 元画像
     * @return 生成したPixels
     * @throws IllegalArgumentException imageがnullの時
     */
    public static Pixels convertToPixels(BufferedImage image)throws IllegalArgumentException{
        if(image ==null)
            throw new IllegalArgumentException("image is null!");
        int w = image.getWidth(),h = image.getHeight();
        if(image.getType() != TYPE_BYTE_GRAY){
            BufferedImage i = new BufferedImage(w,h, TYPE_BYTE_GRAY);
            Graphics2D g = i.createGraphics();
            g.drawImage(image,0,0,null);
            g.dispose();
            return convertToPixels(i);
        }
        byte[] p = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        float[] pixels = new float[p.length];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (float)(p[i]&0xff);
        }
        return new Pixels(w, h, pixels);
    }
    
    private static float[] convert(int[] data){
        if(data==null)throw new IllegalArgumentException("data is null!");
        float[] ret = new float[data.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = data[i];
        }
        return ret;
    }
    
    /**
     * 範囲外の画素の補間方法の定義
     */
    public static enum InterplationMethod{
        /**0で補間*/
        Black,
        /**最も近い画素で補間*/
        NEAREST{
            @Override
            protected float _getPixel(Pixels p,int x,int y){
                if(x< 0)x = 0;
                else if(x>=p.width)x = p.width-1;
                if(y<0)y=0;
                else if(y >=p.height)y = p.height-1;
                return p.pixels[x+y*p.width];
            }
        },
        /**画像をループしている物として扱う*/
        LOOP{
            @Override
            protected float _getPixel(Pixels p,int x,int y){
                x = x%p.width;
                if(x < 0)x += p.width;
                y = y%p.height;
                if(y<0)y += p.height;
                return p.pixels[x+y*p.width];
            }
        },
        /**画像がミラーしている物として扱う*/
        MIRROR{
            @Override
            protected float _getPixel(Pixels p,int x,int y){
                x = x%(2*p.width);
                y = y%(2*p.height);
                if ( x < 0)x += 2*p.width;
                if (y < 0) y += 2*p.height;
                if(x >=p.width)x = 2*p.width-1-x;
                if(y >=p.height)y = 2*p.height-1-y;
                return p.pixels[x+y*p.width];
            }
        }
        ;
        private static boolean isOut(Pixels p,int x,int y){
            return x < 0 || x >=p.width || y < 0 || y>=p.height;
        }
        private float getPixel(Pixels p,int x,int y){
            if(isOut(p, x, y)){
                return _getPixel(p,x,y);
            }else return p.pixels[x+y*p.width];
        }
        protected float _getPixel(Pixels p,int x,int y){
            return 0;
        }
    }
}
