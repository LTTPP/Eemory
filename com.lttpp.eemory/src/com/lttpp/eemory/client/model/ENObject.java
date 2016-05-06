package com.lttpp.eemory.client.model;

import com.lttpp.eemory.client.metadata.ENObjectType;

public interface ENObject {

    public String getName();

    public void setName(final String name);

    public String getGuid();

    public void setGuid(final String guid);

    public boolean isArgsReset();

    public void setArgsReset(boolean reset);

    public abstract void adopt(ENObject note);

    public boolean isArgsAdopt();

    public void setArgsAdopt(boolean adopt);

    public ENObjectType getType();

    public void setType(ENObjectType type);

    public Object getLinkedObject();

    public void setLinkedObject(Object linkedObject);

}
