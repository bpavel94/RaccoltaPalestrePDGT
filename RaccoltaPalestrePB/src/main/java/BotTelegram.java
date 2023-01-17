import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.*;
import java.util.*;

public class BotTelegram extends TelegramLongPollingBot {
    private Connection connection;
    private ConnectionDB gestioneDB;
    private String Token;
    private String username;
    private SessionState gestioneStatoSessione;
    private CommandHelper comandi;
    private MessageHelper messageHelper;

    public BotTelegram() throws Exception {
        this.gestioneDB = new ConnectionDB();
        this.connection = gestioneDB.getConnectionToDB();
        this.gestioneStatoSessione = new SessionState();
        this.comandi = new CommandHelper();
        this.Token = gestioneDB.getToken();
        this.username = gestioneDB.getUsername();
        this.messageHelper = new MessageHelper();
    }

    public String getBotUsername() {
        return this.username;
    }

    public String getBotToken() {
        return this.Token;
    }

    // funzione che comunica direttamente con il bot telegram
    public void onUpdateReceived(Update update) {
        String chatId = "";
        String msg = "";
        try {
            // msg = comando digitato sul bot telegram
            msg = update.getMessage().getText();
            // chatId = id della persona che sta utilizzando il bot
            chatId=update.getMessage().getChatId().toString();
            // PUNTO PIU IMPORTANTE
            // GESTIONE DELLA SESSIONE BASATO SU DB
            int statoSessione = gestioneStatoSessione.getUserSessionState(chatId, this.connection);

            // A seconda del comando chiami la rispettiva API
            if(statoSessione == gestioneStatoSessione.sessionStart()){
                commandsForStartState(chatId,msg);
            }else if(statoSessione == gestioneStatoSessione.sessionDetailsPalestre()){
                commandsForPalestreState(chatId,msg);
            }else if(statoSessione == gestioneStatoSessione.sessionAddressPalestre()){ // corsi palestra
                dettagliPalestraAPI(chatId,msg,"via");
            }else if(statoSessione == gestioneStatoSessione.sessionIstruttoriPalestre()){ // istruttori palestra
                istruttoriPalestraAPI(chatId,msg);
            }else if(statoSessione == gestioneStatoSessione.sessionInfoPalestra()){ // orari palestra
                dettagliPalestraAPI(chatId,msg,"orari");
            }
        } catch (Exception e) {
            // gestione errore in invio
            e.printStackTrace();
            System.out.println("Non è stato possibile comunicare con il bot e concludere con successo l'API: " + msg);
            if(chatId != ""){
                manageError(chatId, msg);
            }
        }
    }
    // ritorno del messaggio direttamente al bot
    public void manageError(String chatId, String msg){
        try {
            gestioneStatoSessione.setSessionState(connection,chatId,gestioneStatoSessione.sessionStart());
            SendMessage mess = new SendMessage();
            mess.setText(messageHelper.getErrorBotMessage(msg));
            mess.setChatId(chatId);
            sendMessage(mess);
        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        }
    }


    // vere e proprie funzionalità (API)
    public void commandsForStartState(String chatId, String msg) throws Exception {
        if(comandi.isStart(msg)){
            startAPI(chatId);
        }else if(comandi.isPalestre(msg)){
            infoPalestraAPI(chatId);
        }else if(comandi.isCorsi(msg)){
            corsiAPI(chatId);
        }else if(comandi.isIstruttori(msg)){
            istruttoriAPI(chatId);
        }else if(comandi.isInfo(msg)){
            infoAPI(chatId);
        }
    }

    public void commandsForPalestreState(String chatId, String msg) throws Exception {
        if(comandi.isIndirizzoPalestra(msg)){
            palestreAPI(chatId,gestioneStatoSessione.sessionAddressPalestre());
        }else if(comandi.isIstruttoriPalestra(msg)){
            palestreAPI(chatId,gestioneStatoSessione.sessionIstruttoriPalestre());
        }else{
            palestreAPI(chatId,gestioneStatoSessione.sessionInfoPalestra());
        }
    }

