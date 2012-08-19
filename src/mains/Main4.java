package mains;

import java.awt.image.BufferedImage;
import java.io.IOException;

import data.Pixels;
import filter.CanyFilter;
import filter.convolution.LoGFilter;
import filter.convolution.ConvolutionFilter.Direction;

/**
 * Cany法のテスト 
 *
 */
public class Main4{

    public static void main(String[] args)throws IOException{
        float sigma = 5f;
        
        Pixels p = Mains.readPGMImage("car4s.pgm");
        
        CanyFilter cany = new CanyFilter(sigma);
        Pixels ret = cany.filter(p);
        
        ret.multiple(255);//値が0か1になっているので255倍して画像に変換
        BufferedImage img = ret.convertToImage();
        
        Mains.showImage(img);
        
        
        
     
    }
}
