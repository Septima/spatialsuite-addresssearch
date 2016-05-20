# Local address search

Prerequisites:
S4 must be installed

## Installation

### Unzip and copy the module to [cbinfo.config.dir]/modules/thirdparty/septima/adrsearch

### Update modules.xml:
```xml
<module name="adrsearch" dir="thirdparty/septima/adrsearch" permissionlevel="public"/>
```

### Copy .jar file  
* Copy the included custom-dk.septima.spatialsuite.adrsearch-XX.jar file from lib to your WEB-INF/lib
* Remove old .jar file if there is one

### Include tool in profile(s):
```xml
<tool module="adrsearch" name="adrsearch"/>
```  
  
## Configuration

### In cbinfo.xml create a param pointing to the configuration folder  
```xml
<!-- =================================== -->
<!-- adrSearch parameters                 -->
<!-- =================================== -->  
<param name="adrsearch.config.dir">[cbinfo.misc.dir]/custom/adrsearch</param>
```

### Create configuration file  
* Copy the example configuration file (config/config.xml) to [cbinfo.misc.dir]/custom/adrsearch/config.xml
* The configuration file points to a datasource/command, ds_adrsearch/read_adresser, selecting your addresses (Please see the file for documentation)  
* Create a datasource ds_adrsearch with a command read_adresser in your custom datasources.xml
** The command MUST return these columns which MUST be not null:  
	addressaccessid varchar(255) Unique id of address  
	streetname varchar(255)  
	streetbuildingidentifier varchar(5) number and evt litra combined, eg "11A"  
	postcodeidentifier varchar(10)  
	districtname varchar(50)  
	presentationstring varchar(325) Full presentation of address  
	geometrywkt varchar(50) Point in wkt format  
** The command MUST sort the streetbuildingidentifier in a natural order  
** The command name MUST start with "read"  

### Build your address index  
Reload your site and    
Call [YOURSITE]/jsp/modules/adrsearch/build.jsp

### Test  
Open the profile in Spatial Map and test the searcher

### Ekstern db
Create schema using sql file
Create params
    <param name="adrsearchadrSearch.index.externdb.type">postgis</param>
    <param name="adrsearchadrSearch.index.externdb.connect">localhost:5432/data</param>
    <param name="adrsearchadrSearch.index.externdb.user">postgres</param>
    <param name="adrsearchadrSearch.index.externdb.pwd">postgres</param>
    <param name="adrsearchadrSearch.index.externdb.srid">25832</param>

