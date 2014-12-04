package com.prairie.eemory.client;

public interface ENObject {

    public String getName();

    public void setName(final String name);

    public String getGuid();

    public void setGuid(final String guid);

    public boolean isGuidReset();

    public void setGuidReset(boolean reset);

    public abstract void adopt(ENObject note);

    public boolean isGuidAdopt();

    public void setGuidAdopt(boolean adopt);

    public ENObjectType getType();

    public void setType(ENObjectType type);

}
