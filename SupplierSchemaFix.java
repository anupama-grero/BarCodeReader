import java.sql.*;
public class SupplierSchemaFix {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
    String user = "postgres.dcshuawxukmbmbojqdsv";
    String pass = "Anupama@1234%";
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      try (Statement st = c.createStatement()) {
        st.execute("ALTER TABLE suppliers ADD COLUMN IF NOT EXISTS contact_person VARCHAR(100)");
        st.execute("UPDATE suppliers SET contact_person = name WHERE contact_person IS NULL");
      }
    }
  }
}
