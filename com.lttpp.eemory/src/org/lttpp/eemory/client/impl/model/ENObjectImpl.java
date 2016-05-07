package org.lttpp.eemory.client.impl.model;

import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.client.metadata.ENObjectType;
import org.lttpp.eemory.client.model.ENObject;

public class ENObjectImpl implements ENObject {

    private String name;
    private String guid;

    private boolean reset = false;
    private boolean adopt = false;

    private ENObjectType type = ENObjectType.NORMAL;
    private Object linkedObject;

    public ENObjectImpl() {

    }

    public ENObjectImpl(final String guid) {
        this.guid = guid;
    }

    public ENObjectImpl(final String name, final String guid) {
        this(guid);
        this.name = name;
    }

    public ENObjectImpl(final String name, final String guid, final ENObjectType type) {
        this(name, guid);
        this.type = type;
    }

    public ENObjectImpl(final String name, final String guid, final ENObjectType type, final Object linkedObject) {
        this(name, guid, type);
        this.linkedObject = linkedObject;
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
    public boolean isArgsReset() {
        return reset;
    }

    @Override
    public void setArgsReset(final boolean reset) {
        this.reset = reset;
    }

    @Override
    public boolean isArgsAdopt() {
        return adopt;
    }

    @Override
    public void setArgsAdopt(final boolean adopt) {
        this.adopt = adopt;
    }

    @Override
    public ENObjectType getType() {
        return type;
    }

    @Override
    public void setType(final ENObjectType type) {
        this.type = type;
    }

    @Override
    public Object getLinkedObject() {
        return linkedObject;
    }

    @Override
    public void setLinkedObject(final Object linkedObject) {
        this.linkedObject = linkedObject;
    }

    /**
     * Adopt if not null/empty/blank
     */
    @Override
    public void adopt(final ENObject note) {
        // this/set-name should always be done
        if (StringUtils.isNotBlank(note.getName())) {
            setName(note.getName());
        }

        if (StringUtils.isNotBlank(note.getGuid())) { // user do re-select an existing note in Quick Organize
            setGuid(note.getGuid());

            if (note.getType() != null) {
                setType(note.getType());
            }
            if (note.getLinkedObject() != null) {
                setLinkedObject(note.getLinkedObject());
            }

            setArgsAdopt(true);
        }
    }

    /**
     * equals if Guid equals.
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ENObject)) {
            return false;
        }
        ENObject o = (ENObject) other;
        return StringUtils.equals(guid, o.getGuid());
    }

    public static ENObject forGuid(final String guid) {
        return new ENObjectImpl(guid);
    }

    public static ENObject forNameAndGuid(final String name, final String guid) {
        return new ENObjectImpl(name, guid);
    }

    public static ENObject forValues(final String name, final String guid, final ENObjectType type, final Object linkedObject) {
        return new ENObjectImpl(name, guid, type, linkedObject);
    }

}
