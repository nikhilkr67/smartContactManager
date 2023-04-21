package com.smartcontact.controller;

import com.smartcontact.entities.User;
import com.smartcontact.repository.UserRepository;
import com.smartcontact.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private EmailService emailService;

    private UserRepository userRepository;

    public ForgotController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    //email id form handler
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email";
    }

    @PostMapping("/send-OTP")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){

        System.out.println("EMAIL:" +email);

        //generating 4 digit OTP
        Random random = new Random();
        int otp = random.nextInt(9999);

        String subject="OTP from smart contact manager";
        String message=""
                +"<div style='border:1px solid #e2e2e2; padding:20px'>"
                +"<h1>"
                +"OTP is: "
                +"<b>"+otp
                +"</b>"
                +"</h1>"
                +"</div>";

        String to=email;
        boolean flag = emailService.sendEmail(subject, message, to);

        if(flag){

            session.setAttribute("myOtp",otp);
            session.setAttribute("Email",email);

            return "verify_OTP";
        }else {

            session.setAttribute("message","Check your email id");
            return "forgot_email";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp")int otp, HttpSession session) {
        int myOtp = (int) session.getAttribute("myOtp");

        System.out.println("User OTP:" + otp);
        System.out.println("User OTP:" + myOtp);

        String email = (String) session.getAttribute("Email");

        User user = userRepository.getUserByUserName(email);

        if (myOtp == otp && user != null) {
            session.setAttribute("user", user);
            return "password_change";
        } else if (user == null) {
            session.setAttribute("message", "User doesn't exist for this email.");
        } else {
            session.setAttribute("message", "You have entered wrong OTP.");
        }
        return "verify_OTP";
    }


    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
        User user = null;
        String email = (String) session.getAttribute("email");

        if (email != null && !email.isEmpty()) {
            user = userRepository.getUserByUserName(email);
        }

        if (user != null) {
            user.setPassword(passwordEncoder.encode(newpassword));
            userRepository.save(user);
        }

        return "redirect:/signin?change=password change successfully..";
    }

}
