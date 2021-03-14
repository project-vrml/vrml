# Contributing to VRML Project

您只需要遵循标准的Github开发流程，使用Github Issues提交和跟踪Bug，使用Pull Requests合并您的代码。 

秉着对开源友好，我们对贡献者没有具体的要求，你可以通过中文或者英文提问和提交代码都可以。

但请尽一切努力遵循现有的约定和样​​式，以保持代码的可读性。

## 如何贡献

### 先决条件

* JDK8+
* IntelliJ IDEA

### 1. Check Branch

* branches的名称格式为`feature/xxx`。
* 在一个feature branch已经合并到上游分支之后checkout出一个新的分支，不要在旧的branch中提交。

### 2. Pull Request

#### 2.1 Commit Style

PR标题格式为`<head>: <subject>`，标题应该简单并且显示您的意图。

同时commit遵循[通用规范](https://github.com/feflow/git-commit-style-guide)，按照以下规则：

  >- `feat:`      新功能（feature） 
  >- `fix:`       修复bug 
  >- `docs:`      文档（documentation）
  >- `style:`     格式（不影响代码运行的变动）
  >- `refactor:`  重构（即不是新增功能，也不是修改bug的代码变动）
  >- `perf:`      优化相关，比如提升性能、体验
  >- `test:`      增加测试
  >- `chore:`     构建过程或辅助工具的变动
  >- `revert:`    回滚到上一个版本
  >- `merge:`     代码合并
  >- `sync:`      同步主线或分支的Bug 

commit body:

```text
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

您可以借助IDEA插件来规范git commit style：[Git Commit Template](https://plugins.jetbrains.com/plugin/9861-git-commit-template)

### 3. Review and Merge

当PR处于可合并状态时，其他成员将会进行CodeReview并将其合并到master分支。

## 编码约定

### JavaDoc

 * Public API需要javadoc，例如public类和public方法。
 * 有意义的私有方法也需要javadoc。
 * 设计决策/设计模式值得一提。
 * `package`是public API的一部分，应该包含`package-info.java`。
 * 单元测试不需要包含javadoc（因为它们不引入任何新API且不包含业务逻辑）。
 
### Packages

* Package包名称以单数表示。
* Package包按domain划分（无util或tool包）。
* Package私有类用于隐藏非public API。
* 在一对一依赖的情况下，内部类优先于package私有类。

### Coding Style

一种编码风格建议是[阿里巴巴Java开发手册](https://github.com/alibaba/p3c)
