package com.duoc.recetas.controller;

import com.duoc.recetas.entity.Usuario;
import com.duoc.recetas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String showRegistroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioService.registrarUsuario(usuario);
            return "redirect:/login?registroExitoso";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}