# libakamaibmp Research

This repository documents a method for partially deobfuscating the `libakamaibmp.so` library.
The focus is on recovering a readable version of the library by dumping the memory after it has been loaded by the runtime. Control flow flattening is **not** addressed in this research.

Both the **original obfuscated** `libakamaibmp.so` and the **reconstructed deobfuscated** `libakamai_deobfuscated.so` are provided in this repository.

---

# Description

When the original `libakamaibmp.so` was opened in Ghidra, the library appeared heavily obfuscated. The disassembly contained many invalid instructions, and the decompiled output was largely unreadable.

Instead of attempting static analysis on the obfuscated binary, the approach used here relies on the behavior of the Android runtime loader. When the library is loaded and initialized, some of the obfuscation is resolved in memory. By executing the library through a wrapper program and dumping the process memory afterward, it is possible to reconstruct a cleaner version of the binary.

The resulting dumped library is significantly easier to analyze in reverse engineering tools such as Ghidra.

### Decompiled view of `buildN` function before deobfuscation

Insert link here.

---

# Requirements

* Rooted Android device (an emulator such as Waydroid is sufficient)
* Ghidra
* Termux

---

# Steps

### 1. Install Termux

Install **Termux** on the Android device.

### 2. Prepare the working directory

Push the obfuscated `libakamaibmp.so` library to the Android device.
The working directory used in this guide is:

```
/data/local/tmp
```

### 3. Clone the repository

Clone this repository into the working directory.

### 4. Configure the wrapper

Edit the file:

```
com/cyberfend/cyfsecurity/SensorDataBuilder.java
```

Provide the **full path** to `libakamaibmp.so`.

### 5. Compile the wrapper

```
javac -cp android.jar:. android/util/Pair.java com/cyberfend/cyfsecurity/SensorDataBuilder.java
```

### 6. Run the wrapper

```
java -cp .:android.jar com.cyberfend.cyfsecurity.SensorDataBuilder
```

This loads the native library inside a Java process.

### 7. Locate the process memory

```
cd /proc/$(pidof java)/
```

This opens the directory of the running Java process.

### 8. Identify the memory mappings

```
cat maps | grep akamai
```

Example output:

```
7f1cc346f000-7f1cc3701000 rwxp 00000000 103:03 6331505 /data/local/tmp/libakamaibmp.so
7f1cc3704000-7f1cc3731000 r--p 00291000 103:03 6331505 /data/local/tmp/libakamaibmp.so
7f1cc3734000-7f1cc3737000 rw-p 002bd000 103:03 6331505 /data/local/tmp/libakamaibmp.so
7f1cc373f000-7f1cc3740000 rw-p 002c8000 103:03 6331505 /data/local/tmp/libakamaibmp.so
7f1cc3743000-7f1cc37b9000 rwxp 002cc000 103:03 6331505 /data/local/tmp/libakamaibmp.so
7f1cc37b9000-7f1cc37bb000 r-xp 00342000 103:03 6331505 /data/local/tmp/libakamaibmp.so
```

Each entry represents a memory segment belonging to the loaded library.

### 9. Dump each memory segment

Dump each region using `dd`. Example:

```
dd if=$(pwd)/mem skip=$((0x7f1cc346f000)) bs=1 count=$((0x7f1cc3701000-0x7f1cc346f000)) of=/data/local/tmp/seg1.tmp
```

Repeat this process for every mapped region.

### 10. Merge the dumped segments

After dumping all segments, they must be merged back into a single file.

Navigate to the directory where the segments were saved.

### 11. Rebuild the library

Create a new file large enough to hold all segments:

```
truncate -s $((size)) libakamai_deobfuscated.so
```

`size` should be the total size required to contain all segments.

Then write each dumped segment back to its correct file offset using `dd`.

### 12. Analyze the rebuilt library

Copy the reconstructed file to the PC:

```
libakamai_deobfuscated.so
```

Open it in **Ghidra**. The disassembly and decompiled output should now be significantly clearer.

### 13. Improve readability

Import structures such as `JNIEnv` into Ghidra to further improve readability of the native code.

---

# buildN function after deobfuscation

Insert image here.

---

This README was formatted and grammar-corrected with the assistance of ChatGPT.
