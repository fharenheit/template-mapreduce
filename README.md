Flamingo Community의 MapReduce Job 템플릿 프로젝트
====================

## 환경 요구사항

* JDK 1.6 이상
  * JDK 1.6 Download : http://www.oracle.com/technetwork/java/javase/downloads/jdk6downloads-1902814.html
* Apache Maven 3.x 이상
  * Windows : http://ftp.daum.net/apache/maven/maven-3/3.0.4/binaries/apache-maven-3.0.4-bin.zip
  * Linux, MacOSX : http://ftp.daum.net/apache/maven/maven-3/3.0.4/binaries/apache-maven-3.0.4-bin.tar.gz
* Apache Ant 1.8 이상
  * Windows : http://ftp.daum.net/apache/ant/binaries/apache-ant-1.8.4-bin.zip
  * Linux, MacOSX : http://ftp.daum.net/apache/ant/binaries/apache-ant-1.8.4-bin.tar.gz
* Eclipse (M2Eclipse 플러그인 설치 필요) 또는 IntelliJ IDEA
  * M2Eclipse Update Site : http://download.eclipse.org/technology/m2e/releases

## 소스코드 빌드 및 MapReduce Job JAR 파일 패키징

다음의 커맨드를 실행하면 MapReduce Job JAR 파일을 패키징할 수 있습니다.

```text
#mvn package
```

위 과정에는 다음의 작업이 실행됩니다.

* 소스코드 컴파일
* 단위 테스트
* 의존하는 외부 라이브러리(dependency) unpack
* MapReduce Job의 JAR 파일 생성

## JavaDoc 생성

JavaDoc 생성은 build.xml 파일에 정의되어 있으며 기본으로 다음과 같이 동작합니다.

* target/javadoc 디렉토리 삭제
* UML을 포함한 HTML 형식의 JavaDoc API 생성(JDK 1.6, JDK 1.7이 서로 다름)
* Microsoft Word에서 편집할 수 있는 RTF 형식의 JavaDoc API 생성

### JDK 1.6

JDK 1.6을 사용하는 환경에서(기본 권장) 다음의 커맨드로 UML을 포함하는 HTML 형식의 JavaDoc API를 생성할 수 있습니다.

```text
#ant javadoc-1.6-html
```

### JDK 1.7

JDK 1.7을 사용하는 환경에서(기본 권장) 다음의 커맨드로 UML을 포함하는 HTML 형식의 JavaDoc API를 생성할 수 있습니다.

```text
#ant javadoc-1.7-html
```

### HTML 형식의 JavaDoc API의 압축 파일 생성

HTML 형식의 JavaDoc API를 생성한 후 ZIP으로 압축하려면 다음의 커맨드를 실행합니다.

```text
#ant javadoc-zip
```

### RTF 형식의 JavaDoc API 생성

HTML 형식의 JavaDoc API를 생성한 후 ZIP으로 압축하려면 다음의 커맨드를 실행합니다.

```text
#ant javadoc-rtf
```

### 모든 것을 한번에 생성하기

UML을 포함한 JavaDoc API와 RTF JavaDoc API를 한번에 생성하려면 다음의 커맨드를 실행합니다. 다만 이 커맨드는 JDK 1.6에서 실행해야 합니다.

```text
#ant javadoc
```

인자없이 Ant를 호출하면 위와 동일한 커맨드가 실행됩니다.

```text
#ant
```

JDK 1.7을 사용하는 환겨에서는 다음과 같이 입력합니다.

```text
#ant javadoc-1.7
```

## MapReduce Job JAR 파일에 다른 라이브러리를 같이 패키징하는 방법

Apache Maven의 POM 파일은 pom.xml 파일에 다음과 같이 maven-dependency-plugin 플러그인이 정의되어 있습니다. 이 플러그인에 MapReduce Job JAR 파일 내에 외부 라이브러리를 같이 패키징하고자 하는 Maven Artifact를 artifactItem 항목으로 추가합니다.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <id>unpack</id>
            <phase>test</phase>
            <goals>
                <goal>unpack</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.outputDirectory}</outputDirectory>
                <artifactItems>
                    <artifactItem>
                        <groupId>com.google.guava</groupId>
                        <artifactId>guava</artifactId>
                        <version>${guava.version}</version>
                    </artifactItem>
                    <artifactItem>
                        <groupId>org.apache.mahout.commons</groupId>
                        <artifactId>commons-cli</artifactId>
                        <version>${commons.cli2.version}</version>
                    </artifactItem>
                    <artifactItem>
                        <groupId>commons-cli</groupId>
                        <artifactId>commons-cli</artifactId>
                        <version>${commons.cli.version}</version>
                    </artifactItem>
                    <artifactItem>
                        <groupId>commons-lang</groupId>
                        <artifactId>commons-lang</artifactId>
                        <version>${commons.lang.version}</version>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
```
