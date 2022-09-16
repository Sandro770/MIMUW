package com.company.agent;

import com.company.giełda.Giełda;
import com.company.oferta.Oferta;
import com.company.oferta.OfertaRobotnika;
import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;
import com.company.strategia.coRobi.StrategiaCoRobiDanegoDnia;
import com.company.strategia.kupowania.StrategiaKupowaniaIStosowaniaProgramów;
import com.company.strategia.produkcji.StrategiaProdukcji;
import com.company.strategia.zmianyKariery.StrategiaZmianyŚcieżkiKariery;
import com.company.strategia.ścieżkaKariery.*;
import com.company.symulacja.InformacjeStartoweOSymulacji;
import com.company.strategia.ścieżkaKariery.*;
import com.squareup.moshi.Json;

import java.util.*;

import static com.company.oferta.Oferta.Typ.SPRZEDAZY;
import static com.company.produkt.Produkt.Typ.*;
import static com.company.strategia.ścieżkaKariery.ŚcieżkaKariery.Typ.PROGRAMISTA;
import static java.lang.Math.*;

public class Robotnik extends Agent {
    @Json(name = "uczenie")
    private StrategiaCoRobiDanegoDnia strategiaNauki;
    @Json(name = "zmiana")
    private StrategiaZmianyŚcieżkiKariery strategiaZmianyŚcieżkiKariery;
    @Json(name = "kupowanie")
    private StrategiaKupowaniaIStosowaniaProgramów strategiaKupowania;
    @Json(name = "kariera")
    private ŚcieżkaKariery aktualnaŚcieżkaKariery;
    @Json(name = "produktywnosc")
    private Map<Produkt.Typ, Integer> bazowyWektorProduktywnosci;
    @Json(name = "produkcja")
    private StrategiaProdukcji strategiaProdukcji;

    transient private List<ŚcieżkaKariery> kariery;
    transient private int dzienOstatniegoPosilku = 0;
    transient private boolean czyUczylSie;
    transient private List<Produkt> ostatnioWyprodukowane;

    private Robotnik() {
        kariery = List.of(new Rolnik(), new Górnik(), new Rzemieślnik(),
                new Programista(), new Inżynier());
    }

    public void pracujLubUczSie(Giełda giełda) {
        if (strategiaNauki.czyUczeSie(this, giełda)) {
            uczSie(giełda);
            czyUczylSie = true;
        }
        else {
            pracuj(giełda);
            czyUczylSie = false;
        }
    }

    private void uczSie(Giełda giełda) {
        najedzSie(giełda);

        ŚcieżkaKariery tmp = strategiaZmianyŚcieżkiKariery.naCoZmiana(this,
                giełda);

        if (tmp == aktualnaŚcieżkaKariery)
            aktualnaŚcieżkaKariery.zwiekszPoziom();
        else
            aktualnaŚcieżkaKariery = tmp;
    }

    private void pracuj(Giełda giełda) {
        ArrayList<Produkt> wyprodukowane = produkujPrzedmioty(giełda);

        this.ostatnioWyprodukowane = wyprodukowane;

        zajmijSieWyprodukowanymiPrzedmiotami(wyprodukowane, giełda);
        dodajOfertyKupna(giełda);
    }

    private void zajmijSieWyprodukowanymiPrzedmiotami(ArrayList<Produkt> wyprodukowane, Giełda giełda) {
        if (wyprodukowane.isEmpty())
            return;

        if (wyprodukowane.get(0).dajTyp() == DIAMENT) {
            for (Produkt x : wyprodukowane)
                dodajZasob(x);

            return;
        }

        dodajOfertySprzedazy(wyprodukowane, giełda);
    }

    private void dodajOfertyKupna(Giełda giełda) {
        strategiaKupowania.wystawOfertęKupna(this, giełda);
    }

    private void dodajOfertySprzedazy(ArrayList<Produkt> wyprodukowane, Giełda giełda) {
        for (Produkt produkt: wyprodukowane) {
            Oferta oferta = new OfertaRobotnika(SPRZEDAZY, this, produkt);

            giełda.dodajOferte(oferta);
        }
    }

