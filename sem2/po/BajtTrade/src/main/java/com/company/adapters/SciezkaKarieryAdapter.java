package com.company.adapters;

import com.company.strategia.ścieżkaKariery.*;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public class SciezkaKarieryAdapter {
    @FromJson
    ŚcieżkaKariery fromJson(ŚcieżkaKariery.Typ typ) {
        return switch (typ) {
            case PROGRAMISTA -> new Programista();
            case INŻYNIER -> new Inżynier();
            case RZEMIEŚLNIK -> new Rzemieślnik();
            case GÓRNIK -> new Górnik();
            case ROLNIK -> new Rolnik();
        };
    }
    @ToJson ŚcieżkaKariery.Typ toJson(ŚcieżkaKariery kariera) {
        return kariera.typ;
    }
}
