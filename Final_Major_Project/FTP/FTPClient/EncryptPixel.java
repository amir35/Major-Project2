import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class EncryptPixel {
   BufferedImage image;
   int width,height,L,W,S,n,k,next;static String outputimage;
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
        pixels=new int[height][width];
        pix=new int[height][width];
        int count=0,n=0;
        for(int i=0;i<height;i++) {
          for(int j=0;j<width;j++) {
            int pixcol=image.getRGB(j, i);
            int a = (pixcol >>> 24) & 0xff;
            int red = (pixcol >>> 16) & 0xff;
            int green = (pixcol >>> 8) & 0xff;
            int blue = (pixcol ) & 0xff;
            int rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
            
            String redbits=toBinary(red);
            String greenbits=toBinary(green);
            String bluebits=toBinary(blue);
            pix[i][j]=rgb;
            int xor=Integer.parseInt(redbits.charAt(0)+"");
            for(int m=1;m<redbits.length()-1;m++) {
            	xor^=Integer.parseInt(redbits.charAt(m)+"");
            }
            for(int m=0;m<greenbits.length();m++) {
            	xor^=Integer.parseInt(greenbits.charAt(m)+"");
            }
            for(int m=0;m<bluebits.length();m++) {
            	xor^=Integer.parseInt(bluebits.charAt(m)+"");
            }
            xorbits=xorbits.append(xor);
            if(xor==1)pixels[i][j]=16777215;else pixels[i][j]=0;
          }
        }
       	password=xorbits.toString();
       	xbits=new int[height*width];
        cipherpix=new int[height][width];
        passwordBreak();
        selectBlock();
        lfsr();
      } catch (Exception e) { e.printStackTrace(); }
   }
   static public void main(String args[]) throws Exception {
   		outputimage=args[1];
    	EncryptPixel obj = new EncryptPixel(); 
      	obj.processImage(args[0]);
      	obj.setPassword();
      	obj.cipherImage();
   }
   void selectBlock() {
   		try { 
   			System.out.println("Enter the value of n, k, L and W  : ");
        	sc=new Scanner(System.in);
        	n=sc.nextInt(); k=sc.nextInt();
        	L=sc.nextInt(); W=sc.nextInt();
        	B=new int[L][W];
        	for(int i=0;i<=L-1;i++) {
        		for(int j=0;j<=W-1;j++) {
        			B[i][j]=pixels[(n+i)%height][(k+j)%width];
        		}
        	}
          S=(int)Math.floor(Math.log(L*W)/Math.log(2));
          key=new int[S];
        	BufferedImage im=new BufferedImage(W,L,BufferedImage.TYPE_3BYTE_BGR);
        	for(int i=0;i<=L-1;i++)
            	for(int j=0;j<=W-1;j++)
                	im.setRGB(j,i,B[i][j]);
	        File file=new File("block.png");
    	    ImageIO.write(im, "png", file);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
   }
  void lfsr() {
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
      	rand=new int[S];
      	for(int i=0;i<S;i++) {
        	rand[i]=i;
      }   
    }
    void keyGen() {
    	StringBuffer sb=new StringBuffer();
    	for(int i=0;i<rand.length;i++) {
        	key[i]=a[rand[i]];
        	sb.append(key[i]);
      	}
      	int address=Integer.parseInt(sb.toString(),2);
        int row=address/L;int column=address%L;
        xbits[next++]=B[row][column];
    }
   	String toBinary(int n) {
       if(n==0) {
           return("0");
       }
       StringBuffer binary=new StringBuffer();
       while(n>0) {
           int rem=n%2;
           binary=new StringBuffer(rem+binary.toString());
           n=n/2;
       }
       while(binary.length()<8) {
			binary.insert(0,"0");
		}
       return binary.toString();
	}
	void passwordBreak() {
		for(int i=0;i<100;i++) {
			a[i]=Integer.parseInt(password.charAt(i)+"");
		}
		/*for(int i=0;i<a.length-100;i++) {

		}*/
	}
	void cipherImage()throws IOException {
		int m=0;
		for(int i=0;i<height;i++) {
          for(int j=0;j<width;j++) {
            cipherpix[i][j]=pix[i][j]^xbits[m++];
          }
        }
        BufferedImage im=new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        for(int i=0;i<height;i++)
          for(int j=0;j<width;j++)
            im.setRGB(j,i,cipherpix[i][j]);
        File file=new File(outputimage);
        ImageIO.write(im, "png", file);
	}

	void setPassword() throws IOException {
		PrintStream ps=new PrintStream(new FileOutputStream("password.txt"));
		ps.println(password);
        ps.close();
	}
}
 