package com.company.symulacja;

import com.company.giełda.Giełda;
import com.company.produkt.Produkt;
import com.squareup.moshi.Json;

import java.util.HashMap;
import java.util.Map;

public class InformacjeOgolne {
    @Json(name = "gielda")
    Giełda.Typ typGieldy;
    int dlugosc;
    public int kara_za_brak_ubran;
    public Map<Produkt.Typ, Double> ceny;
}

