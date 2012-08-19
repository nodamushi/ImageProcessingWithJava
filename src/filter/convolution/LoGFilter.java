package filter.convolution;

import data.Pixels;

public class LoGFilter implements ConvolutionFilter{
    
    private Convolution
    difgau,//一階微分
    gau;//ガウスフィルタ
    public LoGFilter(float sigma){
        gau = Convolutions.createGaussianFilter(sigma);
        difgau = Convolutions.createDifferentialGaussianFilter(sigma);
    }

    @Override
    public Pixels convolution(Pixels src ,Direction d){
        return convolution(src, new Pixels(src.width, src.height), d);
    }

    @Override
    public Pixels convolution(Pixels src ,Pixels dst ,Direction d)
            throws IllegalArgumentException{
        int w = src.width,h = src.height;
        if(dst.width<w || dst.height<h){
            throw new IllegalArgumentException("dst size error!");
        }
        Pixels tmp = new Pixels(w,h);
        
        if(d==Direction.X){
            difgau.convolution(src, tmp,Direction.X);
            gau.convolution(tmp, dst, Direction.Y);
        }else{
            gau.convolution(src, tmp,Direction.X);
            difgau.convolution(tmp, dst, Direction.Y);
        }
        return dst;
    }
    
    /**
     * x,y方向両方に二階微分
     * @param src
     * @param dst
     * @return
     */
    public Pixels convolution(Pixels src,Pixels dst)throws IllegalArgumentException{
        int w = src.width,h = src.height;
        if(dst.width<w || dst.height<h){
            throw new IllegalArgumentException("dst size error!");
        }
        Pixels tmp = new Pixels(w,h);
        
        difgau.convolution(src, tmp,Direction.X);
        difgau.convolution(tmp, dst, Direction.Y);
        return dst;
    }
    
    /**
     * x,y方向両方に二階微分
     * @param src
     * @return
     */
    public Pixels convolution(Pixels src){
        return convolution(src, new Pixels(src.width, src.height));
    }
}
