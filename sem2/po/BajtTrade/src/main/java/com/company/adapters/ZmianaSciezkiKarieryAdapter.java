package com.company.adapters;

import com.company.strategia.zmianyKariery.Konserwatysta;
import com.company.strategia.zmianyKariery.Rewolucjonista;
import com.company.strategia.zmianyKariery.StrategiaZmianyŚcieżkiKariery;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public class ZmianaSciezkiKarieryAdapter {
    @FromJson
    StrategiaZmianyŚcieżkiKariery fromJson(StrategiaZmianyŚcieżkiKariery.Typ typ) {
        return switch (typ) {
            case KONSERWATYSTA -> new Konserwatysta();
            case REWOLUCJONISTA -> new Rewolucjonista();
        };
    }
    @ToJson
    StrategiaZmianyŚcieżkiKariery.Typ toJson(StrategiaZmianyŚcieżkiKariery strategia) {
        return strategia.typ;
    }
}
