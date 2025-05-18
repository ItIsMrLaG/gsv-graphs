import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
    id("com.diffplug.spotless") version "6.13.0"
}

group = "org.example"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.apache.hadoop:hadoop-client:2.7.7")
    implementation("org.apache.giraph:giraph-core:1.3.0-SNAPSHOT") {
        isChanging = true
    }

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        target("src/**/*.java") // Указываем, какие файлы проверять
        googleJavaFormat("1.7") // Или другую версию / формат по вкусу
    }
}

val GIRAPH_DIR = File("src/main/resources/giraph/")
val GIRAPH_RUNCFG_DIR = File("$GIRAPH_DIR/runcfg/")
val GIRAPH_OUTPUT_DIR = File("$GIRAPH_DIR/output/")

/* ============== EXAMPLE ==============   */

tasks.register<JavaExec>("runExample") {
    dependsOn(exampleJar)
    classpath = files(exampleJar.get().archiveFile) +
               configurations.runtimeClasspath.get()

    group = "Execution"
    mainClass.set("org.myexample.Fun")
    systemProperty("giraph.output.dir", "$GIRAPH_OUTPUT_DIR/example/${getTimestamp()}")
}

/* ============== MS-BFS EXAMPLE ==============   */

val MSBFS_NAME = "msbfs"

tasks.register<JavaExec>("runMSBfsExample") {
    dependsOn(exampleJar)
    classpath = files(exampleJar.get().archiveFile) +
               configurations.runtimeClasspath.get()
    mainClass.set("org.algos.$MSBFS_NAME.runexample.Fun")

    group = "Execution"
    systemProperty("giraph.output.dir", "$GIRAPH_OUTPUT_DIR/$MSBFS_NAME/${getTimestamp()}")
    systemProperty("giraph.input.graph", "$GIRAPH_RUNCFG_DIR/$MSBFS_NAME/example_graph.txt")
    systemProperty("giraph.input.sourceIds", "$GIRAPH_RUNCFG_DIR/$MSBFS_NAME/source_ids.txt")
}

/* ============== BORUVKA EXAMPLE ==============   */

val BORUVKA_NAME = "boruvka"

tasks.register<JavaExec>("runBoruvkaExample") {
    dependsOn(exampleJar)
    classpath = files(exampleJar.get().archiveFile) +
               configurations.runtimeClasspath.get()
    mainClass.set("org.algos.$BORUVKA_NAME.runexample.Fun")

    group = "Execution"
    systemProperty("giraph.output.dir", "$GIRAPH_OUTPUT_DIR/$BORUVKA_NAME/${getTimestamp()}")
    systemProperty("giraph.input.graph", "$GIRAPH_RUNCFG_DIR/$BORUVKA_NAME/example_graph_triangle.txt")

//    jvmArgs = listOf("-Xmx512m")
}

/* ============== TOOLS ==============   */

val exampleJar by tasks.registering(Jar::class) {
    archiveClassifier.set("example")
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

fun getTimestamp() = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date())