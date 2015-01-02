package com.prairie.eemory;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

    public static String Plugin_Configs_Shell_Title;
    public static String Plugin_Configs_QuickOrgnize_Shell_Title;
    public static String Plugin_Configs_Title;
    public static String Plugin_Configs_Message;
    public static String Plugin_Configs_Organize;
    public static String Plugin_Configs_Notebook;
    public static String Plugin_Configs_Note;
    public static String Plugin_Configs_Tags;
    public static String Plugin_Configs_Notebook_Hint;
    public static String Plugin_Configs_Note_Hint;
    public static String Plugin_Configs_Tags_Hint;
    public static String Plugin_Configs_Comments;
    public static String Plugin_Configs_Refresh;
    public static String Plugin_Configs_Authenticating;
    public static String Plugin_Configs_FetchingNotebooks;
    public static String Plugin_Configs_FetchingNotes;
    public static String Plugin_Configs_FetchingTags;
    public static String Plugin_Runtime_ClipFileToEvernote;
    public static String Plugin_Runtime_ClipSelectionToEvernote;
    public static String Plugin_Runtime_ClipScreenshotToEvernote_Hint;
    public static String Plugin_Runtime_CreateNewNote;
    public static String Plugin_Runtime_ClipToDefault;
    public static String Plugin_Runtime_CreateNewNoteWithGivenName;
    public static String Plugin_OutOfDate;
    public static String Plugin_Error_Occurred;
    public static String Plugin_Error_OutOfDate;
    public static String Plugin_Error_NoFile;
    public static String Plugin_Error_NoText;
    public static String Plugin_OAuth_Cancel;
    public static String Plugin_OAuth_Copy;

    public static String Plugin_OAuth_TokenNotConfigured;
    public static String Plugin_OAuth_Title;
    public static String Plugin_OAuth_Configure;
    public static String Plugin_OAuth_NotNow;
    public static String Plugin_OAuth_Waiting;
    public static String Plugin_OAuth_Confirm;
    public static String Plugin_OAuth_AuthExpired_Message;
    public static String Plugin_OAuth_AuthExpired_Title;
    public static String Plugin_OAuth_AuthExpired_ReAuth;
    public static String Plugin_OAuth_DoItManually;

    public static String DOM_Error0;
    public static String DOM_Error1;
    public static String DOM_Error2;
    public static String DOM_Error3;
    public static String DOM_Error4;
    public static String DOM_Error5;
    public static String DOM_Error6;
    public static String DOM_Error7;
    public static String DOM_Error8;
    public static String DOM_Error9;
    public static String Throwable_IllegalArgumentException_Message;
    public static String Throwable_NotSerializable_Message;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
