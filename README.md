# Object-oriented HTML
HTML isn't a programming language as such, it's actually a *markup language*.  This means that it misses out on a lot of the good stuff that real programming languages have, including the joys of object-oriented programming.  This project brings inheritance, polymorphism, and public "methods" to HTML.  With startling imagination, I've called it object-oriented HTML and chosen the file extension `.oohtml`.

## Installation and usage
Download the `OOHTML.jar` file and run it from the command line, passing a list of space-separated file paths to compile as arguments.  (If you pass a path to a directory, it will recursively compile all .oohtml files in the directory.)
```bash
$ java -jar OOHTML.jar /path/to/file/one.oohtml /path/to/directory/
```
Normally, the compiler saves compiled HTML to a .html file with the same name and file path as the `.oohtml` file it compiled.  However, preceding file or directory paths with `-o` will make the compiler overwrite the original `.oohtml` file with the compiled code.  Note that its extension will *not* be changed to `.html`.
```bash
$ java -jar /file/to/not/overwrite.oohtml -o /file/to/overwrite.oohtml
```

## Syntax
### Public 'methods'
Use the `expose` tag to mark a public block of HTML.
```html
<!--Part of file1.oohtml-->
<body>
	<div id="not_exposed"></div>
    <div id="exposed" expose>Lorem ipsum.</div>
</body>
```
The `id` attribute serves as the identifier for the block.  To reference a block, use the `block` element with the `src` attribute.
```html
<!--Part of file2.oohtml-->
<body>
	<block src="relative/path/to/file1.oohtml/exposed" />
</body>
```
When the above code is compiled, the `block` tag will be replaced with the block to which it points.  (Note that the block name is seen as a continuation of the path to the file containing the block.)
```html
<!--Part of the compiled file2.html-->
<body>
	<div id="exposed">Lorem ipsum.</div>
</body>
```
The `expose` attribute, no longer needed, has been removed.

Blocks can be nested within each other, and a `block` element can reference a block in the same file -- just use its `id` attribute as the `src` with no file path.
### Extension
One object-oriented HTML document can extend another one, as follows:
```html
<html extends="file2.oohtml">
```
The `extends` attribute is only valid if it is found on the `html` element.

When an element of any sort *except* for a complete document is extended, the sub-elements of the "superclass element" are copied into the "subclass element" as the first of its children.
```html
<div id="1">
	Text.
	<span>Text text.</span>
</div>

<!--A second div element, div 2, extends div 1.  This is it before the extension.-->
<div id="2">
	Lorem ipsum.
	<canvas />
</div>

<!--This is div 2 after the compiler has dealt with the extension.  Div 1 is unchanged.-->
<div id="2">
	Lorem ipsum.
	<span>Text text.</span>
	<canvas />
</div>
```
Note that the plain text in div 1 was not copied into div 2.  This is by design, so that you can build templates that use dummy text without the dummy text being copied into the pages that extend it.

Although the `extends` element is found in the `html` tag, the `html` element is never extended.  It is probably best to read `<html extends="x">` as `<html><head extends="x">...<body extends="x">`.

When a child of the superclass element shares an `id` with a child of the subclass element, that child is itself treated as an element to be extended.
```html
<div id="1">
	<div id="subdiv">
    	<canvas />
    </div>
    <span>Text.</span>
</div>

<!--Before compilation.  Div 2 extends div 1.-->
<div id="2">
	<div id="subdiv">
    	Lorem ipsum dolor sit amet!
    </div>
</div>

<!--After compilation.-->
<div id="2">
	<div id="subdiv">
    	Lorem ipsum dolor sit amet!
        <canvas />
    </div>
    <span> Text.</span>
</div>
```
It is perfectly possible to have a 'chain' of extensions, so `file1.oohtml` could extend `file2.oohtml`, which itself extends `file3.oohtml`.
###Compilation order
The `extends` attribute is always resolved *before* the `block` element.
## What is it good for?
### Templates
This is, beyond doubt, the thing that I think object-oriented HTML would be most useful for.  You can create a nicely laid-out template with blank spaces where the content should be, and then a whole series of bare-bones files containing nothing but the content, but which extend the template file.  You can then compile them and have a set of files containing both the content and the formatting needed to display it right.  If you wanted to make a change to the formatting, all you would need to do would be to edit the template and recompile the content files.
### Libraries
One of the brilliant things about public methods in other programming languages is the ability to create libraries, like JSoup (on which OOHTML relies).  Using public methods and extension, it would be possible to create a 'library' for HTML.  You could have pages full of things  like social network feeds and weather applets, which could be integrated into any page that needed them.  The library page wouldn't even have to live on the server -- it would only have to be accessible to the compiler.
### Anything you want!
I think that templates and libraries would be handy, but that's just my opinion.  You can do whatever you want with this, including adding new features to the compiler.  Feel free to send me any pull requests or bug reports.
## Contribution guide
There's a lot of stuff that I think would be great for this compiler to have, but that I don't have the time or the know-how to build.  If you want to contribute, I would suggest starting here.
### Plugins for build systems
Right now, you can only build from the command line.  It would be quite helpful if you could build from Maven or Gradle or another build system as well.
### IDE integration
There are a lot of IDEs for object-oriented languages, and a lot for HTML, but no IDE or IDE plugin for object-oriented HTML.  We should change that.
## Legal
Object-oriented HTML uses JSoup, which is licensed under the MIT license.
```
© 2009-2015, Jonathan Hedley <jonathan@hedley.net>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

Object-oriented HTML is also MIT-licensed.

# Have fun!
