# Getting Started

### How to use chatgpt-spring-boot-starter

* Clone this project and install to your local maven repository.
* Put the code below into your pom file:

```
<dependency>
  <groupId>com.dqj</groupId>
  <artifactId>chatgpt-spring-boot-stater</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

* Complete your service code.
* Start your application and test.

### What should I do to modify application.yaml

```
gpt:
  config:
    key: xxxx
    model: gpt-4
```

* key: The token which you got from openAI.
* model: Gpt Model. Support values are as below:
    - gpt-4 (default)
    - gpt-3.5-turbo
