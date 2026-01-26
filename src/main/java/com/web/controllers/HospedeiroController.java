package com.web.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.model.entities.Hospedeiro;
import com.web.model.entities.Servico;
import com.web.model.repositories.Facade;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/hospedeiro")
public class HospedeiroController {

    @Autowired
    private Facade facade;

    @Autowired
    private HttpSession session;

    /* ========= REGISTRO ========= */
    @PostMapping("/save")
    public String newHospedeiro(Hospedeiro h) {
        try {
            facade.create(h);
            Hospedeiro logado = facade.loginHospedeiro(h.getVulgo(), h.getSenha());
            session.setAttribute("hospedeiroLogado", logado);
            return "redirect:/hospedeiro/home";
        } catch (SQLException e) {
            System.err.println("ERRO NO REGISTRO: " + e.getMessage());
            e.printStackTrace(); 
            return "redirect:/?erroRegistro";
        }
    }

    /* ========= LOGIN ========= */
    @PostMapping("/login")
    public String login(@RequestParam String vulgo,
                        @RequestParam String senha) {

        try {
            Hospedeiro logado = facade.loginHospedeiro(vulgo, senha);
            if (logado != null) {
                session.setAttribute("hospedeiroLogado", logado);
                return "redirect:/hospedeiro/home";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/?erroLogin";
    }

    /* ========= HOME ========= */
    @GetMapping("/home")
    public String home(Model m) {
        Hospedeiro logado = (Hospedeiro) session.getAttribute("hospedeiroLogado");
        if (logado == null) return "redirect:/";
        try {
            List<Servico> meusServicos = facade.readServicoByHospedeiro(logado.getCodigo());
            m.addAttribute("hospedagens", facade.filterHospedagemByHospedeiro(logado.getCodigo()));
            m.addAttribute("servicos", meusServicos); // Garante que a lista vai para o modal de nova hospedagem
        } catch (SQLException e) { e.printStackTrace(); }
        return "hospedeiro/home";
    }

    @GetMapping("/aceitar")
    public String aceitarInteresse(@RequestParam("id") int id, @RequestParam("fugitivoId") int fugitivoId) {
        try {
            facade.confirmarHospedagem(id, fugitivoId);
            return "redirect:/hospedeiro/home"; // O erro pode estar aqui se o redirect falhar
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/hospedeiro/home?erroAceite";
        }
    }

    /* ========= LOGOUT ========= */
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }

}