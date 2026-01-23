package com.web.model.repositories;

import com.web.model.entities.Fugitivo;
import com.web.model.entities.Hospedagem;
import com.web.model.entities.Hospedeiro;
import com.web.model.entities.Servico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.sql.SQLException; 
import java.util.List;

@Repository
public class Facade {

    private final ServicoRepository rServico;
    private final FugitivoRepository rFugitivo;
    private final HospedeiroRepository rHospedeiro;
    private final HospedagemRepository rHospedagem;

    @Autowired
    public Facade(ServicoRepository rServico,
                  FugitivoRepository rFugitivo,
                  HospedeiroRepository rHospedeiro,
                  HospedagemRepository rHospedagem) {
        this.rServico = rServico;
        this.rFugitivo = rFugitivo;
        this.rHospedeiro = rHospedeiro;
        this.rHospedagem = rHospedagem;
    }

    // ---------------- SERVICO ----------------
    public void create(Servico s) throws SQLException {
        this.rServico.create(s);
    }

    public void update(Servico s) throws SQLException {
        this.rServico.update(s);
    }

    public Servico readServico(int codigo) throws SQLException {
        return this.rServico.read(codigo);
    }

    public void deleteServico(int codigo) throws SQLException {
        this.rServico.delete(codigo);
    }

    public List<Servico> readAllServico() throws SQLException {
        return this.rServico.readAll();
    }

    public List<Servico> readServicoByHospedeiro(int hospedeiroId) throws SQLException {
        return this.rServico.readByHospedeiro(hospedeiroId);
    }

    // ---------------- FUGITIVO ----------------
    public void create(Fugitivo f) throws SQLException {
        this.rFugitivo.create(f);
    }

    public void update(Fugitivo f) throws SQLException {
        this.rFugitivo.update(f);
    }

    public Fugitivo readFugitivo(int codigo) throws SQLException {
        return this.rFugitivo.read(codigo);
    }

    public Fugitivo loginFugitivo(String vulgo, String senha) throws SQLException {
        return this.rFugitivo.login(vulgo, senha);
    }

    // ---------------- HOSPEDEIRO ----------------
    public void create(Hospedeiro h) throws SQLException {
        this.rHospedeiro.create(h);
    }

    public Hospedeiro loginHospedeiro(String vulgo, String senha) throws SQLException {
        return this.rHospedeiro.login(vulgo, senha);
    }

    // ---------------- HOSPEDAGEM ----------------
    public void create(Hospedagem h) throws SQLException {
        this.rHospedagem.create(h);
    }

    public void deleteHospedagem(int codigo) throws SQLException {
        this.rHospedagem.delete(codigo);
    }

    public Hospedagem readHospedagem(int codigo) throws SQLException {
        return this.rHospedagem.read(codigo);
    }

    public List<Hospedagem> filterHospedagemByAvailable() throws SQLException {
        return this.rHospedagem.filterByAvailable();
    }

    public List<Hospedagem> filterHospedagemByHospedeiro(int codigoHospedeiro) throws SQLException {
        return this.rHospedagem.filterByHospedeiro(codigoHospedeiro);
    }

    public List<Hospedagem> filterHospedagemByCriterios(String local, Double preco) throws SQLException {
        return this.rHospedagem.filterByCriterios(local, preco);
    }
}
