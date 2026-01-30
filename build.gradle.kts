plugins {
    id("java")
}

group = "com.ingvarruulib"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    // Source: https://mvnrepository.com/artifact/org.im4java/im4java
    implementation("org.im4java:im4java:1.4.0")
    implementation("net.dv8tion:JDA:6.3.0") { // replace $version with the latest version
        // Optionally disable audio natives to reduce jar size by excluding `opus-java` and `tink`
        // Kotlin DSL:
        exclude(module = "opus-java") // required for encoding audio into opus, not needed if audio is already provided in opus encoding
        exclude(module = "tink") // required for encrypting and decrypting audio
    }
}

tasks.test {
    useJUnitPlatform()
}