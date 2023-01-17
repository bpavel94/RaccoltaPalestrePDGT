// gestire i messaggi di ritorno
// sia centralizzato il codice che gestisce i messaggi in maniera tale che qualunque modifica al testo del messaggio
// sia sufficiente applicarla qui dentro al fine di trovarla in tutte le funzioni in cui vengono utilizzate
public class MessageHelper {
    public String getWelcomeMessage(){
        return "Benvenuto nel bot per la ricerca sulle palestre di Fano";
    }

    public String getAlertApiError(){
        return "Non è stato possibile recuperare le informazioni richieste.";
    }

    public String getConsoleSuccessApi(String api){
        return "API " +  api + " conclusa con successo!!";
    }

    public String getConsoleErrorApi(String api){
        return "API " + api +" non conclusa, vedi errori in eccezione!!";
    }

    public String getErrorBotMessage(String api){
        return "C'è stato un errore nella comunicazione con il bot durante l'utilizzo dell'API " + api;
    }
}
