# KotSuite agent

Usage examples:

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