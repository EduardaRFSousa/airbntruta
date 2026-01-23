package com.web.model.repositories;

import com.web.model.entities.Servico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ServicoRepository implements GenericRepository<Servico, Integer> {

    private final ConnectionManager connectionManager;

    @Autowired
    public ServicoRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void create(Servico s) throws SQLException {
        // Adicionado hospedeiro_id no INSERT
        String sql = "INSERT INTO servico(nome, tipo, descricao, hospedeiro_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNome());
            stmt.setString(2, s.getTipo());
            stmt.setBytes(3, s.getDescricao().getBytes());
            stmt.setInt(4, s.getHospedeiroId());
            stmt.execute();
        }
    }

    @Override
    public void update(Servico s) throws SQLException {
        String sql = "UPDATE servico SET nome=?, tipo=?, descricao=? WHERE codigo=?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getNome());
            stmt.setString(2, s.getTipo());
            stmt.setBytes(3, s.getDescricao().getBytes());
            stmt.setInt(4, s.getCodigo());

            stmt.execute();
        }
    }

    @Override
    public Servico read(Integer k) throws SQLException {
        String sql = "SELECT * FROM servico WHERE codigo=?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, k);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Servico s = new Servico();
                    s.setCodigo(rs.getInt("codigo"));
                    s.setNome(rs.getString("nome"));
                    s.setTipo(rs.getString("tipo"));
                    s.setDescricao(new String(rs.getBytes("descricao")));
                    return s;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(Integer k) throws SQLException {
        String sql = "DELETE FROM servico WHERE codigo=?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, k);
            stmt.execute();
        }
    }

    @Override
    public List<Servico> readAll() throws SQLException {
        String sql = "SELECT * FROM servico";
        List<Servico> lista = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servico s = new Servico();
                s.setCodigo(rs.getInt("codigo"));
                s.setNome(rs.getString("nome"));
                s.setTipo(rs.getString("tipo"));
                s.setDescricao(new String(rs.getBytes("descricao")));
                lista.add(s);
            }
        }
        return lista;
    }

    public List<Servico> filterByHospedagem(int hospedagemId) throws SQLException {
        String sql = "SELECT s.* FROM servico s " +
                     "JOIN hospedagem_servico hs ON s.codigo = hs.servico_id " +
                     "WHERE hs.hospedagem_id=?";

        List<Servico> lista = new ArrayList<>();
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hospedagemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servico s = new Servico();
                    s.setCodigo(rs.getInt("codigo"));
                    s.setNome(rs.getString("nome"));
                    s.setTipo(rs.getString("tipo"));
                    s.setDescricao(new String(rs.getBytes("descricao")));
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    public List<Servico> readByHospedeiro(int hospedeiroId) throws SQLException {
        String sql = "SELECT * FROM servico WHERE hospedeiro_id = ?";
        List<Servico> lista = new ArrayList<>();
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hospedeiroId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servico s = new Servico();
                    s.setCodigo(rs.getInt("codigo"));
                    s.setNome(rs.getString("nome"));
                    s.setTipo(rs.getString("tipo"));
                    s.setDescricao(new String(rs.getBytes("descricao")));
                    s.setHospedeiroId(rs.getInt("hospedeiro_id"));
                    lista.add(s);
                }
            }
        }
        return lista;
    }
}
