# KotSuite agent

## Functionality

1. Add test case call to main method body
2. Collect execution result of the last statement of the test case (the target method execution result)

## java agent option

e.g.,

```bash
java -javaagent:./kotsuite-agent-shadow-1.2-all.jar=mainclass=KotMain,testclass=Test,testmethod='foo' -jar hello.jar KotMain 
```

- `mainclass`: jar 包中的主类名
- `testclass`: 测试 case 所在的类名
- `testmethod`: 测试 case 的方法名

## Usage examples

```bash
cd kotsuite-agent/build/libs/

java \
-javaagent:kotsuite-agent-1.2.jar=ExampleTest.testPrint \
-cp ".:/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs/*" \
KotMain
```

```bash
java \
-javaagent:./kotsuite-agent/build/libs/kotsuite-agent-1.2.jar=com.example.myapplication.TempCalleePrintHelloRound0.test_printHello_1 \
-cp "example-projects/MyApplication/app/sootOutput/:libs/*" \
KotMain
```
## How to combine the kotsuite agent with jacoco to dynamically generate test case coverage

```bash
# Generate exec file with kotsuite agent and jacoco agent
java \
-javaagent:"./kotsuite-agent/build/libs/kotsuite-agent-1.2.jar=com.example.myapplication.TempCalleePrintHelloRound0.test_printHello_1" \
-javaagent:"./libs/org.jacoco.agent-0.8.10-rumtime.jar=includes=*,destfile=./output/jacoco-coverage.exec,output=file" \
-cp "example-projects/MyApplication/app/sootOutput/:libs/*" \
KotMain

# Analyze the exec file to get coverage information
java \
-jar libs/org.jacoco.cli-0.8.10-nodeps.jar report output/jacoco-coverage.exec \
--classfile="./example-projects/MyApplication/app/sootOutput/" \
--sourcefile="./example-projects/MyApplication/app/src/main/java" \
--html output/report
```

## Some useful commands

1. Compile java source code to class files

```bash
javac -d path/to/directory file.java
```

2. Zip .jar file

```bash
jar -cvfm path/to/file.jar MANIFEST.MF *
```

3. Run .jar with java agent

```bash
java -javaagent:./kotsuite-agent-shadow-1.2-all.jar=mainclass=KotMain,testclass=Test,testmethod='*' -jar hello.jar KotMain
```
