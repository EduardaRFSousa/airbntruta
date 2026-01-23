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

    @Autowired
    public HospedagemRepository(ConnectionManager connectionManager,
                                HospedeiroRepository hospedeiroRepository,
                                FugitivoRepository fugitivoRepository,
                                ServicoRepository servicoRepository) {
        this.connectionManager = connectionManager;
        this.hospedeiroRepository = hospedeiroRepository;
        this.fugitivoRepository = fugitivoRepository;
        this.servicoRepository = servicoRepository;
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
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
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

    public List<Hospedagem> filterByAvailable() throws SQLException {
        String sql = "SELECT * FROM hospedagem WHERE fugitivo_id IS NULL";
        return filterBy(sql);
    }

    public List<Hospedagem> filterByHospedeiro(int codigoHospedeiro) throws SQLException {
        String sql = "SELECT * FROM hospedagem WHERE hospedeiro_id = " + codigoHospedeiro;
        return filterBy(sql);
    }


    private Hospedagem mapResultSetToHospedagem(ResultSet rs) throws SQLException {
        Hospedagem h = new Hospedagem();
        h.setCodigo(rs.getInt("codigo"));
        h.setDescricaoCurta(rs.getString("descricao_curta"));
        h.setDescricaoLonga(rs.getString("descricao_longa"));
        h.setLocalizacao(rs.getString("localizacao"));
        h.setDiaria(rs.getDouble("diaria"));
        h.setInicio(rs.getDate("inicio"));
        h.setFim(rs.getDate("fim"));

        // Carregar relacionamentos via repositórios injetados
        h.setHospedeiro(hospedeiroRepository.read(rs.getInt("hospedeiro_id")));
        h.setFugitivo(fugitivoRepository.read(rs.getInt("fugitivo_id")));
        h.setServicos(servicoRepository.filterByHospedagem(h.getCodigo()));

        return h;
    }
}
