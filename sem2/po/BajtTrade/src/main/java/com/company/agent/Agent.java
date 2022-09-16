package com.company.agent;

import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;

import java.util.*;

import static com.company.produkt.Produkt.Typ.DIAMENT;

public abstract class Agent {
    private int id;

    protected Map<Produkt.Typ, List<Produkt>> zasoby;

    public int dajId() {
        return this.id;
    }

    protected boolean czySkonczylSieZasob(Produkt.Typ typ) {
        return 0 == dajIlosc(typ, Optional.empty());
    }

    public int dajIlosc(Produkt.Typ typProduktu, Optional<Integer> poziom) {
        int ilosc = 0;

        for (Produkt produkt: zasoby.get(typProduktu))
            if (poziom.isEmpty())
                ilosc += produkt.dajIlosc();
            else if (produkt.dajPoziom() == poziom)
                ilosc += produkt.dajIlosc();

        return ilosc;
    }

    public void dodajZasob(Produkt x) {
        zasoby.get(x.dajTyp()).add(x);
    }

    public double ileDiamentow() {
        return dajIlosc(DIAMENT, Optional.empty());
    }

    public void pobierzDiamenty(Produkt.Typ diament, double sumarycznaKwotaTranskacji) {
        double ileJest = ileDiamentow();
        zasoby.get(DIAMENT).clear();

        Produkt nowyDiament = FabrykaProduktów.dajProdukt(DIAMENT,
                ileJest - sumarycznaKwotaTranskacji);

        zasoby.get(DIAMENT).add(nowyDiament);
    }
}
