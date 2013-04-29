strTospr
========

What is it?
=====================
The Project that creates a spreadsheet out of android strings.xml files. It will be helpful to get all localized strings to single excel file that can be given to client or marketting team for reference.

How do you write excell spreadsheets?
=====================================
We use [Jxl Library](http://sourceforge.net/projects/jexcelapi/) to create xls spreadsheets.

How do I use this project for me?
===============================
 - We ask to input three parameters runtime
	1. Parent Path : Path where your file is being saved.
	2. Project Name : (As we refer to it) Name of the file. 
	3. Res folder path : Path for your projects' "res" folder or it's parent folder.

 - We get strings.xml for all resource qualifiers. 
 - For each resoure qualifier that has strings.xml for it will have a column in the excell file. (i.e. Values will have "default" column name, Values-fr will have "fr" column name)
 - Every key of the strings.xml will go into rows.
 - We will save generated xls file as parentPath>StrToSpr>projectName.xls
 
 And you are ready to distribute your translations.