    private ArrayList<Produkt> produkujPrzedmioty(Giełda giełda) {
        Produkt.Typ typProduktu =
                strategiaProdukcji.jakiPrzedmiotDoProdukcji(this, giełda);

        return produkujPrzedmioty(typProduktu,
                ileMozeszWyprodukowac(typProduktu, giełda));
    }

    private ArrayList<Produkt> produkujPrzedmioty(Produkt.Typ typProduktu, int ilosc) {
        ArrayList<Produkt> wyprodukowane = new ArrayList<>();

        if (!Produkt.czyMaPoziomy(typProduktu))
            wyprodukowane.add(FabrykaProduktów.dajProdukt(typProduktu, ilosc));
        else
            wyprodukowane = wyprodukujPrzedmiotZPoziomami(typProduktu, ilosc);

        return wyprodukowane;
    }

    private ArrayList<Produkt> wyprodukujPrzedmiotZPoziomami(Produkt.Typ typProduktu, int ilosc) {
        ArrayList<Produkt> wyprodukowane = new ArrayList<>();

        if (typProduktu == PROGRAMY_KOMPUTEROWE) {
            Produkt produkt = FabrykaProduktów.dajProdukt(typProduktu, ilosc,
                    dajNajwyzszyPoziomJakiMozeWyprodukowacDanegoProduktu(typProduktu));

            wyprodukowane.add(produkt);
        }
        else {
            while (ilosc > 0) {
                Produkt wyprodukowanyProdukt;

                Optional<Integer> poziom =
                        dajNajwyzszyPoziomJakiMozeWyprodukowacDanegoProduktu(typProduktu);
                int iloscDanegoPoziomu = dajIlosc(typProduktu, poziom);

                if (iloscDanegoPoziomu == 0)
                    iloscDanegoPoziomu = ilosc;

                int zuzywam = min(ilosc, iloscDanegoPoziomu);

                wyprodukowanyProdukt = FabrykaProduktów.dajProdukt(typProduktu,
                        zuzywam, poziom);

                ilosc -= zuzywam;
                zuzyjWykorzystaneProgramyKomputerowe(poziom.get(), zuzywam);

                wyprodukowane.add(wyprodukowanyProdukt);
            }
        }

        return wyprodukowane;
    }

    private Optional<Integer> dajNajwyzszyPoziomJakiMozeWyprodukowacDanegoProduktu(
            Produkt.Typ typProduktu) {
        if (!Produkt.czyMaPoziomy(typProduktu))
            return Optional.empty();

        if (typProduktu == PROGRAMY_KOMPUTEROWE) {
            if (aktualnaŚcieżkaKariery.typ == PROGRAMISTA)
                return Optional.of(aktualnaŚcieżkaKariery.dajPoziom());

            return Optional.of(1);
        }

        List<Produkt> programy = zasoby.get(PROGRAMY_KOMPUTEROWE);

        if (programy.stream().noneMatch(x -> x.dajIlosc() > 0))
            return Optional.of(1);

        return programy.stream()
                .filter(x -> x.dajIlosc() > 0)
                .max(Comparator.comparing(x -> x.dajPoziom().get()))
                .get()
                .dajPoziom();
    }

    public int ileMozeszWyprodukowac(Produkt.Typ typProduktu, Giełda giełda) {
        int bazowa = bazowyWektorProduktywnosci.get(typProduktu);
        int premia = dajPremięWProcentachBazowej(typProduktu, giełda);

        return (int) max(0.0, (bazowa * (100.0 + premia) )/ 100.0);
    }

    private int dajPremięWProcentachBazowej(Produkt.Typ typProduktu,
                                            Giełda giełda) {
        return premiaZaKariere(typProduktu)
                + premiaZaNarzedziaWProcentachBazowej()
                + dajKaryWProcentachBazowej(giełda);
    }

    private int premiaZaNarzedziaWProcentachBazowej() {
        return zasoby.get(NARZĘDZIA)
                .stream()
                .mapToInt(x -> x.dajPoziom().get())
                .sum();
    }

    private int dajKaryWProcentachBazowej(Giełda giełda) {
        return dajKareZaBrakJedzeniaWProcentachBazowej(giełda)
                + dajKareZaBrakUbranWProcentachBazowej(giełda);
    }

