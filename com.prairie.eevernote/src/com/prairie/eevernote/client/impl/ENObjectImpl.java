package com.prairie.eevernote.client.impl;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eevernote.client.ENObject;

public class ENObjectImpl implements ENObject {

    private String name;
    private String guid;

    private boolean reset = false;
    private boolean adopt = false;

    public ENObjectImpl() {

    }

    public ENObjectImpl(final String guid) {
        this.guid = guid;
    }

    public ENObjectImpl(final String name, final String guid) {
        this.name = name;
        this.guid = guid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public void adopt(final ENObject note) {
        if (StringUtils.isNotBlank(note.getName())) {
            setName(note.getName());
        }

        if (StringUtils.isNotBlank(note.getGuid())) {
            setGuid(note.getGuid());
            setGuidAdopt(true);
        }

    }

    @Override
    public boolean isGuidReset() {
        return reset;
    }

    @Override
    public void setGuidReset(final boolean reset) {
        this.reset = reset;
    }

    @Override
    public boolean isGuidAdopt() {
        return adopt;
    }

    @Override
    public void setGuidAdopt(final boolean adopt) {
        this.adopt = adopt;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ENObject)) {
            return false;
        }
        ENObject o = (ENObject) other;
        return StringUtils.equals(guid, o.getGuid());
    }

}
