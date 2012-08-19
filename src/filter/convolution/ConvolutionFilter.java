package filter.convolution;

import data.Pixels;

public interface ConvolutionFilter{
    public static enum Direction{
        /**x方向の畳み込み*/X,
        /**y方向の畳み込み*/Y
    }
    /**
     * srcに対してd方向に畳み込みを行います。
     * @param src 元データ
     * @param d たたみ込む方向
     * @return たたみ込んだ結果
     */
    public Pixels convolution(Pixels src,Direction d);
    
    /**
     * srcに対してd方向に畳み込みを行います。
     * @param src 元データ
     * @param dst 書き込み先
     * @param d たたみ込む方向
     * @return たたみ込んだ結果（dst）
     * @throws IllegalArgumentException dstのサイズがsrcより小さい場合
     */
    public Pixels convolution(Pixels src,Pixels dst,Direction d)
            throws IllegalArgumentException;
    
}
