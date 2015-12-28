# Vertx EventBus Typed RPC Services
Simple wrapper for Vertx EventBus that allows organize type safety RPC like communication.

## Features
* Type safety (it used [Kryo](https://github.com/EsotericSoftware/kryo) library for object serialization)
* Asynchronous RPC calls by using `java.util.concurrent.CompletableFuture`
* Server side exception handling

## Installation

The library JARs are available on the [releases page](https://github.com/xored/vertx-typed-rpc/releases) and at [Maven Central](https://search.maven.org/#browse).

### Integration with Maven

To use the Vert.x Typed RPC Services, add the following dependency to the dependencies section of your `pom.xml`:
```xml
<dependency>
  <groupId>com.xored.vertx.typed.rpc</groupId>
  <artifactId>vertx-typed-rpc</artifactId>
  <version>1.0</version>
</dependency>
```

## Quickstart

The example below describes a simple scenario of usage EventBus services:
* Specify RPC service interface `PersonService.java` that should be shared for client and server side: 

```java
package example;

import com.xored.vertx.typed.rpc.EventBusService;

import java.util.concurrent.CompletableFuture;

@EventBusService("person-service")
public interface PersonService {
    
    CompletableFuture<Person> getPersonByName(String name);
}
```

* Add implementation class `PersonServiceImpl.java` for service

```java
package example;

import java.util.concurrent.CompletableFuture;

public class PersonServiceImpl implements PersonService {

    @Override
    public CompletableFuture<Person> getPersonByName(String name) {
        return CompletableFuture.completedFuture(new Person());
    }
}
```
and register service with the following code:

```java
EventBusServiceFactory.registerServer(eventBus, new PersonServiceImpl())
```

* On client side you need create proxy for service:

```java
PersonService client = EventBusServiceFactory.createClient(eventBus, PersonService.class);
client.getPersonByName("test").thenAccept(person -> {
    // person logic here
});
```
