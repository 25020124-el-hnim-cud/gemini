package gemini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;

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

                    // Kiem tra xem Client muon lam gi
                    if ("register".equals(clientData.action)) {
                        
                        boolean isSuccess = saveUserToDatabase(clientData);
                        if (isSuccess) {
                            out.println("{\"status\":\"success\", \"message\":\"Tuyet voi! Da dang ky thanh cong.\"}");
                        } else {
                            out.println("{\"status\":\"error\", \"message\":\"Loi! Tai khoan co the da ton tai.\"}");
                        }
                        
                    } else if ("login".equals(clientData.action)) { // <--- THÊM ĐOẠN NÀY
                        
                        // Goi ham check Database
                        String userRole = checkUserLogin(clientData.username, clientData.password);
                        
                        if (userRole != null) {
                            // Neu dung, tra ve success va gui kem cai Role luon
                            out.println("{\"status\":\"success\", \"message\":\"Dang nhap thanh cong!\", \"role\":\"" + userRole + "\"}");
                        } else {
                            out.println("{\"status\":\"error\", \"message\":\"Sai tai khoan hoac mat khau!\"}");
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

    // --- HAM CHECK DANG NHAP ---
    private static String checkUserLogin(String username, String password) {
        // Dung lenh SELECT de tim tai khoan. Chi lay cot 'role' cho nhe mang.
        String sql = "SELECT role FROM users WHERE username = ? AND password_hash = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            // Dung executeQuery de lay ket qua tu lenh SELECT
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Neu tim thay 1 dong, lay gia tri cua cot 'role' tra ve
                    return rs.getString("role"); 
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Loi luc check Dang nhap trong Database: " + e.getMessage());
        }
        
        return null; // Neu khong tim thay hoac loi thi tra ve null
    }
}