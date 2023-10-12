# Usage

> After Run `build` task, the compiled `KotMain.class` will automatically copy to the `libs/cli` directory

## Two sceneries

- Call only one test method
- Call whole test classes

## Command Line Options

e.g.,

```shell
java -cp "" KotMain -c org.kotsuite.FooTest -m testFoo
```

or

```shell
java -cp "" KotMain -c org.kotsuite.FooTest,org.kotsuite.BarTest -m "*"
```

- `-class` (`-c`): the full name of the test class which needs to be called
  - If you want to call multiple test classes, you can use `,` to join them
  - e.g., `-c org.kotsuite.BarTest,org.kotsuite.FooTest`
- `-method` (`-m`): the name of the test method which needs to be called
  - If you want to call only one test method, use the method name for the option value
  - If you want to call multiple test classes, use the `*` for the option value
  - e.g., `-m testFoo`

> You cannot call multiple test methods without using the `*` option value.
> 
> That is to say, if you pass multiple test class names to the `-class` option, you can only use the `*` option value for the `-method` option.
