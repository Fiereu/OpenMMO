plugins {
    java
    id("org.jooq.jooq-codegen-gradle") version "3.19.10"
}

group = "de.fiereu.openmmo"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    jooqCodegen("org.postgresql:postgresql:42.7.3")
    jooqCodegen("org.jooq:jooq-meta-extensions:3.19.13")
    jooqCodegen("org.jooq:jooq-postgres-extensions:3.19.13")

    implementation("org.jooq:jooq:3.19.13")
    implementation("org.jooq:jooq-codegen:3.19.13")
    implementation("org.jooq:jooq-meta:3.19.13")
    implementation("org.jooq:jooq-postgres-extensions:3.19.13")
}

tasks.build {
    mustRunAfter("jooqCodegen")
}

jooq {
    configuration {
        val host = "127.0.0.1"
        val port = 5432
        val db_user = "openmmo"
        val db_password = "openmmo"
        val database = "openmmo_db"
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://$host:$port/$database"
            user = db_user
            password = db_password
        }
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                includes = ".*"
                excludes = """
                               UNUSED_TABLE                # This table (unqualified name) should not be generated
                             | PREFIX_.*                   # Objects with a given prefix should not be generated
                             | SECRET_SCHEMA\.SECRET_TABLE # This table (qualified name) should not be generated
                             | SECRET_ROUTINE              # This routine (unqualified name) ...
                            """
                inputSchema = "public"
            }
            generate {}
            target {
                packageName = "de.fiereu.openmmo.db.jooq"
                directory = "src/main/java"
            }
        }

    }
}

// old DDL config
/*
generator {
    name = "org.jooq.codegen.DefaultGenerator"
    database {
        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
        inputSchema = ""
        properties {
            property {
                key = "scripts"
                value = "schemas/*.sql"
            }
            property {
                key = "sort"
                value = "schematic"
            }
            property {
                key = "unqualifiedSchema"
                value = "none"
            }
            property {
                key = "defaultNameCase"
                value = "lower"
            }
        }
    }
    generate {}
    target {
        packageName = "de.fiereu.openmmo.db.jooq"
        directory = "src/main/java"
    }
}
*/