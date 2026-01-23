package com.web.model.repositories;

import com.web.model.entities.Fugitivo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FugitivoRepository implements GenericRepository<Fugitivo, Integer> {
    private final ConnectionManager connectionManager;

    @Autowired
    public FugitivoRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public void create(Fugitivo c) throws SQLException {
       String sql = "INSERT INTO fugitivo (nome, vulgo, especialidade, faccao, descricao, senha) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

       try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getVulgo());
            stmt.setString(3, c.getEspecialidade());
            stmt.setString(4, c.getFaccao());
            stmt.setString(5, c.getDescricao());
            stmt.setString(6, c.getSenha());

            stmt.execute(); 
        }
    }

    @Override
    public void update(Fugitivo c) throws SQLException {
        String sql = "UPDATE fugitivo SET nome=?, vulgo=?, especialidade=?, faccao=?, "
                   + "descricao=? WHERE codigo=?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { 

            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getVulgo());
            stmt.setString(3, c.getEspecialidade());
            stmt.setString(4, c.getFaccao());
            stmt.setString(5, c.getDescricao());
            stmt.setInt(6,c.getCodigo());

            stmt.executeUpdate();
        }
    }

    @Override
    public Fugitivo read(Integer k) throws SQLException {
        String sql = "SELECT * FROM fugitivo WHERE codigo=?";
        Fugitivo f = null;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, k);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    f = mapResultSetToFugitivo(rs);
                }
            }
        }

        return f;
    }

    @Override
    public void delete(Integer k) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Fugitivo> readAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readAll'");
    }

    public Fugitivo login(String vulgo, String senha) throws SQLException{
        String sql = "SELECT * FROM fugitivo WHERE vulgo=? and senha=?";
        Fugitivo f = null;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vulgo);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    f = mapResultSetToFugitivo(rs);
                }
            }
        }
 
        return f;
    } 
    
    private Fugitivo mapResultSetToFugitivo(ResultSet rs) throws SQLException {
        Fugitivo f = new Fugitivo();
        f.setCodigo(rs.getInt("codigo"));
        f.setNome(rs.getString("nome"));
        f.setVulgo(rs.getString("vulgo"));
        f.setEspecialidade(rs.getString("especialidade"));
        f.setFaccao(rs.getString("faccao"));
        f.setDescricao(rs.getString("descricao"));
        f.setSenha(rs.getString("senha"));
        return f;
    }
} 