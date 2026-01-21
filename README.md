[Bahasa Indonesia](README.id.md) | English
# FocusTimer ⏳

**FocusTimer** is a productivity-driven Android application that ensures you stay off your phone while working. Unlike traditional timers, FocusTimer only runs when your device is placed **face-down** on a solid surface. If you flip the phone over or pick it up, the timer immediately pauses.

---

## 🚀 How it Works
FocusTimer uses **Sensor Fusion** logic to verify that the phone is properly placed for a focus session. It monitors two specific hardware sensors:

1.  **Accelerometer ($Z$-axis):** * Android's coordinate system defines the $Z$-axis as pointing out of the screen. 
    * When the phone is flat on its back (face-up), the sensor reads $+9.8 \text{ m/s}^2$. 
    * When the phone is **face-down**, the sensor reads approximately **$-9.8 \text{ m/s}^2$**.
2.  **Ambient Light Sensor (Lux):** * To prevent "cheating" (simply holding the phone upside down in the air), the app checks the light level.
    * When placed against a solid surface, the lux value drops to **near 0**, confirming the screen is covered.

The timer starts only when **$Z \approx -8$** AND **Lux $\approx 0$**.

---

## ✨ Key Features
* **FaceDown Start:** The timer only counts down when the phone is physically placed face-down.
* **Lift-to-Pause:** Immediate pausing logic if the phone is picked up, preventing distractions.
* **Persistent Alarm:** Once the timer hits zero, the phone vibrates continuously until the user manually stops it, ensuring you are alerted even if the phone is still face-down.
* **Quick-Set Presets:** Dedicated buttons for 30m, 10m, and 2m for rapid session starting.
* **Manual Reset:** Stopping the timer mid-session will automatically reset the clock, encouraging full-session completion.

---

## 📥 Installation
This is an open-source project and is not available on the Google Play Store.

1.  Go to the **[Releases](https://github.com/VHarya/FocusTimer/releases)** tab.
2.  Download the `FocusTimer.apk`.
3.  Transfer the APK to your phone and open it.
4.  Enable **"Install from Unknown Sources"** if prompted.

---

## 🛠 Technical Details
* **Language:** Kotlin / Java
* **API Level:** Min SDK 24+
* **Sensors:** `TYPE_ACCELEROMETER`, `TYPE_LIGHT`
