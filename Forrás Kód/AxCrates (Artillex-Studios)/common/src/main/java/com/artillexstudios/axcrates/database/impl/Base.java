package com.artillexstudios.axcrates.database.impl;

import com.artillexstudios.axcrates.database.Database;
import com.artillexstudios.axcrates.keys.Key;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Base implements Database {

    public Connection getConnection() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setup() {
        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_keys (
                	id INT NOT NULL AUTO_INCREMENT,
                	name VARCHAR(512) NOT NULL,
                	UNIQUE (name),
                	PRIMARY KEY (id)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_crates (
                	id INT NOT NULL AUTO_INCREMENT,
                	name VARCHAR(512) NOT NULL,
                	UNIQUE (name),
                	PRIMARY KEY (id)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_players (
                	id INT NOT NULL AUTO_INCREMENT,
                	uuid VARCHAR(36) NOT NULL,
                	name VARCHAR(512) NOT NULL,
                	UNIQUE (uuid),
                	PRIMARY KEY (id)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_keydata (
                	playerId INT NOT NULL,
                	keyId INT NOT NULL,
                	amount INT NOT NULL,
                	PRIMARY KEY (playerId, keyId)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_statistics (
                	playerId INT NOT NULL,
                	crateId INT NOT NULL,
                	opens INT NOT NULL,
                	lastOpen BIGINT NOT NULL,
                	PRIMARY KEY (playerId, crateId)
                );
                """
        );
    }

    private void execute(String sql) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getKeyId(Key key) {
        String sql = "SELECT id FROM axcrates_keys WHERE name = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String sql2 = "INSERT INTO axcrates_keys (name) VALUES (?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, key.name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Kulcs nem található az adatbázisban!");
    }

    @Override
    public int getPlayerId(OfflinePlayer player) {
        String sql = "SELECT id FROM axcrates_players WHERE uuid = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String sql2 = "INSERT INTO axcrates_players (uuid, name) VALUES (?, ?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Kulcs nem található az adatbázisban!");
    }

    @Override
    public void setVirtualKey(OfflinePlayer player, Key key, int amount) {
        String sql = "UPDATE axcrates_keydata SET amount = amount + ? WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, getPlayerId(player));
            stmt.setInt(3, getKeyId(key));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void giveVirtualKey(OfflinePlayer player, Key key, int amount) {
        if (createPlayerKey(player, key, amount)) return;

        String sql = "UPDATE axcrates_keydata SET amount = amount + ? WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, getPlayerId(player));
            stmt.setInt(3, getKeyId(key));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean takeVirtualKey(OfflinePlayer player, Key key, int amount) {
        if (createPlayerKey(player, key, 0)) return false;

        int keys = getVirtualKeys(player, key);
        amount = Math.min(keys, amount);

        String sql = "UPDATE axcrates_keydata SET amount = amount - ? WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, getPlayerId(player));
            stmt.setInt(3, getKeyId(key));
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean createPlayerKey(OfflinePlayer player, Key key, int amount) {
        String sql = "INSERT INTO axcrates_keydata (playerId, keyId, amount) VALUES (?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getKeyId(key));
            stmt.setInt(3, amount);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public void resetVirtualKey(OfflinePlayer player, Key key) {
        String sql = "DELETE FROM axcrates_keydata WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getKeyId(key));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void reset(OfflinePlayer player) {
        String sql = "DELETE FROM axcrates_keydata WHERE playerId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getVirtualKeys(OfflinePlayer player, Key key) {
        String sql = "SELECT amount FROM axcrates_keydata WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getKeyId(key));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public void disable() {

    }
}
