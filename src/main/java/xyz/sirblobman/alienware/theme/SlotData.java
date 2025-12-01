package xyz.sirblobman.alienware.theme;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.codes.Zone;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class SlotData {
    private final Map<Zone, SequenceList> zoneMap;

    public SlotData() {
        this.zoneMap = new EnumMap<>(Zone.class);
    }

    public Map<Zone, SequenceList> getZoneMap() {
        return Collections.unmodifiableMap(this.zoneMap);
    }

    public void setZoneSettings(@NotNull Zone zone, @Nullable SequenceList list) {
        if (list == null) {
            this.zoneMap.remove(zone);
        } else {
            this.zoneMap.put(zone, list);
        }
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        return String.format(Locale.US, "%s{zoneMap=%s}", className, this.zoneMap);
    }
}
