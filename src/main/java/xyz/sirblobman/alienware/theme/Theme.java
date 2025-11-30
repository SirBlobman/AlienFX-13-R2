package xyz.sirblobman.alienware.theme;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.codes.PowerState;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class Theme {
    private final Map<PowerState, SlotData> slotMap;

    public Theme() {
        this.slotMap = new EnumMap<>(PowerState.class);
    }

    public Map<PowerState, SlotData> getSlotMap() {
        return Collections.unmodifiableMap(this.slotMap);
    }

    public @Nullable SlotData getSlotData(@NotNull PowerState state) {
        return this.slotMap.get(state);
    }

    public void setSlotData(@NotNull PowerState state, @Nullable SlotData data) {
        if (data == null) {
            this.slotMap.remove(state);
        } else {
            this.slotMap.put(state, data);
        }
    }

    public static @NotNull Theme loadTheme(@NotNull String json) throws ThemeParseException {
        // TODO
        return new Theme();
    }
}
