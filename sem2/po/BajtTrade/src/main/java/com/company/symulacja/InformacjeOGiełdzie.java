package com.company.symulacja;

import com.company.produkt.Produkt;

import java.util.Map;

public class InformacjeOGiełdzie {
    int dzien;
    Map<Produkt.Typ, Double> ceny_srednie, ceny_max, ceny_min;

    public InformacjeOGiełdzie(int dzien,
                               Map<Produkt.Typ, Double> ceny_srednie,
                               Map<Produkt.Typ, Double> ceny_max,
                               Map<Produkt.Typ, Double> ceny_min) {
        this.dzien = dzien;
        this.ceny_srednie = ceny_srednie;
        this.ceny_max = ceny_max;
        this.ceny_min = ceny_min;
    }
}
