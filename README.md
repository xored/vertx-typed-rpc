# Vertx EventBus Typed RPC Services
[![Build Status](https://travis-ci.org/xored/vertx-typed-rpc.svg?branch=master)](https://travis-ci.org/xored/vertx-typed-rpc)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xored.vertx/vertx-typed-rpc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xored.vertx/vertx-typed-rpc/)
[![License](https://img.shields.io/badge/license-Apache 2.0 -blue.svg?style=flat)](https://github.com/xored/vertx-typed-rpc/blob/master/LICENSE)

Simple wrapper for Vertx EventBus that allows organize type safety RPC communication.

## Features
* Type safety (it used [Kryo](https://github.com/EsotericSoftware/kryo) library for object serialization)
* Asynchronous RPC calls by using `java.util.concurrent.CompletableFuture`
* Server side exception handling

## Installation

The library JARs are available on the [releases page](https://github.com/xored/vertx-typed-rpc/releases) and at [Maven Central](https://search.maven.org/#browse).
Latest snapshots of the library including snapshot builds of master are in the [Sonatype Repository](https://oss.sonatype.org/content/groups/public/com/xored/vertx/vertx-typed-rpc).

### Integration with Maven

To use the Vert.x Typed RPC Services, add the following dependency to the dependencies section of your `pom.xml`:
```xml
<dependency>
  <groupId>com.xored.vertx</groupId>
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
