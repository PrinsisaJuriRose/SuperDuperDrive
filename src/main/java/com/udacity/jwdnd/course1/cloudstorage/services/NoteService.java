package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public int insertNote(Note note)
    {
        return noteMapper.InsertNote(note);
    }

    public List<Note> getAllNotes(Integer userId)
    {
        return noteMapper.getNotesOfUser(userId);
    }

    public int deleteNote(Integer noteId)
    {
        return noteMapper.deleteNote(noteId);
    }

    public int updateNote(Note note)
    {
        return noteMapper.updateNote(note);
    }
}
