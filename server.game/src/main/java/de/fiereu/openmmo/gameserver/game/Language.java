package de.fiereu.openmmo.gameserver.game;

public enum Language {
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

    private final String code;
    private final String name;
    private final String nativeName;

    Language(String code, String name, String nativeName) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
    }

}
