
# Java and Maven Version

Java: 25LTS

Maven: 3.8.7 (other versions may work)

# Building & Running
The project is a standalone maven project, only dependencies are for tests.

Build and test project by doping the following (from project-root dir):

```bash
mvn install
mvn pacakge
```

If test dependencies are causing issues. Try the following:

```bash
mvn package -DskipTests
```

This should generate a jar under the /target directory with name 'martian-robots-1.0.jar' 

The program can take either:
- No arguments. Reading the input from stdin.
- One argument. Location to a file which it will read for input.

Examples:

```bash
java -cp target/martian-robots-1.0.jar org.example.application.Main
```

```bash
java -cp target/martian-robots-1.0.jar org.example.application.Main src/main/resources/example_input.txt
```

## Tests

Tests can be run using maven.

```bash
mvn test
```

## Provided Jar

Jar provided under target with name martian-robots.jar encase unable to build.

# Design Choices

 - I opted to not use any non-test dependencies. Some companies block certain dependencies 
   and didn't want to cause issues. Normally I would use Apache Commons for String utils, and google
   guava for Preconditions, I built my own under util. 
 - I didn't use an AI, not because I don't use any AI at work. I just don't pay for any personal use AI 
   software. If I did have AI, I would have used it to generate more tests and Javadocs.
 - Only other interesting choice is to use a supplier for the RobotInstructions, compared to 
   fully parsing entire file or standard in. I went with this so the program can handle large streams of inputs. 
   Instead of waiting for complete input before processing.
