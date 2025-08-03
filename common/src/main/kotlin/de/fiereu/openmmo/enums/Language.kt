package de.fiereu.openmmo.common.enums

enum class Language(
  val code: String,
  private val english: String,
  private val display: String
) {
  EN("en", "english", "English"),
  FR("fr", "french", "Français"),
  DE("de", "german", "Deutsch"),
  ES("es", "spanish", "Español"),
  PT("pt", "portuguese", "Português"),
  IT("it", "italian", "Italiano"),
  NL("nl", "dutch", "Nederlands"),
  PL("pl", "polish", "Polski"),
  EL("el", "greek", "Eλληνικá"),
  TR("tr", "turkish", "Türkçe"),
  FIL("fil", "filipino", "Filipino"),
  RU("ru", "russian", "Русский"),
  KO("ko", "korean", "한국어"),
  JA("ja", "japanese", "日本語"),
  CN("cn", "chinese", "简体中文"),
  OTHER("other", "other", "Other"),
  ;

  companion object {
    private val codeMap = entries.associateBy { it.code }

    fun fromCode(code: String): Language {
      return codeMap[code] ?: OTHER
    }
  }
}