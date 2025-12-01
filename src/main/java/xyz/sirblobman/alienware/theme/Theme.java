package xyz.sirblobman.alienware.theme;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.BasicColor;
import xyz.sirblobman.alienware.application.converter.BasicColorConverter;
import xyz.sirblobman.alienware.codes.Command;
import xyz.sirblobman.alienware.codes.PowerState;
import xyz.sirblobman.alienware.codes.Zone;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
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
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return loadTheme(jsonObject);
        } catch (JsonParseException | IllegalStateException ex) {
            throw new ThemeParseException(ex);
        }
    }

    private static @NotNull Theme loadTheme(@NotNull JsonObject json) throws ThemeParseException {
        Theme theme = new Theme();
        PowerState[] slotArray = PowerState.values();
        for (PowerState slot : slotArray) {
            String slotName = slot.name();
            if (json.has(slotName)) {
                JsonObject slotObject = json.get(slotName).getAsJsonObject();
                SlotData slotData = loadSlotData(slotObject);
                theme.setSlotData(slot, slotData);
            } else {
                IO.println("WARN: Did not find power slot '" + slotName + "' in theme.");
            }
        }

        return theme;
    }

    private static @NotNull SlotData loadSlotData(@NotNull JsonObject json) throws ThemeParseException {
        SlotData slotData = new SlotData();
        Zone[] zoneArray = Zone.values();
        for (Zone zone : zoneArray) {
            String zoneName = zone.name();
            if (json.has(zoneName)) {
                JsonObject sequenceListObject = json.get(zoneName).getAsJsonObject();
                SequenceList sequenceList = loadSequenceList(sequenceListObject);
                slotData.setZoneSettings(zone, sequenceList);
            } else {
                IO.println("WARN: Did not find zone '" + zone + "' in current slot.");
            }
        }

        return slotData;
    }

    private static @NotNull SequenceList loadSequenceList(@NotNull JsonObject json) throws ThemeParseException {
        if (!json.has("tempo")) {
            throw new ThemeParseException("Missing required element 'tempo' for sequence list.");
        }

        if (!json.has("sequences")) {
            throw new ThemeParseException("Missing required array 'sequences' for sequence list.");
        }

        int tempo = json.get("tempo").getAsInt();
        SequenceList sequenceList = new SequenceList(tempo);
        JsonArray sequencesArray = json.get("sequences").getAsJsonArray();
        for (JsonElement sequenceElement : sequencesArray) {
            JsonObject sequenceObject = sequenceElement.getAsJsonObject();
            Sequence sequence = loadSequence(sequenceObject);
            sequenceList.addSequence(sequence);
        }

        return sequenceList;
    }

    private static @NotNull Sequence loadSequence(@NotNull JsonObject json) throws ThemeParseException {
        if (!json.has("command")) {
            throw new ThemeParseException("Missing required element 'command' for sequence.");
        }

        if (!json.has("color")) {
            throw new ThemeParseException("Missing required element 'color' for sequence.");
        }

        String commandName = json.get("command").getAsString();
        Command command;

        try {
            command = Command.valueOf(commandName);
        } catch (IllegalArgumentException ex) {
            throw new ThemeParseException("Invalid command name '" + commandName + "'. Must be one of MORPH_COLOR, PULSE_COLOR, or SET_COLOR", ex);
        }

        if (command != Command.SET_COLOR && command != Command.PULSE_COLOR && command != Command.MORPH_COLOR) {
            throw new ThemeParseException("Invalid command name '" + commandName + "'. Must be one of MORPH_COLOR, PULSE_COLOR, or SET_COLOR");
        }

        String colorString1 = json.get("color").getAsString();
        BasicColorConverter converter = new BasicColorConverter();
        BasicColor color1;
        try {
            color1 = converter.convert(colorString1);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Failed to parse color '" + colorString1 + "':", ex);
        }

        if (command == Command.MORPH_COLOR) {
            if (!json.has("color2")) {
                throw new ThemeParseException("Missing required element 'color2' for morph sequence.");
            }

            String colorString2 = json.get("color2").getAsString();
            BasicColor color2;
            try {
                color2 = converter.convert(colorString2);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Failed to parse color '" + colorString2 + "':", ex);
            }

            return new Sequence(command, color1, color2);
        } else {
            return new Sequence(command, color1, null);
        }
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        return String.format(Locale.US, "%s{slotMap=%s}", className, this.slotMap);
    }
}
