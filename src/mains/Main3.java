package mains;

import java.awt.image.BufferedImage;
import java.io.IOException;

import data.Pixels;
import filter.convolution.LoGFilter;
import filter.convolution.ConvolutionFilter.Direction;

/**
 * LoGのテスト
 */
public class Main3{

    public static void main(String[] args)throws IOException{
        float sigma = 2f;//標準偏差
        
        //画像の読み込み
        Pixels p = Mains.readPGMImage("car4s.pgm");
        
        //LoGフィルターを作成
        LoGFilter log = new LoGFilter(sigma);
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

        
        //xy方向両方に微分　試すときはコメント解除
//        Pixels conv =log.convolution(p);
        //ゼロ交差点をx方向とy方向で検出
//        Pixels ret = new Pixels(p.width, p.height);//エッジ画像を出力する領域
//        for(int x=0;x<p.width-1;x++)for(int y=0;y<p.height;y++){
//            float v1 = conv.getPixel(x, y),v2 = conv.getPixel(x+1, y);
//            if(v1*v2 < 0)ret.setPixel(x, y, 255);
//        }
//        for(int x=0;x<p.width;x++)for(int y=0;y<p.height-1;y++){
//            float v1 = conv.getPixel(x, y),v2 = conv.getPixel(x, y+1);
//            if(v1*v2 < 0)ret.setPixel(x, y, 255);
//        }
        
        
        
        
    }
}
