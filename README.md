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
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building Flamingo MapReduce Template 0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-clean-plugin:2.4.1:clean (default-clean) @ mapreduce-template ---
[INFO] Deleting C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target
[INFO]
[INFO] --- maven-resources-plugin:2.5:resources (default-resources) @ mapreduce-template ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ mapreduce-template ---
[INFO] Compiling 40 source files to C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\classes
[INFO]
[INFO] --- maven-resources-plugin:2.5:testResources (default-testResources) @ mapreduce-template ---
[debug] execute contextualize
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 0 resource
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ mapreduce-template ---
[INFO] Compiling 1 source file to C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\test-classes
[INFO]
[INFO] --- maven-surefire-plugin:2.10:test (default-test) @ mapreduce-template ---
[INFO] Surefire report directory: C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\surefire-reports

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.openflamingo.mapreduce.etl.groupby.GroupByMapReduceTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.6 sec

Results :

Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

[INFO]
[INFO] --- maven-dependency-plugin:2.1:unpack (unpack) @ mapreduce-template ---
[INFO] Configured Artifact: com.google.guava:guava:r09:jar
[INFO] Configured Artifact: org.apache.mahout.commons:commons-cli:2.0-mahout:jar
[INFO] Configured Artifact: commons-cli:commons-cli:1.2:jar
[INFO] Configured Artifact: commons-lang:commons-lang:2.5:jar
[INFO] Unpacking C:\Users\Cloudine\.m2\repository\com\google\guava\guava\r09\guava-r09.jar to
  C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\classes
   with includes null and excludes:null
[INFO] Unpacking C:\Users\Cloudine\.m2\repository\org\apache\mahout\commons\commons-cli\2.0-mahout\commons-cli-2.0-mahout.jar to
  C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\classes
   with includes null and excludes:null
[INFO] Unpacking C:\Users\Cloudine\.m2\repository\commons-cli\commons-cli\1.2\commons-cli-1.2.jar to
  C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\classes
   with includes null and excludes:null
[INFO] Unpacking C:\Users\Cloudine\.m2\repository\commons-lang\commons-lang\2.5\commons-lang-2.5.jar to
  C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\classes
   with includes null and excludes:null
[INFO]
[INFO] --- maven-jar-plugin:2.3.2:jar (default-jar) @ mapreduce-template ---
[INFO] Building jar: C:\Users\Cloudine\Desktop\mapreduce-template-0.1\target\mapreduce-template-0.1-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 9.110s
[INFO] Finished at: Sun Feb 03 03:41:10 KST 2013
[INFO] Final Memory: 21M/328M
[INFO] ------------------------------------------------------------------------
```

위 과정에는 다음의 작업이 실행됩니다.

* 소스코드 컴파일
* 단위 테스트
* 의존하는 외부 라이브러리(dependency) unpack
* MapReduce Job의 JAR 파일 생성

## MapReduce 프로그래밍

MapReduce Job을 최소의 노력으로 Hadoop에서 실행하려면 다음의 클래스를 작성해야 합니다.

* MapReduce Job의 파라미터 처리, 설정 정보 구성 및 MapReduce Job을 실행하는 Driver
* 처리할 입력 로그 경로의 파일을 로딩하여 Key Value로 출력하는 Mapper
* Mapper의 출력 Key Value로 취합하여 처리하는 Reducer
* 그 외 기타 Combiner, Partitioner, DistributedCache 등등

### Driver 작성하기

Driver는 다음과 같이 Configured, Tool을 상속 및 구현하며 최소 형식은 다음과 같습니다.

```java
public class SampleDriver extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new SampleDriver(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = new Job();

        ...

        job.setJarByClass(SampleDriver.class);

        // Mapper Class
        job.setMapperClass(SampleMapper.class);

        // Output Key/Value
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        // Reducer Task
        job.setNumReduceTasks(0);

        // Run a Hadoop Job
        return job.waitForCompletion(true) ? 0 : 1;
    }

}
```

### 커맨드 라인 파라미터 처리하기

커맨드 라인을 처리하는 작업은 매우 중요하며 본 프로젝트 템플릿에서는 다음의 두 가지 경우를 제공한다.

* 단순 파라미터 처리
  * 장점 : 코드가 단순
  * 단점 : 필수 파라미터 검증을 직접 구성해야 함, 파라미터가 Linux 표준형식이 아님
* Linux 형식의 파라미터 처리
  * 장점 : Linux 형식의 파라미터(긴 이름, 짧은 이름) 지원, 필수 파라미터 검증
  * 단점 : 상대적으로 코드 복잡도 증가

#### 형식1

이 형식은 가장 단순하게 구현할 수 있는 방법으로 parseArguements() 메소드에서 파라미터를 처리합니다. 항상 파라미터명 다음에 파라미터의 값을 지정해야 합니다. 다음 예제는 org.openflamingo.mapreduce.sample.SampleDriver을 참고하시기 바랍니다.

```java
public class SampleDriver extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new SampleDriver(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = new Job();
        parseArguements(args, job);

        job.setJarByClass(SampleDriver.class);

        // Mapper Class
        job.setMapperClass(SampleMapper.class);

        // Output Key/Value
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        // Reducer Task
        job.setNumReduceTasks(0);

        // Run a Hadoop Job
        return job.waitForCompletion(true) ? 0 : 1;
    }

    private void parseArguements(String[] args, Job job) throws IOException {
        for (int i = 0; i < args.length; ++i) {
            if ("-input".equals(args[i])) {
                FileInputFormat.addInputPaths(job, args[++i]);
            } else if ("-output".equals(args[i])) {
                FileOutputFormat.setOutputPath(job, new Path(args[++i]));
            } else if ("-jobName".equals(args[i])) {
                job.getConfiguration().set("mapred.job.name", args[++i]);
            } else if ("-columnToClean".equals(args[i])) {
                job.getConfiguration().set("columnToClean", args[++i]);
            } else if ("-delimiter".equals(args[i])) {
                job.getConfiguration().set("delimiter", args[++i]);
            }
        }
    }
}
```

위 형식을 실행하려면 다음과 같이 커맨드를 실행합니다.

```text
#hadoop jar <JAR_FILE> org.openflamingo.mapreduce.sample.SampleDriver -input <IN> -output <OUT> ..
```

#### 형식2

이 형식은 복잡하지만 도움말과 필수 옵션을 처리할 수 있는 기능을 사용할 수 있습니다. 다음과 같이 작성합니다. 다음 예제는 org.openflamingo.mapreduce.sample.Sample2Driver을 참고 하시기 바랍니다.

```java
public class Sample2Driver extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool {

