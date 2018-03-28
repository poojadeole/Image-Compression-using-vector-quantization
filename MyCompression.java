/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mycompression;

/**
 *
 * @author poojadeole
 */
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.*;

public class MyCompression {

    /**
     * @param args the command line arguments
     */
    JFrame frame;
  //  JFrame frame2\;
    JLabel lbIm1;
    JLabel lbIm2;
    BufferedImage img1;
    BufferedImage img2;
    BufferedImage img3;
    BufferedImage img4;
    public ArrayList<MyVector> vectors = new ArrayList<MyVector>();
    public ArrayList<CodeWord> codewords = new ArrayList<CodeWord>();
    public HashMap<CodeWord, ArrayList<MyVector>> hmap = new HashMap<CodeWord, ArrayList<MyVector>>();
    ArrayList<MyVector> arr = new ArrayList<MyVector>();
    
    public ArrayList<MyVectorRGB> vectorsrgb = new ArrayList<MyVectorRGB>();
   
    public ArrayList<CodeWordRGB> codewordsrgb = new ArrayList<CodeWordRGB>();
  //   public ArrayList<Pixel> codewordsrgby = new ArrayList<Pixel>();
    public HashMap<CodeWordRGB, ArrayList<MyVectorRGB>> hmaprgb = new HashMap<CodeWordRGB, ArrayList<MyVectorRGB>>();
    ArrayList<MyVectorRGB> arrrgb = new ArrayList<MyVectorRGB>();
        public void showImsraw(String[] args) {
    
        int N = Integer.parseInt(args[1]);
        int maxval = Integer.MIN_VALUE;
        int dist = 0;
        int mindist = 0;
        

        CodeWord codewordtoput = null;
        ArrayList<CodeWord> codewords1 = new ArrayList<CodeWord>();
        ArrayList<CodeWord> codewords2 = new ArrayList<CodeWord>();

        if (!powerOfTwo(N)) {
            System.exit(0);
        }
        img1 = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        img2 = new BufferedImage(352,288, BufferedImage.TYPE_INT_RGB);
         
        try {
            File file = new File(args[0]);
            InputStream is = new FileInputStream(file);
            boolean converged = false;

            long len = file.length();
            byte[] bytes = new byte[(int) len];
            byte[] bytesarr = new byte[(int) len];

            System.out.println("file length " + len);
            int p = 0, q = 0;

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

int ind = 0;
for(int y = 0; y < 288; y++){
for(int x = 0; x < 352; x++){

byte a = 0;
byte r =bytes[ind];
//byte g = bytes[ind+height*width];
//byte b = bytes[ind+height*width*2];
                                     
int pix = 0xff000000 | ((r & 0xff) << 16) | ((r & 0xff) << 8) | (r & 0xff);

img1.setRGB(x,y,pix);
                                      
ind++;
}
}
            for (int i = 0; i < bytes.length; i = i + 2) {
                p = ByteToInt(bytes[i]);
                q = ByteToInt(bytes[i + 1]);
                MyVector vector = new MyVector(p, q);
                vectors.add(vector);
            }

            codewords2 = makeCodeBook(N);

//                      for(int y = 0; y < codewords1.size() ; y++){
//            System.out.println("codes x "+codewords1.get(y).x);
//            System.out.println("codes y "+codewords1.get(y).y);
//        }
            while (!converged) {
                System.out.println("inside this here loop");

                codewords1 = codewords2;

                for (int i = 0; i < vectors.size(); i++) {

                    mindist = Integer.MAX_VALUE;
                    for (int j = 0; j < codewords1.size(); j++) {
                        dist = calculatedistance(vectors.get(i), codewords1.get(j));
                        if (mindist > dist) {
                            mindist = dist;
                            codewordtoput = codewords1.get(j);
                            //System.out.println("Inside codewordtoput");
                        }
                    }
                    if (!hmap.containsKey(codewordtoput)) {

                        ArrayList<MyVector> arr = new ArrayList<MyVector>();
                        arr.add(vectors.get(i));

                        hmap.put(codewordtoput, arr);
                    } else {

                        ArrayList<MyVector> arr1 = hmap.remove(codewordtoput);
                        arr1.add(vectors.get(i));
                        hmap.put(codewordtoput, arr1);
                    }

                }

                codewords2 = new ArrayList<CodeWord>();
                for (CodeWord cd : codewords1) {
                    //   codewords2.add(cd);
                    int xval = 0;
                    int yval = 0;
                    int j = codewords1.indexOf(cd);
                    if (hmap.containsKey(cd)) {

                        ArrayList<MyVector> arr2 = hmap.get(cd);
                        for (int i = 0; i < arr2.size(); i++) {
                            xval += arr2.get(i).x;
                            //   System.out.println("x val:"+xval);
                            yval += arr2.get(i).y;
                            // System.out.println("y val:"+yval);
                        }
                        xval = xval / arr2.size();
                        //System.out.println("x val:"+xval);
                        yval = yval / arr2.size();
                        //System.out.println("y val:"+yval);
                        CodeWord cw = new CodeWord(xval, yval);
                        codewords2.add(cw);
                    } else {
                        codewords2.add(cd);
                    }
                }
                System.out.println("size of codeword " + codewords2.size());
//          for(int y = 0; y < codewords2.size() ; y++){
//            System.out.println("codes x "+codewords2.get(y).x);
//            System.out.println("codes y "+codewords2.get(y).y);
//        }
                converged = checkdiff(codewords1, codewords2);
            }
            bytesarr = quantize(codewords2, bytes);
            System.out.println("byte arr length" + bytesarr.length);

            offset = 0;
            numRead = 0;
            while (offset < bytesarr.length && (numRead = is.read(bytesarr, offset, bytesarr.length - offset)) >= 0) {
                offset += numRead;
            }
//            for (int i = 0; i < bytesarr.length; i++) {
//                System.out.println("byte array " + bytesarr[i]);
//            }

            ind = 0;
            for (int y = 0; y < 288; y++) {
                for (int x = 0; x < 352; x++) {

                    byte a = 0;
                    byte r = bytesarr[ind];
//                  byte g = bytes[ind+height*width];
//	            byte b = bytes[ind+height*width*2]; 
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((r & 0xff) << 8) | (r & 0xff);
                    img2.setRGB(x, y, pix);
                    ind++;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        displayimage();

//        JPanel panel = new JPanel();
//	panel.add (new JLabel (new ImageIcon(img1)));
//	panel.add( new JLabel (new ImageIcon(img2)));
//	JFrame frame = new JFrame("Video height: 352, width: 288");
//	frame.getContentPane().add(panel);
//	frame.pack();
//	frame.setVisible(true);
//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
        public void showImsrgb(String[] args) {
        int N = Integer.parseInt(args[1]);
        int maxval = Integer.MIN_VALUE;
        int dist = 0;
        int mindist = 0;
        CodeWordRGB codewordtoput = null;
        ArrayList<CodeWordRGB> codewords1 = new ArrayList<CodeWordRGB>();
        ArrayList<CodeWordRGB> codewords2 = new ArrayList<CodeWordRGB>();

        if (!powerOfTwo(N)) {
            System.exit(0);
        }
        img3 = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        img4 = new BufferedImage(352,288, BufferedImage.TYPE_INT_RGB);
        String[] splitarr = new String[2];
        try {
            File file = new File(args[0]);
          
            InputStream is = new FileInputStream(file);
            boolean converged = false;
            long len = file.length();
            byte[] bytes = new byte[(int) len];
            byte[] bytesarr = new byte[(int) len];
            
            System.out.println("file length " + len);
            int r1 = 0, g1 = 0, b1 = 0;
            int r2 = 0, g2 = 0, b2 = 0;

            int offset = 0;
            int numRead = 0;
while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
offset += numRead;
}
          
int ind = 0;
for(int y = 0; y < 288; y++){
for(int x = 0; x < 352; x++){

byte a = 0;
byte r =bytes[ind];
byte g = bytes[ind+288*352];
byte b = bytes[ind+288*352*2];
                                     
int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);

img3.setRGB(x,y,pix);
                                      
ind++;
}
}

            for (int i = 0; i < bytes.length/3; i = i + 2) {
                r1 = ByteToInt(bytes[i]);
                r2 = ByteToInt(bytes[i+1]);
                g1 = ByteToInt(bytes[i + 288 * 352]);
                g2 = ByteToInt(bytes[(i + 288 * 352)+1]);
                b1 = ByteToInt(bytes[i + 288 * 352*2]);
                b2 = ByteToInt(bytes[(i + 288 * 352*2)+1]);
                Pixel pixelx = new Pixel(r1,g1,b1);
                Pixel pixely = new Pixel(r2,g2,b2);
                MyVectorRGB vector = new MyVectorRGB(pixelx, pixely);  
                vectorsrgb.add(vector);
            }

         codewords2 = makeCodeBookRGB(N);
            
           // int i, j;

      
            
            
//
//            for(int y = 0; y < codewords2.size() ; y++){
//            System.out.println("codes x "+codewords2.get(y).x.g);
//            System.out.println("codes y "+codewords2.get(y).y.g);
//        }
            while (!converged) {
                System.out.println("inside this here loop");

                codewords1 = codewords2;

                for (int i = 0; i < vectorsrgb.size(); i++) {

                    mindist = Integer.MAX_VALUE;
                    for (int j = 0; j < codewords1.size(); j++) {
                        dist = calculatedistanceRGB(vectorsrgb.get(i), codewords1.get(j));
                        //System.out.println("distance "+dist);
                        if (mindist > dist) {
                            mindist = dist;
                            codewordtoput = codewords1.get(j);
                            //System.out.println("Inside codewordtoput");
                        }
                    }
                    if (!hmaprgb.containsKey(codewordtoput)) {
                        ArrayList<MyVectorRGB> arr = new ArrayList<MyVectorRGB>();
                        arr.add(vectorsrgb.get(i));
                        hmaprgb.put(codewordtoput, arr);
                    } else {
                        ArrayList<MyVectorRGB> arr1 = hmaprgb.remove(codewordtoput);
                        arr1.add(vectorsrgb.get(i));
                        hmaprgb.put(codewordtoput, arr1);
                    }

                }

                codewords2 = new ArrayList<CodeWordRGB>();
                for (CodeWordRGB cd : codewords1) {
                    //   codewords2.add(cd);
                    int xrval = 0;
                    int yrval = 0;
                    int xgval = 0;
                    int ygval = 0;
                    int xbval = 0;
                    int ybval = 0;
                    Pixel px = new Pixel(0,0,0);
                    Pixel py = new Pixel(0,0,0);
                    int j = codewords1.indexOf(cd);
                    if (hmaprgb.containsKey(cd)) {

                        ArrayList<MyVectorRGB> arr2 = hmaprgb.get(cd);
                        for (int i = 0; i < arr2.size(); i++) {
                            xrval += arr2.get(i).x.r;
                            yrval += arr2.get(i).y.r;
                            xgval += arr2.get(i).x.g;
                            ygval += arr2.get(i).y.g;
                            xbval += arr2.get(i).x.b;
                            ybval += arr2.get(i).y.b;  
                        }
                        xrval = xrval / arr2.size();
                        //System.out.println("x val:"+xval);
                        yrval = yrval / arr2.size();
                        //System.out.println("y val:"+yval);
                        xgval = xgval / arr2.size(); 
                        xbval = xbval / arr2.size();
                        ygval = ygval / arr2.size();
                        ybval = ybval / arr2.size();
                        px.r = xrval;
                        px.g = xgval;
                        px.b = xbval;
                        py.r = xrval;
                        py.g = xgval;
                        py.b = xbval;
                        
                        CodeWordRGB cw = new CodeWordRGB(px, py);
                        codewords2.add(cw);
                    } else {
                        codewords2.add(cd);
                    }
                }
                System.out.println("size of codeword " + codewords2.size());
          for(int y = 0; y < codewords2.size() ; y++){
            System.out.println("codes x "+codewords2.get(y).x.r);
            System.out.println("codes y "+codewords2.get(y).y.r);
        }
                converged = checkdiffRGB(codewords1, codewords2);
            }
            bytesarr = quantizeRGB(codewords2, bytes);
       //     System.out.println("byte arr length" + bytesarr.length);
//            for(int i =0; i < bytesarr.length;i++){
//                System.out.println("bytes arr"+bytesarr[i]);
//            }

            offset = 0;
            numRead = 0;
            while (offset < bytesarr.length && (numRead = is.read(bytesarr, offset, bytesarr.length - offset)) >= 0) {
                offset += numRead;
            }
//            for (int i = 0; i < bytesarr.length; i++) {
//                System.out.println("byte array " + bytesarr[i]);
//            }

            ind = 0;
            for (int y = 0; y < 288; y++) {
                for (int x = 0; x < 352; x++) {

                    byte a = 0;
                    byte r = bytesarr[ind];
		    byte g = bytes[ind+288*352];
		    byte b = bytes[ind+288*352*2]; 
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    img4.setRGB(x, y, pix);
                    ind++;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        JPanel panel = new JPanel();
	panel.add (new JLabel (new ImageIcon(img3)));
	panel.add( new JLabel (new ImageIcon(img4)));
	JFrame frame = new JFrame("Video height: 352, width: 288");
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
           MyCompression ren = new MyCompression();
        String fname = args[0];
        args[0].trim();
        String[] splitarr = args[0].split("\\.");
        args[0] = fname;
            if(splitarr[1].equals("raw")){
                ren.showImsraw(args);
            }
            else{
                ren.showImsrgb(args);
            }
    }
    
    public int ByteToInt(byte b) {
        return (int) b & 0x000000FF;

    }

    public boolean powerOfTwo(int N) {
        return N != 1 && (N & (N - 1)) == 0;
    }
    
    
    public int calculatedistance(MyVector vectorf, CodeWord codewordf) {
        int distance = (int) (Math.pow(vectorf.x - codewordf.x, 2) + Math.pow(vectorf.y - codewordf.y, 2));
        return distance;
    }
    public byte[] quantize(ArrayList<CodeWord> code, byte[] bytes) {
        byte[] arrofbytes = new byte[bytes.length];

        int distf = 0;
        int mindistf = Integer.MAX_VALUE;
        CodeWord cdd = new CodeWord(0, 0);

        int i = 0;
        System.out.println("vector length " + vectors.size());
        System.out.println(code.size());
//        for(int y = 0; y < code.size() ; y++){
//            System.out.println("codes x "+code.get(y).x);
//            System.out.println("codes y "+code.get(y).y);
//        }
        for (MyVector vec : vectors) {
            mindistf = Integer.MAX_VALUE;
            for (CodeWord cdf : code) {
                distf = calculatedistance(vec, cdf);
                if (mindistf > distf) {
                    mindistf = distf;
                    cdd.x = cdf.x;
                    cdd.y = cdf.y;
                }
            }
//            System.out.println(cdd.x);
//            System.out.println(cdd.y);
            arrofbytes[i] = (byte) cdd.x;
            arrofbytes[i + 1] = (byte) cdd.y;
            i = i + 2;
        }

        System.out.println("byte length " + arrofbytes.length);
        return arrofbytes;
    }
    
    public ArrayList<CodeWord> makeCodeBook(int N) {
        int i, j;
        int div = (int) (Math.log(N) / Math.log(2));
        int stepsize = (int) Math.floor(256.0 / div);
        int init = ((stepsize / 2) - 1);

        int k = 0;

        if (N == 2) {
            CodeWord cw1 = new CodeWord(127, 127);
            CodeWord cw2 = new CodeWord(191, 191);
            codewords.add(cw1);
            codewords.add(cw2);
        } else {

            for (i = 0; k < N; i += stepsize) {
                for (j = 0; j <= 255 && k < N; j += stepsize) {
                    CodeWord cw = new CodeWord(i + init, j + init);
                    codewords.add(cw);
                    k++;
                }
            }
        }

        return codewords;
    }
    
        public boolean checkdiff(ArrayList<CodeWord> codewords1, ArrayList<CodeWord> codewords2) {
        for (int i = 0; i < codewords1.size(); i++) {
            if (Math.abs(codewords1.get(i).x - codewords2.get(i).x) < 0.0001 && Math.abs(codewords1.get(i).y - codewords2.get(i).y) < 0.001) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
     public void displayimage(){
        JPanel panel = new JPanel();
	panel.add (new JLabel (new ImageIcon(img1)));
	panel.add( new JLabel (new ImageIcon(img2)));
	JFrame frame = new JFrame("Video height: 352, width: 288");
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
     class MyVector {

        int x, y;

        MyVector(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class CodeWord {

        int x, y;

        CodeWord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            CodeWord c = (CodeWord) o;
            return this.x == c.x && this.y == c.y;
        }
    }
       class CodeWordRGB {

        Pixel x, y;

        CodeWordRGB(Pixel x, Pixel y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            CodeWordRGB c = (CodeWordRGB) o;
            return this.x.r == c.x.r && this.x.g == c.x.g && this.x.b == c.x.b && this.y.r == c.y.r 
                    && this.y.g == c.y.g && this.y.b == c.y.b ;
        }
    }
       
    class MyVectorRGB {

        Pixel x, y;

        MyVectorRGB(Pixel x, Pixel y) {
            this.x = x;
            this.y = y;
        }
    }
    class Pixel{
        int r,g,b;
        
        Pixel(int r, int g, int b){
            this.r = r;
            this.g = g;
            this.b = b;
        }
        
    }
    public boolean checkdiffRGB(ArrayList<CodeWordRGB> codewords1, ArrayList<CodeWordRGB> codewords2) {
        for (int i = 0; i < codewords1.size(); i++) {
            if (Math.abs(codewords1.get(i).x.r - codewords2.get(i).x.r) < 0.00001 && Math.abs(codewords1.get(i).y.r- codewords2.get(i).y.r) < 0.00001 &&
                Math.abs(codewords1.get(i).x.g - codewords2.get(i).x.g) < 0.00001 && Math.abs(codewords1.get(i).y.g- codewords2.get(i).y.g) < 0.00001 &&
                Math.abs(codewords1.get(i).x.b - codewords2.get(i).x.b) < 0.00001 && Math.abs(codewords1.get(i).y.b- codewords2.get(i).y.b) < 0.00001) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
        public ArrayList<CodeWordRGB> makeCodeBookRGB(int N) {
        int i, j;
        int div = (int) (Math.log(N) / Math.log(2));
        int stepsize = (int) Math.floor(256.0 / div);
        int init = ((stepsize / 2) - 1);

        int k = 0;

        if (N == 2) {
            Pixel pixelx1 = new Pixel(127,127,127);
            Pixel pixelx2 = new Pixel(191,191,191);
            Pixel pixely1 = new Pixel(127,127,127);
            Pixel pixely2 = new Pixel(191,191,191);
            CodeWordRGB cw1 = new CodeWordRGB(pixelx1, pixely1);
            CodeWordRGB cw2 = new CodeWordRGB(pixelx2, pixely2);
            codewordsrgb.add(cw1);
            codewordsrgb.add(cw2);
     
        } else {

            for (i = 0; k < N; i += stepsize) {
                for (j = 0; j <= 255 && k < N; j += stepsize) {
                    Pixel px = new Pixel(i+init,i+init,i+init);
                    Pixel py = new Pixel(j+init,j+init,j+init);
                    CodeWordRGB cw = new CodeWordRGB(px, py);
                    codewordsrgb.add(cw);
                    k++;
                }
            }
        }
        
        return codewordsrgb;
        
    }
          public byte[] quantizeRGB(ArrayList<CodeWordRGB> code, byte[] bytes) {
        byte[] arrofbytes = new byte[bytes.length];

        int distf = 0;
        int mindistf ;
        Pixel p1 = new Pixel(0,0,0);
        Pixel p2 = new Pixel(0,0,0);
        CodeWordRGB cdd = new CodeWordRGB(p1, p2);

        int i = 0;
        System.out.println("vector length " + vectors.size());
        System.out.println(code.size());
//        for(int y = 0; y < vectors.size() ; y++){
//            System.out.println("codes x "+vectors.get(y).x.r);
//            System.out.println("codes y "+vectors.get(y).y.r);
//        }
        for (MyVectorRGB vec : vectorsrgb) {
            mindistf = Integer.MAX_VALUE;
            for (CodeWordRGB cdf : code) {
                distf = calculatedistanceRGB(vec, cdf);
                //System.out.println("distance is "+distf);
                if (mindistf > distf) {
                   // System.out.println("in here");
                   // System.out.println("cdf value"+cdf.x.r);
                    mindistf = distf;
                   // System.out.println(mindistf);
                    cdd.x.r = cdf.x.r;
                    cdd.x.g = cdf.x.g;
                    cdd.x.b = cdf.x.b;
                    cdd.y.r = cdf.y.r;
                    cdd.y.g = cdf.y.g;
                    cdd.y.b = cdf.y.b;
                   
                }
            }
//          System.out.println("this val"+cdd.x.g);
//        System.out.println("this val"+cdd.y.g);
            arrofbytes[i] = (byte) cdd.x.r;
            arrofbytes[i + 1] = (byte) cdd.y.r;
            arrofbytes[i + 288 * 352] = (byte) cdd.x.g;
            arrofbytes[(i + 288 * 352)+1] = (byte) cdd.y.g;
            arrofbytes[(i + 288 * 352 * 2)] = (byte) cdd.x.g;
            arrofbytes[(i + 288 * 352*2)+1] = (byte) cdd.x.g;
            i = i + 2;
        }

        System.out.println("byte length " + arrofbytes.length);
        return arrofbytes;
    }
      public int calculatedistanceRGB(MyVectorRGB vectorf, CodeWordRGB codewordf) {
        int distance = (int)(Math.pow(vectorf.x.r - codewordf.x.r, 2) + Math.pow(vectorf.y.r - codewordf.y.r, 2)+
                Math.pow(vectorf.x.g - codewordf.x.g, 2) + Math.pow(vectorf.y.g - codewordf.y.g, 2)+
                Math.pow(vectorf.x.b - codewordf.x.b, 2) + Math.pow(vectorf.y.b - codewordf.y.b, 2));
        return distance;
    }
      
}
