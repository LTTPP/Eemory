package com.prairie.eevernote.client.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eevernote.client.ClipperArgs;
import com.prairie.eevernote.enml.StyleText;
import com.prairie.eevernote.util.ListUtil;

public class ClipperArgsImpl implements ClipperArgs {

    /* parent Notebook is optional; if omitted, default notebook is used */
    private String notebookGuid;
    private boolean notebookGuidReset;
    private boolean notebookGuidAdopt;
    private String notebookName;
    /*
     * existing Note is optional; if omitted, create a new note; otherwise
     * append
     */
    private String noteGuid;
    private boolean noteGuidReset;
    private boolean noteGuidAdopt;
    private String noteName;

    // new added
    private String title;
    private List<List<StyleText>> styleText;
    private List<File> files;
    private String tags;
    private String comments;

    /**
     * Create a <code>ClipperArgs</code> instance using given notebook guid.
     *
     * @param guid
     * @return
     */
    public static ClipperArgs forNotebookGuid(final String guid) {
        ClipperArgs args = new ClipperArgsImpl();
        args.setNotebookGuid(guid);
        return args;
    }

    public static ClipperArgs forNotebookNameAndGuid(final String name, final String guid) {
        ClipperArgs args = new ClipperArgsImpl();
        args.setNotebookGuid(guid);
        args.setNotebookName(name);
        return args;
    }

    public static ClipperArgs forNoteGuid(final String guid) {
        ClipperArgs args = new ClipperArgsImpl();
        args.setNoteGuid(guid);
        return args;
    }

    public static ClipperArgs forNoteNameAndGuid(final String name, final String guid) {
        ClipperArgs args = new ClipperArgsImpl();
        args.setNoteGuid(guid);
        args.setNoteName(name);
        return args;
    }

    @Override
    public String getNotebookGuid() {
        return notebookGuid;
    }

    @Override
    public void setNotebookGuid(final String notebookGuid) {
        this.notebookGuid = notebookGuid;
    }

    @Override
    public void setNotebookGuidReset(final boolean set) {
        notebookGuidReset = set;
    }

    @Override
    public boolean getNotebookGuidReset() {
        return notebookGuidReset;
    }

    public void setNotebookGuidAdopt(final boolean set) {
        notebookGuidAdopt = set;
    }

    @Override
    public boolean getNotebookGuidAdopt() {
        return notebookGuidAdopt;
    }

    @Override
    public String getNotebookName() {
        return notebookName;
    }

    @Override
    public void setNotebookName(final String notebookName) {
        this.notebookName = notebookName;
    }

    @Override
    public String getNoteGuid() {
        return noteGuid;
    }

    @Override
    public void setNoteGuid(final String noteGuid) {
        this.noteGuid = noteGuid;
    }

    @Override
    public void setNoteGuidReset(final boolean set) {
        noteGuidReset = set;
    }

    @Override
    public boolean getNoteGuidReset() {
        return noteGuidReset;
    }

    public void setNoteGuidAdopt(final boolean set) {
        noteGuidAdopt = set;
    }

    @Override
    public boolean getNoteGuidAdopt() {
        return noteGuidAdopt;
    }

    @Override
    public String getNoteName() {
        return noteName;
    }

    @Override
    public void setNoteName(final String noteName) {
        this.noteName = noteName;
    }

    @Override
    public String getTags() {
        return tags;
    }

    @Override
    public void setTags(final String tags) {
        this.tags = tags;
    }

    @Override
    public String getComments() {
        return comments;
    }

    @Override
    public void setComments(final String comments) {
        this.comments = comments;
    }

    @Override
    public List<File> getFiles() {
        return files;
    }

    @Override
    public void setFiles(final List<File> files) {
        this.files = files;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public List<List<StyleText>> getStyleText() {
        return styleText;
    }

    @Override
    public void setStyleText(final List<List<StyleText>> styleText) {
        this.styleText = styleText;
    }

    @Override
    public void adopt(final ClipperArgs args) {
        if (!StringUtils.isBlank(args.getNotebookName())) {
            setNotebookName(args.getNotebookName());
        }
        if (!StringUtils.isBlank(args.getNotebookGuid())) {
            setNotebookGuid(args.getNotebookGuid());
            setNotebookGuidAdopt(true);
        }

        if (!StringUtils.isBlank(args.getNoteName())) {
            setNoteName(args.getNoteName());
        }
        if (!StringUtils.isBlank(args.getNoteGuid())) {
            setNoteGuid(args.getNoteGuid());
            setNoteGuidAdopt(true);
        }

        if (!StringUtils.isBlank(args.getTags())) {
            setTags(args.getTags());
        }

        if (!StringUtils.isBlank(args.getComments())) {
            setComments(args.getComments());
        }

        if (!StringUtils.isBlank(args.getTitle())) {
            setTitle(args.getTitle());
        }

        if (!ListUtil.isNullOrEmptyList(args.getStyleText())) {
            setStyleText(args.getStyleText());
        }

        if (!ListUtil.isNullOrEmptyList(args.getFiles())) {
            setFiles(args.getFiles());
        }
    }

}
