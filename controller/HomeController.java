package com.smartcontact.controller;

import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home-Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About-smart contact manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register-smart contact manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	//handler for registering user
	@RequestMapping(value="/do_register", method= RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
							   @RequestParam(value="agreement", defaultValue="false") boolean agreement, Model model, HttpSession session) {

		try {
			if(!agreement) {
				System.out.println("You have not agree terms and conditions");
				throw new Exception("You have not agree terms and conditions");
			}

			if(bindingResult.hasErrors()){
				System.out.println("ERRORS"+bindingResult.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement" +agreement);
			System.out.println("User"+user);
			
			
			User result=this.userRepository.save(user);
			
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully Registered !!","alert-success"));
			return "signup";
			
		}catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong !!" +e.getMessage(),"alert-danger"));
			return "signup";
		}
		
	}
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model){
		model.addAttribute("title","Login Page");
		return "login";
	}

}


