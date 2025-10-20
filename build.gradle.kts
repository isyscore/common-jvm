/**
 * to relaase a new library, just do this:
 * $ gradle clean publishMavenKotlinPublicationToPreDeployRepository
 * $ gradle publish jreleaserDeploy
 */
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val signingPassword: String by project
val centralUsername: String by project
val centralPassword: String by project

plugins {
    java
    kotlin("jvm") version "2.1.20"
    `maven-publish`
    signing
    id("org.jreleaser") version "1.18.0"
}

group = "com.github.isyscore"
version = "3.0.0.6"

repositories {
    mavenCentral()
}

dependencies {
    api("org.burningwave:core:12.64.3")
    api("com.google.guava:guava:33.1.0-jre")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.squareup.okio:okio-jvm:3.7.0")
    api("commons-io:commons-io:2.16.0")
    api("org.apache.commons:commons-compress:1.26.1")
    api("org.apache.commons:commons-lang3:3.13.0")
    api("org.jsoup:jsoup:1.16.1")
    api("com.google.code.gson:gson:2.10.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    api("org.ktorm:ktorm-core:3.6.0")
    api("org.ktorm:ktorm-jackson:3.6.0")
    api("com.alibaba:druid:1.2.18")
    api("org.ktorm:ktorm-support-mysql:3.6.0")
    api("org.ktorm:ktorm-support-postgresql:3.6.0")
    api("org.ktorm:ktorm-support-sqlite:3.6.0")
    api("org.ktorm:ktorm-support-oracle:3.6.0")
    api("org.ktorm:ktorm-support-sqlserver:3.6.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    api("com.squareup.okhttp3:okhttp-sse:4.12.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("mysql:mysql-connector-java:8.0.33")
    testImplementation("cn.com.kingbase:kingbase8:8.6.0")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["java"])

            pom {
                name.set("common-jvm")
                description.set("iSysCore Common Kotlin Library")
                url.set("https://github.com/isyscore/common-jvm")
                packaging = "jar"

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/isyscore/common-jvm/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("isyscore")
                        name.set("isyscore")
                        email.set("hexj@isyscore.com")
                    }
                }
                scm {
                    connection.set("https://github.com/isyscore/common-jvm")
                    developerConnection.set("https://github.com/isyscore/common-jvm")
                    url.set("https://github.com/isyscore/common-jvm")
                }
            }
        }
    }

    repositories {
        maven {
            name = "LocalMavenWithChecksums"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
        maven {
            name = "PreDeploy"
            url = uri(layout.buildDirectory.dir("pre-deploy"))
        }
    }
}

tasks.withType<Jar> {
    doLast {
        ant.withGroovyBuilder {
            "checksum"("algorithm" to "md5", "file" to archiveFile.get())
            "checksum"("algorithm" to "sha1", "file" to archiveFile.get())
        }
    }
}

jreleaser {
    project {
        copyright.set("isyscore.com")
        description.set("iSysCore Common Kotlin Library")
    }
    signing {
        setActive("ALWAYS")
        armored = true
        setMode("FILE")
        publicKey = "public.key"
        secretKey = "private.key"
        passphrase = signingPassword
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    setActive("ALWAYS")
                    url = "https://central.sonatype.com/api/v1/publisher"
                    username = centralUsername
                    password = centralPassword
                    stagingRepository("build/pre-deploy")
                }
            }
        }
    }
}