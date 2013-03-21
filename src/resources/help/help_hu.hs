<?xml version="1.0" encoding="ISO-8859-2"?>
<!DOCTYPE helpset   
PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

<helpset xml:lang="hu" version="1.0">
  <!-- title -->
  <title>PDFIndexer Súgó</title>

  <!-- maps -->
  <maps>
     <homeID>greeting</homeID>
     <mapref location="hu/help.jhm"/>
  </maps>

  <!-- views -->
  <view mergetype="javax.help.UniteAppendMerge">
    <name>TOC</name>
    <label>Tartalom</label>
    <type>javax.help.TOCView</type>
    <data>hu/helpTOC.xml</data>
  </view>

  <view mergetype="javax.help.SortMerge">
    <name>Index</name>
    <label>Tárgymutató</label>
    <type>javax.help.IndexView</type>
    <data>hu/helpIndex.xml</data>
  </view>

  <presentation default="true" displayviewimages="false">
    <name>main window</name>
    <title>PDFIndexer Súgó</title>
    <image>icon.help</image>
    <toolbar>
      <helpaction image="icon.back">javax.help.BackAction</helpaction>
      <helpaction image="icon.forward">javax.help.ForwardAction</helpaction>
      <helpaction>javax.help.SeparatorAction</helpaction>
      <helpaction image="icon.home">javax.help.HomeAction</helpaction>
    </toolbar>
  </presentation>
</helpset>
