//Indique qu'une classe est un contrôleur Spring MVC qui gère les requêtes HTTP et retourne des vues.
package com.cicd.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    //Indique que la méthode home() sera mappée à la requête GET sur "/".
    public String home() {
        return "index";
    }
}
