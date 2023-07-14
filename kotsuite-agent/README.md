# KotSuite agent

## Usage examples:

```bash
cd kotsuite-agent/build/libs/

java \
-javaagent:kotsuite-agent-1.0-SNAPSHOT.jar=ExampleTest.testPrint \
-cp ".:/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs/*" \
KotMain
```

```bash
java \
-javaagent:./kotsuite-agent/build/libs/kotsuite-agent-1.0-SNAPSHOT.jar=com.example.myapplication.TempCalleePrintHelloRound0.test_printHello_1 \
-cp "example-projects/MyApplication/app/sootOutput/:libs/*" \
KotMain
```
## How to combine the kotsuite agent with jacoco to dynamically generate test case coverage

```bash
# Generate exec file with kotsuite agent and jacoco agent
java \
-javaagent:"./kotsuite-agent/build/libs/kotsuite-agent-1.0-SNAPSHOT.jar=com.example.myapplication.TempCalleePrintHelloRound0.test_printHello_1" \
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
