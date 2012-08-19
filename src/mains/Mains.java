package mains;

import io.pnm.PGMImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import data.Pixels;

/**
 * 
 *画像を表示したり、保存したりを簡単にするためのユーティリティークラス
 */
public class Mains{

    /**
     * 画像を読み込んでPixelsに変換する。
     * @param path
     * @return
     * @throws IOException
     */
    public static Pixels readImage(String path)throws IOException{
        BufferedImage img = ImageIO.read(new File(path));
        return Pixels.convertToPixels(img);
    }
    
    /**
     * PGM画像を読み込んでPixelsに変換する。
     * @param path
     * @return
     * @throws IOException
     */
    public static Pixels readPGMImage(String path)throws IOException{
        BufferedImage img = PGMImageIO.readPGM(new File(path));
        return Pixels.convertToPixels(img);
    }
    
    /**
     * 画像を表示するだけ
     * @param img
     */
    public static void showImage(final Image img){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                JFrame f = new JFrame("Show Image");
                f.add(new JLabel(new ImageIcon(img)));
                f.pack();
                f.setDefaultCloseOperation(3);
                f.setVisible(true);
            }
        });
    }
    
    /**
     * 画像を保存するだけ。
     * @param img
     * @param path
     */
    public static void saveImage(BufferedImage img,String path){
        File f = new File(path);
        int p = path.lastIndexOf(".");
        String ex = path.substring(p+1);
        try {
            ImageIO.write(img, ex, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Mains(){}
}
