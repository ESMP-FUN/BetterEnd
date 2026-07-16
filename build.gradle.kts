plugins {
    kotlin("jvm") version "2.3.21"
    id("com.gradleup.shadow") version "9.0.0"
}

group = "com.esmpfun"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://jitpack.io")
}

dependencies {
    // Paper API — targets ALL of MC 26.x with one jar. Built against the OLDEST
    // supported line (26.1.2 stable) so 26.2-only API can't slip in; api-version
    // '26.1' in paper-plugin.yml is a minimum, so the jar loads on 26.1.x and
    // 26.2+ alike. Everything BetterEnd touches (dialogs, structure API,
    // PlayerItemFrameChangeEvent, events) exists in 26.1.2.
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.72-stable")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // Persistence: SQLite (default, zero-setup) + MySQL (opt-in), pooled by HikariCP.
    // SQLite JDBC must NOT be relocated (JNI native lib loading breaks if it is).
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.mysql:mysql-connector-j:9.1.0")

    // PluginPulse — update checking + verified install staging.
    implementation("com.github.darkstarworks.PluginPulse:pluginpulse-core:v0.8.0")

    // Anonymous usage metrics (relocated below — bStats requires it)
    implementation("org.bstats:bstats-bukkit:3.2.1")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.papermc.paper:paper-api:26.1.2.build.72-stable")
}

// MC 26.x runs on JDK 25; Paper 26.1.2 API classes are compiled to Java 25
// (class file v69), so the build MUST run on a JDK 25 toolchain to read them.
kotlin {
    jvmToolchain(25)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        // Kotlin stdlib / kotlinx-coroutines NOT relocated — Bukkit must find
        // kotlin.* at runtime. HikariCP relocated to avoid clashing with other
        // plugins' shaded copies.
        relocate("com.zaxxer.hikari", "com.esmpfun.betterend.hikari")
        relocate("io.github.darkstarworks.pluginpulse", "com.esmpfun.betterend.pluginpulse")
        // bStats mandates relocation so multiple plugins can shade different versions
        relocate("org.bstats", "com.esmpfun.betterend.bstats")
        // Do NOT relocate org.sqlite (JNI native loading) or the MySQL driver
        // (driverClassName references its real package name at runtime).
        mergeServiceFiles()

        // Strip signature files from the (signed) MySQL connector jar — shading a
        // signed jar without this throws "Invalid signature file digest" at load.
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")

        // Trim SQLite natives to the platforms a MC server actually runs on.
        exclude("org/sqlite/native/FreeBSD/**")
        exclude("org/sqlite/native/Linux-Android/**")
        exclude("org/sqlite/native/Linux-Musl/**")
        exclude("org/sqlite/native/Mac/**")
    }
    jar {
        enabled = false
    }
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
