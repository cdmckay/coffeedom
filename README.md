CoffeeDOM
=========

CoffeeDOM is a JDOM fork that has been updated to support Java 5 features like generics, covariant return types (for clone(), detach() and getParent()), enums, the for-each loop (for getDescendants()) , and new classes like StringBuilder (to replace the threaded StringBuffer). CoffeeDOM also does away with JDOM's checked exceptions.

JDOM is a Java representation of an XML document. JDOM provides a way to represent that document for easy and efficient reading, manipulation, and writing. It has a straightforward API, is a lightweight and fast, and is optimized for the Java programmer. It's an alternative to DOM and SAX, although it integrates well with both DOM and SAX.

CoffeeDOM strives to be as compatible as possible with JDOM. CoffeeDOM's Java 5 improvements should be natural to a JDOM programmer, with the main benefits being superior compile-time error checking and fewer casts.

To use in Maven:

```
<dependency>
  <groupId>org.cdmckay.coffeedom</groupId>
  <artifactId>coffeedom</artifactId>
  <version>1.0.0</version>
</dependency>
```

[Access the API documentation here.](http://cdmckay.org/coffeedom/apidocs/)

