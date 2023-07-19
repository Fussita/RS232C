package Dom;

import giovynet.serial.Com;

public class HiloLectura4 implements Runnable {
	private Com com;
	private man2 m;
	
	public HiloLectura4(Com com, man2 m) {
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
