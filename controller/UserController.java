package com.smartcontact.controller;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repository.ContactRepository;
import com.smartcontact.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME:" + userName);

		//getting the user using userName(Email)...

		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER" + user);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "user Dashboard");
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session){

		try {
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);

			if(file.isEmpty()){
				//if the file is empty then try message
				System.out.println("File is empty");
				contact.setImage("contact1.jpg");
			}
			else {
				//upload the file to folder and update the name to contacts
				contact.setImage(file.getOriginalFilename());

				File savefile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");
			}

			user.getContacts().add(contact);
			contact.setUser(user);

			userRepository.save(user);

			//System.out.println("DATA"+contact);
			//System.out.println("Added to database");

			// message success..........................
			session.setAttribute("message",new Message("contact is added!! Add more..","alert-success"));
		}catch (Exception e){
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();

			//error message........................
			session.setAttribute("message",new Message("Something went wrong !! try again.."+e.getMessage(),"alert-danger"));
		}
		return "normal/contact_form";

	}

	//per page=5[n]
	//CurrentPage=0[page]
	@GetMapping("/view-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal){

		m.addAttribute("title","show contacts");
		String userName = principal.getName();

		User user = userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = contactRepository.findContactByUser(user.getId(),pageable);
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());

		return "normal/view_contacts";
	}

	//showing particular contact details

	@RequestMapping("/{cid}/contact")
	public String viewContactDetail(@PathVariable("cid")Integer cid, Model model,Principal principal){
		System.out.println("cId"+cid);
		Optional<Contact>contactOptional = contactRepository.findById(cid);
		Contact contact=contactOptional.get();

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);


		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}

	//Delete contact Handler...
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid")Integer cid, Model model,HttpSession session,Principal principal){
		Optional<Contact> contactOptional = contactRepository.findById(cid);
		Contact contact = contactOptional.get();

		//contact.setUser(null);
		//contactRepository.deleteById(cid);

		User user = userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		userRepository.save(user);

		session.setAttribute("message",new Message("Contact deleted successfully..","success"));

		return "redirect:/user/view-contacts/0";
	}

	// Update contact Handler...
	@PostMapping("/update-contact/{cid}")
	public String UpdateContact(@PathVariable("cid")Integer cid, Model model){
		model.addAttribute("title","Update Contact");

		Contact contact = contactRepository.findById(cid).get();
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}

	//update contact handler
	@RequestMapping(value = "/process-update",method = RequestMethod.POST)
	public String updateHandler(
			@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,Model model,HttpSession session,Principal principal){

		try{
			//old contact Details..
			Contact oldContactDetails = contactRepository.findById(contact.getCid()).get();

			if(!file.isEmpty()){

				//Delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1= new File(deleteFile,oldContactDetails.getImage());
				file1.delete();

				//update new image profile
				File savefile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			}else {
				contact.setImage(oldContactDetails.getImage());
			}

			User user = userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);

			contactRepository.save(contact);
			session.setAttribute("message", new Message("your contact is updated..","success"));

		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("CONTACT NAME:"+contact.getName());
		return "redirect:/user/"+contact.getCid()+"/contact";
	}

	//profile-setting
	@GetMapping("/profile")
	public String profile(Model model){
		model.addAttribute("title","profile-page");
		return "normal/profile";

	}

	//setting-handler
	@GetMapping("/settings")
	public String openSettings(){

		return "normal/settings";
	}

	//change password handler
	@PostMapping("/change-password")
	public String changePassword(
			@RequestParam("oldPassword")String oldPassword,
			@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session){

		System.out.println("OLD Password"+oldPassword);
		System.out.println("NEW Password"+newPassword);

		String userName = principal.getName();
		User currentUser = userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());

		if(passwordEncoder.matches(oldPassword,currentUser.getPassword())){
			//change password..

			currentUser.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(currentUser);

			session.setAttribute("message",new Message("Password is successfully changed","alert-success"));

		}else {
			//error..
			session.setAttribute("message",new Message("Wrong old password !!","alert-danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}

}
