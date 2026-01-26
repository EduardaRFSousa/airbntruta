package com.web.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.web.model.entities.Fugitivo;
import com.web.model.entities.Hospedagem;
import com.web.model.entities.Interesse;
import com.web.model.repositories.Facade;

import jakarta.servlet.http.HttpSession; 
import org.springframework.web.bind.annotation.PostMapping; 

@Controller
@RequestMapping("/fugitivo")
public class FugitivoController {
    @Autowired
    private Facade facade;
    @Autowired
    private HttpSession session;

    @PostMapping("/registrar")
    public String registrar(Fugitivo f) {
        try {
            facade.create(f);
            Fugitivo logado = facade.loginFugitivo(f.getVulgo(), f.getSenha());
            session.setAttribute("fugitivoLogado", logado);
            return "redirect:/fugitivo/home";
        } catch (SQLException e) {
            System.err.println("ERRO NO REGISTRO: " + e.getMessage());
            e.printStackTrace(); 
            return "redirect:/?erroRegistro";
        }
    }


    @PostMapping("/login")
    public String login(@RequestParam String vulgo,
                        @RequestParam String senha) {

        try {
            Fugitivo logado = facade.loginFugitivo(vulgo, senha);
            if (logado != null) {
                session.setAttribute("fugitivoLogado", logado);
                return "redirect:/fugitivo/home";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/?erroLogin";
    }

    /* ================= HOME COM FILTROS ================= */
    @GetMapping("/home")
    public String homeFugitivo(Model model, 
                            @RequestParam(required = false) String localizacao, 
                            @RequestParam(required = false) Double precoMax) {
        
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        if (logado == null) return "redirect:/";

        try {
            // Aba 1: Hospedagens disponíveis (com ou sem filtro)
            List<Hospedagem> disponiveis;
            if ((localizacao != null && !localizacao.isEmpty()) || precoMax != null) {
                disponiveis = facade.filterHospedagemByCriterios(localizacao, precoMax);
            } else {
                disponiveis = facade.filterHospedagemByAvailable(logado.getCodigo());
            }

            // Aba 2: Hospedagens que o fugitivo logado demonstrou interesse
            // Certifique-se de ter esse método na Facade/Repository
            List<Hospedagem> meusInteresses = facade.filterHospedagemByFugitivo(logado.getCodigo());

            model.addAttribute("hospedagens", disponiveis);
            model.addAttribute("interesses", meusInteresses);
            model.addAttribute("localAtual", localizacao);
            model.addAttribute("precoAtual", precoMax);

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Erro ao carregar esconderijos.");
        }
        return "fugitivo/home";
    }

    /* ================= MANIFESTAR INTERESSE ================= */
    @GetMapping("/interesse")
    public String manifestarInteresse(@RequestParam("id") int hospedagemId) {
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        if (logado == null) return "redirect:/";

        try {
            // Criamos o objeto de interesse para popular a tabela 'interesse'
            Interesse novoInteresse = new Interesse();
            novoInteresse.setInteressado(logado); // O fugitivo logado
            
            // Criamos uma hospedagem temporária apenas com o ID para vincular
            Hospedagem h = new Hospedagem();
            h.setCodigo(hospedagemId);
            novoInteresse.setInteresse(h);
            
            // Tempo de permanência padrão ou vindo de formulário (ex: 7 dias)
            novoInteresse.setTempoPermanencia(7); 
            novoInteresse.setProposta("Preciso de abrigo urgente!");

            // Chamada correta na Facade
            facade.createInteresse(novoInteresse); 
            
            return "redirect:/fugitivo/home?tab=interesses";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/fugitivo/home?erroInteresse";
        }
    }

    /* ================= REMOVER INTERESSE ================= */
    @GetMapping("/removerInteresse")
    public String removerInteresse(@RequestParam("id") int hospedagemId) {
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        try {
            facade.removerInteresse(hospedagemId, logado.getCodigo());
            return "redirect:/fugitivo/home?tab=interesses";
        } catch (SQLException e) { return "redirect:/fugitivo/home?erro"; }
    }

    /* ================= LOGOUT ================= */
    @GetMapping("/logout")
    public String logout() {
        session.removeAttribute("fugitivoLogado");
        return "redirect:/";
    }
}