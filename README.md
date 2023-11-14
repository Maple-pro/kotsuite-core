# KotSuite

> **Note**
> This is a test suite generation tool for Kotlin Android projects.

## How to run the program?

1. Build `kotsuite-agent` module: it will generate the `kotsuite-agent-shadow-1.x-all.jar` and put it in `libs/cli`
2. Build `kotsuite-kotmain` module: it will generate the `KotMain.class` file and put it in `libs/cli`

## How to transform Kotlin source code to bytecode?

```bash
# 1. Generate .jar file
kotlinc -include-runtime -d output.jar ./Example.kt

# 2. Move and extract .jar file
cp \
./src/test/targets-resources/src/SimpleClass/output.jar \
./src/test/targets-resources/generated/

jar xf output.jar
```
## About gradle

```bash
# Logging options
gradlew build -i
```

## About JaCoCo

```bash
# 1. Generate .jar file for target bytecodes
cd example-projects/MyApplication/sootOutput
jar -cvf ../kotsuite/MyApplication.jar *.class com/

# 2. Run the generated .jar file to validate
cd example-projects/MyApplication/sootOutput
java \
-cp "./example-projects/MyApplication/kotsuite/MyApplication.jar;./libs/kotlin-runtime-1.2.71.jar" \
ExampleTest

java \
-cp "./example-projects/MyApplication/sootOutput:./libs/kotlin-runtime-1.2.71.jar:./libs/kotlin-stdlib-1.8.10.jar" \
KotMain

# 3. Generate .exec file
java \
-javaagent:./libs/cli/org.jacoco.agent-0.8.10-runtime.jar=includes=*,destfile=./example-projects/MyApplication/sootOutput/report/jacoco-MyApplication.exec,output=file \
-cp ./example-projects/MyApplication/kotsuite/MyApplication.jar \
ExampleTest

# 4. Generate coverage report
java \
-jar ./libs/cli/org.jacoco.cli-0.8.10-nodeps.jar report \
./example-projects/MyApplication/sootOutput/report/jacoco-MyApplication.exec \
--classfile=./example-projects/MyApplication/sootOutput \
--sourcefile=./example-projects/MyApplication/app/src/main/java \
--html ./example-projects/MyApplication/sootOutput/report/HTML
```

1. Generate test class file
2. Run cmd command to package a jar file
3. Generate .exec file
4. Run cmd command to generate HTML report

## About java decompiler ([fernflower](https://github.com/fesh0r/fernflower))

```bash
java -jar final/classes final/decompiled
```

## How to use `kotsuite-1.0-SNAPSHOT.jar`

```bash
java \
-cp "./build/libs/kotsuite-1.0-SNAPSHOT.jar;./libs/*" \
org.kotsuite.client.MainKt \
--project ".\example-projects\MyApplication" \
--includes "com.example.myapplication.Example&com.example.myapplication.Callee" \
--strategy "random" \
--libs "./libs/"
```

```bash
java \
-jar "./build/libs/kotsuite-1.0-SNAPSHOT.jar" \
--project ".\example-projects\MyApplication" \
--includes "com.example.myapplication.Example&com.example.myapplication.Callee" \
--strategy "random" \
--libs "./libs/"
```

## Changelog

<!-- Keep a Changelog guide -> https://keepachangelog.com -->

### [1.1.3] - 2023.11.14

1. 新增：输出测试生成的结果统计 
   1. 测试报告：`$MODULE_ROOT/kotsuite/$TIMESTAMP/final/report/report_xxx.json`
   2. 测试数据统计`$MODULE_ROOT/kotsuite/$TIMESTAMP/final/report/statistic_xxx.json`
2. 新增：测试生成时的日志文件
   1. 日志文件：`$MODULE_ROOT/kotsuite.log`
3. 变更：测试生成时生成的中间文件和最终文件的路径
   1. 修改为 `$MODULE_ROOT/kotsuite/$TIMESTAMP/`
4. 变更：最终输出的测试用例的筛选逻辑
   1. 由「根据覆盖率高低筛选」变为「选择覆盖信息不同的测试用例，去除覆盖信息完全相同的测试用例」
5. 修复：classpath 过长导致无法创建 Java 进程的问题

