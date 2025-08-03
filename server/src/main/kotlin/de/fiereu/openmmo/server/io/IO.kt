package de.fiereu.openmmo.server.io

import java.io.FileInputStream
import java.io.InputStream

fun resource(path: String): InputStream
    = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
        ?: throw IllegalArgumentException("Resource not found: $path")

fun file(path: String): InputStream
    = FileInputStream(path)