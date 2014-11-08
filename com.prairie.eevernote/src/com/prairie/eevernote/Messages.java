package com.prairie.eevernote;

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
    public static String Plugin_Configs_TokenNotConfigured;
    public static String Plugin_Configs_OAuth_Title;
    public static String Plugin_Configs_OAuth_Configure;
    public static String Plugin_Configs_OAuth_NotNow;
    public static String Plugin_Configs_OAuth_Waiting;
    public static String Plugin_Configs_OAuth_Confirm;
    public static String Plugin_Configs_Authenticating;
    public static String Plugin_Configs_FetchingNotebooks;
    public static String Plugin_Configs_FetchingNotes;
    public static String Plugin_Configs_FetchingTags;
    public static String Plugin_Runtime_AddFileToEvernote;
    public static String Plugin_Runtime_AddFileToEvernote_Subtask;
    public static String Plugin_Runtime_AddFileToEvernote_OutOfDate;
    public static String Plugin_Runtime_AddSelectionToEvernote;
    public static String Plugin_Runtime_AddSelectionToEvernote_Subtask;
    public static String Plugin_Runtime_AddSelectionToEvernote_OutOfDate;
    public static String Plugin_Runtime_ClipScreenshotToEvernote_Message;
    public static String Plugin_Runtime_ClipScreenshotToEvernote_Subtask;
    public static String Plugin_Runtime_ClipScreenshotToEvernote_OutOfDate;
    public static String Plugin_Runtime_ClipScreenshotToEvernote_Hint;
    public static String Plugin_Runtime_AuthExpired_Message;
    public static String Plugin_Runtime_AuthExpired_Title;
    public static String Plugin_Runtime_CreateNewNote;
    public static String Plugin_Runtime_ClipToDefault;
    public static String Plugin_Runtime_CreateNewNoteWithGivenName;
    public static String Plugin_Error_Occurred;
    public static String Plugin_Error_OutOfDate;
    public static String Plugin_Throwable_NotFatal;
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

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
