package com.company.strategia.produkcji;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

import java.util.Random;

public class Losowy extends StrategiaProdukcji {
    @Override
    public double jakaWagaDlaDanegoProduktu(Produkt.Typ typProduktu, Giełda giełda, Robotnik robotnik) {
        return new Random().nextDouble();
    }
}
