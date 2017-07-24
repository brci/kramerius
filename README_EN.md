# KRAMERIUS digital library
Kramerius is a JEE web application to access digital documents. It is primarily designed for digitized library collections, monographs and periodicals. Other types of documents like maps, music and old prints, or parts of documents like articles and chapters can be added. The system is suitable for so-called born digital documents, documents that have been created in electronic form. 

Kramerius' metadata structure conforms to the standards of the National Library of the Czech Republic.

The metadata structure can be changed to comply to other standards.

The system provides an end-user interface to
* search in metadata and fulltext, 
* generate multi-page PDF documents from selected pages, 
* create virtual collections,
* provide further operations over a digital document collection. 

Current version v5.0 (since 2013). Java 8.

---
 
* 1 [About](#about)
    * 1.1 [Open Source Software used](#open-source-software-used)  
    * 1.2 [Software requirements](#software-requirements) 
    * 1.3 [Hardware requirements](#hardware-requirements) 
    * 1.4 [Production](#production) 
    * 1.5 [Content of kramerius-x.zip](#contentofkrameriusxzip)
    * 1.6 [Kramerius configuration files](#krameriusconfigurationfiles)


* 2 [Installation on Ubuntu 16.04 LTS](#installation)  
    * 2.1 [Bash, env variables, system user](#bashenvvariablessystemuser)


* 3 [Prerequisites](#prerequisites)
    * 3.1 [System user](#systemuser)
    * 3.2 [E.g. Tomcat](#tomcat)
    * 3.3 [Postgres](#postgres)
    * 3.4 [Fedora Commons](#fedoracommons)
    * 3.5 [Solr](#solr)


* 4 [Kramerius](#kramerius)  
    * 4.1 [Fedora](#krameriusfedora)  
        * 4.1.1 [fedora.fcfg](#fedorafcfg)     
       * 4.1.2 [Access rights](#krameriusaccessrights)  
       * 4.1.3 [Fedora.war](#fedorawar)  
               
    * 4.2 [Solr](#krameriussolr)
        
    * 4.3 [Tomcat](#krameriustomcat)
        * 4.3.1 [webapps](#tomcatforkrameriuswebapps)
        * 4.3.2 [security](#tomcatforkrameriussecurity)      
        
    * 4.4 [Kramerius configuration files](#configurationfiles)
        
    * 4.5 [FOXML models](#foxmlmodels)


* 5 [Kramerius further settings](#furthersettings)  
    * 5.1 [URI encoding](#uriencoding)  
    * 5.2 [Authentication](#furtherauthenticationg)  
        * 5.2.1 [Single sign-on](#sso)
        * 5.2.2 [JAAS](#jaas)
 
    * 5.3 [Style and view](#styleandview)  
       * 5.3.1 [GUI labels](#guilabels)  
       * 5.3.2 [Bookmark order, content of the home page](#bookmarkorder)  
       * 5.3.3 [Css, graphics, icons](#cssgraphicsicons)  
       * 5.3.4 [DeepZoom and zoomify protocols (Optional)](#zoom)  

    * 5.4 [Performance](#performance)  
       * 5.4.1 [IIP server](#iipserver)  
       * 5.4.2 [Internal cache](#internalcache)  

    * 5.5 [Optional applications](#optionalapps)  
       * 5.5.1 [Client](#client)  
       * 5.5.2 [Editor](#editor)  


---

## About
<a name="open-source-software-used"></a>
Open Source Software used:

* [Fedora repository](http://www.fedora-commons.org), the core of the system
* Apache web server, latest version
* J2EE container, e.g. Apache Tomcat (< 8.0.39)
* Apache Solr	latest version
* Postgres SQL	latest version

---

<a name="software-requirements"></a>
Software requirements  
 
* Java: 			JDK, at least 1.6     
* Application server: 	Supporting Servlet 2.5 and JSP 2.1 (at least)
    * Please note (!): Please use [Tomcat version < 8.0.39](http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.38/) (please refer to Kramerius-bug# 470)

* Database: Postgres, at least 8.4     
* Storage: Fedora Commons 3.4, 3.5, 3.6, 3.7, 3.8 or 3.8.1     
* SMTP server: any

---
<a name="hardware-requirements"></a>
Hardware requirements

- Storage: at least twice the anticipated image data size
- RAM: for graphic data (thumbnails, zooming), at least: 8 GB

---
<a name="production"></a>
Production

- Webserver with port 80, e.g. Apache with mod_proxy and mod_proxy_ajp
- J2EE server (Tomcat)

- Each Kramerius component can be installed on a separate server:
    - Fedora repositories, 
    - Kramerius application (search.war), 
    - SOLR indexer, 
    - PostgreSQL database
    
- K5 should be installed under a separate user account, in the following: kramerius4

---
<a name="contentofkrameriusxzip"></a>
Content of kramerius-x.zip

* Installation-x.x.x.zip: support files for initial installation
    * contents do not change for updates     


* Core-x.x.x.tar.gz: Kramerius application (search.war)
    * install during initial installation and subsequent updates     


* Security-core-x.x.x.tar.gz: security-core.jar single-sign-on support 
    * should be installed in the tomcat/libs directory
	* during initial installation and subsequent updates


* Client-x.x.x.tar.gz: alternate separate client application (client.war)
    * install during initial installation and subsequent updates


* Editors-x.x.x.zip: application editor, order editor of the document parts

If a file is missing in a release, please keep the files from the previous release.

---
<a name="krameriusconfigurationfiles"></a>
Kramerius configuration files

* e.g. with system user kramerius4:

* When first running the server with Kramerius installed (= search.war unpacked) the .kramerius4 directory is created.

* location: $USER_HOME/.kramerius4

{$USER_HOME}/.kramerius4:

* configuration.properties: basic shared properties
* search.properties: GUI
* client.properties
* indexer.properties: plug-in modules
* migration.properties: plug-in module

---
---
<a name="installation"></a>

## Installation on Ubuntu 16.04 LTS

<a name="bashenvvariablessystemuser"></a>

### Bash, env variables, system user

using bash:

[terminal]  
echo $0  
// answer: bash  
[/terminal]

---

Variables to set:
* CATALINA_HOME
* FEDORA_HOME
* JAVA_HOME
* JAVA_OPTS

Examples:

[bash]  
Export CATALINA_HOME = $HOME/tomcat  
Export FEDORA_HOME = $HOME/fedora  
Export JAVA_HOME = /usr/java/jre1.6.0_20  
Export JAVA_OPTS="-Djava.awt.headless=true -Dsolr.solr.home=$FEDORA_HOME/solr -XX:MaxPermSize=128m -Xms512m -Xmx1024m"  
Export PATH=$PATH:$FEDORA_HOME/server/bin:$FEDORA_HOME/client/bin:$CATALINA_HOME/bin:$JAVA_HOME/bin  
[/bash]

Users to create:

* system user: kramerius4
* postgres: fedoraAdmin

---
<a name="prerequisites"></a>

## Prerequisites

<a name="systemuser"></a>

### System user

* Create the system user

[bash]  
adduser --system --group kramerius4   
[/bash]  

$USER_HOME, then, is: /home/kramerius4/

* become kramerius4:

[bash]  
sudo -u kramerius4 bash   
[\bash]   

* (later) start Tomcat as user 'kramerius4':
/bin/startup.sh

---

<a name="tomcat"></a>

### Tomcat

* e.g. https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04

* Tomcat is installed in folder CATALINA_HOME (e.g. /opt/apache-tomcat-8/

* set CATALINA_HOME

---

<a name="postgres"></a>

### Postgres

* e.g. https://www.digitalocean.com/community/tutorials/how-to-install-and-use-postgresql-on-ubuntu-16-04

* create: user fedoraAdmin 

* create: databases: 
    * fedora3
    * kramerius4
    * riTriples (when using the Fedora Resource Index implementation MPTTripleStore). 

[bash]  
sudo -i -u postgres
> switches to the postgres account

psql
> come into the postgres prompt

\q
> .. to leave the postgres prompt
...

\l
> list all databases

\c kramerius4
> connect to the database 'kramerius4'

\dt
> list tables of the connected database

> change password:

ALTER USER "user_name" WITH PASSWORD 'new_password';

> create database:

CREATE DATABASE fedora3;  
CREATE DATABASE kramerius4;  
CREATE DATABASE "riTriples";  

> change database owner:

ALTER DATABASE fedora3 OWNER TO "fedoraAdmin";  
ALTER DATABASE kramerius4 OWNER TO "fedoraAdmin";  
ALTER DATABASE "riTriples" OWNER TO "fedoraAdmin";  

> set rights to a database:

\c fedora3
grant all privileges on database fedora3 to "fedoraAdmin";  
GRANT SELECT ON ALL TABLES IN SCHEMA fedora3 TO "fedoraAdmin";  

\c riTriples
grant all privileges on database riTriples to "fedoraAdmin";  
GRANT SELECT ON ALL TABLES IN SCHEMA "riTriples" TO "fedoraAdmin";  

\c kramerius4
grant all privileges on database kramerius4 to "fedoraAdmin";  
GRANT SELECT ON ALL TABLES IN SCHEMA kramerius4 TO "fedoraAdmin";  
[/bash]

* Driver:

Postgres JDBC driver: http://jdbc.postgresql.org/download.html  
For Tomcat, putting it in /lib/ 

---

<a name="fedoracommons"></a>

### Fedora Commons

* set FEDORA_HOME

[bash]  
export FEDORA_HOME=/{Fedora-folder-path}  
> check it:  

printenv | sort  
> lists environment variables alphabetically  

[/bash]

* Installation, e.g. http://asingh.com.np/blog/fedora-commons-installation-and-configuration-guide/
    * start here: Obtain Fedora Commons Installer
    * 'common' installation
    * did with: tomcat external
    * XACML: false
    * resource index enabled: true
    * deploy local demos: false
   
   
* The installation is done with the message:
      Will not overwrite existing /{tomcat folder}/conf/server.xml.
      Wrote example server.xml to: /{FEDORA_HOME}/install/server.xml


* now add this file 'server.xml' to {CATALINA_HOME}/conf/


* Fedora settings, please read:
    * [Configuration chain](https://wiki.duraspace.org/display/FEDORA4x/Deploying+Fedora+4+Complete+Guide#DeployingFedora4CompleteGuide-ConfigurationChain)
    * [Catalina Java properties](https://wiki.duraspace.org/display/FEDORA4x/Deploying+Fedora+4+Complete+Guide#DeployingFedora4CompleteGuide-javapropsCatalinaJavaProperties)


---

<a name="solr"></a>

### Solr

SOLR 6.X

Download SOLR, standalone server. 

SOLR as a side-by-side application within a single tomcat is no longer supported: [whynowar](https://wiki.apache.org/solr/WhyNoWar)

Unzip the files somewhere (we call the folder now: {solr_home})

---
---

<a name="kramerius"></a>

## Kramerius


<a name="krameriusfedora"></a>

### Fedora

<a name="fedorafcfg"></a>

#### fedora.fcfg

* Set aliases 

sudo nano $FEDORA_HOME/server/config/fedora.fcfg


Add aliasing definitions for XML namespaces, find: 

    <module role="org.fcrepo.server.resourceIndex.ResourceIndex" 
    class="org.fcrepo.server.resourceIndex.ResourceIndexModule">

add:

    <param name="alias:kramerius" value="http://www.nsdl.org/ontologies/relationships#">   
	  <comment>Alias ​​for Kramerius</comment>   
    </param>   
    <param name="alias:oai" value="http://www.openarchives.org/OAI/2.0/">      
	  <comment>Alias ​​for OAI</comment >     
    </param>     

Change postgres parameter, find:

    <datastore id="localPostgresMPTTriplestore">

adjust e.g. the password:

    <param name="password" value="fedoraAdmin"/>

Please note: Kramerius supports the implementation of the Mulgara Resource Index and the MPTtripleStore.
Default is: MPTtripleStore. The Mulgara-implementation, currently, is not usable with the OAI provider.

---

<a name="krameriusaccessrights"></a>

#### Access rights 

- Kramerius has its own access control system and does not use Fedora Commons XACML. 

- Deactivation, one options is:

    * in fedora.fcfg, find the parameter, set the value to 'all requests':
    
     <module role="org.fcrepo.server.security.Authorization" ..>
        <param name="ENFORCE-MODE" value="permit-all-requests"/>. 


If you want to use the Fedora Commons authorization system, the following situation might occur:   

- If you want to access the Fedora Admin Interface from another computer than the one on which it is installed, you must deactivate or modify the access restrictions:

    deny-apim-if-not-localhost.xml     
  
- When using FOXML objects with externally referenced data stream content, you must deactivate or modify the rule:

    deny-unallowed-file-resolution.xml

- XACML rules are stored in $FEDORA_HOME/data/fedora-xacml-policies/repository-policies 

Deactivation can be done by editing xacml-rules in a file, or by simply deleting it. 

---

<a name="fedorawar"></a>

#### fedora.war

please find fedora.war in {FEDORA_HOME} and copy it (copy, paste) to {CATALINA_HOME}/webapps

* now start tomcat:

go to folder where tomcat is installed   
{CATALINA_HOME}/bin/

* become kramerius4:   
sudo -u kramerius4 bash

start tomcat as user 'kramerius4':   
./startup.sh

Open a browswer. Try to open localhost:   
// with the port, that was set. default: 8080. here changed to e.g.: 8084

http://localhost:8084

> should show the tomcat page

change in {CATALINA_HOME}/confg/tomcat-users.xml, add a user with role: gui-manager

restart tomcat:  
{CATALINA_HOME}/bin/shutdown.sh    
{CATALINA_HOME}/bin/startup.sh  

open the app-manager, use user and password:

http://localhost:8084/manager

* Try to 'start' Fedora

* If not working, that is probably a case of permissions:
    * was tomcat started 'being' the system user? (e.g. kramerius4)?
    * user 'fedoraAdmin' might not have sufficient permissions for the databases (postgres)
    * a folder /upload/ exists

a) become system user kramerius4, stop/start tomcat

b) check the database permissions and repeat the steps of 'grant' above, for postgres

c) folder /upload/ does exist in FEDORA_HOME/server/management

sudo rm -R $FEDORA_HOME/server/management/upload/

---

<a name="krameriussolr"></a>

### Solr

Files: Kramerius installation-x.zip: /solr-6.x/kramerius/ or [Git](Git: https://github.com/ceskaexpedice/kramerius/tree/master/installation/solr-6.x/kramerius)


Add the files to: {solr_home}/server/solr/kramerius

goto: {solr_home}    
bin/solr start

Open in browser: http://localhost:8983/administration

Here, create a new core: kramerius.    
name, instanceDir: kramerius, others as predefined    

PLEASE note: might through an error. Ignore it.

---

<a name="krameriustomcat"></a>

### Tomcat

<a name="tomcatforkrameriuswebapps"></a>

#### webapps

* installation-x.zip:
    * client.war
    * editor.war
    * search.war
    * rightseditor.war

copy them to tomcat: {CATALINA_HOME}/webapps

<a name="tomcatforkrameriussecurity"></a>

---

#### security

* security-core.jar

copy to tomcat: {CATALINA_HOME}/lib

become kramerius4:

sudo -u kramerius4 bash

start tomcat as user 'kramerius4':

/bin/startup.sh


Browse: localhost:8085/manager, 

.. unpack the war-files

---

<a name="configurationfiles"></a>

### Kramerius configuration files

After first running the application server with Kramerius installed (= search.war unpacked)
the .kramerius4 directory is created.

location: $USER_HOME/.kramerius4/

After the 1st installation, do these settings:

configuration.properties:

\#Search core settings  
solrHost=http://localhost:8983/solr/kramerius

.........  
To migrate an old index, please use the "migration process". Therefore:

\# Destination solr index  
.dest.solrHost=http://localhost:8983/solr/kramerius/update  

Then, start the migration process: Administrative Menu, Index Migration ...    
Then, move kramerius to a new index. See point 7.//?? WHERE  
.........  

---

<a name="foxmlmodels"></a>

### FOXML models

FOXML objects are in the installation-x.x.zip, /fedora/
 
Through the Fedora Administrator Interface, import the FOXML objects.

Fedora Commons Admin:

$FEDORA_HOME/client/bin/fedora-admin.sh

u/p: fedoraAdmin/fedoraAdmin
 
If you import the OAI Fedora model, you need to configure it before importing it. 

---

<a name="furthersettings"></a>

## Further settings


<a name="uriencoding"></a>

### URI encoding

UTF-8 (example for Tomcat) 

$CATALINA_HOME/conf/server.xml

    <Connector port="8080" protocol="HTTP/1.1" 
      connectionTimeout="20000" URIEncoding="UTF-8"
      redirectPort="8443" />

---

<a name="furtherauthentication"></a>

### Authentication

<a name="sso"></a>

#### Single sign-on

Login for all 3 Kramerius web applications (search, admin editor rightseditor):

$CATALINA_HOME/conf/server.xml

    <Host name="localhost" ...>
     ...
    <Valve className="org.apache.catalina.authenticator.SingleSignOn" />
     ...
    </Host>

---

<a name="jaas"></a>

### JAAS

Kramerius uses the standard authentication mechanism JAAS
 
(cz.incad.kramerius.security.jaas.K4LoginModule,   
user role: cz.incad.kramerius.security.jaas.K4RolePrincipal). 

$CATALINA_HOME/conf/Catalina/localhost 

search.xml    
rightseditor.xml

    <Context>
	  <Realm className="org.apache.catalina.realm.JAASRealm"                 
	   appName="search"
	   roleClassNames="cz.incad.kramerius.security.jaas.K4RolePrincipal"
	   debug="99"/>
    </Context>


If you are running Kramerius on the same server as Fedora, enable the authentication of these modules in the Fedora Commons JAAS configuration (example for Tomcat) 

If you are running Kramerius on a server other than Fedora, set the authentication data according to your JAAS documentation. The first time you run the application, database tables with predefined authorization rules and the default administrator are created. 

Predefined: KrameriusAdmin/KrameriusAdmin

---

<a name="styleandview"></a>

### Style and view

<a name="guilabels"></a>

#### GUI labels

labels_cs.properties for user editor //?? where is it?   
/.kramerius4/bundles/labels.properties //?? not existing

/kramerius4/.kramerius4/texts/default_intro_EN_en

Intended structure //??folders seem not to be used 

/texts/first_page_nolines_xml 	PDF Generation dialog, and in the document    
/texts/help 	 Contains help text.     
/texts/rightMsg	 text about unavailability of a document. If not defined: labels.properties, key: rightMsg     
/texts/intro	 About the digital library. Default text intro.   
/texts/k5info	 Display in client application. Default text intro.

---

<a name="bookmarkorder"></a>

#### Bookmark order, content of the home page

Bookmarks with selected items and tabs displayed are set in: 

~/.kramerius4/search.properties


* List and rank lists
    * Search.home.tabs = custom, mostDesirables, newest, facets, browseAuthor, browseTitle, info, collections, favorites


*  List of pids (separate carkos) of the object displayed on the custom tab
    * Search.home.tab.custom.uuids =

(simply add uuid: 045b1250-7e47-11e0-add1-000d606f5dc6, uuid: 0eaa6730-9068-11dd-97de-000d606f5dc6 ...)

Contents of the information tab can be replaced by ~/.kramerius4/texts/intro

---

<a name="cssgraphicsicons"></a>

#### Css, graphics, icons 

/css  
/img

in tomcat/webapps/search

Alternatively, you can edit the jQuery UI theme as follows: 

On http://jqueryui.com/themeroller/ create your own graphical theme and save it in the css directory, 
css file must be: jquery-ui.custom.css (ie without the version number).   

You can toggle between themes using the theme parameter URL whose value is the name of the subdirectory with the theme. 

Example:  
Predefined jQuery theme 'sunny'. Unzip into webapps/search/css/sunny  
Change the css file name to jquery-ui.custom.css and try address:  

 	k4server/search/?Theme=sunny 

Set a topic as default: change line 76 in search/inc/html_header.jsp  instead of "smoothness" write your topic-name.

---

<a name="zoom"></a>

#### DeepZoom and zoomify protocols (Optional) 

It is possible to use the integrated Seadragon browser [http://www.zoom.it](http://www.zoom.it). 

The Seadragon browser is enabled for digital objects, like monographs, if the FOXML definition contains:

    <kramerius4: tiles-url> 

in the RELS-EXT RDF data stream. Two alternatives can be used??

---

<a name="Performance"></a>

### Performance

<a name="iipserver"></a>

#### IIP server

[http://help.oldmapsonline.org/jpeg2000](http://help.oldmapsonline.org/jpeg2000) 

Kramerius serves as a mediator. Client queries are forwarded to the IIP server, and it only takes care of request authorization. 

The value of 

    <kramerius4: tiles-url> 

is the URL for the zoomed image in the IIP server. Examples of definitions in RELS-EXT:

[txt]

---

<a name="internalcache"></a>

#### Internal cache

Use the kramerius system to generate files in the internal cache. The internal cache replaces the external server image function. 

Therefore: set kramerius4:tiles-url in kramerius4://deepZoomCache. 

Example: 

    <Kramerius4:tiles-url>kramerius4://deepZoomCache</kramerius4:tiles-url> 

Note: The cache must be filled by executing "Generating Deep Zoom Cache ..." in the Kramerius Admin menu. 

* You can use 
    * Seadragon (deepZoom) or 
    * OpenLayers (zoomify protocol).

Default settings (OpenLayers) can be changed in search.properties: 

    zoom.viewer=deepzoom  

---

<a name="optionalapps"></a>

### Optional applications

<a name="client"></a>

#### Client (Optional)

Self-serving application. 

Copy client.war to tomcat/webapps.

Modify client.properties: 

    api.point = ${k4.host}/api/v5.0

---

<a name="editor"></a>

#### Editor (Optional)

Stand-alone web application. 

Copy editor.war to tomcat/webapps.

Configure single-sign-on (see Access Rights).

You can replace the editor with another editor. The editor is specified by the EditUrl entry in the file

