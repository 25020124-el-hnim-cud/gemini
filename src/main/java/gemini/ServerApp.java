package gemini;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.sql.*;

public class ServerApp {

    // Thong tin ket noi thang den nha kho MySQL cua ban
    private static final String DB_URL = "jdbc:mysql://localhost:3306/app_ban_hang";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = ""; 

    public static void main(String[] args) {
        
        Gson gson = new Gson();

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("=== SERVER DA BAT ===");
            System.out.println("Dang ngoi cho Client go cua o phong 8080...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n[+] Bat duoc mot Client vua ket noi!");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                
                String jsonReceived = in.readLine();
                System.out.println("-> Client noi: " + jsonReceived);

                if (jsonReceived != null && !jsonReceived.isEmpty()) {
                    
                    UserRequest clientData = gson.fromJson(jsonReceived, UserRequest.class);

                    if ("register".equals(clientData.action)) {
                        
                        boolean isSuccess = saveUserToDatabase(clientData);
                        
                        if (isSuccess) {
                            out.println("{\"status\":\"success\", \"message\":\"Tuyet voi! Da dang ky va luu vao Database thanh cong.\"}");
                        } else {
                            out.println("{\"status\":\"error\", \"message\":\"Loi roi! Co the tai khoan nay da ton tai.\"}");
                        }
                    } else {
                        out.println("{\"status\":\"error\", \"message\":\"Khong hieu lenh nay!\"}");
                    }
                }
                
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Loi Server (Co the cong 8080 dang bi chiem dung): " + e.getMessage());
        }
    }

    // --- HAM XU LY DATABASE (JDBC) ---
    private static boolean saveUserToDatabase(UserRequest data) {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, data.username);
            stmt.setString(2, data.password); 
            stmt.setString(3, data.role);
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; 
            
        } catch (SQLException e) {
            System.out.println("Loi luc chui vao Database: " + e.getMessage());
            return false;
        }
    }
}