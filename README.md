# Renderer
Static HTML code generator using annotation combinations

This project has been started from resolving problem of burden of `simple repetitive tasks`<br>
From Thymeleaf template based view to any other server side templates,<br>
&nbsp;&nbsp;those make the job very burden.<br>
So I created this to resolve / avoid the `simple repetitive tasks`.<br>
This gives us compressed HTML code from simple class with annotated fields.<br>

Renderer requires Spring Boot to run.

# Build
The project includes maven wrapper.

You can build the library in macOS / Linux using:
```bash
./mvnw clean package
```
If you are using Windows:
```bash
mvnw.cmd clean package
```

## First-party supported annotations

#### Input Tag
```java
@Input(id = "username",
        classappend = "form-value btn-blue width100",
        placeholder = "Enter your username")
private String name;
```
|Attribute      |Default Value  |Description
|---            |---            |---
|id             |               |
|name           |Field name     |Prioritize attribute value than field name
|type           |text           |Visit [here](https://developer.mozilla.org/docs/Web/HTML/Element/input).
|classappend    |               |CSS class names that joined with spaces
|placeholder    |               |
|value          |Field value    |Prioritize attribute value than field value
|checked        |false          |Radio and checkbox only
|readonly       |false          |

#### Text Area Tag
```java
@Input(id = "content",
        rows = 5,
        value = "Content: ")
private String message;
```

|Attribute      |Default Value  |Description
|---            |---            |---
|id             |               |
|name           |Field name     |Prioritize attribute value than field name
|placeholder    |               |
|classappend    |               |CSS class names that joined with spaces
|rows           |-1 (No attribute specific)|
|value          |               |Prioritize attribute value than field value

#### Select Tag

```java
@Select(name = "langaugeType",
        options = {
            @Option(text = "English", value = "en"),
            @Option(text = "Korean", value = "ko"),
            @Option(text = "Japanese", value = "ja"),
            @Option(text = "Thai", value = "th")
        })
private String language = "en";
```

**@Select annotation attributes:** 

|Attribute      |Default Value                      |Description
|---            |---                                |---
|id             |                                   |
|name           |Field name                         |Prioritize attribute value than field name
|selected       |Field value matching with options  |Prioritize attribute value than field value
|classappend    |                                   |CSS class names that joined with spaces
|options        |                                   |An array of @Option annotation

**@Option annotation attributes:**

|Attribute      |Default Value|Description
|---            |---          |---
|text           |Required     |Visual text for options
|value          |Required     |Option values
|disabled       |false        |

# TODO

- Currently, renderer requires Renderer annotation-renderer implementation pair to render. I must remove the registry, make renderer detect annotation with specific class type and cache them all. 