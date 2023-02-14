[![Release](https://jitpack.io/v/Mat1337/jni-maven-plugin.svg)](https://jitpack.io/Mat1337/jni-maven-plugin)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LICENSE)

# jni-maven-plugin
*Plugin for developing native Java applications through Maven*

## Usage

Place all the native source code in ```src/main/c++```

```xml
<pluginRepositories>
    <pluginRepository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </pluginRepository>
</pluginRepositories>


<build>
    <plugins>
        <plugin>
            <groupId>com.github.Mat1337</groupId>
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
