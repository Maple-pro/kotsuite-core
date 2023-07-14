# KotSuite

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
-javaagent:./libs/jacocoagent.jar=includes=*,destfile=./example-projects/MyApplication/sootOutput/report/jacoco-MyApplication.exec,output=file \
-cp ./example-projects/MyApplication/kotsuite/MyApplication.jar \
ExampleTest

# 4. Generate coverage report
java \
-jar ./libs/jacococli.jar report \
./example-projects/MyApplication/sootOutput/report/jacoco-MyApplication.exec \
--classfile=./example-projects/MyApplication/sootOutput \
--sourcefile=./example-projects/MyApplication/app/src/main/java \
--html ./example-projects/MyApplication/sootOutput/report/HTML
```

1. Generate test class file
2. Run cmd command to package a jar file
3. Generate .exec file
4. Run cmd command to generate HTML report

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
