package com.prairie.eevernote.dom.impl;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eevernote.dom.DocumentType;
import com.prairie.eevernote.dom.Node;
import com.prairie.eevernote.util.ConstantsUtil;

public class DocumentTypeImpl extends NodeImpl implements DocumentType, ConstantsUtil {

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
            return LEFT_ANGLE_BRACKET + EXCLAMATION_MARK + ENML_DOCTYPE + StringUtils.SPACE + name + StringUtils.SPACE + ENML_DOCTYPE_SYSTEM + StringUtils.SPACE + DOUBLE_QUOTATION_MARK + systemId + DOUBLE_QUOTATION_MARK + RIGHT_ANGLE_BRACKET;
        } else {
            return LEFT_ANGLE_BRACKET + EXCLAMATION_MARK + ENML_DOCTYPE + StringUtils.SPACE + name + StringUtils.SPACE + ENML_DOCTYPE_PUBLIC + StringUtils.SPACE + DOUBLE_QUOTATION_MARK + publicId + DOUBLE_QUOTATION_MARK + StringUtils.SPACE + DOUBLE_QUOTATION_MARK + systemId + DOUBLE_QUOTATION_MARK + RIGHT_ANGLE_BRACKET;
        }
    }

}
