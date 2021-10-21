# crud-admin-spring-boot-starter
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.qlefevre/crud-admin-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.qlefevre/crud-admin-spring-boot-starter)
[![build](https://github.com/qlefevre/crud-admin-spring-boot-starter/actions/workflows/build.yml/badge.svg)](https://github.com/qlefevre/crud-admin-spring-boot-starter/actions/workflows/build.yml)

# Installation
To use the latest release of crud-admin-spring-boot-starter, please use the following snippet in your pom.xml.
```xml
<dependency>
    <groupId>com.github.qlefevre</groupId>
    <artifactId>crud-admin-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```
# Features

Supported Types for annotation @Id : 
* String
* Long
* Integer

Supported Types for persistent field (@Column) :
* String
* Long, Integer, Short, Byte, Float, Double
* Boolean
* Enum
* Date

Input spinner components used for numbers can be controlled with the folling annotations:
* minimal value with @Min or @Range
* maximum value with @Max or @Range
* decimals with @Column(scale=2) for value like 1.00

Date format used is "yyyy-MM-dd HH:mm:ss"

Add the following properties to your application.properties to change the behavior:
```properties
# Enable / Disable module
crudadmin.enabled=true
# Admin URL path
crudadmin.url=admin
# Default page size
crudadmin.defaultpagesize=10
# Edit template name
crudadmin.templateedit=adminedit
# List template name
crudadmin.templatelist=adminlist  
# View template name
crudadmin.templateview=adminview
# Class to instantiate for custom ObjectIdSerializer
crudadmin.objectidserializer=com.package.CustomCrudAdminObjectIdSerializer
``` 
