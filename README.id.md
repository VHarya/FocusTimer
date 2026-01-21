Bahasa Indonesia | [English](README.md)

# FocusTimer

**FocusTimer** adalah aplikasi produktivitas Android yang memastikan Anda tetap fokus dan menjauh dari ponsel saat bekerja. Tidak seperti timer tradisional, FocusTimer hanya akan berjalan jika perangkat diletakkan dengan posisi **layar menghadap ke bawah** (face-down) di atas permukaan yang solid. Jika ponsel dibalik atau diangkat, timer akan otomatis berhenti (pause).

## Screenshots

<img src="https://drive.google.com/uc?export=view&id=18QC9QpOeDJWVdhCtFDnhBwmmuNAPlbxQ" alt="Logo" width="200"> <img src="https://drive.google.com/uc?export=view&id=1Z-s7ihEYx_aqG2ZAGCo2Whf_YDGBwPGm" alt="Logo" width="200">

## 🚀 Cara Kerja
FocusTimer menggunakan logika **Sensor Fusion** untuk memverifikasi bahwa ponsel benar-benar diletakkan dalam posisi fokus. Aplikasi ini memantau dua sensor perangkat keras khusus:

1.  **Accelerometer (Sumbu-$Z$):** * Sistem koordinat Android mendefinisikan sumbu-$Z$ mengarah keluar dari layar.
    * Saat ponsel terlentang (layar menghadap atas), sensor membaca $+9.8 \text{ m/s}^2$.
    * Saat ponsel **menghadap ke bawah**, sensor membaca sekitar **$-9.8 \text{ m/s}^2$**.
2.  **Sensor Cahaya (Ambient Light/Lux):**
    * Untuk mencegah "kecurangan" (seperti hanya memegang ponsel terbalik di tangan), aplikasi mengecek tingkat cahaya.
    * Saat diletakkan di atas permukaan solid, nilai lux akan turun menjadi **mendekati 0**, mengonfirmasi bahwa layar tertutup sepenuhnya.

Timer hanya akan mulai jika **$Z \approx -9.8$** DAN **Lux $\approx 0$**.



## Fitur Utama
* **Pemicu Gravitasi:** Timer hanya berjalan jika ponsel diletakkan menghadap ke bawah.
* **Angkat Pause:** Logika jeda otomatis jika ponsel diangkat untuk mencegah gangguan.
* **Alarm Persisten:** Saat timer habis, ponsel akan bergetar terus-menerus hingga pengguna menekan tombol stop secara manual.
* **Preset Cepat:** Tombol khusus untuk durasi 30 menit, 10 menit, dan 2 menit.
* **Reset Manual:** Menghentikan timer di tengah sesi akan mereset waktu ke awal, mendorong penyelesaian sesi secara penuh.



## Instalasi
Ini adalah proyek open-source mahasiswa dan saat ini tidak tersedia di Google Play Store.

1.  Buka tab **[Releases](https://github.com/VHarya/FocusTimer/releases)**.
2.  Unduh file `FocusTimer.apk`.
3.  Pindahkan APK ke ponsel Anda dan buka.
4.  Aktifkan **"Install from Unknown Sources"** jika diminta oleh sistem.



## Detail Teknis
* **Bahasa:** [Kotlin / Java]
* **API Level:** Min SDK 24+
* **Sensor:** `TYPE_ACCELEROMETER`, `TYPE_LIGHT`



## Lisensi
Proyek ini dilisensikan di bawah **MIT License**. Lihat file `LICENSE` untuk detail lebih lanjut.



*Dikembangkan sebagai tugas kuliah untuk mata kuliah Pemrograman Perangkat Bergerak 2 (PPB2).*
