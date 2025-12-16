import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/fuzz_testing_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&allowZeroDates=true&zeroDateTimeBehavior=convertToNull";
        String username = "root";
        String password = "123456";

        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("驱动加载成功");

            // 建立连接
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功");

            // 执行查询
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_case");
            if (rs.next()) {
                System.out.println("test_case表中有" + rs.getInt(1) + "条记录");
            }

            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("数据库连接已关闭");
        } catch (Exception e) {
            System.err.println("错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}