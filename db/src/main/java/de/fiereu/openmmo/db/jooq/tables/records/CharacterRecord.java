/*
 * This file is generated by jOOQ.
 */
package de.fiereu.openmmo.db.jooq.tables.records;


import de.fiereu.openmmo.db.jooq.tables.Character;

import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CharacterRecord extends UpdatableRecordImpl<CharacterRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.character.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.character.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.character.user_id</code>.
     */
    public void setUserId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.character.user_id</code>.
     */
    public Integer getUserId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.character.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.character.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.character.forehead</code>.
     */
    public void setForehead(Short value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.character.forehead</code>.
     */
    public Short getForehead() {
        return (Short) get(3);
    }

    /**
     * Setter for <code>public.character.forehead_color</code>.
     */
    public void setForeheadColor(Short value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.character.forehead_color</code>.
     */
    public Short getForeheadColor() {
        return (Short) get(4);
    }

    /**
     * Setter for <code>public.character.hat</code>.
     */
    public void setHat(Short value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.character.hat</code>.
     */
    public Short getHat() {
        return (Short) get(5);
    }

    /**
     * Setter for <code>public.character.hat_color</code>.
     */
    public void setHatColor(Short value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.character.hat_color</code>.
     */
    public Short getHatColor() {
        return (Short) get(6);
    }

    /**
     * Setter for <code>public.character.hair</code>.
     */
    public void setHair(Short value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.character.hair</code>.
     */
    public Short getHair() {
        return (Short) get(7);
    }

    /**
     * Setter for <code>public.character.hair_color</code>.
     */
    public void setHairColor(Short value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.character.hair_color</code>.
     */
    public Short getHairColor() {
        return (Short) get(8);
    }

    /**
     * Setter for <code>public.character.eyes</code>.
     */
    public void setEyes(Short value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.character.eyes</code>.
     */
    public Short getEyes() {
        return (Short) get(9);
    }

    /**
     * Setter for <code>public.character.eyes_color</code>.
     */
    public void setEyesColor(Short value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.character.eyes_color</code>.
     */
    public Short getEyesColor() {
        return (Short) get(10);
    }

    /**
     * Setter for <code>public.character.facial_hair</code>.
     */
    public void setFacialHair(Short value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.character.facial_hair</code>.
     */
    public Short getFacialHair() {
        return (Short) get(11);
    }

    /**
     * Setter for <code>public.character.facial_hair_color</code>.
     */
    public void setFacialHairColor(Short value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.character.facial_hair_color</code>.
     */
    public Short getFacialHairColor() {
        return (Short) get(12);
    }

    /**
     * Setter for <code>public.character.back</code>.
     */
    public void setBack(Short value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.character.back</code>.
     */
    public Short getBack() {
        return (Short) get(13);
    }

    /**
     * Setter for <code>public.character.back_color</code>.
     */
    public void setBackColor(Short value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.character.back_color</code>.
     */
    public Short getBackColor() {
        return (Short) get(14);
    }

    /**
     * Setter for <code>public.character.top</code>.
     */
    public void setTop(Short value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.character.top</code>.
     */
    public Short getTop() {
        return (Short) get(15);
    }

    /**
     * Setter for <code>public.character.top_color</code>.
     */
    public void setTopColor(Short value) {
        set(16, value);
    }

    /**
     * Getter for <code>public.character.top_color</code>.
     */
    public Short getTopColor() {
        return (Short) get(16);
    }

    /**
     * Setter for <code>public.character.gloves</code>.
     */
    public void setGloves(Short value) {
        set(17, value);
    }

    /**
     * Getter for <code>public.character.gloves</code>.
     */
    public Short getGloves() {
        return (Short) get(17);
    }

    /**
     * Setter for <code>public.character.gloves_color</code>.
     */
    public void setGlovesColor(Short value) {
        set(18, value);
    }

    /**
     * Getter for <code>public.character.gloves_color</code>.
     */
    public Short getGlovesColor() {
        return (Short) get(18);
    }

    /**
     * Setter for <code>public.character.footwear</code>.
     */
    public void setFootwear(Short value) {
        set(19, value);
    }

    /**
     * Getter for <code>public.character.footwear</code>.
     */
    public Short getFootwear() {
        return (Short) get(19);
    }

    /**
     * Setter for <code>public.character.footwear_color</code>.
     */
    public void setFootwearColor(Short value) {
        set(20, value);
    }

    /**
     * Getter for <code>public.character.footwear_color</code>.
     */
    public Short getFootwearColor() {
        return (Short) get(20);
    }

    /**
     * Setter for <code>public.character.leggings</code>.
     */
    public void setLeggings(Short value) {
        set(21, value);
    }

    /**
     * Getter for <code>public.character.leggings</code>.
     */
    public Short getLeggings() {
        return (Short) get(21);
    }

    /**
     * Setter for <code>public.character.leggings_color</code>.
     */
    public void setLeggingsColor(Short value) {
        set(22, value);
    }

    /**
     * Getter for <code>public.character.leggings_color</code>.
     */
    public Short getLeggingsColor() {
        return (Short) get(22);
    }

    /**
     * Setter for <code>public.character.fishing_rod</code>.
     */
    public void setFishingRod(Short value) {
        set(23, value);
    }

    /**
     * Getter for <code>public.character.fishing_rod</code>.
     */
    public Short getFishingRod() {
        return (Short) get(23);
    }

    /**
     * Setter for <code>public.character.bike</code>.
     */
    public void setBike(Short value) {
        set(24, value);
    }

    /**
     * Getter for <code>public.character.bike</code>.
     */
    public Short getBike() {
        return (Short) get(24);
    }

    /**
     * Setter for <code>public.character.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(25, value);
    }

    /**
     * Getter for <code>public.character.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(25);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CharacterRecord
     */
    public CharacterRecord() {
        super(Character.CHARACTER);
    }

    /**
     * Create a detached, initialised CharacterRecord
     */
    public CharacterRecord(Integer id, Integer userId, String name, Short forehead, Short foreheadColor, Short hat, Short hatColor, Short hair, Short hairColor, Short eyes, Short eyesColor, Short facialHair, Short facialHairColor, Short back, Short backColor, Short top, Short topColor, Short gloves, Short glovesColor, Short footwear, Short footwearColor, Short leggings, Short leggingsColor, Short fishingRod, Short bike, LocalDateTime createdAt) {
        super(Character.CHARACTER);

        setId(id);
        setUserId(userId);
        setName(name);
        setForehead(forehead);
        setForeheadColor(foreheadColor);
        setHat(hat);
        setHatColor(hatColor);
        setHair(hair);
        setHairColor(hairColor);
        setEyes(eyes);
        setEyesColor(eyesColor);
        setFacialHair(facialHair);
        setFacialHairColor(facialHairColor);
        setBack(back);
        setBackColor(backColor);
        setTop(top);
        setTopColor(topColor);
        setGloves(gloves);
        setGlovesColor(glovesColor);
        setFootwear(footwear);
        setFootwearColor(footwearColor);
        setLeggings(leggings);
        setLeggingsColor(leggingsColor);
        setFishingRod(fishingRod);
        setBike(bike);
        setCreatedAt(createdAt);
        resetChangedOnNotNull();
    }
}
