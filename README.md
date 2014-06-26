# restendpoint [![Build Status](https://avarabyeu.ci.cloudbees.com/job/restendpoint/badge/icon)](https://avarabyeu.ci.cloudbees.com/job/restendpoint/)

Asynchronous REST client based on Apache Http Async Client

* [Maven Dependencies](#maven-dependencies)
* [Getting Started](#getting-started)
    * [Creating](#creating)
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

### Creating
```java
RestEndpoint restEndpoint = new HttpClientRestEndpoint(HttpAsyncClients.createDefault(),
                Lists.<Serializer>newArrayList(
                        new StringSerializer(), 
                        new ByteArraySerializer()), 
                new DefaultErrorHandler(),
                "http://REST_SERVICE_URL");
```
### Sending Requests

#### GET

```java
Will<String> responseBody = endpoint.get("/", String.class);
```
