package com.project.main.thymeleaf;

import com.project.main.signinup.LoginService;
import com.project.main.signinup.RegisterRequest;
import com.project.main.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping(path = "api")
public class ContentController {

    //TODO: remove this garbage when proper frontend is done
    private final LoginService loginService;
    private final UserService userService;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String handleLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String handleRegister() {
        return "register";
    }

    @GetMapping("/signinup")
    public String handleSigninUp() {
        return "signinup";
    }

    @GetMapping("/verify/enable")
    public ModelAndView handleVerification(@RequestParam("token") String token) {
        ResponseEntity<?> enableResponse = userService.enable(token);
        ModelAndView modelAndView = new ModelAndView();
        if (enableResponse.getStatusCode() == HttpStatus.OK) {
            modelAndView.setViewName("verification");
            modelAndView.addObject("confirmed", enableResponse.getBody());
            return modelAndView;
        }
        if (enableResponse.getStatusCode() == HttpStatus.IM_USED || enableResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            modelAndView.setViewName("verification");
            modelAndView.addObject("expired", enableResponse.getBody());
            return modelAndView;
        }
        else {
            modelAndView.setViewName("error");
            modelAndView.addObject("error", enableResponse.getBody());
            return modelAndView;
        }
    }

    @PostMapping("/auth/register")
    public ModelAndView register(@ModelAttribute RegisterRequest request) {
        ResponseEntity<?> register = loginService.register(request);
        ModelAndView modelAndView = new ModelAndView();
        if (register.getStatusCode() == HttpStatus.OK) {
            modelAndView.setViewName("login");
            modelAndView.addObject("confirm", register.getBody());
            return modelAndView;
        } else {
            modelAndView.setViewName("register");
            modelAndView.addObject("error", register.getBody());
            return modelAndView;
        }
    }

}
