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

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public String login(@RequestParam String vulgo, @RequestParam String senha) {
        try {
            Fugitivo logado = facade.loginFugitivo(vulgo, senha);
            if (logado != null) {
                session.setAttribute("fugitivoLogado", logado);
                return "redirect:/fugitivo/home";
            }

        } catch (SQLException e) { e.printStackTrace(); }

        return "redirect:/?erroLogin";
    }

    /* ================= REGISTER ================= */
    @PostMapping("/register")
    public String register(Fugitivo f) {
        try {
            facade.create(f);
            session.setAttribute("fugitivoLogado", f);
            return "redirect:/fugitivo/home";

        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/?erroRegistro";
        }
    }

    /* ================= HOME COM FILTROS ================= */
    @GetMapping("/home")
    public String homeFugitivo(Model model, @RequestParam(required = false) String localizacao, @RequestParam(required = false) Double precoMax) {
        if (session.getAttribute("fugitivoLogado") == null) return "redirect:/";

        try {
            List<Hospedagem> lista;
            
            // Se houver filtros, busca personalizada. Se não, busca todas disponíveis.
            if ((localizacao != null && !localizacao.isEmpty()) || precoMax != null) {
                lista = facade.filterHospedagemByCriterios(localizacao, precoMax);
            } else {
                lista = facade.filterHospedagemByAvailable();
            }
            
            model.addAttribute("hospedagens", lista);
            model.addAttribute("localAtual", localizacao);
            model.addAttribute("precoAtual", precoMax);
            
        } catch (SQLException e) {
            model.addAttribute("msg", "Erro ao buscar esconderijos.");
        }
        return "fugitivo/home";
    }

    /* ================= LOGOUT ================= */
    @GetMapping("/logout")
    public String logout() {
        session.removeAttribute("fugitivoLogado");
        return "redirect:/";
    }
}