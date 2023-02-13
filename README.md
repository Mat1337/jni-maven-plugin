[![Release](https://jitpack.io/v/Mat1337/jni-maven-plugin.svg)](https://jitpack.io/Mat1337/jni-maven-plugin)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LICENSE)

# jni-maven-plugin
*Plugin for developing native Java applications through Maven*

## Usage

Place all the native source code in ```src/main/c++```

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<build>
    <plugins>
        <plugin>
            <groupId>me.mat.jni</groupId>
            <artifactId>jni-maven-plugin</artifactId>
            <version>%VERSION%</version>
            <executions>
                <execution>
                    <id>clean</id>
                    <phase>clean</phase>
                    <goals>
                        <goal>clean</goal>
                    </goals>
                </execution>
                <execution>
                    <id>build</id>
                    <phase>process-classes</phase>
                    <goals>
                        <goal>generate-headers</goal>
                        <goal>build</goal>
                    </goals>
                </execution>
                <execution>
                    <id>package</id>
                    <phase>package</phase>
                    <goals>
                        <goal>package</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Linking configuration example

```xml
<configuration>
    <includes>
        <include>C:/path/to/include/directory</include>
    </includes>
    <linker>
        <directories>
            <directory>C:/path/to/link/directory</directory>
        </directories>
        <libraries>
            <library>library_name</library>
        </libraries>
    </linker>
</configuration>
```

## Extra compilation flags
```xml
<configuration>
    <flags>
        <flag>-fPIC</flag>
    </flags>
</configuration>
```
