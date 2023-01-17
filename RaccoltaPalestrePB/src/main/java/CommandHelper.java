import java.util.Locale;


// serie di metodi che consentono di riconoscere la stringa d'ingresso quale comando in riferimento alle funzionalita' esposte
public class CommandHelper {
    public boolean isStart(String msg){
        boolean ret = false;
        if(msg.equals("/start"))
            ret = true;
        return ret;
    }

    public boolean isPalestre(String msg){
        boolean ret = false;
        if(msg.equals("/palestre"))
            ret = true;
        return ret;
    }

    public boolean isIndirizzoPalestra(String msg){
        boolean ret = false;
        if(msg.toUpperCase(Locale.ROOT).equals("INDIRIZZO"))
            ret = true;
        return ret;
    }

    public boolean isIstruttoriPalestra(String msg){
        boolean ret = false;
        if(msg.toUpperCase(Locale.ROOT).equals("ISTRUTTORI"))
            ret = true;
        return ret;
    }

    public boolean isCorsi(String msg){
        boolean ret = false;
        if(msg.equals("/corsi"))
            ret = true;
        return ret;
    }

    public boolean isIstruttori(String msg){
        boolean ret = false;
        if(msg.equals("/istruttori"))
            ret = true;
        return ret;
    }

    public boolean isInfo(String msg){
        boolean ret = false;
        if(msg.equals("/info"))
            ret = true;
        return ret;
    }
}
