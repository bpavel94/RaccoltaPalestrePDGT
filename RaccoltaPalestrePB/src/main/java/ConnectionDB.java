import java.io.File;
import java.util.Scanner;
import org.json.JSONObject;

import java.sql.*;

public class ConnectionDB{

    // metodo che consente di avviare una connessione con il DB
    public Connection getConnectionToDB() throws Exception{
        Connection conn = null;
        try{
            // Classe per la connessione a mysql
            Class.forName("com.mysql.cj.jdbc.Driver");
            // connetto il DB
            System.out.println("Connessione al DB in corso...");
            // leggo il json per le credenziali di accesso al db
            JSONObject json = getCredentialJSON();
            // estraggo user, pwd e connection string
            String user = json.getString("username");
            String pw = json.getString("pw");
            String connectionString = json.getString("connection");
            // effettuo la vera e propria connessione al db
            conn = DriverManager.getConnection(connectionString, user, pw);
            System.out.println("Connesso al DB...");
            // creo lo statement necessario per l'esecuzione delle query
            System.out.println("Sessione avviata");
            // generica
        }catch(Exception ex){
            System.out.println("E' avvenuto un errore in getConnectionToDB.");
            ex.printStackTrace();
        }
        return conn;
    }

    // metodo per leggere ed estrarre le credenziali del DB
    public JSONObject getCredentialJSON() throws Exception {
        JSONObject json = null;
        String data = "";
        try {
            File myObj = new File("src\\main\\resources\\json-files\\Credenziali.json");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
            }
            myReader.close();
            JSONObject json2 = new JSONObject(data);
            json = json2;

        } catch (Exception e) {
            System.out.println("E' avvenuto un errore  in getCredenziali.");
            e.printStackTrace();
        }
        return json;
    }

    // metodo per estrarre il token dal file toke.json che consente di collegarsi al bot telegram
    public String getToken() throws Exception{
        String token = "";
        String data = "";
        try {
            File myObj = new File("src\\main\\resources\\json-files\\Token.json");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
            }
            myReader.close();
            JSONObject json = new JSONObject(data);
            token = json.getString("Token");
        } catch (Exception e) {
            System.out.println("E' avvenuto un errore  in getToken.");
            e.printStackTrace();
        }
        return token;
    }

    // metodo per estrarre l'username dal file toke.json che consente di collegarsi al bot telegram
    public String getUsername() throws Exception{
        String user = "";
        String data = "";
        try {
            File myObj = new File("src\\main\\resources\\json-files\\Token.json");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
            }
            myReader.close();
            JSONObject json = new JSONObject(data);
            user = json.getString("username");
        } catch (Exception e) {
            System.out.println("E' avvenuto un errore  in getToken.");
            e.printStackTrace();
        }
        return user;
    }
}
