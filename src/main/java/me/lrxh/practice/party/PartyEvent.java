package me.lrxh.practice.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PartyEvent {

    FFA("FFA"),
    SPLIT("Split");

    private String name;

}
