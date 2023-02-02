# jni-maven-plugin
Plugin for developing native Java applications through Maven

## Profiles
* Currently, a profile setup is required for Windows & Mac operating systems
```xml
    <profiles>
        <profile>
            <id>platform-windows</id>
            <activation>
                <os>
                    <family>windows</family>
                </os>
            </activation>
            <properties>
                <native.output>library.dll</native.output>
            </properties>
        </profile>
        <profile>
            <id>platform-mac</id>
            <activation>
                <os>
                    <family>mac</family>
                </os>
            </activation>
            <properties>
                <native.output>library.dylib</native.output>
            </properties>
        </profile>
    </profiles>
```

## Usage

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>me.mat.jni</groupId>
                <artifactId>jni-maven-plugin</artifactId>
                <version>%VERSION%</version>
                <configuration>
                    <output>target/${native.output}</output>
                </configuration>
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