package xyz.sirblobman.alienware.theme;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public final class SequenceList {
    private final int tempo;
    private final List<Sequence> sequenceList;

    public SequenceList(int tempo) {
        this.tempo = tempo;
        this.sequenceList = new LinkedList<>();
    }

    public int getTempo() {
        return this.tempo;
    }

    public List<Sequence> getSequenceList() {
        return Collections.unmodifiableList(this.sequenceList);
    }

    public void addSequence(@NotNull Sequence sequence) {
        this.sequenceList.add(sequence);
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        return String.format(Locale.US, "%s{tempo=%s,sequences=%s}", className, this.tempo, this.sequenceList);
    }
}
