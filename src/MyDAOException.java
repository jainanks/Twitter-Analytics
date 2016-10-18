
/*
 * Name- Aayush Chandra
 * Date- 06 Dec 2014
 * Course- 08-600
*/
public class MyDAOException extends Exception{

	
	private static final long serialVersionUID = 1L;

	public MyDAOException(Exception e) { super(e); }
	public MyDAOException(String s)    { super(s); }
}
