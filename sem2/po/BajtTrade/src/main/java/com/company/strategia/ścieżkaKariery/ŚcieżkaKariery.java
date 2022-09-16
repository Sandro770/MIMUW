package com.company.strategia.ścieżkaKariery;

import com.company.strategia.Strategia;
import com.squareup.moshi.Json;

public abstract class ŚcieżkaKariery extends Strategia {
    public enum Typ{
        @Json(name = "rolnik") ROLNIK,
        @Json(name = "gornik")GÓRNIK,
        @Json(name = "rzemieslnik")RZEMIEŚLNIK,
        @Json(name = "inzynier")INŻYNIER,
        @Json(name = "programista")PROGRAMISTA
    }
    public final Typ typ;
    private int poziomŚcieżkiKariery = 1;

    protected ŚcieżkaKariery(Typ typ) {
        this.typ = typ;
    }

    public void zwiekszPoziom() {
        poziomŚcieżkiKariery++;
    }

    public int dajPoziom() {return poziomŚcieżkiKariery;}

    public int dajPremięDoProdukcji() {
        return switch (poziomŚcieżkiKariery) {
            case 1 -> 50;
            case 2 -> 150;
            default -> 300 + 25 * (poziomŚcieżkiKariery - 3);
        };
    }
}
