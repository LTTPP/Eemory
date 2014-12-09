# ![Eemory](com.prairie.eemory/icons/plugin_172x56.png)

Eemory is an Eclipse plug-in aimed at integrating Evernote within the Eclipse IDE.

I love Evernote, but every time I want to save something to Evernote, I have to switch between Eclipse IDE and Evernote client, copy sometihng and paste, then go back to Eclipse and continue on my work. This is heavy and boring, so Eemory was born, which bring the benefits offered by Evernote into Eclipse. No need to open Evernote client anymore to add a note, just select something and one-click to save to Evernote right in Eclipse elegantly.

Visit [Eemory Home](http://prairie.github.io/Eemory).  
Visit [Eemory](http://marketplace.eclipse.org/content/eemory) on Eclipse Marketplace.

## Features

+ Adding code snippet, source file, screenshot and more to a new note on Evernote.
+ Prepending code snippet, source file, screenshot and more to an existing note on Evernote.
+ Adding/Prepending multiple files once.
+ Notebook, Note and Tag can be specified(no need to remember their names, we have auto-suggestion) beforehand or in runtime.
+ Also can add a comment for each clip.
+ Syntax highlighting for any language on Evernote just like what you see in Eclipse.
+ Authentication is delegated to Eemory using the OAuth protocol. One-click OAuth authentication as easy to use as a web application did.

## Limits

+ Can not handle `>` appear in the value for the attributes of an XML(called ENML in Evernote context) document. This only happens when you clip content to an existing note on Evernote. Even though Eemory will not break your data, just an error dialog will pop up. If you don't clear about what are talking about here, don't worry, this just means Eemory accept a [SGML-compatible](http://www.w3.org/TR/REC-xml/#dt-compat) XML, which is almost most cases.

## Screenshots

![screenshot](com.prairie.eemory/res/plugin-screenshot.png)

## Installation

Pre-built package is ready to install and requires a minimum of JDK 1.6.

+ Install from Eclipse Marketplace within Eclipse(recommended).
+ Install from update site URL: `http://dl.bintray.com/prairie/eemory-update-site`.
+ Install from [archived update site](https://github.com/prairie/Eemory/releases) locally.

New to Eclipse plug-in? Please check out this [Wiki](https://github.com/prairie/Eemory/wiki/Installation-instructions) for complete instructions, or go to [Eclipse Marketplace](http://marketplace.eclipse.org/content/eemory) website for more information.

## Contributing

Interested? Awesome! Please refer to Todo list below, once get ready for your code and feel free to submit a pull request. You have your features want to implement? Just go ahead.

We are always thrilled to receive pull requests, and do our best to process them as fast as possible.

The only principle when you working on this we need to remember is simple and intuitive.

Need more information? Please check out this [Wiki](https://github.com/prairie/Eemory/wiki/Contributing).

## Todo

+ Add preference page with preference configurations:
 - Customize title of clipped note. User can specify the format of clipped note title using Regex or something else.
 - Appending or prepending for updating note case.
 - To one note or separate notes when clipping multiple files once.
+ Clip video support. Allow user to record a video and add to Evernote.
+ Caching user data that was sent to Evernote when the error occurred so it can be sent again after troubleshooted.
+ YXBJ support.
+ Revoke authorization from plug-in side.
+ Chinese language support.
+ Business support.

## Authors and Contributors

+ Jianwei Liu

## License

Eemory is released under the [MIT license](http://www.opensource.org/licenses/MIT).
