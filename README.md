# Questly
[![Android Build](https://github.com/kristof39612/Questly/actions/workflows/android.yml/badge.svg)](https://github.com/kristof39612/Questly/actions/workflows/android.yml)

A Questly egy NHF projekt részünkről a BMEVIAUMA21 (Szoftverarchitektúrák) c. tárgyhoz a 2024/25/1 félévben.

**Csapattagok:**

- Lovácsi Kristóf - N3EEWB
- Szladek Máté Nándor - TGPZTT
- Tóth Ádám László - TK6NT3
- Kókai Roland - DTKJYT

**Eredeti cím** Közösségi városi kincskereső alkalmazás fejlesztése 

**Eredeti kiírás:** 
*A hallgatók feladata egy olyan játék elkészítése, amely a felhasználókat a játékterület/város különböző pontjaira küldi bizonyos küldetések elvégzésére. A játék legyen elérhető mobil készülékekről. Mikor a játékos a megfelelő lokációra/pontra érkezik, megkapja a feladatot, melynek elvégzéséért pontokat kaphat. A különböző feladatokban elért, illetve az összesített pontszámok toplistán megtekinthetők. A játékban több feladattípust is meg kell valósítani.A játékosok küldhetnek be feladatokat, illetve útvonalakat (amiket adminisztrátorok engedélyezhetnek), így változtatva közösségivé a kincskeresést.*

## Bevezetés

A **Questly** egy modern, közösségi városi kincskereső Android-alkalmazás. Célunk, hogy a játékosokat a város különböző pontjaira vezessük izgalmas feladatok teljesítése érdekében. A játék során pontokat gyűjthetsz, toplistákon versenyezhetsz, és akár saját feladatokat vagy útvonalakat is beküldhetsz a közösség számára.

## Fő funkciók

- **Feladatok teljesítése**: Különböző típusú kihívások várnak a felhasználókra a város több pontján.
- **Toplista**: Nyomonkövethető állások felhasználók között a Toplista fülön.
- **Közösségi tartalom**: Felhasználók saját feladatpontokat küldhetnek be, melyet az adminisztrátorok engedélyezhetnek.
- **Többféle feladattípus**: Szöveges válasz, kiválasztásos feladat, sétálós feladat.
---
![combined_with_bars](https://github.com/user-attachments/assets/d94427b2-4a39-4a68-b72e-44411c1d0d48)
---

## Telepítés

1. **Klónozd a projektet**:

    ```bash

    git clone https://github.com/kristof39612/Questly.git
    ```

2. **Nyisd meg Android Studio-ban**:
    - Importáld a projektet az Android Studio legújabb verziójával.

3. **Futtasd az alkalmazást**:
    - Válassz egy kompatibilis emulátort vagy csatlakoztass egy Android-eszközt.
    - Indítsd el az alkalmazást az IDE-ből.

## Rendszerkövetelmények

- **Minimális Android verzió**: 8.0 (API 26)
- **Fejlesztői eszközök**: Android Studio Ladybug vagy újabb
- **Internetkapcsolat**: Szükséges a játék funkcióinak teljes körű használatához.
- **Engedélyek**: Az alkalmazás futtatásához a helymeghatározás és kamera engedélyezése is szükséges.

## Egyedi backend
Jelenleg a backend egy élő verziója hosztolt futtatókörnyezetben megtalálható a ```https://questly.lovacsi.me/``` URL cím alatt.
Amennyiben egyedi backend URL címet szeretnél használni, kérlek írd felül a [RetrofitInstance.kt](app/src/main/java/hu/bme/aut/szoftverarch/questly/data/networking/RetrofitInstance.kt) fájlban a címet:
```kotlin
const val BASE_URL = "http://<HOST_PC>:8080"
```
