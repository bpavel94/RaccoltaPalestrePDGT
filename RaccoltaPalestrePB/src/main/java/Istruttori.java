import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Istruttori {
    public int IDIstruttore;
    public int IDPalestra;
    public String nome;
    public String cognome;
    public String nomeCorso;

    // GET SINGOLO ISTRUTTORE
    public ResultSet getIstruttore(Connection conn, int IDIstruttore) throws SQLException {
        ResultSet rs = null;
        try{
            Statement myStmt = conn.createStatement();
            String sql = "select * from istruttori where idistruttore = " + IDIstruttore;
            rs = myStmt.executeQuery(sql);
        }catch(Exception ex){
            System.out.println("An error occurred in getIstruttore.");
            ex.printStackTrace();
        }
        return rs;
    }

    public ResultSet getIstruttori(Connection conn) throws SQLException{
        ResultSet rs = null;
        try{
            Statement myStmt = conn.createStatement();
            String sql = "select istruttori.nome as nome, istruttori.cognome as cognome, istruttori.IDPalestra as IDPalestra" +
                    ", corsi.nome as nomeCorso from istruttori inner join corsi on istruttori.IDIstruttore = corsi.IDIstruttore";
            rs = myStmt.executeQuery(sql);
        }catch(Exception ex){
            System.out.println("C'è stato un errore in getIstruttori.");
            ex.printStackTrace();
        }
        return rs;
    }

    public ResultSet getIstruttoreByPalestra(Connection conn, String nomePalestra) throws SQLException{
        ResultSet rs = null;
        try{
            Statement myStmt = conn.createStatement();
            String pal = "select * from palestre where nome = '" + nomePalestra + "'";
            ResultSet resp = myStmt.executeQuery(pal);
            int IDPalestra = 0;
            while(resp.next()){
                IDPalestra = resp.getInt("IDPalestra");
            }
            if(IDPalestra != 0){
                String sql = "select * from istruttori where IDPalestra = " + IDPalestra;
                rs = myStmt.executeQuery(sql);
            }
        }catch(Exception ex){
            System.out.println("C'è stato un errore in getIstruttoreByPalestra.");
            ex.printStackTrace();
        }
        return rs;
    }
}

