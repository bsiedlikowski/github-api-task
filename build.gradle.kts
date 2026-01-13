plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "pl.bsiedlikowski"
version = "0.0.1-SNAPSHOT"
description = "GitHub API application"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-restclient")

	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation ("org.springframework.boot:spring-boot-webtestclient")
	testImplementation("org.wiremock:wiremock-standalone:3.9.1")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
