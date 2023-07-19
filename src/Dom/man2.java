package Dom;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;

import giovynet.serial.ActionListenerReadPort;
import giovynet.serial.Baud;
import giovynet.serial.Buffer;
import giovynet.serial.Com;
import giovynet.serial.Parameters;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Font;

public class man2 {
	
	private JFrame frame;
	Com com1;
	Thread hiloLectura;
	lista der, izq;
	JPanel panel, panel_1;
	JButton hand1, hand2, hand3, hand4, hand5, hand6, hand7, bn6, bttrepar;
	ArrayList<JButton> bttns, hand;
	ArrayList<ficha> fichas, fichasCom, J2, J3, J4, tablero, jugadas;
	boolean turno = false, initH=true;
	int ini=0, intder=31, intizq=29, master=1, repar = 0, puntosJ1=0, puntosJ2=0, ronda=0;
	JTextPane team1, team2;
	String urlMesa = System.getProperty("user.dir") + "\\src\\IMG\\Mesa\\", c1 = "COM1";
	
	public static void main(String[] args) {
		
		String[] options = {"1v1", "2v2"};
		int seleccion = JOptionPane.showOptionDialog( null, "Seleccione el modo de Juego", "Sala", 
				JOptionPane.DEFAULT_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]
		);
		if (seleccion == 0) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						man2 window = new man2();
						window.frame.setVisible(true);
					} catch (Exception e) { e.printStackTrace(); }
				}
			});
		} else {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						man3 window = new man3();
						window.frame.setVisible(true);
					} catch (Exception e) { e.printStackTrace(); }
				}
			});
		}
	}

	public man2() throws Exception {
		fichasCom = new ArrayList<>();
		fichas = new ArrayList<>();
		J2 = new ArrayList<>();
		bttns = new ArrayList<>();
		hand = new ArrayList<>();
		tablero = new ArrayList<>();
		jugadas = new ArrayList<>();
		der = null;
		izq = null;

		String[] options = {"COM1", "COM2"};
		int seleccion = JOptionPane.showOptionDialog( null, "Puertos Seriales", "Seleccione los Puertos", 
				JOptionPane.DEFAULT_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, options[0]
		);
		
		if (seleccion==0) { c1 = "COM1";
		} else if (seleccion==1) { c1 = "COM2";	}
		
		initialize();
	}
	
	public String obtenerData() {
		String bit = ">", data = "";
		while (bit != "") {
			try { bit = com1.receiveSingleString();
			} catch (Exception e) { e.printStackTrace(); }
			if (bit.equals("")) break;
			data += bit;
		} return data;
	}
	
	public void enviarData(String data) {
		try { com1.sendString(">"+data);	
		} catch (Exception e1) { e1.printStackTrace(); }
	}

	public void act() {
		hiloLectura.interrupt();
		hiloLectura = null;
		System.gc();
		String data = obtenerData(); 

		if ( data.charAt(0) == '&' ) {
			String dt = data.substring(1);
			int di = 0;
			reset();
			for ( int x=1; x<29; x+=2 ) {
				fichas.add(di, new ficha( data.substring(x, x+1), data.substring(x+1, x+2) ) );
				di++;
			} initHand();
			
		} else if ( data.charAt(0) == '=' ) {
			String dt = data.substring(1);
			data = dt;
			String iz = "";
			String de = "";
			boolean change = false;
			
			for (int x=0; x<data.length(); x++) { 
				if ( data.charAt(x) == ']' ) { change = true; continue; }
				if (change == false) { iz += data.charAt(x); }
				if (change == true) { de += data.charAt(x); }
			}
			
			if (iz.length()%2 == 0 || de.length()%2 == 0) {		
				JOptionPane.showMessageDialog(null, "Error", "Error en la Transmision", JOptionPane.WARNING_MESSAGE);	
			} else {
				if (data.length() > 1) { construirMesa(data); }
				JOptionPane.showMessageDialog(null, "Tu turno", "Turno", JOptionPane.WARNING_MESSAGE);
			}
			
		} else if ( data.charAt(0) == ';' ) {
			puntosJ2 += misPuntos();
			team2.setText("Rival:"+puntosJ2+"pts");
			enviarData("*"+misPuntos());
			JOptionPane.showMessageDialog(null, "+"+misPuntos()+" pts para el Rival", "Ronda Perdida", JOptionPane.WARNING_MESSAGE);
			
		} else if ( data.charAt(0) == '*' ) { 
			int pt = Integer.parseInt(data.substring(1));
			puntosJ1 += pt;
			team1.setText("Yo:"+puntosJ1+"pts");
			JOptionPane.showMessageDialog(null, "+"+pt+" pts", "Ronda Ganada", JOptionPane.WARNING_MESSAGE);	

		} else if ( data.charAt(0) == '}' ) {
			int pt = Integer.parseInt(data.substring(1));
			int mpt = misPuntos();
			
			if ( pt < mpt ) {
				puntosJ2 += mpt;
				enviarData("("+mpt);
				team2.setText("Rival:"+puntosJ2+"pts");
				JOptionPane.showMessageDialog(null, "Derrota en Tranca:" + "\n" + mpt +"(Rival) vs "+pt+"(Yo)", "Tranca", JOptionPane.WARNING_MESSAGE);	
			} else if ( pt > mpt ) {
				puntosJ1 += pt;
				enviarData(")"+mpt);
				team1.setText("Yo:"+puntosJ1+"pts");
				JOptionPane.showMessageDialog(null, "Victoria en Tranca:" + "\n" + mpt +"(Rival) vs "+pt+"(Yo)", "Tranca", JOptionPane.WARNING_MESSAGE);	
			} else { 
				JOptionPane.showMessageDialog(null, "Empate en Tranca:" + "\n" + mpt +"(Rival) vs "+pt+"(Yo)", "Tranca", JOptionPane.WARNING_MESSAGE);	
			}
			
		} else if ( data.charAt(0) == ')' ) {			
			int pt = Integer.parseInt(data.substring(1));
			puntosJ2 += misPuntos();
			team2.setText("Rival:"+puntosJ2+"pts");
			JOptionPane.showMessageDialog(null, "Derrota en Tranca:" + "\n" + pt +"(Rival) vs "+misPuntos()+"(Yo)", "Tranca", JOptionPane.WARNING_MESSAGE);	
		} else if ( data.charAt(0) == '(' ) {
			int pt = Integer.parseInt(data.substring(1));
			puntosJ1 += pt;
			team2.setText("Yo:"+puntosJ1+"pts");
			JOptionPane.showMessageDialog(null, "Victoria en Tranca:" + "\n" + pt +"(Rival) vs "+misPuntos()+"(Yo)", "Tranca", JOptionPane.WARNING_MESSAGE);	
		}
		
		if (puntosJ2 >= 100) {
			JOptionPane.showMessageDialog(null, "Derrota", "Derrota", JOptionPane.WARNING_MESSAGE);
		} else if (puntosJ1 >= 100) {
			JOptionPane.showMessageDialog(null, "Victoria", "Victoria", JOptionPane.WARNING_MESSAGE);	
		}
		
		hiloLectura = new Thread(new HiloLectura4(com1, this));
		hiloLectura.start();
	}
	
	private void initialize() throws Exception {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(128, 128, 128));
		frame.setBounds(100, 100, 1190, 697);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		if (ini == 0) {
			Parameters param = new Parameters();
			param.setPort(c1);
			param.setBaudRate(Baud._9600);
			com1 = new Com(param);
			ini = 1;
			hiloLectura = new Thread(new HiloLectura4(com1, this));
			hiloLectura.start();	
		}
		
		panel = new JPanel();
		panel.setBounds(174, 548, 928, 80);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JTextPane txtpnJugador = new JTextPane();
		txtpnJugador.setFont(new Font("Tahoma", Font.BOLD, 10));
		txtpnJugador.setBounds(10, 48, 70, 19);
		panel.add(txtpnJugador);
		txtpnJugador.setText("   Jugador");
		txtpnJugador.setEditable(false);
		
		JButton btnNewButton_1_2 = new JButton("Confirmar");
		btnNewButton_1_2.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_1_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				enviarMesa();	
			}
		});
		btnNewButton_1_2.setBounds(670, 10, 96, 57);
		panel.add(btnNewButton_1_2);
		
		team1 = new JTextPane();
		team1.setFont(new Font("Tahoma", Font.BOLD, 10));
		team1.setText("Yo: 0pts");
		team1.setEditable(false);
		team1.setBounds(776, 10, 121, 19);
		panel.add(team1);
		
		team2 = new JTextPane();
		team2.setFont(new Font("Tahoma", Font.BOLD, 10));
		team2.setText("Rival: 0pts");
		team2.setEditable(false);
		team2.setBounds(776, 39, 121, 19);
		panel.add(team2);
		panel_1 = new JPanel();
		panel_1.setBounds(60, 36, 1042, 470);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		buttons();
		
		bttrepar = new JButton("Repartir");
		frame.getContentPane().add(bttrepar);
		bttrepar.setBounds(61, 548, 100, 57);
		
		bttrepar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				reset();
				int a = 0, b = 0;
				for (int x=0; x<28; x++) {
						fichasCom.add( x, new ficha( Integer.toString(a) , Integer.toString(b) ) );
					if (b == 6) { b=a; a++; } b++;
				}
					
				for (int x=0; x<5; x++) { Collections.shuffle( fichasCom ); }
					
				for ( ficha x : fichasCom ) {
					if ( fichas.size() != 14 ) { fichas.add(x);
					} else if (J2.size() != 14) { J2.add(x); }
				}
					
				initHand();
				String msg = "&";
				for (ficha x : J2) { msg += x.numA + x.numB; }
				enviarData(msg);

			}
		});
	}

	public void reset() {
		for (int x=0; x<59; x++) { bttns.get(x).setIcon( null ); }
		for (int x=0; x<14; x++) { hand.get(x).setEnabled(true); }	
		for (int x=0; x<14; x++) { hand.get(x).setIcon(null); }	
		intder = 31;
		intizq = 29;
		der = null;
		izq = null;
		jugadas.clear();
		fichasCom.clear();
		fichas.clear();
		J2.clear();
	}
	
	public void buttons() {
		for (int x=0; x<14; x++) { hand.add( x, new JButton("") ); panel.add( hand.get(x) ); }
		for (int x=0; x<59; x++) { bttns.add( x, new JButton("") ); }
		for (JButton x : bttns) { panel_1.add(x); }
		setButtons();	
	}

	public void enviarMesa() {
		if (der != null && izq != null) {
			String msg = "=";
			String a = izq.obtenerArbol(); 
			String b = der.obtenerArbol();
			msg += a;
			msg += "]";
			msg += b;
			enviarData(msg);
		}
	}

	public void setFicha( int id ) {
		turno = true;
		
		if ( turno == true ) {
			if ( der == null ) {
				ficha m =  fichas.get(id);
				izq = new lista(m.numA);
				der = new lista(m.numB);
				hand.get(id).setEnabled(false);
				bttns.get(30).setIcon( m.changeImgRectaC() );	
				turno = false;
				jugadas.add( fichas.get(id) );
			} else if ( der != null ) {
				ficha m =  fichas.get(id);
				
				if ( (der.insertable(m.numA) || der.insertable(m.numB)) && (izq.insertable(m.numA) || izq.insertable(m.numB)) ) {
					String[] options = {"Izquierda", "Derecha"};
					int seleccion = JOptionPane.showOptionDialog( null, "Seleccione una opción", "Decisión", 
							JOptionPane.DEFAULT_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null, options, options[0]
					);
					
					if (seleccion == 0) {
						if ( izq.insertable(m.numA) ) {
							configSeteo(id, m.numA, m.numB, m.changeImgRectaB(), 1);
						} else if ( izq.insertable(m.numB) ) {
							configSeteo(id, m.numB, m.numA, m.changeImgRectaC(), 1);
						} 
					} else if (seleccion == 1) {
						if ( der.insertable(m.numA) ) {
							configSeteo(id, m.numA, m.numB, m.changeImgRectaC(), 0);
						} else if ( der.insertable(m.numB) ) {
							configSeteo(id, m.numB, m.numA, m.changeImgRectaB(), 0);
						} 
					}
				} else {
					if ( der.insertable(m.numA) ) {
						configSeteo(id, m.numA, m.numB, m.changeImgRectaC(), 0);
					} else if ( der.insertable(m.numB) ) {	
						configSeteo(id, m.numB, m.numA, m.changeImgRectaB(), 0);	
					} else if ( izq.insertable(m.numA) ) {
						configSeteo(id, m.numA, m.numB, m.changeImgRectaB(), 1);
					} else if ( izq.insertable(m.numB) ) {
						configSeteo(id, m.numB, m.numA, m.changeImgRectaC(), 1);
					} 	
				}
			}	
			actMapa();
		}
	}

	public void confirmarVictoria() {
		if (fichas.size()==jugadas.size()) {
			enviarData(";");
		} else if (revisarUltIzq()==7) {
			enviarData("}" + misPuntos());
		}
	}
	
	public int revisarUltIzq() {
		String uizq = izq.ultimoVal();
		int contDer = der.contarElemento( uizq );
		int contIzq = izq.contarElemento( uizq );
		if ( der.val.equals(uizq) == true) { contDer++; }
		else { if ( izq.val.equals(uizq) == true) { contDer++; } }
		return contDer+contIzq;
	}
	
	public int misPuntos() {
		int cont = 0;
		for ( ficha x : fichas ) {
			if ( jugadas.contains(x) == false ) {
				cont += Integer.parseInt(x.numA) + Integer.parseInt(x.numB); 
			}
		}
		return cont;
	}
	
	public void actMapa() {
		if ( izq != null ) {
			int intd=31;
			int inti=29;
			String amm = izq.obtenerArbol();
			amm = amm.substring(1);
			String bmm = der.obtenerArbol();
			bmm = bmm.substring(1);
			
			int nn = 0; // 29 base
			for (int x=0; x<amm.length(); x++) {
				if (nn>=amm.length()) break;
				new ImageIcon(urlMesa+amm.charAt(nn)+amm.charAt(nn+1)+".png");
				
				if ( 29-x >= 23 || 29-x <= 11 ) {
					bttns.get(29-x).setIcon( new ImageIcon(urlMesa+amm.charAt(nn+1)+amm.charAt(nn)+".png") );
				} else {
					bttns.get(29-x).setIcon( new ImageIcon(urlMesa+amm.charAt(nn)+amm.charAt(nn+1)+".png") );
				}	
				nn+=2;
				inti--;
			}
			
			nn = 0; // 31 base
			for (int x=0; x<bmm.length(); x++) {
				if (nn>=bmm.length()) break;
				new ImageIcon(urlMesa+bmm.charAt(nn)+bmm.charAt(nn+1)+".png");
				if ( 31+x >= 35 && 31+x <= 47 ) {
					bttns.get(31+x).setIcon( new ImageIcon(urlMesa+bmm.charAt(nn+1)+bmm.charAt(nn)+".png") );
				} else {
					bttns.get(31+x).setIcon( new ImageIcon(urlMesa+bmm.charAt(nn)+bmm.charAt(nn+1)+".png") );
				}
				nn+=2;
				intd++;
			}	
		}
	}

	public void configSeteo(int id, String numA, String numB, ImageIcon nc, int ori ) {
		hand.get(id).setEnabled(false);		
		turno = false; 
		if (ori == 0) {
			der.insertarDoble( numA, numB );
			bttns.get(intder).setIcon( nc );	 
			intder++; 
		} else {  
			izq.insertarDoble( numA, numB );
			bttns.get(intizq).setIcon( nc );	
			intizq--;
		}
		jugadas.add( fichas.get(id) );
		confirmarVictoria();
	}
	
	public void construirMesa ( String mesa ) {
		String izqq = "";
		String derr = "";
		boolean change = false;
		
		for (int x=0; x<mesa.length(); x++) { 
			
			if ( mesa.charAt(x) == ']' ) { change = true; continue; }
			if (change == false) { izqq += mesa.charAt(x); }
			if (change == true) { derr += mesa.charAt(x); }
	
		}
		
		if ( izqq.length() > 0 && derr.length() > 0 ) {

			String centro = "";
			centro += "" + izqq.charAt(0) + derr.charAt(0); 
			izqq = izqq.substring(1);
			
			String tn = "";
			for (int x=0; x<izqq.length(); x+=2) {
				tn += "" + izqq.charAt(x+1) + izqq.charAt(x);
			}
			
			derr = derr.substring(1);
			izq = null;
			der = null;
			izq = new lista(""+centro.charAt(0));
			der = new lista(""+centro.charAt(1));
			
			for (int x=0; x<izqq.length(); x++) {
				izq.insertarCola( ""+izqq.charAt(x) );
			}
			for (int x=0; x<derr.length(); x++) {
				der.insertarCola( ""+derr.charAt(x) );
			}
			new ImageIcon(urlMesa+centro.charAt(0)+centro.charAt(1)+".png");
			bttns.get(30).setIcon( new ImageIcon(urlMesa+centro.charAt(0)+centro.charAt(1)+".png") );	
			
			intder=31;
			intizq=29;
					
			int nn = 0; // 29 base
			for (int x=0; x<tn.length(); x++) {
				if (nn>=tn.length()) break;
				new ImageIcon(urlMesa+tn.charAt(nn)+tn.charAt(nn+1)+".png");
				if ( 29-x >= 23 || 29-x <= 11 ) {
					bttns.get(29-x).setIcon( new ImageIcon(urlMesa+tn.charAt(nn+1)+tn.charAt(nn)+".png") );
				} else {
					bttns.get(29-x).setIcon( new ImageIcon(urlMesa+tn.charAt(nn)+tn.charAt(nn+1)+".png") );
				}
				nn+=2;
				intizq--;
			}
			
			nn = 0; // 31 base
			for (int x=0; x<derr.length(); x++) {
				if (nn>=derr.length()) break;
				new ImageIcon(urlMesa+derr.charAt(nn)+derr.charAt(nn+1)+".png");
				if ( 31+x >= 35 && 31+x<=47 ) {
					bttns.get(31+x).setIcon( new ImageIcon(urlMesa+derr.charAt(nn+1)+derr.charAt(nn)+".png") );		
				} else {
					bttns.get(31+x).setIcon( new ImageIcon(urlMesa+derr.charAt(nn)+derr.charAt(nn+1)+".png") );					
				}
				nn+=2;
				intder++;
			}
		
		} else {
			JOptionPane.showMessageDialog(null, "Error", "Error en la Transmision", JOptionPane.WARNING_MESSAGE);	
		}

		actMapa();
	}
	
	public void initHand() {
		
		fichas.get(0).changeImgRectaA();
		for (int x=0; x<14; x++) { hand.get(x).setIcon( fichas.get(x).changeImgRectaA() ); }	
		if (initH==true) {
			hand.get(0).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(0); }
			});
			hand.get(1).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(1); }
			});
			hand.get(2).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(2); }
			});
			hand.get(3).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(3); }
			});
			hand.get(4).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(4); }
			});
			hand.get(5).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(5); }
			});
			hand.get(6).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(6); }
			});
			hand.get(7).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(7); }
			});
			hand.get(8).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(8); }
			});
			hand.get(9).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(9); }
			});
			hand.get(10).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(10); }
			});
			hand.get(11).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(11); }
			});
			hand.get(12).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(12); }
			});
			hand.get(13).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { setFicha(13); }
			});
			initH=false;
		}		
	}
	
	public void setButtons() {
		hand.get(0).setBounds(90, 10, 33, 57);
		hand.get(1).setBounds(130, 10, 33, 57);
		hand.get(2).setBounds(170, 10, 33, 57);
		hand.get(3).setBounds(210, 10, 33, 57);
		hand.get(4).setBounds(250, 10, 33, 57);		
		hand.get(5).setBounds(290, 10, 33, 57);
		hand.get(6).setBounds(330, 10, 33, 57);
		hand.get(7).setBounds(380, 10, 33, 57);
		hand.get(8).setBounds(420, 10, 33, 57);
		hand.get(9).setBounds(460, 10, 33, 57);
		hand.get(10).setBounds(500, 10, 33, 57);
		hand.get(11).setBounds(540, 10, 33, 57);		
		hand.get(12).setBounds(580, 10, 33, 57);
		hand.get(13).setBounds(620, 10, 33, 57);
		
		bttns.get(29).setBounds(475, 219, 57, 29);
		bttns.get(30).setBounds(542, 219, 57, 29);
		bttns.get(31).setBounds(609, 219, 57, 29);
		bttns.get(32).setBounds(676, 219, 57, 29);
		bttns.get(33).setBounds(741, 219, 57, 29);
		bttns.get(34).setBounds(808, 219, 57, 29);
		bttns.get(35).setBounds(808, 179, 57, 29);
		bttns.get(36).setBounds(808, 140, 57, 29);
		bttns.get(37).setBounds(741, 140, 57, 29);
		bttns.get(38).setBounds(676, 140, 57, 29);
		bttns.get(39).setBounds(609, 140, 57, 29);
		bttns.get(40).setBounds(542, 140, 57, 29);
		bttns.get(41).setBounds(475, 140, 57, 29);
		bttns.get(42).setBounds(408, 140, 57, 29);
		bttns.get(43).setBounds(343, 140, 57, 29);
		bttns.get(44).setBounds(276, 140, 57, 29);
		bttns.get(45).setBounds(209, 140, 57, 29);
		bttns.get(46).setBounds(142, 140, 57, 29);
		bttns.get(47).setBounds(142, 92, 57, 29);
		bttns.get(48).setBounds(142, 53, 57, 29);
		bttns.get(49).setBounds(209, 53, 57, 29);
		bttns.get(50).setBounds(276, 53, 57, 29);
		bttns.get(51).setBounds(343, 53, 57, 29);
		bttns.get(52).setBounds(408, 53, 57, 29);
		bttns.get(53).setBounds(475, 53, 57, 29);
		bttns.get(54).setBounds(541, 53, 57, 29);
		bttns.get(55).setBounds(608, 53, 57, 29);
		bttns.get(56).setBounds(675, 53, 57, 29);
		bttns.get(57).setBounds(742, 53, 57, 29);
		bttns.get(58).setBounds(809, 53, 57, 29);
		bttns.get(28).setBounds(408, 219, 57, 29);
		bttns.get(27).setBounds(343, 219, 57, 29);
		bttns.get(26).setBounds(276, 219, 57, 29);
		bttns.get(25).setBounds(209, 219, 57, 29);
		bttns.get(24).setBounds(142, 219, 57, 29);
		bttns.get(23).setBounds(142, 258, 57, 29);
		bttns.get(22).setBounds(142, 297, 57, 29);
		bttns.get(21).setBounds(209, 297, 57, 29);
		bttns.get(20).setBounds(276, 297, 57, 29);
		bttns.get(19).setBounds(343, 297, 57, 29);
		bttns.get(18).setBounds(410, 297, 57, 29);
		bttns.get(17).setBounds(477, 297, 57, 29);
		bttns.get(16).setBounds(544, 297, 57, 29);
		bttns.get(15).setBounds(611, 297, 57, 29);
		bttns.get(14).setBounds(678, 297, 57, 29);
		bttns.get(13).setBounds(743, 297, 57, 29);
		bttns.get(12).setBounds(810, 297, 57, 29);		
		bttns.get(11).setBounds(808, 349, 57, 29);
		bttns.get(10).setBounds(808, 395, 57, 29);
		bttns.get(9).setBounds(743, 395, 57, 29);
		bttns.get(8).setBounds(676, 395, 57, 29);
		bttns.get(7).setBounds(611, 395, 57, 29);
		bttns.get(6).setBounds(544, 395, 57, 29);
		bttns.get(5).setBounds(477, 395, 57, 29);
		bttns.get(4).setBounds(410, 395, 57, 29);
		bttns.get(3).setBounds(343, 395, 57, 29);
		bttns.get(2).setBounds(276, 395, 57, 29);
		bttns.get(1).setBounds(209, 395, 57, 29);
		bttns.get(0).setBounds(142, 395, 57, 29);
		
	}
}
