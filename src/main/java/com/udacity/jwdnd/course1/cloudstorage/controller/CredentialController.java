package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/credential")
public class CredentialController {

    private UserService userService;
    private CredentialService credentialService;

    public CredentialController(UserService userService, CredentialService credentialService) {
        this.userService = userService;
        this.credentialService = credentialService;
    }

    @PostMapping("/add")
    public String saveCredential(Authentication authentication, Credential credential, Model model) throws AuthenticationException
    {
        int rowUpdated = 0;
        Integer userId = userService.getUserIdByUserName(authentication.getName()); //Get the ID of the current login user

        credential.setUserId(userId);

        if(credential.getCredentialId() != null)
        {
            rowUpdated = credentialService.updateCredential(credential);
        }
        else
        {
            rowUpdated = credentialService.insertCredential(credential);
        }

        if(rowUpdated == 1)
        {
            model.addAttribute("errorFlag", false);
        }
        else
        {
            model.addAttribute("errorFlag", true);
            model.addAttribute("displayMessage", "There is error when adding or updating the credentials");
        }
        model.addAttribute("activeTab", "nav-credentials");
        return "result";
    }

    @GetMapping("/delete")
    public String deleteCredential(@RequestParam("id") Integer credentialId, Model model)
    {
        if(credentialId > 0) {

           int rowDeleted = credentialService.deleteCredential(credentialId);

            if(rowDeleted == 1)
            {
                model.addAttribute("errorFlag", false);
            }
            else
            {
                model.addAttribute("errorFlag", true);
                model.addAttribute("displayMessage", "There is error when deleting the credential");
            }
        }
        else
        {
            model.addAttribute("errorFlag", true);
            model.addAttribute("displayMessage", "Can not find the credential");
        }
        model.addAttribute("activeTab", "nav-credentials");

        return "result";
    }
}
