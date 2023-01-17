import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Palestre {
    public int IDPalestra;
    public String nomePalestra;
    public String orari;
    public int telefono;
    public String indirizzo;

    //GET PALESTRE
    public ResultSet getPalestre(Connection conn) throws SQLException{
        ResultSet rs = null;
        try{
            Statement myStmt = conn.createStatement();
            String sql = "select * from Palestre";
            rs = myStmt.executeQuery(sql);
        }catch(Exception ex){
            System.out.println("An error occurred in getPalestre.");
            ex.printStackTrace();
        }
        return rs;
    }

    // GET SINGOLA PALESTRA
    public ResultSet getPalestra(Connection conn, String nomePalestra) throws SQLException {
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
                String sql = "select * from palestre where idpalestra = " + IDPalestra;
                rs = myStmt.executeQuery(sql);
            }
        }catch(Exception ex){
            System.out.println("An error occurred in getPalestra");
            ex.printStackTrace();
        }
        return rs;
    }

    public ResultSet getPalestra(Connection conn, int IDPalestra) throws SQLException {
        ResultSet rs = null;
        try{
            Statement myStmt = conn.createStatement();
            String sql = "select * from palestre where idpalestra = " + IDPalestra;
            rs = myStmt.executeQuery(sql);
        }catch(Exception ex){
            System.out.println("An error occurred in getPalestra");
            ex.printStackTrace();
        }
        return rs;
    }
}

