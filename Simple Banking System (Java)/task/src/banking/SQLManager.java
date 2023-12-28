package banking;

import java.sql.*;

public class SQLManager {
    private final String url;

    public SQLManager(String url) {
        this.url = url;
        createNewDatabase();
    }

    public void insertAccount(Account account) {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO card (number, pin, balance) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, account.getCardNumber());
                pstmt.setString(2, account.getPIN());
                pstmt.setInt(3, account.getBalance());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error while adding account: " + e.getMessage());
        }
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "CREATE TABLE IF NOT EXISTS card (" +
                    "id INTEGER PRIMARY KEY," +
                    "number TEXT NOT NULL," +
                    "pin TEXT NOT NULL," +
                    "balance INTEGER DEFAULT 0" +
                    ");";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println("Error while creating table: " + e.getMessage());
        }
    }

    public boolean correctAccountCredentials(String userCardInput, String userPINInput) {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT * FROM card WHERE number = ? AND pin = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userCardInput);
                pstmt.setString(2, userPINInput);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while searching for account: " + e.getMessage());
        }
        return false;
    }

    public Integer getAccountBalance(String userCardInput, String userPINInput) {
        if (correctAccountCredentials(userCardInput, userPINInput)) {
            try (Connection conn = DriverManager.getConnection(url)) {
                String sql = "SELECT balance FROM card WHERE number = ? AND pin = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, userCardInput);
                    pstmt.setString(2, userPINInput);
                    try (ResultSet resultSet = pstmt.executeQuery()) {
                        return resultSet.next() ? resultSet.getInt("balance") : null;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error while retrieving account balance: " + e.getMessage());
            }
        }
        return null;
    }

    public void addToBalance(String cardNumber, double amountToAdd) {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amountToAdd);
                pstmt.setString(2, cardNumber);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error while updating account balance: " + e.getMessage());
        }
    }

    public boolean transferFromBalance(Account account, double amountToTransfer) {
        Integer currAccountBalance = getAccountBalance(account.getCardNumber(), account.getPIN());
        if (currAccountBalance != null && currAccountBalance >= amountToTransfer) {
            try (Connection conn = DriverManager.getConnection(url)) {
                String sql = "UPDATE card SET balance = balance - ? WHERE number = ? AND pin = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setDouble(1, amountToTransfer);
                    pstmt.setString(2, account.getCardNumber());
                    pstmt.setString(3, account.getPIN());
                    pstmt.executeUpdate();
                }
                return true;
            } catch (SQLException e) {
                System.out.println("Error while updating account balance: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean findAccount(String userCardInput) {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT * FROM card WHERE number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userCardInput);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while searching for account: " + e.getMessage());
        }
        return false;
    }

    public void closeAccount(String cardNumber) {
        try (Connection conn = DriverManager.getConnection(url)) {
            String deleteSql = "DELETE FROM card WHERE number = ?";
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setString(1, cardNumber);
                deletePstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error while closing the account: " + e.getMessage());
        }
    }
}
