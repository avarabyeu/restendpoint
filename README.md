# restendpoint

Asynchronous REST client based on Apache Http Async Client

* [Maven Dependencies](#maven-dependencies)
* [Getting Started](#getting-started)

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

```java
RestEndpoint restEndpoint = new HttpClientRestEndpoint(HttpAsyncClients.createDefault(),
                Lists.<Serializer>newArrayList(
                        new StringSerializer(), 
                        new ByteArraySerializer()), 
                new DefaultErrorHandler(),
                "http://REST_SERVICE_URL");
```
