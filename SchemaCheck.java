import java.sql.*;
public class SchemaCheck {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
    String user = "postgres.dcshuawxukmbmbojqdsv";
    String pass = "Anupama@1234%";
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      try (ResultSet rs = c.getMetaData().getColumns(null, null, "products", null)) {
        while (rs.next()) {
          System.out.println(rs.getString("COLUMN_NAME") + " | " + rs.getString("TYPE_NAME") + " | " + rs.getString("NULLABLE"));
        }
      }
    }
  }
}
