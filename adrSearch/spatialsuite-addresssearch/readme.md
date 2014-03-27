Local address search

Prerequiste:
S4 must be installed

### Unzip and copy the module to [cbinfo.config.dir]/modules/thirdparty/septima/adrsearch

### Update modules.xml:
```xml
<module name="adrsearch" dir="thirdparty/septima/adrsearch" permissionlevel="public"/>
```
  
### In cbinfo.xml create a param pointing to the configuration folder  
```xml
<!-- =================================== -->
<!-- adrSearch parameters                 -->
<!-- =================================== -->  
<param name="adrsearch.config.dir">[cbinfo.misc.dir]/custom/adrsearch</param>
```

### Create configuration file  
* Copy the example configuration file (config/config.xml) to [cbinfo.misc.dir]/custom/adrSearch/config.xml
* Update the configuration file so that it points to a datasource/command selecting your addresses (Please see the file for documentation)  

### Copy .jar file  
* Copy the included custom-dk.septima.spatialsuite.adrsearch-XX.jar file from lib to your WEB-INF/lib
* Remove old .jar file if theres is one

### Build your address index  
Call [YOURSITE]/jsp/modules/adrsearch/build.jsp

### Include tool in profile(s):
```xml
<tool module="adrsearch" name="adrsearch"/>
```  
### Test  
Open the profile in Spatial Map and test the searcher

External database (Only necessary in spatial map prior to 2.6)

1: Create a adrsearch schema in your postgis database using the script in /db/create-schema.sql  
2: Update cbinfo.xml. Assuming that your database is located at localhost include the following parameters in cbinfo.xml:  
```xml	
<!-- =================================== -->
<!-- adrSearch parameters                 -->
<!-- =================================== -->
<param name="module.adrSearch.index.externdb.type">postgis</param>
<param name="module.adrSearch.index.externdb.connect">localhost:5432/DBNAME</param>
<param name="module.adrSearch.index.externdb.user">USERNAME</param>
<param name="module.adrSearch.index.externdb.pwd">PASSWORD</param>
<param name="module.adrSearch.index.externdb.srid">[cbinfo.mapserver.epsg]</param>
```  
3: Restart your site  
