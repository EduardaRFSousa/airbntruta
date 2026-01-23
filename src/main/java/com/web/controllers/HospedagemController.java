package com.web.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.web.model.entities.Hospedagem;
import com.web.model.entities.Hospedeiro;
import com.web.model.entities.Servico;
import com.web.model.repositories.Facade;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hospedagem")
public class HospedagemController {

    @Autowired
    private Facade facade;
    @Autowired
    private HttpSession session;

    /* ================= CREATE (POST) ================= */
    @PostMapping("/new")
    public String create(Hospedagem h, @RequestParam(value = "servs", required = false) String[] servs) {
        
        Hospedeiro logado = (Hospedeiro) session.getAttribute("hospedeiroLogado");
        if (logado == null) return "redirect:/";

        try {
            List<Servico> servicos = new ArrayList<>();
            
            // PROTEÇÃO: Só tenta mapear se o array 'servs' não for nulo
            if (servs != null && servs.length > 0) {
                servicos = Arrays.stream(servs)
                    .map(id -> {
                        try { return facade.readServico(Integer.parseInt(id)); }
                        catch (Exception e) { return null; }
                    })
                    .filter(s -> s != null)
                    .collect(Collectors.toList());
            }

            h.setServicos(servicos);
            h.setHospedeiro(logado);
            facade.create(h);
            
            // Redireciona de volta para a HOME do hospedeiro
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
            facade.deleteHospedagem(id);
            return "redirect:/hospedeiro/home?sucessoDeleteHospedagem";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/hospedeiro/home?erroDeleteHospedagem";
        }
    }
}