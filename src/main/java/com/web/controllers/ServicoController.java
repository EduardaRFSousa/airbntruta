package com.web.controllers;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.web.model.entities.Servico;
import com.web.model.repositories.Facade;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/servico")
public class ServicoController {

    @Autowired
    private Facade facade;
    @Autowired
    private HttpSession session;

    /* ================= SAVE / UPDATE ================= */
    @PostMapping("/save")
    public String save(Servico s) {
        if (session.getAttribute("hospedeiroLogado") == null) return "redirect:/";

        try {
            if (s.getCodigo() == 0) {
                facade.create(s);
            } else {
                facade.update(s);
            }
            // Garanta que esta rota existe no seu HospedeiroController
            return "redirect:/hospedeiro/home";
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/hospedeiro/home?erro";
        }
    }

    /* ================= DELETE ================= */
    @GetMapping("/delete")
    public String delete(@RequestParam int id) {
        
        if (session.getAttribute("hospedeiroLogado") == null) return "redirect:/";

        try {
            facade.deleteServico(id);
            return "redirect:/hospedeiro/home?sucessoDelete";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/hospedeiro/home?erroDelete";
        }
    }
}