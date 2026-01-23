package com.web.model.repositories;

import com.web.model.entities.Hospedeiro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HospedeiroRepository implements GenericRepository<Hospedeiro, Integer> {

    private final ConnectionManager connectionManager;

    @Autowired
    public HospedeiroRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void create(Hospedeiro h) throws SQLException {
        String sql = "INSERT INTO hospedeiro (nome, vulgo, contato, senha) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, h.getNome());
            stmt.setString(2, h.getVulgo());
            stmt.setString(3, h.getContato());
            stmt.setString(4, h.getSenha());

            stmt.execute();
        }
    }

    @Override
    public void update(Hospedeiro h) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Hospedeiro read(Integer k) throws SQLException {
        String sql = "SELECT * FROM hospedeiro WHERE codigo=?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, k);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Hospedeiro h = new Hospedeiro();
                    h.setCodigo(rs.getInt("codigo"));
                    h.setNome(rs.getString("nome"));
                    h.setVulgo(rs.getString("vulgo"));
                    h.setContato(rs.getString("contato"));
                    return h;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(Integer k) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Hospedeiro> readAll() throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'readAll'");
    }

    public Hospedeiro login(String vulgo, String senha) throws SQLException {
        String sql = "SELECT * FROM hospedeiro WHERE vulgo=? AND senha=?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vulgo);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Hospedeiro h = new Hospedeiro();
                    h.setCodigo(rs.getInt("codigo"));
                    h.setNome(rs.getString("nome"));
                    h.setVulgo(rs.getString("vulgo"));
                    h.setContato(rs.getString("contato"));
                    return h;
                }
            }
        }
        return null;
    }
}
