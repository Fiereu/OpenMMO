package de.fiereu.openmmo.gameserver.game;

import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;

public enum SkinType {
  FOREHEAD(-1, true, false),
  HAT(3, true, false),
  HAIR(0, false, false),
  EYES(1, false, false),
  FACIAL_HAIR(2, true, true),
  BACK(-1, true, false),
  TOP(-1, false, false),
  GLOVES(-1, true, false),
  FOOTWEAR(-1, false, false),
  LEGGINGS(-1, false, false),
  FISHING_ROD(-1, false, false),
  BIKE(-1, false, false),
  ;
  public static final SkinType[] wearables = {FOREHEAD, HAT, HAIR, EYES, FACIAL_HAIR, BACK, TOP, GLOVES, FOOTWEAR, LEGGINGS};

  private final int id;
  private final boolean unk1;
  private final boolean unk2;

  SkinType(int id, boolean unk1, boolean unk2) {
    this.id = id;
    this.unk1 = unk1;
    this.unk2 = unk2;
  }

  public short getSkin(CharacterRecord character) {
    return switch (this) {
      case FOREHEAD -> character.getForehead();
      case HAT -> character.getHat();
      case HAIR -> character.getHair();
      case EYES -> character.getEyes();
      case FACIAL_HAIR -> character.getFacialHair();
      case BACK -> character.getBack();
      case TOP -> character.getTop();
      case GLOVES -> character.getGloves();
      case FOOTWEAR -> character.getFootwear();
      case LEGGINGS -> character.getLeggings();
      case FISHING_ROD -> character.getFishingRod();
      case BIKE -> character.getBike();
    };
  }

  public byte getColor(CharacterRecord character) {
    return (byte) switch (this) {
      case FOREHEAD -> character.getForeheadColor();
      case HAT -> character.getHatColor();
      case HAIR -> character.getHairColor();
      case EYES -> character.getEyesColor();
      case FACIAL_HAIR -> character.getFacialHairColor();
      case BACK -> character.getBackColor();
      case TOP -> character.getTopColor();
      case GLOVES -> character.getGlovesColor();
      case FOOTWEAR -> character.getFootwearColor();
      case LEGGINGS -> character.getLeggingsColor();
      case FISHING_ROD, BIKE -> 0;
    };
  }

    public void setSkin(CharacterRecord character, short skin) {
      switch (this) {
        case FOREHEAD -> character.setForehead(skin);
        case HAT -> character.setHat(skin);
        case HAIR -> character.setHair(skin);
        case EYES -> character.setEyes(skin);
        case FACIAL_HAIR -> character.setFacialHair(skin);
        case BACK -> character.setBack(skin);
        case TOP -> character.setTop(skin);
        case GLOVES -> character.setGloves(skin);
        case FOOTWEAR -> character.setFootwear(skin);
        case LEGGINGS -> character.setLeggings(skin);
        case FISHING_ROD -> character.setFishingRod(skin);
        case BIKE -> character.setBike(skin);
      }
    }

    public void setColor(CharacterRecord character, byte color) {
      short colorValue = (short) (color & 0xFF);
      switch (this) {
        case FOREHEAD -> character.setForeheadColor(colorValue);
        case HAT -> character.setHatColor(colorValue);
        case HAIR -> character.setHairColor(colorValue);
        case EYES -> character.setEyesColor(colorValue);
        case FACIAL_HAIR -> character.setFacialHairColor(colorValue);
        case BACK -> character.setBackColor(colorValue);
        case TOP -> character.setTopColor(colorValue);
        case GLOVES -> character.setGlovesColor(colorValue);
        case FOOTWEAR -> character.setFootwearColor(colorValue);
        case LEGGINGS -> character.setLeggingsColor(colorValue);
      }
    }
}
