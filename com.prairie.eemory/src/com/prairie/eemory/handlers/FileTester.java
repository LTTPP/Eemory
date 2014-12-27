package com.prairie.eemory.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import com.prairie.eemory.Constants;

public class FileTester extends PropertyTester {

    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        if (Constants.PLUGIN_TESTERS_ISFILE.equals(property)) {
            if (receiver instanceof IAdaptable) {
                return ((IAdaptable) receiver).getAdapter(IResource.class) instanceof IFile;
            }
        }
        return false;
    }

}
