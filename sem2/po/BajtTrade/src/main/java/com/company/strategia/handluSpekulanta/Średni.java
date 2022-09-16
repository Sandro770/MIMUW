package com.company.strategia.handluSpekulanta;

import com.company.agent.Spekulant;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

public class Średni extends StrategiaHandluSpekulanta {
    private final int historia_spekulanta_sredniego;

    public Średni(int historia_spekulanta_sredniego) {
        this.historia_spekulanta_sredniego = historia_spekulanta_sredniego;
    }

    @Override
    public void wejdźZOfertami(Giełda giełda, Produkt.Typ typ, Spekulant spekulant) {
        double sredniaCena = giełda.dajSredniaCenePrzezOstatnieDni(typ,
                historia_spekulanta_sredniego);
        spekulant.dodajOfertęKupna(giełda, typ, sredniaCena);
        spekulant.dodajWszystkieOfertySprzedazy(giełda, typ, sredniaCena);
    }
}
