# Tucil 1 - Penyelesaian Permainan Queens Linkedin
Program Java untuk menyelesaikan variasi N-Queens berbasis region menggunakan pendekatan brute force. Aplikasi menyediakan mode CLI dan GUI (JavaFX), termasuk live update progres saat proses pencarian berjalan.

---

## Contributor

| NIM | Nama |
| --- | --- |
| 13524133 | Muhammad Daffa Arrizki Yanma |

---

## Tech Stack
- Java 21
- Gradle
- JavaFX

---

## Fitur
- Solver brute force untuk puzzle N-Queens.
- Validasi constraint solusi:
  - jumlah ratu = N,
  - tidak boleh satu baris,
  - tidak boleh satu kolom,
  - tidak boleh satu region,
  - tidak boleh berdampingan.
- GUI JavaFX:
  - Load puzzle `.txt`,
  - Solve,
  - Save solusi `.txt`,
  - live update, jumlah kasus, dan waktu(ms).
- CLI untuk eksekusi berbasis terminal.
- Validasi input dasar:
  - file tidak kosong,
  - ukuran papan valid,
  - format papan persegi `N x N`.

---

## Format Input
Input puzzle berupa file teks persegi `N x N`, setiap karakter mewakili region suatu sel.

Contoh (`6x6`):

```txt
AAABBB
ACCCBB
ACDDBB
ECDDFF
EEDFFF
EEEFFF
```

---

## Cara Menjalankan

### 1. GUI
Menjalankan GUI JavaFX:

```bash
gradle run
```


### 2. CLI
Karena `mainClass` Gradle diarahkan ke GUI, jalankan CLI setelah kompilasi class:

```bash
java -cp build/classes/java/main com.tucil1.Main
```

---

## Struktur Proyek

```txt
.
│-- build.gradle
│-- setting.gradle
│-- src
│   │-- main
│       │-- java
│           │-- com
│               │-- tucil1
│                   │-- Main.java
│                   │-- core
│                   │   │-- Board.java
│                   │   │-- Parser.java
│                   │   │-- Solver.java
│                   │-- gui
│                   │   │-- LiveUpdateController.java
│                   │   │-- QueensGUI.java
│                   │-- util
│                       │-- Cell.java
│-- test
│   │-- test1.txt
│   │-- test2.txt
│   │-- test3.txt
│   │-- test4.txt
│   │-- test5.txt
│   │-- test6.txt
│-- save
    │-- (hasil solusi .txt)
```

---
