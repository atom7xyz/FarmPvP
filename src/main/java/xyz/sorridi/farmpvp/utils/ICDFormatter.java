package xyz.sorridi.farmpvp.utils;

import lombok.NonNull;
import xyz.sorridi.stone.utils.data.Array;

public interface ICDFormatter<T>
{
    String[] TIME_FULL_ITA_PLU = Array.of(" giorni", " ore", " minuti", " secondi", " millisecondi");
    String[] TIME_FULL_ITA_SIN = Array.of(" giorno", " ora", " minuto", " secondo", " millisecondo");

    String[] TIME_FULL_ENG_PLU = Array.of(" days", " hours", " minutes", " seconds", " milliseconds");
    String[] TIME_FULL_ENG_SIN = Array.of(" day", " hour", " minute", " second", " millisecond");

    String[] TIME_SHORT_ENG = Array.of("d", "h", "m", "s", "ms");
    String[] TIME_SHORT_ITA = Array.of("g", "o", "m", "s", "ms");

    String getUsableRemaining(@NonNull T target);
}
