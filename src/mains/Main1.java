package mains;

import java.awt.image.BufferedImage;
import java.io.IOException;

import data.Pixels;
import filter.convolution.Convolution;
import filter.convolution.Convolutions;
import filter.convolution.ConvolutionFilter.Direction;

/**
 *ガウスフィルターをかけるテスト
 */
public class Main1{
    
    public static void main(String[] args)throws IOException{
        float sigma = 10f;//標準偏差
        
        //画像の読み込み
        Pixels p = Mains.readImage("rena.jpg");
        
        //ガウスフィルターを作成
        Convolution gaussian = Convolutions.createGaussianFilter(sigma);
        //x方向に畳み込み（分離可能）
        Pixels xconv =gaussian.convolution(p, Direction.X);
        //y方向に畳み込み（分離可能）
        Pixels conv =gaussian.convolution(xconv,Direction.Y);
        
        //画像に変換
        BufferedImage img = conv.convertToImage();
        
        //表示
        Mains.showImage(img);
    }

}
