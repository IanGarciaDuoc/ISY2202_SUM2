package com.duoc.recetas.controller;

import com.duoc.recetas.service.RecetaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final RecetaService recetaService;

    public HomeController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recetasRecientes", recetaService.obtenerRecetasRecientes());
        model.addAttribute("recetasDestacadas", recetaService.obtenerRecetasMejorValoradas());
        return "index";
    }
}