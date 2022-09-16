package com.company.strategia.produkcji;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

public class Chciwy extends StrategiaProdukcji {
    @Override
    public double jakaWagaDlaDanegoProduktu(Produkt.Typ typProduktu, Giełda giełda, Robotnik robotnik) {
        double średniaCena = giełda.dajSredniaCene(typProduktu);
        double ileWyprodukować = robotnik.ileMozeszWyprodukowac(typProduktu,
                giełda);

        return średniaCena * ileWyprodukować;
    }
}
