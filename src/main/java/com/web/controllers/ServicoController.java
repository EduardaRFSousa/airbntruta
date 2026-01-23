package com.web.controllers;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.web.model.entities.Hospedeiro;
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
        Hospedeiro logado = (Hospedeiro) session.getAttribute("hospedeiroLogado");
        if (logado == null) return "redirect:/";

        try {
            if (s.getCodigo() == 0) {
                s.setHospedeiroId(logado.getCodigo()); // Vincula ao hospedeiro logado
                facade.create(s);
            } else {
                facade.update(s);
            }
            return "redirect:/hospedeiro/home?tab=servicos";
        } catch (SQLException e) {
            return "redirect:/hospedeiro/home?tab=servicos&erro";
        }
    }

    /* ================= DELETE ================= */
    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) { // Adicionado o nome explícito no RequestParam
        
        if (session.getAttribute("hospedeiroLogado") == null) return "redirect:/";

        try {
            facade.deleteServico(id);
            return "redirect:/hospedeiro/home?tab=servicos&sucessoDelete";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/hospedeiro/home?tab=servicos&erroDelete";
        }
    }
 
}