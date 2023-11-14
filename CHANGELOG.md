# Changelog
My awesome project that provides a lot of useful features, like:

- Feature 1
- Feature 2
- and Feature 3

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## 1.1.3 - 2023-11-14

### Added
- 输出测试生成的结果统计
  - 测试报告：`$MODULE_ROOT/kotsuite/$TIMESTAMP/final/report/report_xxx.json`
  - 测试数据统计`$MODULE_ROOT/kotsuite/$TIMESTAMP/final/report/statistic_xxx.json`
- 测试生成时的日志文件
  - 日志文件：`$MODULE_ROOT/kotsuite.log`

### Changed
- 测试生成时生成的中间文件和最终文件的路径
  - 修改为 `$MODULE_ROOT/kotsuite/$TIMESTAMP/`
- 最终输出的测试用例的筛选逻辑 
  - 由「根据覆盖率高低筛选」变为「选择覆盖信息不同的测试用例，去除覆盖信息完全相同的测试用例」

### Fixed
- classpath 过长导致无法创建 Java 进程的问题
