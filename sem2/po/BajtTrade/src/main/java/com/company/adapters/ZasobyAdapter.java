package com.company.adapters;

import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.*;

public class ZasobyAdapter {

    private List<Produkt> dajOdpowiedniZasob(Produkt.Typ typ, int ilosc) {
        Produkt produkt = FabrykaProduktów.dajProdukt(typ, ilosc);

        List<Produkt> list = new ArrayList<>();
        list.add(produkt);

        return list;
    }

    @FromJson
    Map<Produkt.Typ, List<Produkt>> fromJson(Map<Produkt.Typ, Integer> zasoby) {
        Map<Produkt.Typ, List<Produkt>> wynik = new HashMap<>();

        zasoby.forEach((typ, ilosc) -> wynik.put(typ, dajOdpowiedniZasob(typ,
                ilosc)));

        return wynik;
    }

    @ToJson Map<Produkt.Typ, List<Double>> toJson(Map<Produkt.Typ,
            List<Produkt>> zasob) {
        Map<Produkt.Typ, List<Double>> wynik = new HashMap<>();

        for (Produkt.Typ typ: Produkt.Typ.values()) {
            List<Produkt> produkty = zasob.get(typ);

            int maxi = dajNajwyzszyPoziom(produkty);
            List<Double> listaIlosci = new ArrayList<>();

            for (int i = 1; i <= maxi; i++) {
                final int tmp = i;

                double ilosc = produkty.stream()
                        .filter(x -> !Produkt.czyMaPoziomy(x.dajTyp()) || x.dajPoziom().get() == tmp)
                        .mapToDouble(Produkt::dajIlosc)
                        .sum();

                listaIlosci.add(ilosc);
            }

            wynik.put(typ, listaIlosci);
        }

        return wynik;
    }

    private int dajNajwyzszyPoziom(List<Produkt> produkty) {
        if (produkty.isEmpty() || !Produkt.czyMaPoziomy(produkty.get(0).dajTyp()))
            return 1;

        Optional<Integer> poziom =
                produkty.stream().max((a,b) -> a.dajPoziom().isEmpty() ? 0 :
                a.dajPoziom().get() -  b.dajPoziom().get())
                .get()
                .dajPoziom();

        return poziom.orElse(1);
    }
}
