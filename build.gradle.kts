/**
 * Copyright (C) 2025 Ahmed El-Hajjar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    id("java")
}

group = "net.cdahmedeh.poetwrite"
version = "0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://raw.githubusercontent.com/DFKI-MLT/Maven-Repository/main") }
}

dependencies {
    // Syntaxic Sugar
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

    // Utilities for Common Stuff
    implementation("commons-io:commons-io:2.20.0")
    implementation("com.google.guava:guava:33.4.8-jre")

    // Dependency Injection
    implementation("com.google.dagger:dagger:2.57")
    annotationProcessor("com.google.dagger:dagger-compiler:2.57")
    testAnnotationProcessor("com.google.dagger:dagger-compiler:2.57")

    // Parsing and Serialization
    implementation("org.jsoup:jsoup:1.21.1")

    // Testing Frameworks
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Word Analysis Tools
    implementation("de.dfki.mary:voice-cmu-slt-hsmm:5.2.1") {
        exclude(group = "com.twmacinta", module = "fast-md5")
        exclude(group = "gov.nist.math", module = "Jampack")
    }
}

tasks.test {
    useJUnitPlatform()
}