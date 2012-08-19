package filter.convolution;

import static java.lang.Math.*;

/**
 * ユーティリティークラス
 *
 */
public class Convolutions{
    
    /**
     * ガウスフィルターを作成します
     * @param sigma 標準偏差
     * @return ガウスフィルターのConvolution
     */
    public static Convolution createGaussianFilter(float sigma){
        int w = (int)(sigma*3);
        int l = w*2+1;
        float[] m = new float[l];
        float s = (float)(1/sqrt(2*PI)/sigma);
        float div = 2*sigma*sigma;
        for(int i = -w;i<0;i++){
           double k = -i*i/div;
           double e = exp(k);
           float v = (float)(e*s);
           m[i+w]=m[w-i]=v;
        }
        m[w]=s;
        return new Convolution(m);
    }
    
    /**
     * 微分フィルターを作ります
     * @return
     */
    public static Convolution createDifferentialFilter(){
        return new Convolution(-1,0,1);
    }
    
    /**
     * 一階微分ガウスフィルタを作ります
     * @param sigma 標準偏差
     * @return
     */
    public static Convolution createDifferentialGaussianFilter(float sigma){
        int w = (int)(sigma*3);
        int l = w*2+1;
        float[] m = new float[l];
        float s = (float)(1/sqrt(2*PI)/sigma/sigma/sigma);
        float div = 2*sigma*sigma;
        for(int i = -w;i<0;i++){
           double k = -i*i/div;
           double e = exp(k);
           float v = (float)(e*s*-i);
           m[i+w]=v;
           m[w-i]=-v;
        }
        m[w]=0f;
        return new Convolution(m);
    }
    
    /**
     * 一階微分ガウスフィルタをガウスフィルタから作成します。<br>
     * EXPの計算をしないから計算コストが小さい。
     * @param sigma 標準偏差
     * @param gauss createGaussianFilterで同じsigmaの値で作成したConvolutionに限る
     * @return
     */
    public static Convolution createDifferentialGaussianFilter(float sigma,Convolution gauss){
        int w = (int)(sigma*3);
        int l = w*2+1;
        float[] m = new float[l];
        float s = (float)(1/sigma/sigma);
        for(int i = -w;i<0;i++){
           double e = gauss.getMapValue(i);
           float v = (float)(e*s*-i);
           m[i+w]=v;
           m[w-i]=-v;
        }
        m[w]=0f;
        return new Convolution(m);
    }
    
    /**
     * 二階微分ガウスフィルタを作ります
     * @param sigma 標準偏差
     * @return
     */
    public static Convolution createSecondOrderDifferentialGaussianFilter(float sigma){
        int w = (int)(sigma*3);
        int l = w*2+1;
        float[] m = new float[l];
        float ss = sigma*sigma;
        float s = (float)(1/sqrt(2*PI)/sigma/ss/ss);
        float div = 2*ss;
        for(int i = -w;i<0;i++){
           double k = -i*i/div;
           double e = exp(k);
           float v = (float)(e*s*(i*i-ss));
           m[i+w]=m[w-i]=v;
        }
        m[w]=-s*ss;
        return new Convolution(m);
    }
    
    /**
     * 二階微分ガウスフィルタをガウスフィルタから作成します。<br>
     * EXPの計算をしないから計算コストが小さい。
     * @param sigma 標準偏差
     * @param gauss createGaussianFilterで同じsigmaの値で作成したConvolutionに限る
     * @return
     */
    public static Convolution createSecondOrderDifferentialGaussianFilter(float sigma,Convolution gauss){
        int w = (int)(sigma*3);
        int l = w*2+1;
        float[] m = new float[l];
        float ss = sigma*sigma;
        float s = (float)(1/ss/ss);
        for(int i = -w;i<0;i++){
           double e = gauss.getMapValue(i);
           float v = (float)(e*s*(i*i-ss));
           m[i+w]=m[w-i]=v;
        }
        m[w]=-gauss.getMapValue(0)*s*ss;
        return new Convolution(m);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    private Convolutions(){}

}
