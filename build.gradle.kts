@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("io.gitlab.arturbosch.detekt") version ("1.23.0")
}
detekt {

    // The directories where detekt looks for source files.
    // Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
    source.setFrom("src/main/java", "src/main/kotlin")

    // Builds the AST in parallel. Rules are always executed in parallel.
    // Can lead to speedups in larger projects. `false` by default.
    // 并行生成 AST
    parallel = true

    // Define the detekt configuration(s) you want to use.
    // Defaults to the default detekt configuration.
    // 定义要使用的 detekt 配置
    config.setFrom("detekt/detekt-config.yml")

    // Applies the config files on top of detekt's default config file. `false` by default.
    buildUponDefaultConfig = false

    // Turns on all the rules. `false` by default.
    // 打开所有规则
    allRules = false

    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    // 指定基线文件。
    // 所有结果都存储在此文件中，用于后续运行 detekt
    baseline = file("detekt/baseline.xml")

    // Disables all default detekt rulesets and will only run detekt with custom rules
    // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
    // 禁用所有默认的 detekt 规则集，并且只会使用在使用 detektPlugins 配置传入的插件中定义的自定义规则运行 detekt。
    disableDefaultRuleSets = false

    // Adds debug output during task execution. `false` by default.
    // 在任务执行期间添加调试输出
    debug = false

    // If set to `true` the build does not fail when the
    // maxIssues count was reached. Defaults to `false`.
    ignoreFailures = false

    // Android: Don't create tasks for the specified build types (e.g. "release")
    ignoredBuildTypes = listOf("release")

    // Android: Don't create tasks for the specified build flavor (e.g. "production")
    ignoredFlavors = listOf("production")

    // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
    ignoredVariants = listOf("productionRelease")

    // Specify the base path for file paths in the formatted reports.
    // If not set, all file paths reported will be absolute file path.
    basePath = projectDir.absolutePath

    autoCorrect = true

    dependencies {
        // gradle detektGenerateConfig
        // gradle detektBaseline
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
        detektPlugins("ru.kode:detekt-rules-compose:1.2.2")
        detektPlugins("com.twitter.compose.rules:detekt:0.0.26")
    }
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        // observe findings in your browser with structure and code snippets
        html.required.set(true)
        // checkstyle like format mainly for integrations like Jenkins
        xml.required.set(true)
        // similar to the console output, contains issue signature to manually edit baseline files
        txt.required.set(true)
        // standardized SARIF format (https://sarifweb.azurewebsites.net/)
        // to support integrations with GitHub Code Scanning
        sarif.required.set(true)
        // simple Markdown format
        md.required.set(true)
    }
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    this.jvmTarget = "17"
    jdkHome.set(file("detekt/jdkHome"))
}
tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    this.jvmTarget = "17"
    jdkHome.set(file("detekt/jdkHome"))
}
