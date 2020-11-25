package com.shikhardev.mancala.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Class represents a pit. Pit information includes id, owner, type of pit and the current number of stones.
 * The class implements get / set for each of the fields, along with methods to increment and decrement the
 * current number of stones.
 */
@Component
public class Pit {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private Player owner;

    @Getter
    @Setter
    private PitType pitType;

    @Getter
    @Setter
    private Integer numberOfStones;

    public void incrementStoneCountBy(int increment) {
        this.numberOfStones += increment;
    }

    public void decrementStoneCountBy(int dec) {
        this.numberOfStones -= dec;
    }
}
