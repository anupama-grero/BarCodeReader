import java.sql.*;
public class SchemaAlter {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
    String user = "postgres.dcshuawxukmbmbojqdsv";
    String pass = "Anupama@1234%";
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      try (Statement st = c.createStatement()) {
        st.execute("ALTER TABLE products ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'");
        st.execute("UPDATE products SET status = 'ACTIVE' WHERE status IS NULL");
      }
      try (ResultSet rs = c.getMetaData().getColumns(null, null, "products", null)) {
        while (rs.next()) {
          System.out.println(rs.getString("COLUMN_NAME") + " | " + rs.getString("TYPE_NAME") + " | " + rs.getString("NULLABLE"));
        }
      }
    }
  }
}
