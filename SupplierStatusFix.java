import java.sql.*;
public class SupplierStatusFix {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
    String user = "postgres.dcshuawxukmbmbojqdsv";
    String pass = "Anupama@1234%";
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      try (Statement st = c.createStatement()) {
        st.execute("ALTER TABLE suppliers ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'");
        st.execute("UPDATE suppliers SET status = 'ACTIVE' WHERE status IS NULL");
      }
    }
  }
}