    /**
     * 필수 옵션
     */
    private final String[][] requiredOptions =
            {
                    {"i", "입력 경로를 지정해 주십시오. 입력 경로가 존재하지 않으면 MapReduce가 동작할 수 없습니다."},
                    {"o", "출력 경로를 지정해 주십시오."},
                    {"d", "컬럼의 구분자를 지정해주십시오. CSV 파일의 컬럼을 처리할 수 없습니다."},
            };

    /**
     * 사용가능한 옵션 목록을 구성한다.
     *
     * @return 옵션 목록
     */
    private static Options getOptions() {
        Options options = new Options();
        options.addOption("i", "input", true, "입력 경로 (필수)");
        options.addOption("o", "output", true, "출력 경로 (필수)");
        options.addOption("d", "delimiter", true, "컬럼 구분자 (필수)");
        options.addOption("od", "delete", false, "출력 경로가 이미 존재하는 경우 삭제");
        return options;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Sample2Driver(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = new Job();

        int result = parseArguements(args, job);
        if (result != 0) {
            return result;
        }

        job.setJarByClass(Sample2Driver.class);

        // Mapper Class
        job.setMapperClass(SampleMapper.class);

        // Output Key/Value
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        // Reducer Task
        job.setNumReduceTasks(0);

        // Run a Hadoop Job
        return job.waitForCompletion(true) ? 0 : 1;
    }

    private int parseArguements(String[] args, Job job) throws Exception {
        ////////////////////////////////////////
        // 옵션 목록을 구성하고 검증한다.
        ////////////////////////////////////////

        Options options = getOptions();
        HelpFormatter formatter = new HelpFormatter();
        if (args.length == 0) {
            formatter.printHelp("org.openflamingo.hadoop jar <JAR> " + getClass().getName(), options, true);
            return -1;
        }

        // 커맨드 라인을 파싱한다.
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);

        // 파라미터를 검증한다.
        for (String[] requiredOption : requiredOptions) {
            if (!cmd.hasOption(requiredOption[0])) {
                formatter.printHelp("org.openflamingo.hadoop jar <JAR> " + getClass().getName(), options, true);
                return -1;
            }
        }

        ////////////////////////////////////////
        // 파라미터를 Hadoop Configuration에 추가한다
        ////////////////////////////////////////

        if (cmd.hasOption("i")) {
            FileInputFormat.addInputPaths(job, cmd.getOptionValue("i"));
        }

        if (cmd.hasOption("o")) {
            FileOutputFormat.setOutputPath(job, new Path(cmd.getOptionValue("o")));
        }

        if (cmd.hasOption("d")) {
            job.getConfiguration().set("delimiter", cmd.getOptionValue("d"));
        }

        // 옵션을 지정한 경우 출력 경로를 삭제한다.
        if (cmd.hasOption("od")) {
            if (HdfsUtils.isExist(cmd.getOptionValue("o"))) {
                HdfsUtils.deleteFromHdfs(cmd.getOptionValue("o"));
            }
        }

        return 0;
    }
}
```

위 형식을 실행하려면 다음과 같이 커맨드를 실행합니다.

```text
#hadoop jar <JAR_FILE> org.openflamingo.mapreduce.sample.Sample2Driver -input <IN> -output <OUT> -delimiter <DELIMITER> ..
```

### 커맨드 라인 파라미터에 JVM Args 넘기기

종종 MapReduce Job의 Configuration에 자동으로 값을 설정하고 싶을 때가 있습니다. 이 경우 형식2의 파라미터 처리를 이용하고 다음과 같이 커맨드 라인을 입력합니다.

```text
#hadoop jar <JAR_FILE> org.openflamingo.mapreduce.sample.Sample2Driver -Dmapred.job.name="Test MapReduce Job" -input <IN> -output <OUT> -delimiter <DELIMITER> ..
```

위 커맨드 라인 예제에서 -Dmapred.job.name="Test MapReduce Job"와 같이 파라미터를 추가하면 Jo의 Configuration에 알아서 추가됩니다. 단, -D 커맨드는 반드시 클래스명 뒤에서만 사용해야 합니다.

### Mapper 작성하기

Mapper를 작성할 때에는 Mapper 클래스를 상속하고 입출력 파라미터의 형식을 다음과 같이 Generic으로 정의합니다. 그리고 난 후 Eclipse 또는 IntelliJ IDEA에서 method override 기능을 이용하여 cleanup, map, setup 메소드를 오버라이드합니다. 이 때 정상적으로 오버라이드가 되었다면 @Override annotation을 추가했을 때 아무런 문제가 없어야 하며 @Override annotation은 반드시 추가하도록 합니다.

```java
public class WordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private String delimiter;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration configuration = context.getConfiguration();
        delimiter = configuration.get("delimiter");
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String row = value.toString();
        String[] columns = row.split(delimiter);
        for (String word : columns) {
            context.write(new Text(word), new IntWritable(1));
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
    }
}
```

### Reducer 작성하기

Reducer를 작성할 때에는 Reducer 클래스를 상속하고 입출력 파라미터의 형식을 다음과 같이 Generic으로 정의합니다. 그리고 난 후 Eclipse 또는 IntelliJ IDEA에서 method override 기능을 이용하여 cleanup, map, setup 메소드를 오버라이드합니다. 이 때 정상적으로 오버라이드가 되었다면 @Override annotation을 추가했을 때 아무런 문제가 없어야 하며 @Override annotation은 반드시 추가하도록 합니다.

```java
public class WordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        Iterator<IntWritable> iterator = values.iterator();
        int sum = 0;
        while (iterator.hasNext()) {
            IntWritable one = iterator.next();
            sum += one.get();
        }
        context.write(key, new IntWritable(sum));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
    }
}
```

### Program Driver에 MapReduce Driver 등록하기

MapReduce Job을 실행하는 Driver를 실행하려면 패키지명을 포함한 Driver 클래스명을 입력해야 합니다.

```text
#hadoop jar <JAR_FILE> org.openflamingo.mapreduce.sample.SampleDriver ...
```

생산성을 위해서 alias를 지정하도록 하여 패키지명을 포함한 Driver 클래스명을 입력의 수고를 덜 수 있습니다. 이를 위해서 com.yourcompany.hadoop.mapreduce.MapReduceDriver 클래스를 다음과 같이 추가하면 됩니다. com.yourcompany.hadoop.mapreduce.MapReduceDriver을 Program Driver라고 합니다.

```java
public class MapReduceDriver {

    public static void main(String argv[]) {
        ProgramDriver programDriver = new ProgramDriver();
        try {
            programDriver.addClass("gropuby", GroupByDriver.class, "Group By MapReduce Job");
            programDriver.driver(argv);

            System.exit(Constants.JOB_SUCCESS);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(Constants.JOB_FAIL);
        }
    }
}
```

이렇게 등록한 MapReduce Job Driver는 다음과 같이 alias로 실행할 수 있습니다.

```text
#hadoop jar <JAR_FILE> groupby  ...
```

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

## 기타 참고 URL

* datafu
  * Github : https://github.com/linkedin/datafu
* PigUnit
  * http://pig.apache.org/docs/r0.8.1/pigunit.html
