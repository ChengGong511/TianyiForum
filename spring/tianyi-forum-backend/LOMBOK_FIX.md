# Lombok 配置问题解决方案

## 问题现象

启动时报错：`User 的 set/get 方法未识别到符号`

## 原因

Lombok 注解处理器版本号没有指定，导致编译时无法正确生成 getter/setter 方法。

## 解决方案

### 1. 清除 Maven 缓存并重新编译

**方式一：使用 Maven 命令行**

```bash
cd d:\TianyiForum\spring\tianyi-forum-backend
mvn clean install -DskipTests
```

**方式二：在 IDE 中操作（推荐）**

- VS Code：打开终端，运行 `mvn clean install`
- IntelliJ IDEA：
  1. 右键点击项目 → Maven → Reimport
  2. 然后 Build → Rebuild Project
- Eclipse：
  1. 右键项目 → Maven → Update Project
  2. 然后 Project → Clean

### 2. 确认 IDE Lombok 支持

**VS Code：**

```json
// .vscode/settings.json 中添加
{
  "java.jdt.ls.vmargs": "-XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -Dsun.zip.disableMemoryMapping=true -Xmx2G -Xms100m"
}
```

**IntelliJ IDEA：**

1. File → Settings → Plugins
2. 搜索 "Lombok" → Install
3. Restart IDE

**Eclipse：**

1. 下载 Lombok jar：https://projectlombok.org/download
2. 双击 jar 文件运行安装器
3. 选择 Eclipse.exe 位置
4. 重启 Eclipse

### 3. 配置 pom.xml 已完成

以下配置已更新：

```xml
<!-- Lombok 依赖 -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <optional>true</optional>
</dependency>

<!-- Maven 编译器配置 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>21</source>
        <target>21</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.30</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## 验证

编译后应该看到：

```bash
[INFO] BUILD SUCCESS
```

启动时应该不再有 `User.get***() 未识别到符号` 的错误。

## 如果问题仍然存在

1. **完全清除缓存：**

   ```bash
   mvn clean -DskipTests
   rm -rf ~/.m2/repository  # Linux/Mac
   rmdir /s %USERPROFILE%\.m2\repository  # Windows
   ```

2. **重新下载依赖：**

   ```bash
   mvn dependency:resolve
   ```

3. **检查 User.java 注解：**
   确保 User 类有这些注解：

   ```java
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class User { ... }
   ```

4. **重启 IDE**

## Lombok 常用注解说明

| 注解                  | 作用                                            |
| --------------------- | ----------------------------------------------- |
| `@Data`               | 自动生成 getter/setter/toString/equals/hashCode |
| `@Getter`             | 只生成 getter                                   |
| `@Setter`             | 只生成 setter                                   |
| `@AllArgsConstructor` | 生成全参构造器                                  |
| `@NoArgsConstructor`  | 生成无参构造器                                  |
| `@ToString`           | 生成 toString 方法                              |
| `@EqualsAndHashCode`  | 生成 equals 和 hashCode 方法                    |

## 后续建议

- 为其他实体类也添加 `@Data` 注解
- 在项目中统一使用 Lombok 简化代码
