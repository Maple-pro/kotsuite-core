# KotSuite-Core

## How to transform Kotlin source code to bytecode?

```bash
# 1. Generate .jar file
kotlinc -include-runtime -d output.jar ./Example.kt

# 2. Move and extract .jar file
cp ./src/test/targets-resources-src/SimpleClass/output.jar ./src/test/targets-resources/generated/
jar xf output.jar
```