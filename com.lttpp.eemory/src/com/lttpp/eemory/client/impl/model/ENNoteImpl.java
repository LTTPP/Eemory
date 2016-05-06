package com.lttpp.eemory.client.impl.model;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.lttpp.eemory.client.model.ENNote;
import com.lttpp.eemory.client.model.ENObject;
import com.lttpp.eemory.enml.StyleText;
import com.lttpp.eemory.util.ListUtil;

public class ENNoteImpl extends ENObjectImpl implements ENNote {

    private ENObject notebook;
    private List<List<StyleText>> content;
    private List<File> attachments;
    private List<String> tags;
    private String comments;
    private int tabWidth;

    public ENNoteImpl() {
        content = ListUtil.list();
        attachments = ListUtil.list();
        tags = ListUtil.list();
    }

    public ENNoteImpl(final String guid) {
        super(guid);
        content = ListUtil.list();
        attachments = ListUtil.list();
        tags = ListUtil.list();
    }

    public ENNoteImpl(final String name, final String guid) {
        super(name, guid);
        content = ListUtil.list();
        attachments = ListUtil.list();
        tags = ListUtil.list();
    }

    @Override
    public ENObject getNotebook() {
        if (notebook == null) {
            notebook = new ENObjectImpl();
        }
        return notebook;
    }

    @Override
    public void setNotebook(final ENObject notebook) {
        this.notebook = notebook;
    }

    @Override
    public List<List<StyleText>> getContent() {
        return content;
    }

    @Override
    public void setContent(final List<List<StyleText>> content) {
        this.content = content;
    }

    @Override
    public List<File> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(final List<File> attachments) {
        this.attachments = attachments;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public void setTags(final List<String> tags) {
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
    public int getTabWidth() {
        return tabWidth;
    }

    @Override
    public void setTabWidth(final int tabWidth) {
        this.tabWidth = tabWidth;
    }

    /**
     * Adopt if not null/empty/blank
     */
    @Override
    public void adopt(final ENObject obj) {

        super.adopt(obj);

        if (!(obj instanceof ENNote)) {
            return;
        }
        ENNote note = (ENNote) obj;

        if (note.getNotebook() != null) {
            getNotebook().adopt(note.getNotebook());
        }

        if (!ListUtil.isNullOrEmptyList(note.getContent())) {
            setContent(note.getContent());
        }

        if (!ListUtil.isNullOrEmptyList(note.getAttachments())) {
            setAttachments(note.getAttachments());
        }

        if (!ListUtil.isNullOrEmptyList(note.getTags())) {
            setTags(note.getTags());
        }

        if (StringUtils.isNotBlank(note.getComments())) {
            setComments(note.getComments());
        }
    }

    public static ENNote forGuid(final String guid) {
        return new ENNoteImpl(guid);
    }

    public static ENNote forNameAndGuid(final String name, final String guid) {
        return new ENNoteImpl(name, guid);
    }

    public static ENNote forNotebook(final ENObject notebook) {
        ENNote n = new ENNoteImpl();
        if (notebook != null) {
            n.getNotebook().setName(notebook.getName()); // maybe no use but just in case
            n.getNotebook().setGuid(notebook.getGuid());
            n.getNotebook().setType(notebook.getType());
            n.getNotebook().setLinkedObject(notebook.getLinkedObject());
        }
        return n;
    }

}
