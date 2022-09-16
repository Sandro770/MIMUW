package com.company.oferta;

import com.company.agent.Agent;
import com.company.produkt.Produkt;

import java.util.Comparator;

public abstract class Oferta {

    public enum Typ {KUPNA, SPRZEDAZY}
    public enum Czyja {ROBOTNIKA, SPEKULANTA}
    public final Typ typ;
    public final Agent czyja;
    public Produkt produkt;

    public Oferta(Typ typ, Agent czyja, Produkt produkt) {
        this.typ = typ;
        this.czyja = czyja;
        this.produkt = produkt;
    }
}
