package xyz.sirblobman.alienware.theme;

import xyz.sirblobman.alienware.codes.Zone;

import java.util.EnumMap;
import java.util.Map;

public final class SlotData {
    private final Map<Zone, SequenceList> zoneMap;

    public SlotData() {
        this.zoneMap = new EnumMap<>(Zone.class);
    }
}
