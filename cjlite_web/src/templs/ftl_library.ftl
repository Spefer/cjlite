<#--
 -->
<#macro css fileName>${path.getCSSPath(viewResolver,themeName,fileName)}</#macro> 
<#--
 -->
<#macro js fileName>${path.getJSPath(viewResolver,themeName,fileName)}</#macro> 
<#--
 -->
<#macro image fileName>${path.getImagePath(viewResolver,themeName,fileName)}</#macro> 
<#--
 -->
<#macro AppPath url>${path.getContentPath(url)}</#macro> 
