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

## [1.2.0] - 2023-11-24

### Added
- 已能正常使用，用于结项

## 1.1.3 - 2023-11-14

### Added
- - 测试报告：`$MODULE_ROOT/kotsuite/$TIMESTAMP/final/report/report_xxx.json`
  - 测试数据统计`$MODULE_ROOT/kotsuite/$TIMESTAMP/final/report/statistic_xxx.json`
- - 日志文件：`$MODULE_ROOT/kotsuite.log`

### Changed
- - 修改为 `$MODULE_ROOT/kotsuite/$TIMESTAMP/`
- - 由「根据覆盖率高低筛选」变为「选择覆盖信息不同的测试用例，去除覆盖信息完全相同的测试用例」

### Fixed
- classpath 过长导致无法创建 Java 进程的问题
