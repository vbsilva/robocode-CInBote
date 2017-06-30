package justice_league;


/**
 * Point - a serializable point class
 */
public class Sentido implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int x = 0;
	
	public Sentido(int x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

}