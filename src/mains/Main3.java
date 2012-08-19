package mains;

import java.awt.image.BufferedImage;
import java.io.IOException;

import data.Pixels;
import filter.convolution.LoGFileter;
import filter.convolution.ConvolutionFilter.Direction;

/**
 * LoGのテスト
 */
public class Main3{

    public static void main(String[] args)throws IOException{
        float sigma = 2f;//標準偏差
        
        //画像の読み込み
        Pixels p = Mains.readImage("test.jpg");
        
        //LoGフィルターを作成
        LoGFileter log = new LoGFileter(sigma);
        //x方向に微分
        Pixels conv =log.convolution(p, Direction.X);
        //y方向に微分する時はコメント解除
//        Pixels conv =log.convolution(p,Direction.Y);
        
        //画像に変換
        //差分フィルタはマイナスの結果が生成されるから、正規化する必要がある。
        conv.normalize();
        conv.multiple(255);//BufferedImageに変換するために255倍
        BufferedImage img = conv.convertToImage();
        
        //表示
        Mains.showImage(img);
    }
}
