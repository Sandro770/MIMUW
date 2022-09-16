package com.company.agent;

import com.company.giełda.Giełda;
import com.company.oferta.Oferta;
import com.company.oferta.OfertaSpekulanta;
import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;
import com.company.strategia.handluSpekulanta.StrategiaHandluSpekulanta;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.company.oferta.Oferta.Typ.KUPNA;
import static com.company.oferta.Oferta.Typ.SPRZEDAZY;
import static com.company.produkt.Produkt.Typ.DIAMENT;

public class Spekulant extends Agent {
    @Json(name = "kariera")
    private StrategiaHandluSpekulanta strategiaHandlu;

    public void wejdźZOfertami(Giełda giełda) {
        for (Produkt.Typ typ: Produkt.Typ.values())
            if (typ == DIAMENT)
                continue;
            else
                strategiaHandlu.wejdźZOfertami(giełda, typ, this);
    }

    public void dodajWszystkieOfertySprzedazy(Giełda giełda, Produkt.Typ typ,
                                              double sredniaCena) {
        List<Produkt> produkty = zasoby.get(typ);

        for (Produkt produkt : produkty)
            dodajOferteSprzedazy(giełda, produkt, sredniaCena);
    }

    protected void dodajOferteSprzedazy(Giełda giełda, Produkt produkt, double sredniaCena) {
        double cena = sredniaCena * 1.1;

        Oferta oferta = new OfertaSpekulanta(SPRZEDAZY, this, produkt, cena);
        giełda.dodajOferte(oferta);
    }

    public void dodajOfertęKupna(Giełda giełda, Produkt.Typ typ,
                                 double sredniaCena) {
        double cena = dajCeneKupna(typ, sredniaCena);

        Produkt produkt = FabrykaProduktów.dajProdukt(typ, 100,
                Optional.empty());

        Oferta kupna = new OfertaSpekulanta(KUPNA, this, produkt, cena);

        giełda.dodajOferte(kupna);
    }

    protected double dajCeneKupna(Produkt.Typ typ, double sredniaCena) {
        if (this.czySkonczylSieZasob(typ))
            return sredniaCena * 0.95;
        else
            return sredniaCena * 0.9;
    }

    public enum Typ {
        @Json(name = "sredni") SREDNI,
        @Json(name = "wypukly") WYPUKLY,
        @Json(name = "regulujacy_rynek") REGULUJACY_RYNEK
    }
}
