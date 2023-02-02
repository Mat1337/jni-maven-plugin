[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LICENSE)

# jni-maven-plugin
Plugin for developing native Java applications through Maven

## Usage

```xml
<build>
    <plugins>
        <plugin>
            <groupId>me.mat.jni</groupId>
            <artifactId>jni-maven-plugin</artifactId>
            <version>1.0</version>
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
                        <goal>generate</goal>
                        <goal>compile</goal>
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
