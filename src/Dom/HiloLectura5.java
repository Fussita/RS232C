package Dom;

import giovynet.serial.Com; //asd

public class HiloLectura5 implements Runnable {
	private Com com;
	private man3 m;
	
	public HiloLectura5(Com com, man3 m) {
		this.com = com;
		this.m = m;
	}
	
	@Override
	public void run() {
		try {
			char data = 0;
			while (true) {
				data = com.receiveSingleChar();
				Thread.sleep(150);
				if (data != 0) {
					this.m.act();
					break;
				}
			}
			
		} catch (Exception e) {
			
		}
	}

}
