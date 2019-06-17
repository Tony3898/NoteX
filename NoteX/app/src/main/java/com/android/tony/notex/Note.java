package com.android.tony.notex;

class Note {
    private String noteTitle,noteContent,noteID;

    Note(String noteId,String noteTitle,String noteContent)
    {
        this.noteID = noteId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
    }

    String getNoteID() {
        return noteID;
    }

    String getNoteTitle() {
        return noteTitle;
    }

    String getNoteContent() {
        return noteContent;
    }
}
