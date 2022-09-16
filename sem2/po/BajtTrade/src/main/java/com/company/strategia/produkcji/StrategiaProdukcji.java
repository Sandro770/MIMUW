package com.company.strategia.produkcji;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;
import com.company.strategia.Strategia;

import java.io.DataInput;

import static com.company.produkt.Produkt.Typ.DIAMENT;

public abstract class StrategiaProdukcji extends Strategia {
    public Produkt.Typ jakiPrzedmiotDoProdukcji(Robotnik robotnik,
                                                         Giełda giełda) {
        double najwiekszaWaga = Double.NEGATIVE_INFINITY;

        Produkt.Typ wybranyTyp = null;

        for (Produkt.Typ typ: Produkt.Typ.values()) {
            if (typ == DIAMENT)
                continue;

            double kandydat = jakaWagaDlaDanegoProduktu(typ, giełda, robotnik);

            if (kandydat >= najwiekszaWaga) {
                najwiekszaWaga = kandydat;
                wybranyTyp = typ;
            }
        }

        return wybranyTyp;
    }
    public abstract double jakaWagaDlaDanegoProduktu(Produkt.Typ typProduktu, Giełda giełda, Robotnik robotnik);


}
