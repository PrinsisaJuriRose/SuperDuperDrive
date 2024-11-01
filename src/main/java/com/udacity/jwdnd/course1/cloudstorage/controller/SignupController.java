package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getSignupView()
    {
        return "signup";
    }

    @PostMapping
    public String submitSignupView(User user,  RedirectAttributes redirectAttributes)
    {
        boolean errorFlag = false;

        if (userService.isUserNameExistBefore(user.getUserName()))
        {
            errorFlag = true;
            redirectAttributes.addFlashAttribute("errorMessage",  "The username already exists. Please try with another username");
        }

        if(!errorFlag) //there is no error
        {
            int rowAdded = userService.insertUser(user);
            if(rowAdded <= 0) {
                errorFlag = true;
                redirectAttributes.addFlashAttribute("errorMessage", "Error when signing you up. Please try again");
            }
            else {
                // Add success message
                redirectAttributes.addFlashAttribute("successMessage", "You successfully signed up! Please login.");
            }
        }

        redirectAttributes.addFlashAttribute("signupError", "errorFlag");

        if(errorFlag)
        {
            return "redirect:/signup";
        }
        else
        {
            // Redirect to the login page
            return "redirect:/login";
        }
    }
}
