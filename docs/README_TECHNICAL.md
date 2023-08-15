# convertAmount-Api - Technical detail

> This project was used the [checks style google](https://checkstyle.org/google_style.html)
> and
> [clean code](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
> padronization.
>
> More information and examples about clean code below
>> [medium-clean-architecture-with-java](https://medium.com/slalom-build/clean-architecture-with-java-11-f78bba431041)
>>
>> [github-clean-architecture-example](https://github.com/mattia-battiston/clean-architecture-example)
>>
>> [clean-architecture-java-spring](https://medium.com/swlh/clean-architecture-java-spring-fea51e26e00)
>>
![](https://github.com/mattia-battiston/clean-architecture-example/blob/master/docs/images/clean-architecture-diagram-2.png?raw=true)

![](https://github.com/mattia-battiston/clean-architecture-example/raw/master/docs/images/clean-architecture-diagram-1.png)
>

## Requirements

```
Java 17
Plugin Lombok
```

## Installation OS X & Linux:

**Intellij**

```
https://www.jetbrains.com/pt-br/idea/
```

**Intellij plugins (Settings > Plugins > Marketplace)**

```
Darkyens Time Tracker
File Watchers
GenerateSerialVersionUID
Git Commit Template
Git Tool Box
Live Edit
Lombok
Save Actions
Spring Assistant
```

**Java/Maven Version - SDKMAN:**

| Command                              | description                                                                           |
|:-------------------------------------|:--------------------------------------------------------------------------------------| 
| https://sdkman.io/install            | `This website is used to install sdkman. Sdkman is used to manipulate java versions.` |
| sdk ls java                          | `Used to list all available java versions `                                           |
| sdk i java (need verify the version) | `Used to install java 11`                                                             |
| sdk ls maven                         | `Used to list all available maven versions`                                           |
| sdk i maven 3.6.3                    | `Maven is a software project management and comprehension tool, like Npm from node. ` | 

**Lombok plugin:**

```
Intellij: https://projectlombok.org/setup/intellij
Eclipse : https://projectlombok.org/setup/eclipse
```

## Configuration for development

**Environment variables:**

| Database                   | value                           | description                                                           | required |
|:---------------------------|:--------------------------------|:----------------------------------------------------------------------|:---------|
| `ENVIRONMENT`              | `develop`                       | Development environment                                               | no       |
| `DEFAULT_PORT`             | `8080`                          | Port that is used to run the project                                  | no       |
| `JWT_KEY`                  | `password`                      | The key pass is passed using base64 but the token is used with base64 | no       |
| `API_CORS_ALLOWED_ORIGINS` | `*`                             | CORS allowed origin                                                   | no       |
| `API_CORS_ALLOWED_HEADERS` | `*`                             | CORS allowed header                                                   | no       |
| `API_CORS_ALLOWED_METHODS` | `GET, PATCH, POST, PUT, DELETE` | CORS allowed method                                                   | no       |
| `API_CORS_PATH`            | `/api/**`                       | CORS allowed path                                                     | no       |

**Intellij Environment variables:**

```
Not is necessary
```

**Install dependencies:**

```
mvn clean install
```