package com.company.produkt;

import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;
import com.squareup.moshi.Json;

import java.util.Optional;

import static com.company.produkt.Produkt.Typ.*;

public abstract class Produkt {
    public Optional<Integer> dajPoziom() {
        return this.poziom;
    }

    public void ustawIlosc(double i) {
        ilosc = i;
    }

    public void uzyj(int uzywam) {
        ilosc -= uzywam;
    }

    public boolean czyMaPoziomJakosci() {
        return typ == UBRANIA || typ == NARZĘDZIA;
    }

    public Produkt skopiuj() {
        return FabrykaProduktów.dajProdukt(typ, ilosc, poziom);
    }


    public enum Typ {
        @Json(name = "jedzenie") JEDZENIE,
        @Json(name = "ubrania") UBRANIA,
        @Json(name = "narzedzia") NARZĘDZIA,
        @Json(name = "diamenty") DIAMENT,
        @Json(name = "programy") PROGRAMY_KOMPUTEROWE
    }

    final Typ typ;

    private double ilosc;
    protected Optional<Integer> poziom;

    public Typ dajTyp() {
        return this.typ;
    }

    public static boolean czyMaPoziomy(Typ typ) {
        return typ == UBRANIA || typ == NARZĘDZIA
                || typ == PROGRAMY_KOMPUTEROWE;
    }

    public double dajIlosc() {
        return this.ilosc;
    }

    public Produkt(Typ typ, double ilosc) {
        this.typ = typ;
        this.ilosc = ilosc;
        this.poziom = Optional.empty();
    }

    public abstract ŚcieżkaKariery dajOdpowiadającąŚcieżkęKariery();
}
