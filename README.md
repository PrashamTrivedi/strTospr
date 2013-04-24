strTospr
========

What is it?
=====================
The Project that creates a spreadsheet out of android strings.xml files. It will be helpful to get all localized strings to single excel file that can be given to client or marketting team for reference.

How do you write excell spreadsheets?
=====================================
Jxl Library has been used

How do I use this code for me?
===============================
 - We ask to input two parameters runtime

	1. Project Name : (As we refer to it) this will be used to enter the file name. 
	2. Res folder path : Path for your projects' "res" folder

 - We get strings.xml for all resource qualifiers. For each resoure qualifier that has strings.xml for it will have a column in the excell file
 - Every key of the strings.xml will go into rows.
 
 - We will save generated Xml file into C:/StrToSprTest/ (So as per current limitations it may not work in any OS that does not have C: Drive, Planning to resolve this feature soon)
 - Your final xml file will C:/StrToSprTest/ProjectName.xls
 
 And you are ready to distribute your translations.