    private int dajKareZaBrakUbranWProcentachBazowej(Giełda giełda) {
        if (dajIlosc(UBRANIA, Optional.empty()) < 100)
            return -giełda.dajKareZaBrakUbran();
        return 0;
    }

    private int dajKareZaBrakJedzeniaWProcentachBazowej(Giełda giełda) {
        int aktualnyDzien = giełda.dajAktualnyDzień();
        int dniBezJedzenia = aktualnyDzien - dzienOstatniegoPosilku - 1;

        return switch (dniBezJedzenia) {
            case 0 -> 0;
            case 1 -> -100;
            case 2 -> -300;
            default -> throw new IllegalStateException("Unexpected value: "
                    + dniBezJedzenia);
        };
    }

    private int premiaZaKariere(Produkt.Typ typProduktu) {
        Produkt tmp = FabrykaProduktów.dajProdukt(typProduktu, 0);

        return tmp.dajOdpowiadającąŚcieżkęKariery().typ == aktualnaŚcieżkaKariery.typ ?
                aktualnaŚcieżkaKariery.dajPremięDoProdukcji()
                : 0;
    }

    public void koniecDnia(Giełda giełda, InformacjeStartoweOSymulacji infoOSymu) {
        if (czyUczylSie)
            return;

        zuzyjJedzenie(giełda);
        zuzyjUbrania();
        zuzyjNarzedzia();
    }

    public boolean czyNieJadlTrzyDniZRzedu(Giełda giełda) {
        return giełda.dajAktualnyDzień() - dzienOstatniegoPosilku == 3;
    }

    private void zuzyjWykorzystaneProgramyKomputerowe(int poziom, int zuzywam) {
        for (Produkt program : zasoby.get(PROGRAMY_KOMPUTEROWE)) {
            if (program.dajPoziom().get() == poziom) {
                int progIlosc = (int) program.dajIlosc();
                int wezme = min(zuzywam, progIlosc);
                program.ustawIlosc(progIlosc - wezme);
                zuzywam -= wezme;

                if (zuzywam == 0)
                    break;
            }
        }
    }

    private void zuzyjNarzedzia() {
        zasoby.get(NARZĘDZIA).clear();
    }

    private void zuzyjUbrania() {
        uzyjZasobu(UBRANIA, 100);
    }

    private void zuzyjJedzenie(Giełda giełda) {
        int ileJedzenia = ileMaŁącznieSztuk(JEDZENIE);

        if (ileJedzenia >= 100)
            najedzSie(giełda);

        uzyjZasobu(JEDZENIE, 100);
    }

    private void uzyjZasobu(Produkt.Typ typ, int ileRazy) {
        for (int i = 0; ileRazy > 0 && i < zasoby.get(typ).size(); i++){
            Produkt produkt = zasoby.get(typ).get(i);

            int ileMoznaUzyc = (int) produkt.dajIlosc();

            int uzywam = min(ileMoznaUzyc, ileRazy);

            produkt.uzyj(uzywam);
            ileRazy -= uzywam;
        }
    }

    private void najedzSie(Giełda giełda) {
        dzienOstatniegoPosilku = giełda.dajAktualnyDzień();
    }

    public ŚcieżkaKariery dajAktualnaSciezkeKariery() {
        return this.aktualnaŚcieżkaKariery;
    }

    public List<ŚcieżkaKariery> dajŚcieżkiKariery() {
        return this.kariery;
    }

    public int ileZuzyjeUbran() {
        int przed = dajIlosc(UBRANIA, Optional.empty());

        zuzyjUbrania();

        int po = dajIlosc(UBRANIA, Optional.empty());
        Produkt zuzyte = FabrykaProduktów.dajProdukt(UBRANIA,
                100, Optional.of(1));

        dodajZasob(zuzyte);

        return przed - po;
    }

    public int ileMaŁącznieSztuk(Produkt.Typ typ) {
        return zasoby.get(typ).stream()
                .mapToInt(x -> x.dajIlosc() > 0 ? 1 : 0)
                .sum();
    }

    public int ileWyprodukowałemSztukProduktowZPoziomamiJakosci() {
        return ostatnioWyprodukowane.stream()
                .filter(Produkt::czyMaPoziomJakosci)
                .mapToInt(x -> (int)x.dajIlosc())
                .sum();
    }
}
