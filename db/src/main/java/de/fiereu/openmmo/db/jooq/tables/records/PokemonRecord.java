/*
 * This file is generated by jOOQ.
 */
package de.fiereu.openmmo.db.jooq.tables.records;


import de.fiereu.openmmo.db.jooq.tables.Pokemon;

import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class PokemonRecord extends UpdatableRecordImpl<PokemonRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.pokemon.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.pokemon.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.pokemon.trainer_id</code>.
     */
    public void setTrainerId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.pokemon.trainer_id</code>.
     */
    public Integer getTrainerId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.pokemon.container_id</code>.
     */
    public void setContainerId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.pokemon.container_id</code>.
     */
    public Integer getContainerId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>public.pokemon.container_position</code>.
     */
    public void setContainerPosition(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.pokemon.container_position</code>.
     */
    public Integer getContainerPosition() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>public.pokemon.dex_id</code>.
     */
    public void setDexId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.pokemon.dex_id</code>.
     */
    public Integer getDexId() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.pokemon.original_trainer_id</code>.
     */
    public void setOriginalTrainerId(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.pokemon.original_trainer_id</code>.
     */
    public Integer getOriginalTrainerId() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>public.pokemon.caught_at</code>.
     */
    public void setCaughtAt(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.pokemon.caught_at</code>.
     */
    public LocalDateTime getCaughtAt() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>public.pokemon.name</code>.
     */
    public void setName(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.pokemon.name</code>.
     */
    public String getName() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.pokemon.seed</code>.
     */
    public void setSeed(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.pokemon.seed</code>.
     */
    public Integer getSeed() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>public.pokemon.level</code>.
     */
    public void setLevel(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.pokemon.level</code>.
     */
    public Integer getLevel() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>public.pokemon.moves</code>.
     */
    public void setMoves(Integer[] value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.pokemon.moves</code>.
     */
    public Integer[] getMoves() {
        return (Integer[]) get(10);
    }

    /**
     * Setter for <code>public.pokemon.moves_pp</code>.
     */
    public void setMovesPp(Integer[] value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.pokemon.moves_pp</code>.
     */
    public Integer[] getMovesPp() {
        return (Integer[]) get(11);
    }

    /**
     * Setter for <code>public.pokemon.ev_hp</code>.
     */
    public void setEvHp(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.pokemon.ev_hp</code>.
     */
    public Integer getEvHp() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>public.pokemon.ev_attack</code>.
     */
    public void setEvAttack(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.pokemon.ev_attack</code>.
     */
    public Integer getEvAttack() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>public.pokemon.ev_defense</code>.
     */
    public void setEvDefense(Integer value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.pokemon.ev_defense</code>.
     */
    public Integer getEvDefense() {
        return (Integer) get(14);
    }

    /**
     * Setter for <code>public.pokemon.ev_special_attack</code>.
     */
    public void setEvSpecialAttack(Integer value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.pokemon.ev_special_attack</code>.
     */
    public Integer getEvSpecialAttack() {
        return (Integer) get(15);
    }

    /**
     * Setter for <code>public.pokemon.ev_special_defense</code>.
     */
    public void setEvSpecialDefense(Integer value) {
        set(16, value);
    }

    /**
     * Getter for <code>public.pokemon.ev_special_defense</code>.
     */
    public Integer getEvSpecialDefense() {
        return (Integer) get(16);
    }

    /**
     * Setter for <code>public.pokemon.ev_speed</code>.
     */
    public void setEvSpeed(Integer value) {
        set(17, value);
    }

    /**
     * Getter for <code>public.pokemon.ev_speed</code>.
     */
    public Integer getEvSpeed() {
        return (Integer) get(17);
    }

    /**
     * Setter for <code>public.pokemon.iv_hp</code>.
     */
    public void setIvHp(Integer value) {
        set(18, value);
    }

    /**
     * Getter for <code>public.pokemon.iv_hp</code>.
     */
    public Integer getIvHp() {
        return (Integer) get(18);
    }

    /**
     * Setter for <code>public.pokemon.iv_attack</code>.
     */
    public void setIvAttack(Integer value) {
        set(19, value);
    }

    /**
     * Getter for <code>public.pokemon.iv_attack</code>.
     */
    public Integer getIvAttack() {
        return (Integer) get(19);
    }

    /**
     * Setter for <code>public.pokemon.iv_defense</code>.
     */
    public void setIvDefense(Integer value) {
        set(20, value);
    }

    /**
     * Getter for <code>public.pokemon.iv_defense</code>.
     */
    public Integer getIvDefense() {
        return (Integer) get(20);
    }

    /**
     * Setter for <code>public.pokemon.iv_special_attack</code>.
     */
    public void setIvSpecialAttack(Integer value) {
        set(21, value);
    }

    /**
     * Getter for <code>public.pokemon.iv_special_attack</code>.
     */
    public Integer getIvSpecialAttack() {
        return (Integer) get(21);
    }

    /**
     * Setter for <code>public.pokemon.iv_special_defense</code>.
     */
    public void setIvSpecialDefense(Integer value) {
        set(22, value);
    }

    /**
     * Getter for <code>public.pokemon.iv_special_defense</code>.
     */
    public Integer getIvSpecialDefense() {
        return (Integer) get(22);
    }

    /**
     * Setter for <code>public.pokemon.iv_speed</code>.
     */
    public void setIvSpeed(Integer value) {
        set(23, value);
    }

    /**
     * Getter for <code>public.pokemon.iv_speed</code>.
     */
    public Integer getIvSpeed() {
        return (Integer) get(23);
    }

    /**
     * Setter for <code>public.pokemon.is_shiny</code>.
     */
    public void setIsShiny(Boolean value) {
        set(24, value);
    }

    /**
     * Getter for <code>public.pokemon.is_shiny</code>.
     */
    public Boolean getIsShiny() {
        return (Boolean) get(24);
    }

    /**
     * Setter for <code>public.pokemon.has_hidden_ability</code>.
     */
    public void setHasHiddenAbility(Boolean value) {
        set(25, value);
    }

    /**
     * Getter for <code>public.pokemon.has_hidden_ability</code>.
     */
    public Boolean getHasHiddenAbility() {
        return (Boolean) get(25);
    }

    /**
     * Setter for <code>public.pokemon.is_alpha</code>.
     */
    public void setIsAlpha(Boolean value) {
        set(26, value);
    }

    /**
     * Getter for <code>public.pokemon.is_alpha</code>.
     */
    public Boolean getIsAlpha() {
        return (Boolean) get(26);
    }

    /**
     * Setter for <code>public.pokemon.is_secret</code>.
     */
    public void setIsSecret(Boolean value) {
        set(27, value);
    }

    /**
     * Getter for <code>public.pokemon.is_secret</code>.
     */
    public Boolean getIsSecret() {
        return (Boolean) get(27);
    }

    /**
     * Setter for <code>public.pokemon.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(28, value);
    }

    /**
     * Getter for <code>public.pokemon.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(28);
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
     * Create a detached PokemonRecord
     */
    public PokemonRecord() {
        super(Pokemon.POKEMON);
    }

    /**
     * Create a detached, initialised PokemonRecord
     */
    public PokemonRecord(Integer id, Integer trainerId, Integer containerId, Integer containerPosition, Integer dexId, Integer originalTrainerId, LocalDateTime caughtAt, String name, Integer seed, Integer level, Integer[] moves, Integer[] movesPp, Integer evHp, Integer evAttack, Integer evDefense, Integer evSpecialAttack, Integer evSpecialDefense, Integer evSpeed, Integer ivHp, Integer ivAttack, Integer ivDefense, Integer ivSpecialAttack, Integer ivSpecialDefense, Integer ivSpeed, Boolean isShiny, Boolean hasHiddenAbility, Boolean isAlpha, Boolean isSecret, LocalDateTime createdAt) {
        super(Pokemon.POKEMON);

        setId(id);
        setTrainerId(trainerId);
        setContainerId(containerId);
        setContainerPosition(containerPosition);
        setDexId(dexId);
        setOriginalTrainerId(originalTrainerId);
        setCaughtAt(caughtAt);
        setName(name);
        setSeed(seed);
        setLevel(level);
        setMoves(moves);
        setMovesPp(movesPp);
        setEvHp(evHp);
        setEvAttack(evAttack);
        setEvDefense(evDefense);
        setEvSpecialAttack(evSpecialAttack);
        setEvSpecialDefense(evSpecialDefense);
        setEvSpeed(evSpeed);
        setIvHp(ivHp);
        setIvAttack(ivAttack);
        setIvDefense(ivDefense);
        setIvSpecialAttack(ivSpecialAttack);
        setIvSpecialDefense(ivSpecialDefense);
        setIvSpeed(ivSpeed);
        setIsShiny(isShiny);
        setHasHiddenAbility(hasHiddenAbility);
        setIsAlpha(isAlpha);
        setIsSecret(isSecret);
        setCreatedAt(createdAt);
        resetChangedOnNotNull();
    }
}