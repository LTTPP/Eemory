package org.lttpp.eemory.dom.impl;

import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.Constants;
import org.lttpp.eemory.dom.DocumentType;
import org.lttpp.eemory.dom.Node;
import org.lttpp.eemory.util.ConstantsUtil;

public class DocumentTypeImpl extends NodeImpl implements DocumentType {

    private String name;
    private String publicId;
    private String systemId;

    protected DocumentTypeImpl(final String name, final String publicId, final String systemId) {
        this.name = name;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(final String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }

    @Override
    public short getNodeType() {
        return Node.DOCUMENT_TYPE_NODE;
    }

    @Override
    public boolean isEqualNode(final Node other) {
        if (!super.isEqualNode(other)) {
            return false;
        }
        if (!getPublicId().equals(((DocumentTypeImpl) other).getPublicId()) || !getSystemId().equals(((DocumentTypeImpl) other).getSystemId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (StringUtils.isBlank(publicId)) {
            return ConstantsUtil.LEFT_ANGLE_BRACKET + ConstantsUtil.EXCLAMATION + Constants.ENML_DOCTYPE + StringUtils.SPACE + name + StringUtils.SPACE + Constants.ENML_DOCTYPE_SYSTEM + StringUtils.SPACE + ConstantsUtil.DOUBLE_QUOTE + systemId + ConstantsUtil.DOUBLE_QUOTE + ConstantsUtil.RIGHT_ANGLE_BRACKET;
        } else {
            return ConstantsUtil.LEFT_ANGLE_BRACKET + ConstantsUtil.EXCLAMATION + Constants.ENML_DOCTYPE + StringUtils.SPACE + name + StringUtils.SPACE + Constants.ENML_DOCTYPE_PUBLIC + StringUtils.SPACE + ConstantsUtil.DOUBLE_QUOTE + publicId + ConstantsUtil.DOUBLE_QUOTE + StringUtils.SPACE + ConstantsUtil.DOUBLE_QUOTE + systemId + ConstantsUtil.DOUBLE_QUOTE + ConstantsUtil.RIGHT_ANGLE_BRACKET;
        }
    }

}
