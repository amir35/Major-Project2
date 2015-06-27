import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class DecryptPixel {
	BufferedImage image;
   int width,height,L,W,S,n,k,next;static String img;
   int[] a=new int[100],key,rand,xbits;int count;
   StringBuffer xorbits=new StringBuffer();
   int[][] pixels,B,cipherpix,pix;
   Scanner sc;String password;

    public void processImage(String imagename) {
      try {
        File input=new File(imagename);
        image=ImageIO.read(input);
        width=image.getWidth();
        height=image.getHeight();
        pix=new int[height][width];
        cipherpix=new int[height][width];
        xbits=new int[height*width];
        int count=0,n=0;
        for(int i=0;i<height;i++) {
          for(int j=0;j<width;j++) {
            int pixcol=image.getRGB(j, i);
            int a = (pixcol >>> 24) & 0xff;
            int red = (pixcol >>> 16) & 0xff;
            int green = (pixcol >>> 8) & 0xff;
            int blue = (pixcol ) & 0xff;
            int rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
            cipherpix[i][j]=rgb;
        	}
        }
        readPassword();
   		processBlock();
   		lfsr();
      } catch (Exception e) { e.printStackTrace(); }
   }
   void lfsr() throws IOException{
    int T=height*width,j=0;
    int N=a.length;
    int TAP=63; rSelect();
   
    for (int t=0;t<T;t++) {
        int next=(a[N-1]^a[TAP-1]);
        for (int i=N-1;i>0;i--)
          a[i]=a[i-1];
        a[0]=next;
        keyGen();
    }
    }
    void rSelect() {
    	S=(int)Math.floor(Math.log(L*W)/Math.log(2));
      	rand=new int[S];
      	key=new int[S];
      	for(int i=0;i<S;i++) {
        	rand[i]=i;
      }   
    }
    void keyGen() throws IOException{
    	StringBuffer sb=new StringBuffer();
    	for(int i=0;i<rand.length;i++) {
        	key[i]=a[rand[i]];
        	sb.append(key[i]); 
      	}
      	try {
      	int address=Integer.parseInt(sb.toString(),2);
        int row=address/L;int column=address%L;
        xbits[next++]=B[row][column];
    	}
        catch(NumberFormatException n){}
    }
   void processBlock() {
   		try {
       	 	File input=new File("block.png");
        	image=ImageIO.read(input);
        	int cou=0;
        	L=image.getHeight();
        	W=image.getWidth();
        	B=new int[L][W];
        	int count=0,n=0;
        for(int i=0;i<L;i++) {
          for(int j=0;j<W;j++) {
            int pixcol=image.getRGB(j, i);
            int a = (pixcol >>> 24) & 0xff;
            int red = (pixcol >>> 16) & 0xff;
            int green = (pixcol >>> 8) & 0xff;
            int blue = (pixcol ) & 0xff;
            int rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
            B[i][j]=rgb;
            //System.out.print(B[i][j]+" ");
          }
        }
    	}	
        catch(Exception e) { e.printStackTrace(); }
   }
   void readPassword() {
   		try {
   			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("password.txt")));
   			String str=br.readLine();
   			for(int i=0;i<100;i++) {
   				a[i]=Integer.parseInt(str.charAt(i)+"");
   			}
   		}
   		catch(IOException ioe) { ioe.printStackTrace(); }
   	}
   	public static void main(String[] args)throws IOException {
   		DecryptPixel dp=new DecryptPixel();
   		dp.processImage(args[0]);
   		img=args[1];
   		dp.decipherImage();
   	}

	void decipherImage()throws IOException {
    int m=0;
    for(int i=0;i<height;i++) {
          for(int j=0;j<width;j++) {
            pix[i][j]=cipherpix[i][j]^xbits[m++];
          }
        }
        BufferedImage im=new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        for(int i=0;i<height;i++)
          for(int j=0;j<width;j++)
            im.setRGB(j,i,pix[i][j]);
        File file=new File(img);
        ImageIO.write(im, "png", file);
  	}
}