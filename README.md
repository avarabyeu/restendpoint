# restendpoint [![Build Status](https://avarabyeu.ci.cloudbees.com/buildStatus/icon?job=restendpoint)](https://avarabyeu.ci.cloudbees.com/job/restendpoint/)

Asynchronous REST client based on Apache Http Async Client


* [Maven Dependencies](#maven-dependencies)
* [Getting Started](#getting-started)
    * [Creating](#creating)    
    * [As Simple As It's Possible](#as-simple-as-its-possible)
    * [Sending Requests](#sending-requests)
        * [GET](#get)

Basically, **restendpoint** is convenient wrapper around 
[Apache HttpComponents Async Client](http://hc.apache.org/httpcomponents-asyncclient-4.0.x/)

## Maven Dependencies

Last stable version:
```xml
<dependency>
    <groupId>com.github.avarabyeu</groupId>
    <artifactId>restendpoint</artifactId>
    <version>0.0.2</version>
</dependency>
```

## Getting Started

### As Simple As It's Possible

```java
/* Creates default RestEndpoint */
RestEndpoint endpoint = RestEndpoints.createDefault("http://airports.pidgets.com/");

 /* Executes GET request to
  * http://airports.pidgets.com/v1/airports?country=Belarus&format=json
  * asynchronously
  */
Will<String> airports = endpoint.get(
   "/v1/airports",
   ImmutableMap.<String, String>builder()
      .put("country", "Belarus").put("format", "json").build(),
   String.class);

/* Waits for result and prints it once received */
System.out.println(airports.obtain());
```

### Creating

#### Default and Simpliest
```java
RestEndpoint endpoint = RestEndpoints.createDefault("http://airports.pidgets.com/");
```
Creates RestEndpoint with all availible serializers, default http client configuration and built-in error handler. 


#### Using Builder
```java
RestEndpoint endpoint = RestEndpoints.create()
   .withBaseUrl("http://base_url_of_rest_service")
   .withSerializer(new GsonSerializer())
   .withErrorHandler(new YourCustomErrorHandler())
   .withBasicAuth("login", "password")
   .build();
```   
Creates RestEndpoint with only JSON serializer based on Google GSON, your custom error handler. Each request to server will contain Basic Authentication headers (preemptive authentication, see more details here: [Apache Client Authentication](http://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html))


### Sending Requests

#### GET

```java
Will<String> responseBody = endpoint.get("/", String.class);
```
