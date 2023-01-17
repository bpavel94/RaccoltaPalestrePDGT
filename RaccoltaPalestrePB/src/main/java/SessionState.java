import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


// classe per gestire lo stato della sessione
// Serve per tenere traccia dell'esequzione cronologica delle operazioni al fine di garantire il corretto funzionamento
public class SessionState {

    // sessionStart = 0
    public int sessionStart(){
        return 0;
    }

    // sessionDetailsPalestre = 1
    public int sessionDetailsPalestre(){
        return 1;
    }

    // sessionAddressPalestre = 2
    public int sessionAddressPalestre(){ return 2; }

    // sessionIstruttoriPalestre = 3
    public int sessionIstruttoriPalestre(){
        return 3;
    }

    // sessionInfoPalestra = 4
    public int sessionInfoPalestra(){
        return 4;
    }

    // legge lo stato attuale della sessione
    public int getUserSessionState(String chatID, Connection connection){
        int ret = 0;
        try{
            Statement myStmt = connection.createStatement();
            String sql = "select * from utenti where chatID = " + chatID;
            ResultSet rs = myStmt.executeQuery(sql);
            int cont = 0;

            while (rs.next()) {
                cont++;
                ret = Integer.parseInt(rs.getString("StatoSessione"));
            }

            if(cont > 1){
                String sql2 = "delete from utenti where chatID = " + chatID;
                myStmt.executeQuery(sql2);
                ret = 0;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ret;
    }

    // aggiorno (setta il nuovo stato della sessione)
    public boolean setSessionState(Connection connection, String chatID, int sessionState){
        boolean ret = false;
        try{
            Statement myStmt = connection.createStatement();
            String sql = "select * from utenti where chatID = " + chatID;
            ResultSet rs = myStmt.executeQuery(sql);
            if(rs.next() != false){
                PreparedStatement updateEXP = connection.prepareStatement("update `Utenti` set `statoSessione` = " + sessionState + " where `chatID` = '"+chatID+"'");
                updateEXP.execute();
                ret = true;
            }else{
                // the mysql insert statement
                String query = " insert into utenti (chatID, statoSessione)"
                        + " values (?, ?)";
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt =  connection.prepareStatement(query);
                preparedStmt.setString (1, chatID);
                preparedStmt.setInt (2, sessionState);
                preparedStmt.execute();
                ret = true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ret;
    }
}
