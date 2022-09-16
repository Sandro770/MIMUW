package com.company.strategia.zmianyKariery;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.strategia.Strategia;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;
import com.squareup.moshi.Json;

public abstract class StrategiaZmianyŚcieżkiKariery extends Strategia {
    protected StrategiaZmianyŚcieżkiKariery(Typ typ) {
        this.typ = typ;
    }

    public enum Typ{
        @Json(name = "rewolucjonista") REWOLUCJONISTA,
        @Json(name = "konserwatysta") KONSERWATYSTA
    }
    public final Typ typ;

    public abstract ŚcieżkaKariery naCoZmiana(Robotnik robotnik, Giełda giełda);
}
