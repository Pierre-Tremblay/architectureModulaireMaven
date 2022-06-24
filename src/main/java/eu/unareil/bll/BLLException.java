package eu.unareil.bll;

public class BLLException extends Exception{
    public BLLException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public BLLException(String message, Throwable cause) {
        super("Erreur BLL "+message, cause);
        // TODO Auto-generated constructor stub
    }

    public BLLException(String message) {
        super("Erreur BLL "+message);
        // TODO Auto-generated constructor stub
    }
}
