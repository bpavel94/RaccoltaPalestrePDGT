import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Corsi {

    public String nome;
    public String descrizione;
    public String orario;

    // GET CORSI
    public ResultSet getCorsi(Connection conn) throws SQLException {
        ResultSet rs = null;
        try{
            Statement myStmt = conn.createStatement();
            String sql = "select * from corsi";
            rs = myStmt.executeQuery(sql);
        }catch(Exception ex){
            System.out.println("An error occurred in getCorsi.");
            ex.printStackTrace();
        }
        return rs;
    }
}
