package com.web.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.model.entities.Hospedagem;
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

    /* ================= REGISTER ================= */
    @PostMapping("/register")
    public String register(Hospedeiro h) {
        try {
            facade.create(h);
            session.setAttribute("hospedeiroLogado", h);
            return "redirect:/hospedeiro/home";

        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/?erroRegistro";
        }
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public String login(@RequestParam String vulgo, @RequestParam String senha) {
        try {
            Hospedeiro logado = facade.loginHospedeiro(vulgo, senha);

            if (logado != null) {
                session.setAttribute("hospedeiroLogado", logado);
                return "redirect:/hospedeiro/home";
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return "redirect:/?erroLogin";
    }

    /* ================= HOME ================= */
    @GetMapping("/home")
    public String home(Model model) {
        Hospedeiro logado = (Hospedeiro) session.getAttribute("hospedeiroLogado");

        if (logado == null) {
            return "redirect:/";
        }

        try {
            // Busca as hospedagens do hospedeiro logado
            List<Hospedagem> hospedagens = facade.filterHospedagemByHospedeiro(logado.getCodigo());
            model.addAttribute("hospedagens", hospedagens);

            // BUSCA TODOS OS SERVIÇOS DISPONÍVEIS (Adicione esta linha)
            List<Servico> servicos = facade.readAllServico();
            model.addAttribute("servicos", servicos);

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Erro ao carregar dados do painel");
        }
        return "hospedeiro/home";
    }

    /* ================= LOGOUT ================= */
    @GetMapping("/logout")
    public String logout() {
        session.removeAttribute("hospedeiroLogado");
        return "redirect:/";
    }
}