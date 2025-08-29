package de.fiereu.openmmo.common.enums

enum class Platform {
    WINDOWS,
    LINUX,
    MACOS,
    ANDROID,
    IOS,
    UNKNOWN;

    companion object {
        fun from(value: Int): Platform {
            return when (value) {
                0x00 -> WINDOWS
                0x01 -> LINUX
                0x02 -> MACOS
                0x03 -> IOS
                0x04 -> ANDROID
                else -> UNKNOWN
            }
        }
    }
}