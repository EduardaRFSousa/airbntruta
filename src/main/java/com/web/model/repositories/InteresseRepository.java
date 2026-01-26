package com.web.model.repositories;

import com.web.model.entities.Fugitivo;
import com.web.model.entities.Interesse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InteresseRepository implements GenericRepository<Interesse,Integer> {

    private final ConnectionManager connectionManager;

    @Autowired
    public InteresseRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void create(Interesse i) throws SQLException {
        String sql = "INSERT INTO interesse (realizado, proposta, tempo_permanencia, fugitivo_id, hospedagem_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, i.getRealizado());
            stmt.setString(2, i.getProposta());
            stmt.setInt(3, i.getTempoPermanencia());
            stmt.setInt(4, i.getInteressado().getCodigo());
            stmt.setInt(5, i.getInteresse().getCodigo());

            stmt.execute();
        }
    }

    public List<Fugitivo> findFugitivosByHospedagem(int hospedagemId) throws SQLException {
        String sql = "SELECT f.* FROM fugitivo f " +
                    "JOIN interesse i ON f.codigo = i.fugitivo_id " +
                    "WHERE i.hospedagem_id = ?";
        List<Fugitivo> lista = new ArrayList<>();
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hospedagemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Fugitivo f = new Fugitivo();
                    f.setCodigo(rs.getInt("codigo"));
                    f.setVulgo(rs.getString("vulgo"));
                    lista.add(f);
                }
            }
        }
        return lista;
    }

    @Override
    public void update(Interesse c) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Interesse read(Integer k) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public void delete(Integer k) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Interesse> readAll() throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'readAll'");
    }
}