    public void startAPI(String chatId) throws Exception{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageHelper.getWelcomeMessage());
        execute(sendMessage);
        System.out.println(messageHelper.getConsoleSuccessApi("Start"));
    }

    public void infoAPI(String chatId) throws Exception{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Questo bot è stato creato allo scopo di fornire informazioni in merito a " +
                "palestre,corsi,istruttori e tutto quello che c'è da sapere sul mondo del fitness nel comune" +
                "di Fano\n\nPotrai accedere alle varie funzioni per consultare le palestre e gli istruttori che vi lavorano" +
                ", i corsi con i relativi insegnanti e tanto altro!");
        execute(sendMessage);
        System.out.println(messageHelper.getConsoleSuccessApi("Info"));
    }


    // creazione dei pulsanti sul bot telegram
    public void infoPalestraAPI(String chatId) throws Exception{
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText("Che tipo di informazioni vuoi sapere a proposito delle palestre di Fano ?");
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        row.add("Istruttori");
        row.add("Orari");
        row.add("Indirizzo");
        // Add the first row to the keyboard
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        // hide inline button after click
        keyboardMarkup.setOneTimeKeyboard(true);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);
        boolean op = gestioneStatoSessione.setSessionState(this.connection,chatId,gestioneStatoSessione.sessionDetailsPalestre());
        sendMessage(message); // Sending our message object to user
        System.out.println(messageHelper.getConsoleSuccessApi("infoPalestra"));
    }

    public void palestreAPI(String chatId, int stato) throws Exception{
        Palestre palestreModel = new Palestre();
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText("Di quale palestra vuoi avere le informazioni?");
        ResultSet rs = palestreModel.getPalestre(this.connection);
        if(rs != null){
            LinkedList<Palestre> listaPalestre = new LinkedList<Palestre>();
            while (rs.next()) {
                Palestre pal = new Palestre();
                pal.nomePalestra = rs.getString("nome");
                listaPalestre.add(pal);
            }
            // Create ReplyKeyboardMarkup object
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            // Create the keyboard (list of keyboard rows)
            List<KeyboardRow> keyboard = new ArrayList<>();
            // Create a keyboard row
            KeyboardRow row = new KeyboardRow();
            for(int i = 0; i < listaPalestre.size(); i++){
                // Set each button, you can also use KeyboardButton objects if you need something else than text
                row.add(listaPalestre.get(i).nomePalestra);
            }
            // Add the first row to the keyboard
            keyboard.add(row);
            // Set the keyboard to the markup
            keyboardMarkup.setKeyboard(keyboard);
            // hide inline button after click
            keyboardMarkup.setOneTimeKeyboard(true);
            // Add it to the message
            message.setReplyMarkup(keyboardMarkup);
            boolean op = gestioneStatoSessione.setSessionState(this.connection,chatId,stato);
            sendMessage(message);
            System.out.println(messageHelper.getConsoleSuccessApi("Palestre"));
        }else{
            SendMessage sendError = new SendMessage();
            sendError.setChatId(chatId);
            sendError.setText(messageHelper.getAlertApiError());
            execute(sendError);
        }
    }

    public void istruttoriPalestraAPI(String chatId, String msg) throws Exception{
        String result = "";
        Istruttori istruttoriModel = new Istruttori();
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId);
        ResultSet rs = istruttoriModel.getIstruttoreByPalestra(this.connection, msg);
        if(rs != null){
            LinkedList<Istruttori> listaIstruttori = new LinkedList<Istruttori>();
            while (rs.next()) {
                Istruttori cor = new Istruttori();
                cor.nome = rs.getString("nome");
                cor.cognome = rs.getString("cognome");
                listaIstruttori.add(cor);
            }
            for(int i = 0; i < listaIstruttori.size(); i++){
                // Set each button, you can also use KeyboardButton objects if you need something else than text
                result += listaIstruttori.get(i).nome + " " + listaIstruttori.get(i).cognome + "\n";
            }
            if(result == "")
                result = messageHelper.getAlertApiError();
            message.setText(result);
            gestioneStatoSessione.setSessionState(connection,chatId,gestioneStatoSessione.sessionStart());

            sendMessage(message);
            System.out.println(messageHelper.getConsoleSuccessApi("istruttoriPalestra"));
        }else{
            SendMessage sendError = new SendMessage();
            sendError.setChatId(chatId);
            sendError.setText(messageHelper.getAlertApiError());
            gestioneStatoSessione.setSessionState(connection,chatId,gestioneStatoSessione.sessionStart());
            sendMessage(sendError);
        }
        try{

        }catch (Exception ex){
            ex.printStackTrace();

            System.out.println(messageHelper.getConsoleErrorApi("istruttoriPalestra"));
        }
    }

    public void dettagliPalestraAPI(String chatId, String msg, String param) throws Exception{
        String result = "";
        Palestre palestreModel = new Palestre();
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId);
        ResultSet rs = palestreModel.getPalestra(this.connection, msg);
        Palestre palestra = new Palestre();
        if(rs != null){
            while (rs.next()) {
                palestra.nomePalestra = rs.getString("nome");
                palestra.orari = rs.getString(param);
                result += "Palestra: " + palestra.nomePalestra + " ,"  + param + ": " + palestra.orari;
            }

            if(result == "")
                result = messageHelper.getAlertApiError();
            message.setText(result);
            gestioneStatoSessione.setSessionState(connection,chatId,gestioneStatoSessione.sessionStart());
            sendMessage(message);
            System.out.println(messageHelper.getConsoleSuccessApi("dettagliPalestra"));
        }else{
            SendMessage sendError = new SendMessage();
            sendError.setChatId(chatId);
            sendError.setText(messageHelper.getAlertApiError());
            gestioneStatoSessione.setSessionState(connection,chatId,gestioneStatoSessione.sessionStart());
            sendMessage(sendError);
        }
    }

    public void corsiAPI(String chatId) throws Exception{
        String result = "";
        Corsi corsiModel = new Corsi();
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId);
        ResultSet rs = corsiModel.getCorsi(this.connection);
        if(rs != null){
            LinkedList<Corsi> listaCorsi= new LinkedList<Corsi>();
            while (rs.next()) {
                Corsi corso = new Corsi();
                corso.nome = rs.getString("nome");
                corso.descrizione = rs.getString("descrizione");
                corso.orario = rs.getString("orario");
                listaCorsi.add(corso);
            }

            for(int i = 0; i < listaCorsi.size(); i++){
                result += "nome corso: " + listaCorsi.get(i).nome + "\n" +
                        "orario: " + listaCorsi.get(i).orario + "\n\n";
            }
            if(result.equals(""))
                result = messageHelper.getAlertApiError();
            message.setText(result);
            sendMessage(message);
            System.out.println(messageHelper.getConsoleSuccessApi("Corsi"));
        }
    }

    public void istruttoriAPI(String chatId) throws Exception{
        String result = "";
        Istruttori istruttoriModel = new Istruttori();
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId);
        ResultSet rs = istruttoriModel.getIstruttori(this.connection);
        if(rs != null){
            LinkedList<Istruttori> listaIstruttori= new LinkedList<Istruttori>();
            while (rs.next()) {
                Istruttori istruttore = new Istruttori();
                istruttore.nome = rs.getString("nome");
                istruttore.cognome = rs.getString("cognome");
                istruttore.IDPalestra = rs.getInt("IDPalestra");
                istruttore.nomeCorso = rs.getString("nomeCorso");
                listaIstruttori.add(istruttore);
            }

            for(int i = 0; i < listaIstruttori.size(); i++){
                Palestre pal = new Palestre();
                ResultSet rs2 = pal.getPalestra(this.connection, listaIstruttori.get(i).IDPalestra);
                String nomePalestra = "";
                while(rs2.next()){
                    nomePalestra += rs2.getString("nome");
                }
                result += "nome : " + listaIstruttori.get(i).nome + "\n" +
                        "cognome: " + listaIstruttori.get(i).cognome + "\n" +
                        "corso: " + listaIstruttori.get(i).nomeCorso + "\n" +
                        "palestra: " + nomePalestra + "\n\n";
            }
            if(result.equals(""))
                result = messageHelper.getAlertApiError();
            message.setText(result);
            sendMessage(message);
            System.out.println(messageHelper.getConsoleSuccessApi("Istruttori"));
        }
    }

}
