package com.web.controllers;

import java.sql.SQLException; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.web.model.entities.Fugitivo;
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

    /* ================= HOME ================= */
    @GetMapping("/home")
    public String home() {
        if (session.getAttribute("fugitivoLogado") == null) {
            return "redirect:/";
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