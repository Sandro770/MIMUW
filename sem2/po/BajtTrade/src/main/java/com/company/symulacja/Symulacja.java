package com.company.symulacja;

import com.company.adapters.SciezkaKarieryAdapter;
import com.company.adapters.ZasobyAdapter;
import com.company.adapters.ZmianaSciezkiKarieryAdapter;
import com.company.agent.Robotnik;
import com.company.agent.Spekulant;
import com.company.giełda.Giełda;
import com.company.giełda.Kapitalistyczna;
import com.company.giełda.Socjalistyczna;
import com.company.giełda.Zrównoważona;
import com.company.strategia.coRobi.*;
import com.company.strategia.handluSpekulanta.RegulujacyRynek;
import com.company.strategia.handluSpekulanta.StrategiaHandluSpekulanta;
import com.company.strategia.handluSpekulanta.Wypukli;
import com.company.strategia.handluSpekulanta.Średni;
import com.company.strategia.kupowania.*;
import com.company.strategia.produkcji.*;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Symulacja {
    private static Moshi dajMoshiBuilder() {
        return new Moshi.Builder()
                .add(PolymorphicJsonAdapterFactory.of(Giełda.class,
                                        "info")
                                .withSubtype(Socjalistyczna.class,
                                        "socjalistyczna")
                                .withSubtype(Kapitalistyczna.class,
                                        "kapitalistyczna")
                                .withSubtype(Zrównoważona.class,
                                        "zrownowazona")
                )
                .add(PolymorphicJsonAdapterFactory.of(StrategiaCoRobiDanegoDnia.class,
                                        "typ")
                                .withSubtype(Student.class,"student")
                                .withSubtype(Oszczędny.class, "oszczedny")
                                .withSubtype(Pracuś.class, "pracus")
                                .withSubtype(Okresowy.class, "okresowy")
                                .withSubtype(Rozkładowy.class, "rozkladowy")
                )
                .add(new ZmianaSciezkiKarieryAdapter())
                .add(PolymorphicJsonAdapterFactory.of(StrategiaKupowaniaIStosowaniaProgramów.class, "typ")
                        .withSubtype(Technofob.class, "technofob")
                        .withSubtype(Czyścioszek.class, "czyscioszek")
                        .withSubtype(Gadżeciarz.class, "gadzeciarz")
                        .withSubtype(Zmechanizowany.class, "zmechanizowany")
                )
                .add(new SciezkaKarieryAdapter())
                .add(PolymorphicJsonAdapterFactory.of(StrategiaProdukcji.class, "typ")
                        .withSubtype(Średniak.class, "sredniak")
                        .withSubtype(Chciwy.class, "chciwy")
                        .withSubtype(Krótkowzroczny.class, "krotkowzroczny")
                        .withSubtype(Perspektywiczny.class, "perspektywiczny")
                        .withSubtype(Losowy.class, "losowy"))
                .add(new ZasobyAdapter())
                .add(PolymorphicJsonAdapterFactory.of(StrategiaHandluSpekulanta.class, "typ")
                        .withSubtype(Średni.class, "sredni")
                        .withSubtype(Wypukli.class, "wypukly")
                        .withSubtype(RegulujacyRynek.class, "regulujacy_rynek"))
                .build();
    }

    public static void symuluj(String sciezkaDoPlikuWejsciowego) throws IOException {
        Moshi moshi = dajMoshiBuilder();
        JsonAdapter<InformacjeStartoweOSymulacji> adapter = dajAdapter(moshi);

        String json = dajJson(sciezkaDoPlikuWejsciowego);
        List<String> wynikSymulacji = new ArrayList<>();

        InformacjeStartoweOSymulacji infoOSymulacji = adapter.fromJson(json);

        Giełda gielda = dajGielde(infoOSymulacji.info);

        dodajOpisDzisiejszegoDnia(moshi, gielda, infoOSymulacji, wynikSymulacji);

        for (int i = 1; i <= infoOSymulacji.info.dlugosc; i++) {
            wykonajDzień(infoOSymulacji, i, gielda);

            dodajOpisDzisiejszegoDnia(moshi, gielda, infoOSymulacji, wynikSymulacji);
        }
    }

    private static void dodajOpisDzisiejszegoDnia(Moshi moshi, Giełda gielda,
                                                  InformacjeStartoweOSymulacji infoOSymulacji, List<String> wynikSymulacji) {
        PrzebiegSymulacji tmp = new PrzebiegSymulacji();

        tmp.info = gielda.dajOpis();
        tmp.robotnicy = infoOSymulacji.robotnicy;
        tmp.spekulanci = infoOSymulacji.spekulanci;

        System.out.println(moshi.adapter(PrzebiegSymulacji.class).indent(
                "\t").toJson(tmp));
    }

    private static String dajJson(String sciezkaDoPliku) {
        try {
            return new String(Files.readAllBytes(Path.of(sciezkaDoPliku)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static JsonAdapter<InformacjeStartoweOSymulacji> dajAdapter(Moshi moshi) {
        return moshi.adapter(InformacjeStartoweOSymulacji.class);
    }

    private static Giełda dajGielde(InformacjeOgolne infoOgolne) {
        return switch (infoOgolne.typGieldy) {
            case SOCJALISTYCZNA -> new Socjalistyczna(infoOgolne);
            case KAPITALISTYCZNA -> new Kapitalistyczna(infoOgolne);
            case ZROWNOWAZONA -> new Zrównoważona(infoOgolne);
        };
    }

    private static void wykonajDzień(InformacjeStartoweOSymulacji informacjeOSymulacji, int dzień, Giełda gielda) {
        gielda.nowyDzień(dzień);

        obsluzUmieranieZGlodu(informacjeOSymulacji, gielda);

        for (Robotnik robotnik: informacjeOSymulacji.robotnicy)
            robotnik.pracujLubUczSie(gielda);

        for (Spekulant spekulant: informacjeOSymulacji.spekulanci)
            spekulant.wejdźZOfertami(gielda);

        gielda.wykonajDzień();

        for (Robotnik robotnik: informacjeOSymulacji.robotnicy)
            robotnik.koniecDnia(gielda, informacjeOSymulacji);
    }

    private static void obsluzUmieranieZGlodu(InformacjeStartoweOSymulacji
                                                      informacjeOSymulacji,
                                              Giełda giełda) {
        List<Robotnik> robotnicy = informacjeOSymulacji.robotnicy;

        informacjeOSymulacji.robotnicy =
                robotnicy.stream()
                        .filter(x -> !x.czyNieJadlTrzyDniZRzedu(giełda))
                .toList();
    }
}
