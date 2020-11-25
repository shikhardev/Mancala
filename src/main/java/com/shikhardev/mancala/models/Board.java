package com.shikhardev.mancala.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the whole board via a collection of Pit objects.
 * Collection has been implemented as a List<Pit> (ArrayList).
 * This allows O(1) fetch operations and O(1) init for a fixed length of pits.
 * Also implements exception handling for mis-specified pitID during fetch operations.
 */
@Component
public class Board {

    @Getter
    @Setter
    private List<Pit> allPits = new ArrayList<>();

    /**
     * Returns the pit object for the corresponding id
     * @param id [0, 13]
     * @return Corresponding Pit object if exists
     * @exception IndexOutOfBoundsException if Pit of the specified id does not exist yet.
     */
    public Pit getPit(Integer id) {
        try {
            return allPits.get(id);
        }
        catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("ID %d is invalid or board uninitiated", id));
        }
    }

}
