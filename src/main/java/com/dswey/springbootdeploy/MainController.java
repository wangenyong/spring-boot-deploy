package com.dswey.springbootdeploy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")
public class MainController {

    @RequestMapping(path = "/")
    public String hello() {
        return "Hello spring deploy";
    }

    @RequestMapping(path = "/test")
    public String test() {
        return  "test";
    }

    @Autowired
    UserRepository userRepository;

    @RequestMapping(path = "/add")
    public @ResponseBody String addNewUser (@RequestParam String name, @RequestParam String email) {
        User n = new User();
        n.setName(name);
        n.setEmail(email);
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody String getAllUsers() {
        return "ok";
    }
}
