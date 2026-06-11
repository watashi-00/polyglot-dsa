# Assembly (x86_64 NASM) 🚀

This directory contains implementations of Data Structures and Algorithms in low-level Assembly for Linux x86_64, using **NASM** (Intel syntax) and the GNU Linker (`ld`).

## 🛠️ Requirements

To run these implementations, make sure you have `nasm` and `ld` (binutils) installed on your system:
- **Arch Linux**: `sudo pacman -S nasm`
- **Ubuntu/Debian**: `sudo apt install nasm build-essential`
- **Fedora**: `sudo dnf install nasm binutils`

## 🚀 Execution with `dinamic_asm_test.sh`

All assembly implementations should be compiled and run using the `dinamic_asm_test.sh` script. This script automates the build process and provides advanced monitoring tools.

### Features
- **Auto-Detection**: Automatically detects if the file uses **NASM** or **GAS** (GNU Assembler) syntax.
- **Smart Linking**: Tries to link using `ld` and falls back to `gcc -no-pie` if necessary.
- **Inactivity Monitoring**: Detects when a program becomes unresponsive or silent for too long.
- **Daemon Mode**: Run tests in the background with output redirected to a log file.
- **Error Handling**: Captures and reports Segmentation Faults and other runtime crashes.
- **Cleanup**: Automatically removes temporary object files and executables after execution.

### Usage
```bash
./dinamic_asm_test.sh [options] <file.asm>
```

### Options
- `-t, --timeout S`: Set inactivity timeout in seconds (default: 15s).
- `-d, --daemon`: Run in background (daemon mode). Output will be saved to `<file>.log`.
- `-h, --help`: Show help message.

### Example
To run the Singly Linked List with a 5-second timeout:
```bash
./dinamic_asm_test.sh -t 5 SinglyLinkedList.asm
```
