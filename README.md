# restendpoint [![Build Status](https://travis-ci.org/avarabyeu/restendpoint.svg?branch=master)](https://travis-ci.org/avarabyeu/restendpoint) [![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.avarabyeu/restendpoint/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.avarabyeu/restendpoint)

Asynchronous REST client based on Apache Http Async Client


* [Maven Dependencies](#maven-dependencies)
* [Getting Started](#getting-started)
   * [As Simple As It's Possible](#as-simple-as-its-possible)
   * [Creating](#creating)    
      * [Default and Simpliest](#default-and-simpliest)
      * [Using Builder](#using-builder)
      * [Build HttpAsyncClient explicitly](#build-httpasyncclient-explicitly)      
   * [Sending Requests](#sending-requests)
      * [GET](#get)
      * [POST/PUT](#post/put)
      * [POST Multipart](#post-multipart)
      * [As Command](#as-command)
   * [Serializers](#serializers)

Basically, **restendpoint** is convenient wrapper around 
[Apache HttpComponents Async Client](http://hc.apache.org/httpcomponents-asyncclient-4.0.x/)

## Maven Dependencies

Last stable version:
```xml
<dependency>
    <groupId>com.github.avarabyeu</groupId>
    <artifactId>restendpoint</artifactId>
    <version>X.X.X</version>
</dependency>
```

## Getting Started

### As Simple As It's Possible

#### Interface-based RestEndpoint
Using RestEndpoint, you do not need to create implementation of you http client. No any implementation at all! 
Only thing you need is to create interface with method declarations marked with appropriate annotations:

```java

public interface SomeYourService {
    /* just simple GET request */
    @Request(method = HttpMethod.GET, url = "/")
    String get();

    /* GET request with placeholder in path */
    @Request(method = HttpMethod.GET, url = "/{path}")
    String getWithPath(@Path("path") String path);

    /* GET request with query parameters, e.g. ?someParameter=someValue */
    @Request(method = HttpMethod.GET, url = "/")
    String getWithQuery(@Query Map<String, String> queryParams);

    /* POST request with some body */
    @Request(method = HttpMethod.POST, url = "/")
    String post(@Body String body);

    /* PUT request with some body */
    @Request(method = HttpMethod.PUT, url = "/")
    String put(@Body String body);

    /* DELETE request */
    @Request(method = HttpMethod.DELETE, url = "/")
    String delete();

    /* Asynchronous GET request */
    @Request(method = HttpMethod.GET, url = "/")
    CompletableFuture<String> getAsync();

    /* Asynchronous POST request */
    @Request(method = HttpMethod.POST, url = "/")
    CompletableFuture<String> postAsync(@Body String body);

    /* Asynchronous PUT request */
    @Request(method = HttpMethod.PUT, url = "/")
    CompletableFuture<String> putAsync(@Body String body);

    /* Asynchronous DELETE request */
    @Request(method = HttpMethod.DELETE, url = "/")
    CompletableFuture<String> deleteAsync();
}

```
... and create instance of your service:

```java
SomeYourService service = RestEndpoints.create()
        .withBaseUrl("http://localhost")
        .withSerializer(new StringSerializer())                
        .forInterface(RestInterface.class);
```

That's it! RestEndpoint reads your interface and builds implementation based on Java Proxies. No any actions is required from you, http client is done. 

#### Classic RestEndpoint

```java
/* Creates default RestEndpoint */
RestEndpoint endpoint = RestEndpoints.createDefault("http://airports.pidgets.com/");

 /* Executes GET request to
  * http://airports.pidgets.com/v1/airports?country=Belarus&format=json
  * asynchronously
  */
CompletableFuture<String> airports = endpoint.get(
   "/v1/airports",
   ImmutableMap.<String, String>builder()
      .put("country", "Belarus").put("format", "json").build(),
   String.class);

/* Waits for result and prints it once received */
System.out.println(airports.get());
```

### Creating

#### Default and Simpliest
```java
RestEndpoint endpoint = RestEndpoints.createDefault("http://airports.pidgets.com/");
```
Creates RestEndpoint with all available serializers, default http client configuration and built-in error handler. 


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


#### Build HttpAsyncClient explicitly
Sometimes you need more deep http client configuration. Here is the example:

```java
HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClientBuilder.create();
CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "password"));
httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
httpClientBuilder.setMaxConnTotal(20);
httpClientBuilder.setMaxConnPerRoute(5);

RestEndpoint endpoint = RestEndpoints.create().withBaseUrl(HTTP_TEST_URK)
   .withSerializer(new StringSerializer()).withHttpClient(httpClientBuilder.build())
   .build();
```
So, you are able to configure HttpClient explicitly, but in this case builder's methods like #withBasicAuth() <b>will be overwritten</b>.


### Sending Requests

#### GET

```java
CompletableFuture<String> responseBody = endpoint.get("/", String.class);
```
#### POST/PUT

```java
CompletableFuture<String> postResponseBody = endpoint.post("/", "this is request body", String.class);
CompletableFuture<String> putResponseBody = endpoint.put("/", "this is request body", String.class);
```

#### POST Multipart

```java
MultiPartRequest multiPartRequest = new MultiPartRequest.Builder().
        addBinaryPart("part name", "filename.txt",
        MediaType.OCTET_STREAM.toString(),
        ByteSource.wrap("here is body".getBytes())).
        addSerializedPart("this part will be serialized using serializer", "part body").
        build();

CompletableFuture<String> post = endpoint.post("/", multiPartRequest, String.class);
```

Take a look at the request builder. We have possibility to provide some part as is (binary part, as byte array) and also
we can add part which will be serialized using some serializer. This pretty convenient when you need to send multipart request
with JSON's, for example

#### DELETE

```java
CompletableFuture<String> deleteResponseBody = endpoint.delete("/", String.class);
```

#### AS COMMAND

```java
RestCommand<String, String> command = new RestCommand<String, String>("/", HttpMethod.POST, "request body", String.class);
CompletableFuture<String> to = endpoint.executeRequest(command);
```

### Serializers

To provide a convenient way for working with different data formats, RestEndpoint uses serializers. 
Serializer is basically abstraction for converting data between java data types and data transmission formats (JSON, XML, String, etc). 
For example, if content type of your responses is 'application/json' you need to add GsonSerializer, based on Google Gson library (https://code.google.com/p/google-gson/)

```java
RestEndpoint endpoint = RestEndpoints.create()
   .withBaseUrl("http://base_url_of_rest_service")
   .withSerializer(new GsonSerializer()).build();
```
RestEndpoint reads content type of incoming response and decides which serializer to use (based on response content type). It also adds correct content type to your
outcoming requests depending on serializer used for body converting.

For now, RestEndpoints supports the following list of serializers:

* XML (based on JAXB - JaxbSerializer) 
* JSON (based on Gson - GsonSerializer)
* Plain String (StringSerializer)
* Byte Array (ByteArraySerializer)

By the way, you can implement your own serializer (by implementing appropriate interface) and provide it to RestEndpoint. 
