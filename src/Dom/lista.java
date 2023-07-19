package Dom;

public class lista {
	public String val;
	public lista next;
	
	public lista(String x) {
		this.val = x;
		this.next = null;
	}
	
	public boolean insertable(String x) {
		if ( this.next != null ) {
			lista m = this.next;
			while (m.next != null) { m = m.next; }
			if ( m.val.equals(x) ) return true;
			return false;
		} else {
			if ( x.equals(this.val) ) return true;
			return false;
		}
	}
	
	public String ultimoVal() {
		if (this.next == null) {
			return this.val;
		} else {
			lista m = this.next;
			while (m.next != null) { m = m.next; }
			return m.val;
		}
	}
	
	public void insertarDoble(String x, String y) {
		lista a = new lista(x);
		lista b = new lista(y);
		a.next = b;
		if (this.next == null) {
			this.next = a;
		} else {
			lista m = this.next;
			while (m.next != null) { m = m.next; }
			m.next = a;
		}
	}
	
	public void insertarCola(String x) {
		lista n = new lista(x);
		if (this.next == null) {
			this.next = n;
		} else {
			lista m = this.next;
			while (m.next != null) { m = m.next; }
			m.next = n;
		}
	}

	public int contarElemento(String x) {
		int cont = 0;
		lista m = this.next;
		while (m != null) { 
			if (m.val.equals(x) == true) cont++;
			else { if (m.next.val.equals(x) == true) cont++; }
			m = m.next.next; 
		}
		return cont;
	}
	
	public String obtenerArbol() {
		String r = this.val;
		lista m = this.next;
		while (m != null) { 
			r += m.val;
			m = m.next; 
		}
		return r;
	}
	public void mostrarArbol() {
		System.out.print(this.val + " - ");
		lista m = this.next;
		while (m != null) { 
			System.out.print(m.val + " - ");
			m = m.next; 
		}
		System.out.println(" ------------ ");
	}
	
}
