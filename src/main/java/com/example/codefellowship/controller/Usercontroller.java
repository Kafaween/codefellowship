package com.example.codefellowship.controller;

import com.example.codefellowship.models.ApplicationUser;
import com.example.codefellowship.models.Post;
import com.example.codefellowship.repositories.PostRepository;
import com.example.codefellowship.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class Usercontroller {

    @Autowired
    UserRepository userRepository;



   @Autowired
   PasswordEncoder encoder;

@Autowired
    PostRepository postRepository;

    @GetMapping("/signup")
    public String getSignUpPage() {
        return "signup";
    }
    @PostMapping("/signup")
    public RedirectView attemptSignUp( @ModelAttribute ApplicationUser user) {
        user.setPassword(encoder.encode(user.getPassword()));
         userRepository.save(user);
        return new RedirectView("/");
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }
    @PostMapping ("/login")
    public RedirectView LoginPage(@RequestParam String username) {
        ApplicationUser user=userRepository.findApplicationUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RedirectView("/home");
    }
    @GetMapping("/profile")
    public String getprofilePage(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user=userRepository.findApplicationUserByUsername(userDetails.getUsername());
        model.addAttribute("userdata", user);
        return "profile";
    }
    @GetMapping("/profile/{id}")
    public String getuserbyId(@PathVariable Long id,Model model){
     ApplicationUser user  = userRepository.findApplicationUserById(id);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser userCurrent=userRepository.findApplicationUserByUsername(userDetails.getUsername());
        model.addAttribute("userdata", user);
        model.addAttribute("current", userCurrent);
        return "profileAnother";
    }

    @PostMapping("/add/friend/{username}")
    public RedirectView addFriend(@PathVariable String username ){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user=userRepository.findApplicationUserByUsername(userDetails.getUsername());
        ApplicationUser follower=userRepository.findApplicationUserByUsername(username);
        user.setFollowers(follower);
        userRepository.save(user);
        return new RedirectView("/home");
    }

    @GetMapping("/posts")
    public String getposts(){
        return "posts";
    }
    @PostMapping ("/posts")
    public RedirectView Addposts(@ModelAttribute Post post){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user=userRepository.findApplicationUserByUsername(userDetails.getUsername());
        post.setUser(user);
        postRepository.save(post);
        user.setPosts(post);
        userRepository.save(user);
        return new RedirectView("/profile");
    }
    @GetMapping("/home")
    public String gethome(Model model){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user=userRepository.findApplicationUserByUsername(userDetails.getUsername());
        model.addAttribute("userdata", user);
        return "index2";
    }

    @GetMapping("/search")
    public String getAllusers(Model model){
      List<ApplicationUser> users = userRepository.findAll();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser current=userRepository.findApplicationUserByUsername(userDetails.getUsername());
        model.addAttribute("actual", current.getUsername());
        model.addAttribute("users", users);
        return "search";
    }

    @GetMapping("/feed")
    public String viewFeed(Model model){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user=userRepository.findApplicationUserByUsername(userDetails.getUsername());
        Set<ApplicationUser> userFeed=user.getFollowers();

        model.addAttribute("following", userFeed);
        return "feed";
    }
}
