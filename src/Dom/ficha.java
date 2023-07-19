package Dom;

import javax.swing.ImageIcon;

public class ficha {
	public String numA;
	public String numB;
	
	public ficha( String a, String b ) {
		this.numA = a;
		this.numB = b;
	}
	
	public ImageIcon changeImgRectaA() {
		String url = System.getProperty("user.dir") + "\\src\\IMG\\Rectas\\";
		return new ImageIcon(url+this.numA+this.numB+".png");
	}
	public ImageIcon changeImgRectaB() {
		String url = System.getProperty("user.dir") + "\\src\\IMG\\DerIzq\\";
		return new ImageIcon(url+this.numA+this.numB+".png");
	}
	public ImageIcon changeImgRectaC() {
		String url = System.getProperty("user.dir") + "\\src\\IMG\\IzqDer\\";
		return new ImageIcon(url+this.numA+this.numB+".png");
	}
}
