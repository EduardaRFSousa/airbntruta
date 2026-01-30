package com.web.model.repositories;

import com.web.model.entities.Hospedagem;
import com.web.model.entities.Servico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HospedagemRepository implements GenericRepository<Hospedagem, Integer> {

    private final ConnectionManager connectionManager;
    private final HospedeiroRepository hospedeiroRepository;
    private final FugitivoRepository fugitivoRepository;
    private final ServicoRepository servicoRepository;  
    private final InteresseRepository interesseRepository; 

    @Autowired
    public HospedagemRepository(ConnectionManager connectionManager,
                                HospedeiroRepository hospedeiroRepository,
                                FugitivoRepository fugitivoRepository,
                                ServicoRepository servicoRepository,
                                InteresseRepository interesseRepository) {
        this.connectionManager = connectionManager;
        this.hospedeiroRepository = hospedeiroRepository;
        this.fugitivoRepository = fugitivoRepository;
        this.servicoRepository = servicoRepository;  
        this.interesseRepository = interesseRepository;
    }

    @Override
    public void create(Hospedagem h) throws SQLException {
        String sql = "INSERT INTO hospedagem (descricao_curta, descricao_longa, localizacao, diaria, inicio, hospedeiro_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, h.getDescricaoCurta());
            stmt.setString(2, h.getDescricaoLonga());
            stmt.setString(3, h.getLocalizacao());
            stmt.setDouble(4, h.getDiaria());
            stmt.setDate(5, new java.sql.Date(h.getInicio().getTime()));
            stmt.setInt(6, h.getHospedeiro().getCodigo());

            stmt.execute();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                h.setCodigo(generatedKeys.getInt(1));
            }

            if (h.getServicos() != null && !h.getServicos().isEmpty()) {
                sql = "INSERT INTO hospedagem_servico (hospedagem_id, servico_id) VALUES (?, ?)";
                try (PreparedStatement stmtServico = conn.prepareStatement(sql)) {
                    for (Servico s : h.getServicos()) {
                        stmtServico.setInt(1, h.getCodigo());
                        stmtServico.setInt(2, s.getCodigo());
                        stmtServico.addBatch();
                    }
                    stmtServico.executeBatch();
                }
            }
        }
    }

    @Override
    public void update(Hospedagem h) throws SQLException {
        // Implementar update se necessário, usando try-with-resources
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Hospedagem read(Integer k) throws SQLException {
        String sql = "SELECT * FROM hospedagem WHERE codigo = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, k);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                Hospedagem h = mapResultSetToHospedagem(rs);
                return h;
            }
        }
    }

    @Override
    public void delete(Integer k) throws SQLException {
        String sql = "DELETE FROM hospedagem WHERE codigo = ?";
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, k);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Hospedagem> readAll() throws SQLException {
        String sql = "SELECT * FROM hospedagem";
        return filterBy(sql);
    }

    public List<Hospedagem> filterBy(String sql) throws SQLException {
        List<Hospedagem> hospedagens = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                hospedagens.add(mapResultSetToHospedagem(rs));
            }
        }

        return hospedagens;
    }

    public List<Hospedagem> filterByHospedeiro(int codigoHospedeiro) throws SQLException {
        String sql = "SELECT * FROM hospedagem WHERE hospedeiro_id = " + codigoHospedeiro;
        return filterBy(sql);
    }

    public List<Hospedagem> filterByCriterios(String local, Double preco) throws SQLException {
        String sql = "SELECT * FROM hospedagem WHERE disponivel = true"; // Alterado aqui
        if (local != null && !local.isEmpty()) sql += " AND localizacao LIKE ?";
        if (preco != null) sql += " AND diaria <= ?";

        List<Hospedagem> lista = new ArrayList<>();
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int pos = 1;
            if (local != null && !local.isEmpty()) stmt.setString(pos++, "%" + local + "%");
            if (preco != null) stmt.setDouble(pos++, preco);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToHospedagem(rs)); // Use seu método de mapeamento
                }
            }
        }
        return lista;
    }

    public List<Hospedagem> filterByAvailable(int fugitivoLogadoId) throws SQLException {
        String sql = "SELECT * FROM hospedagem h WHERE h.disponivel = true " +
                    "AND h.codigo NOT IN (SELECT i.hospedagem_id FROM interesse i WHERE i.fugitivo_id = ?)";
        
        List<Hospedagem> lista = new ArrayList<>();
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fugitivoLogadoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToHospedagem(rs));
                }
            }
        }
        return lista;
    }

    public void registrarInteresse(int hospedagemId, int fugitivoId) throws SQLException {
        String sql = "UPDATE hospedagem SET fugitivo_id = ? WHERE codigo = ?";

        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fugitivoId);
            stmt.setInt(2, hospedagemId);
            stmt.executeUpdate();
        }
    }

    public List<Hospedagem> filterByFugitivo(int fugitivoId) throws SQLException {
        String sql = "SELECT h.* FROM hospedagem h " +
                    "JOIN interesse i ON h.codigo = i.hospedagem_id " +
                    "WHERE i.fugitivo_id = ?";
        List<Hospedagem> lista = new ArrayList<>();
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fugitivoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToHospedagem(rs));
                }
            }
        }
        return lista;
    }

    /* ================= REMOVER INTERESSE ================= */
    public void removerInteresse(int hospedagemId, int fugitivoId) throws SQLException {
        String sqlInteresse = "DELETE FROM interesse WHERE hospedagem_id = ? AND fugitivo_id = ?";
        String sqlHospedagem = "UPDATE hospedagem SET fugitivo_id = NULL, disponivel = true WHERE codigo = ? AND fugitivo_id = ?";
        
        try (Connection conn = connectionManager.getConnection()) {
            conn.setAutoCommit(false); // Inicia transação
            try (PreparedStatement st1 = conn.prepareStatement(sqlInteresse);
                PreparedStatement st2 = conn.prepareStatement(sqlHospedagem)) {
                
                st1.setInt(1, hospedagemId);
                st1.setInt(2, fugitivoId);
                st1.executeUpdate();
                
                st2.setInt(1, hospedagemId);
                st2.setInt(2, fugitivoId);
                st2.executeUpdate();
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private Hospedagem mapResultSetToHospedagem(ResultSet rs) throws SQLException {
        Hospedagem h = new Hospedagem();
        h.setCodigo(rs.getInt("codigo"));
        h.setDescricaoCurta(rs.getString("descricao_curta"));
        h.setDiaria(rs.getDouble("diaria"));
        h.setLocalizacao(rs.getString("localizacao"));
        h.setDisponivel(rs.getBoolean("disponivel"));
        h.setServicos(this.servicoRepository.filterByHospedagem(h.getCodigo()));
        h.setInteressados(this.interesseRepository.findFugitivosByHospedagem(h.getCodigo()));
        h.setHospedeiro(this.hospedeiroRepository.read(rs.getInt("hospedeiro_id")));

        return h;
    }

    /* ================= CONFIRMAR HOSPEDAGEM ================= */
    public void confirmarHospedagem(int hospedagemId, int fugitivoId) throws SQLException {
        String sql = "UPDATE hospedagem SET fugitivo_id = ?, disponivel = false WHERE codigo = ?";
        try (Connection conn = connectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fugitivoId);
            stmt.setInt(2, hospedagemId);
            stmt.executeUpdate();
        }
    }
}
