package mains;

import java.awt.image.BufferedImage;
import java.io.IOException;

import data.Pixels;
import filter.convolution.Convolution;
import filter.convolution.Convolutions;
import filter.convolution.ConvolutionFilter.Direction;

/**
 *微分フィルターをかけるテスト
 */
public class Main2{

    public static void main(String[] args)throws IOException{
        //画像の読み込み
        Pixels p = Mains.readImage("test.jpg");

        //微分フィルターを作成
        Convolution gaussian = Convolutions.createDifferentialFilter();
        //x方向に畳み込み
        Pixels conv =gaussian.convolution(p, Direction.X);
        //y方向に畳み込み
//      Pixels conv =gaussian.convolution(p,Direction.Y);

        //画像に変換
        //差分フィルタはマイナスの結果が生成されるから、正規化する必要がある。
        conv.normalize();
        conv.multiple(255);//BufferedImageに変換するために255倍
        BufferedImage img = conv.convertToImage();

        //表示
        Mains.showImage(img);
    }

}
