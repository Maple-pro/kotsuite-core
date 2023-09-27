# KotSuite agent


## Functionality

1. Add test case call to main method body
2. Collect execution result of the last statement of the test case (the target method execution result)

## java agent option

```bash
java \
  -javaagent:./kotsuite-agent-shadow-1.2-all.jar= \
    insertCall=true, \
    collectAssert=true, \
    outputFile=assert.txt, \
    mainClass=KotMain, \
    testClass=com.FooTest, \
    testMethod=testBar, \
    testMethodDesc='()V', \
    targetClass=com.Foo, \
    targetMethod=bar, \
    targetMethodDesc='()Ljava/lang/String;' \ 
  -cp hello.jar \
  KotMain
```

Java Agent Options:

- `insertCall`: 是否在 main 方法中插入测试 case 的调用，true | false
- `collectAssert`: 是否收集测试 case 中最后一条语句的执行结果，true | false
- `outputFile`: 收集target method的返回结果的输出文件路径
- `mainClass`: jar 包中的主类名
- `testClass`: 测试 case 所在的类名
- `testMethod`: 测试 case 的方法名
- `testMethodDesc`: 测试 case 的方法描述符
- `targetClass`: 测试类对应的待测类
- `targetMethod`: 测试用例对应的待测方法（需要收集返回值用于 Assert）
- `targetMethodDesc`: 测试用例对应的待测方法的描述符

Method descriptor:

- `()I`
- `()V`
- `()Ljava/lang/String;`

## How to combine the kotsuite agent with jacoco to dynamically generate test case coverage

```bash
# Generate exec file with kotsuite agent and jacoco agent
java \
-javaagent:"./kotsuite-agent/build/libs/kotsuite-agent-1.2.jar=com.example.myapplication.TempCalleePrintHelloRound0.test_printHello_1" \
-javaagent:"./libs/org.jacoco.agent-0.8.10-runtime.jar=includes=*,destfile=./output/jacoco-coverage.exec,output=file" \
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

4. Some java command

```bash
javac -g Test.java # compile java source code to class file with debug info

javap -verbose Test.class # disassemble class file
```