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

## [1.2.1] - 2023-11-29

### Changed
- 优化：将成功和失败的测试用例分开输出
- 优化：将 dependency classpath 不再放入 soot 的 process dir
- 优化：过滤策略，但还未完成「待完成」
- 修改：测试用例数量从 3 变为 2
- 待完成：`when().thenReturn()` 方法的支持

## 1.2.0 - 2023-11-24

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
