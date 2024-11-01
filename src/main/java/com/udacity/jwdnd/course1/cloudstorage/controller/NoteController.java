package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/note")
public class NoteController {

    private NoteService noteService;
    private UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public String saveNote(Authentication authentication, Note note, Model model) throws AuthenticationException
    {
        int rowUpdated = 0;
        Integer userId = userService.getUserIdByUserName(authentication.getName()); //Get the ID of the current login user
        note.setUserId(userId);

        if(note.getNoteId() != null)
        {
            rowUpdated = noteService.updateNote(note);
        }
        else
        {
            rowUpdated = noteService.insertNote(note);
        }

        if(rowUpdated == 1)
        {
            model.addAttribute("errorFlag", false);
        }
        else
        {
            model.addAttribute("errorFlag", true);
            model.addAttribute("displayMessage", "There is error when adding or updating the note");
        }

        model.addAttribute("activeTab", "nav-notes");
        return "result";
    }

    @GetMapping("/delete")
    public String deleteNote(@RequestParam("id") Integer noteId, Model model)
    {
        if(noteId > 0) {
            int rowDeleted = noteService.deleteNote(noteId);

            if(rowDeleted == 1)
            {
                model.addAttribute("errorFlag", false);
            }
            else
            {
                model.addAttribute("errorFlag", true);
                model.addAttribute("displayMessage", "There is error when deleting the note");
            }
        }
        else
        {
            model.addAttribute("errorFlag", true);
            model.addAttribute("displayMessage", "Can not find the note");
        }

        model.addAttribute("activeTab", "nav-notes");
        return "result";
    }
}
